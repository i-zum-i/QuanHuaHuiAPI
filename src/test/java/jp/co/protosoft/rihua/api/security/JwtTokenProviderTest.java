package jp.co.protosoft.rihua.api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * JwtTokenProvider単体テスト
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String testSecret = "test-jwt-secret-key-for-testing-only-must-be-at-least-32-characters-long";
    private final long accessTokenExpiration = 3600000L; // 1時間
    private final long refreshTokenExpiration = 2592000000L; // 30日

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(testSecret, accessTokenExpiration, refreshTokenExpiration);
    }

    @Test
    @DisplayName("アクセストークン生成が正常に動作する")
    void generateAccessToken_ValidInput_ReturnsToken() {
        // Given
        String userId = "test-user-id";
        String email = "test@example.com";
        String preferredLanguage = "ja";
        
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_BUSINESS")
        );
        
        UserPrincipal userPrincipal = UserPrincipal.builder()
            .id(userId)
            .email(email)
            .authorities(authorities)
            .build();
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userPrincipal, null, authorities);

        // When
        String token = jwtTokenProvider.generateAccessToken(authentication, userId, email, preferredLanguage);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.isAccessToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(userId);
        assertThat(jwtTokenProvider.getEmailFromToken(token)).isEqualTo(email);
        assertThat(jwtTokenProvider.getLanguageFromToken(token)).isEqualTo(preferredLanguage);
    }

    @Test
    @DisplayName("リフレッシュトークン生成が正常に動作する")
    void generateRefreshToken_ValidInput_ReturnsToken() {
        // Given
        String userId = "test-user-id";

        // When
        String token = jwtTokenProvider.generateRefreshToken(userId);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.isRefreshToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(userId);
    }

    @Test
    @DisplayName("トークンから権限リストを正しく取得できる")
    void getRolesFromToken_ValidToken_ReturnsRoles() {
        // Given
        String userId = "test-user-id";
        String email = "test@example.com";
        
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_BUSINESS")
        );
        
        UserPrincipal userPrincipal = UserPrincipal.builder()
            .id(userId)
            .email(email)
            .authorities(authorities)
            .build();
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userPrincipal, null, authorities);
        
        String token = jwtTokenProvider.generateAccessToken(authentication, userId, email, "ja");

        // When
        List<String> roles = jwtTokenProvider.getRolesFromToken(token);

        // Then
        assertThat(roles).hasSize(2);
        assertThat(roles).contains("ROLE_USER", "ROLE_BUSINESS");
    }

    @Test
    @DisplayName("無効なトークンの検証が失敗する")
    void validateToken_InvalidToken_ReturnsFalse() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("トークンの有効期限チェックが正常に動作する")
    void isTokenExpired_ValidToken_ReturnsFalse() {
        // Given
        String userId = "test-user-id";
        String token = jwtTokenProvider.generateRefreshToken(userId);

        // When
        boolean isExpired = jwtTokenProvider.isTokenExpired(token);

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("トークンの残り有効時間を正しく取得できる")
    void getTokenRemainingTime_ValidToken_ReturnsPositiveValue() {
        // Given
        String userId = "test-user-id";
        String token = jwtTokenProvider.generateRefreshToken(userId);

        // When
        long remainingTime = jwtTokenProvider.getTokenRemainingTime(token);

        // Then
        assertThat(remainingTime).isPositive();
        assertThat(remainingTime).isLessThanOrEqualTo(refreshTokenExpiration / 1000);
    }

    @Test
    @DisplayName("トークンの有効期限を正しく取得できる")
    void getExpirationDateFromToken_ValidToken_ReturnsExpirationDate() {
        // Given
        String userId = "test-user-id";
        String token = jwtTokenProvider.generateRefreshToken(userId);

        // When
        Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);

        // Then
        assertThat(expirationDate).isNotNull();
        assertThat(expirationDate).isAfter(new Date());
    }

    @Test
    @DisplayName("短すぎるシークレットキーで例外が発生する")
    void constructor_ShortSecret_ThrowsException() {
        // Given
        String shortSecret = "short";

        // When & Then
        assertThatThrownBy(() -> new JwtTokenProvider(shortSecret, accessTokenExpiration, refreshTokenExpiration))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("JWT secret must be at least 32 characters long");
    }

    @Test
    @DisplayName("デフォルト言語設定が正常に動作する")
    void generateAccessToken_NullLanguage_UsesDefaultLanguage() {
        // Given
        String userId = "test-user-id";
        String email = "test@example.com";
        
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        
        UserPrincipal userPrincipal = UserPrincipal.builder()
            .id(userId)
            .email(email)
            .authorities(authorities)
            .build();
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userPrincipal, null, authorities);

        // When
        String token = jwtTokenProvider.generateAccessToken(authentication, userId, email, null);

        // Then
        assertThat(jwtTokenProvider.getLanguageFromToken(token)).isEqualTo("zh-CN");
    }
}