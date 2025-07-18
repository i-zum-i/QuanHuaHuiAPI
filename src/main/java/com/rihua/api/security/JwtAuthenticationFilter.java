package com.rihua.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT認証フィルター
 * 
 * <p>HTTPリクエストからJWTトークンを抽出し、認証情報をSecurityContextに設定します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);
            String requestURI = request.getRequestURI();
            log.debug("Processing JWT authentication for URI: {}", requestURI);
            
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                // アクセストークンのみ認証に使用
                if (jwtTokenProvider.isAccessToken(jwt)) {
                    String userId = jwtTokenProvider.getUserIdFromToken(jwt);
                    String email = jwtTokenProvider.getEmailFromToken(jwt);
                    List<String> roles = jwtTokenProvider.getRolesFromToken(jwt);
                    String language = jwtTokenProvider.getLanguageFromToken(jwt);

                    // 権限リストを作成
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    // UserPrincipalを作成
                    UserPrincipal userPrincipal = UserPrincipal.builder()
                            .id(userId)
                            .email(email)
                            .authorities(authorities)
                            .preferredLanguage(language)
                            .build();

                    // 認証トークンを作成
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userPrincipal, 
                                    null, 
                                    authorities);
                    
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // SecurityContextに認証情報を設定
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("JWT authentication successful for user: {}", email);
                } else {
                    log.warn("Invalid token type for authentication: expected access token");
                }
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
            // 認証エラーが発生してもフィルターチェーンは継続
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * HTTPリクエストからJWTトークンを抽出
     * 
     * @param request HTTPリクエスト
     * @return JWTトークン（存在しない場合はnull）
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }

    /**
     * 特定のパスに対してフィルターをスキップするかどうかを判定
     * 
     * @param request HTTPリクエスト
     * @return スキップする場合true
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // 認証が不要なパスはフィルターをスキップ
        return path.startsWith("/api/v1/auth/") ||
               path.startsWith("/actuator/health") ||
               path.startsWith("/actuator/info") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/swagger-ui") ||
               path.equals("/favicon.ico") ||
               path.equals("/error");
    }
}