package com.rihua.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * エラーレスポンスDTO
 * 
 * <p>APIエラー時の統一されたレスポンス形式を定義します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * エラー発生時刻
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    /**
     * HTTPステータスコード
     */
    private int status;

    /**
     * エラーコード
     */
    private String error;

    /**
     * エラーメッセージ
     */
    private String message;

    /**
     * リクエストパス
     */
    private String path;

    /**
     * 詳細エラー情報（バリデーションエラーなど）
     */
    private Map<String, Object> details;

    /**
     * トレースID（分散トレーシング用）
     */
    private String traceId;

    /**
     * 内部エラーID（デバッグ用）
     */
    private String errorId;
}