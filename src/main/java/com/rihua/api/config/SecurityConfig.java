package com.rihua.api.config;

import com.rihua.api.security.JwtAuthenticationEntryPoint;
import com.rihua.api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security設定クラス
 * 
 * <p>JWT認証、CORS、セキュリティヘッダー、認可設定を管理します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * パスワードエンコーダーを設定
     * bcryptを使用してパスワードをハッシュ化
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * 認証マネージャーを設定
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * DAO認証プロバイダーを設定
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * CORS設定
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 許可するオリジン
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001", 
            "http://localhost:19006", // Expo開発サーバー
            "https://*.rihua.com",
            "https://rihua.com"
        ));
        
        // 許可するHTTPメソッド
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // 許可するヘッダー
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Accept-Language",
            "X-Requested-With",
            "X-Request-ID"
        ));
        
        // 認証情報の送信を許可
        configuration.setAllowCredentials(true);
        
        // プリフライトリクエストのキャッシュ時間
        configuration.setMaxAge(3600L);
        
        // 公開するレスポンスヘッダー
        configuration.setExposedHeaders(Arrays.asList(
            "X-Total-Count",
            "X-Page-Number",
            "X-Page-Size",
            "X-Total-Pages"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * セキュリティフィルターチェーンを設定
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF無効化（JWT使用のため）
            .csrf(AbstractHttpConfigurer::disable)
            
            // CORS設定
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // セッション管理をステートレスに設定
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 認証エントリーポイント設定
            .exceptionHandling(exceptions -> 
                exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            
            // 認可設定
            .authorizeHttpRequests(authz -> authz
                // パブリックエンドポイント（認証不要）
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/verify-email").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/reset-password").permitAll()
                
                // ヘルスチェック・監視エンドポイント
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // API文書化
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // 静的リソース
                .requestMatchers("/favicon.ico", "/error").permitAll()
                
                // パブリック読み取り専用エンドポイント
                .requestMatchers(HttpMethod.GET, "/api/v1/events").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/events/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/housing").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/housing/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/jobs").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/jobs/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/forum/posts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/forum/posts/{id}").permitAll()
                
                // 管理者専用エンドポイント
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                
                // ビジネスユーザー以上が必要なエンドポイント
                .requestMatchers(HttpMethod.POST, "/api/v1/events").hasAnyRole("BUSINESS", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/events/**").hasAnyRole("BUSINESS", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/events/**").hasAnyRole("BUSINESS", "ADMIN")
                
                .requestMatchers(HttpMethod.POST, "/api/v1/housing").hasAnyRole("BUSINESS", "USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/jobs").hasAnyRole("BUSINESS", "ADMIN")
                
                // その他すべてのエンドポイントは認証が必要
                .anyRequest().authenticated()
            )
            
            // セキュリティヘッダー設定
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .contentTypeOptions(contentTypeOptions -> {})
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true))
            );

        // JWT認証フィルターを追加
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        // 認証プロバイダー設定
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }
}