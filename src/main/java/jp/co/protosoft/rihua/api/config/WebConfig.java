package jp.co.protosoft.rihua.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web設定クラス
 * 
 * <p>CORS設定やその他のWeb関連設定を管理します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${rihua.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${rihua.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${rihua.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${rihua.cors.allow-credentials}")
    private boolean allowCredentials;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOrigins.split(","))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders(allowedHeaders.split(","))
                .allowCredentials(allowCredentials)
                .maxAge(3600); // プリフライトリクエストのキャッシュ時間（秒）
    }
}