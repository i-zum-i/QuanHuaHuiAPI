package com.rihua.api.domain.enums;

/**
 * 報告理由列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum ReportReason {
    /**
     * スパム
     */
    SPAM,
    
    /**
     * 不適切なコンテンツ
     */
    INAPPROPRIATE_CONTENT,
    
    /**
     * ハラスメント
     */
    HARASSMENT,
    
    /**
     * 偽情報
     */
    MISINFORMATION,
    
    /**
     * 著作権侵害
     */
    COPYRIGHT_VIOLATION,
    
    /**
     * 詐欺
     */
    FRAUD,
    
    /**
     * ヘイトスピーチ
     */
    HATE_SPEECH,
    
    /**
     * 暴力的なコンテンツ
     */
    VIOLENT_CONTENT,
    
    /**
     * その他
     */
    OTHER;

    /**
     * 理由の表示名を取得
     * 
     * @return 表示名
     */
    public String getDisplayName() {
        return switch (this) {
            case SPAM -> "スパム";
            case INAPPROPRIATE_CONTENT -> "不適切なコンテンツ";
            case HARASSMENT -> "ハラスメント";
            case MISINFORMATION -> "偽情報";
            case COPYRIGHT_VIOLATION -> "著作権侵害";
            case FRAUD -> "詐欺";
            case HATE_SPEECH -> "ヘイトスピーチ";
            case VIOLENT_CONTENT -> "暴力的なコンテンツ";
            case OTHER -> "その他";
        };
    }

    /**
     * 緊急度の高い報告かどうかを判定
     * 
     * @return 緊急度が高い場合true
     */
    public boolean isUrgent() {
        return this == HARASSMENT || this == HATE_SPEECH || this == VIOLENT_CONTENT;
    }
}