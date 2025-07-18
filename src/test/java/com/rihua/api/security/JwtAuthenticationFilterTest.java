package com.rihua.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JwtAuthenticationFilter単体テスト
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("有効なJWTトークンで認証が成功する")
    void doFilterInternal_ValidJwtToken_SetsAuthentication() throws ServletException, IOException {
        // Given
        String token = "valid.jwt.token";
        String userId = "test-user-id";
        String email = "test@example.com";
        String language = "ja";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.isAccessToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(token)).thenReturn(userId);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRolesFromToken(token)).thenReturn(Arrays.asList("ROLE_USER"));
        when(jwtTokenProvider.getLanguageFromToken(token)).thenReturn(language);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        assertThat(userPrincipal.getId()).isEqualTo(userId);
        assertThat(userPrincipal.getEmail()).isEqualTo(email);
        assertThat(userPrincipal.getPreferredLanguage()).isEqualTo(language);
        
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("無効なJWTトークンで認証が失敗する")
    void doFilterInternal_InvalidJwtToken_DoesNotSetAuthentication() throws ServletException, IOException {
        // Given
        String token = "invalid.jwt.token";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();
        
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("リフレッシュトークンでは認証が設定されない")
    void doFilterInternal_RefreshToken_DoesNotSetAuthentication() throws ServletException, IOException {
        // Given
        String token = "valid.refresh.token";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.isAccessToken(token)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();
        
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Authorizationヘッダーがない場合は認証をスキップ")
    void doFilterInternal_NoAuthorizationHeader_SkipsAuthentication() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider, never()).validateToken(any());
    }

    @Test
    @DisplayName("Bearer以外のAuthorizationヘッダーは無視される")
    void doFilterInternal_NonBearerAuthorizationHeader_SkipsAuthentication() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0");
        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider, never()).validateToken(any());
    }

    @Test
    @DisplayName("認証不要パスではフィルターがスキップされる")
    void shouldNotFilter_PublicPaths_ReturnsTrue() {
        // Given & When & Then
        when(request.getRequestURI()).thenReturn("/api/v1/auth/login");
        assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isTrue();

        when(request.getRequestURI()).thenReturn("/actuator/health");
        assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isTrue();

        when(request.getRequestURI()).thenReturn("/api-docs/swagger-config");
        assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isTrue();

        when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");
        assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isTrue();
    }

    @Test
    @DisplayName("認証が必要なパスではフィルターが実行される")
    void shouldNotFilter_ProtectedPaths_ReturnsFalse() {
        // Given & When & Then
        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");
        assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isFalse();

        when(request.getRequestURI()).thenReturn("/api/v1/events");
        assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isFalse();

        when(request.getRequestURI()).thenReturn("/api/v1/admin/users");
        assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isFalse();
    }

    @Test
    @DisplayName("JWT処理中の例外でSecurityContextがクリアされる")
    void doFilterInternal_JwtProcessingException_ClearsSecurityContext() throws ServletException, IOException {
        // Given
        String token = "malformed.jwt.token";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getRequestURI()).thenReturn("/api/v1/users/profile");
        when(jwtTokenProvider.validateToken(token)).thenThrow(new RuntimeException("JWT processing error"));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();
        
        verify(filterChain).doFilter(request, response);
    }
}