package jp.co.protosoft.rihua.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson設定クラス
 * 
 * <p>JSON シリアライゼーション・デシリアライゼーションの設定を管理します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Configuration
public class JacksonConfig {

    /**
     * ObjectMapperを設定
     * 
     * @return 設定済みObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Java 8 時間APIサポート
        mapper.registerModule(new JavaTimeModule());
        
        // タイムスタンプを無効化（ISO-8601形式を使用）
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // nullフィールドを除外
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        return mapper;
    }
}