package jp.co.protosoft.rihua.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT トークン生成・検証ユーティリティクラス
 * 
 * <p>JWTアクセストークンとリフレッシュトークンの生成、検証、解析を行います。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenProvider(
            @Value("${rihua.jwt.secret}") String secret,
            @Value("${rihua.jwt.access-token-expiration}") long accessTokenExpirationMs,
            @Value("${rihua.jwt.refresh-token-expiration}") long refreshTokenExpirationMs) {
        
        // シークレットキーの最小長チェック
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }
        
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    /**
     * アクセストークンを生成
     * 
     * @param authentication 認証情報
     * @param userId ユーザーID
     * @param email メールアドレス
     * @param preferredLanguage 優先言語
     * @return JWTアクセストークン
     */
    public String generateAccessToken(Authentication authentication, String userId, String email, String preferredLanguage) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Instant now = Instant.now();
        Instant expiryDate = now.plus(accessTokenExpirationMs, ChronoUnit.MILLIS);

        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .claim("roles", roles)
                .claim("lang", preferredLanguage != null ? preferredLanguage : "zh-CN")
                .claim("type", "access")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * リフレッシュトークンを生成
     * 
     * @param userId ユーザーID
     * @return JWTリフレッシュトークン
     */
    public String generateRefreshToken(String userId) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(refreshTokenExpirationMs, ChronoUnit.MILLIS);

        return Jwts.builder()
                .setSubject(userId)
                .claim("type", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * トークンからユーザーIDを取得
     * 
     * @param token JWTトークン
     * @return ユーザーID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * トークンからメールアドレスを取得
     * 
     * @param token JWTトークン
     * @return メールアドレス
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("email", String.class);
    }

    /**
     * トークンから権限リストを取得
     * 
     * @param token JWTトークン
     * @return 権限リスト
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("roles", List.class);
    }

    /**
     * トークンから優先言語を取得
     * 
     * @param token JWTトークン
     * @return 優先言語
     */
    public String getLanguageFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("lang", String.class);
    }

    /**
     * トークンの種類を取得
     * 
     * @param token JWTトークン
     * @return トークン種類（access/refresh）
     */
    public String getTokenType(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("type", String.class);
    }

    /**
     * トークンの有効期限を取得
     * 
     * @param token JWTトークン
     * @return 有効期限
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }

    /**
     * トークンの有効性を検証
     * 
     * @param token JWTトークン
     * @return 有効な場合true
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * アクセストークンかどうかを確認
     * 
     * @param token JWTトークン
     * @return アクセストークンの場合true
     */
    public boolean isAccessToken(String token) {
        try {
            String tokenType = getTokenType(token);
            return "access".equals(tokenType);
        } catch (Exception ex) {
            log.error("Error checking token type: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * リフレッシュトークンかどうかを確認
     * 
     * @param token JWTトークン
     * @return リフレッシュトークンの場合true
     */
    public boolean isRefreshToken(String token) {
        try {
            String tokenType = getTokenType(token);
            return "refresh".equals(tokenType);
        } catch (Exception ex) {
            log.error("Error checking token type: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * トークンの有効期限が切れているかチェック
     * 
     * @param token JWTトークン
     * @return 期限切れの場合true
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception ex) {
            log.error("Error checking token expiration: {}", ex.getMessage());
            return true;
        }
    }

    /**
     * トークンの残り有効時間を取得（秒）
     * 
     * @param token JWTトークン
     * @return 残り有効時間（秒）
     */
    public long getTokenRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            long remainingTime = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            return Math.max(0, remainingTime);
        } catch (Exception ex) {
            log.error("Error getting token remaining time: {}", ex.getMessage());
            return 0;
        }
    }
}