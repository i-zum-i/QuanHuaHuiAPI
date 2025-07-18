package com.rihua.api.domain.enums;

/**
 * 通知タイプ列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum NotificationType {
    /**
     * システム通知
     */
    SYSTEM,
    
    /**
     * イベント関連
     */
    EVENT,
    
    /**
     * チケット関連
     */
    TICKET,
    
    /**
     * 住居関連
     */
    HOUSING,
    
    /**
     * 求人関連
     */
    JOB,
    
    /**
     * フォーラム関連
     */
    FORUM,
    
    /**
     * コメント関連
     */
    COMMENT,
    
    /**
     * いいね関連
     */
    LIKE,
    
    /**
     * フォロー関連
     */
    FOLLOW,
    
    /**
     * メッセージ関連
     */
    MESSAGE;

    /**
     * タイプの表示名を取得
     * 
     * @return 表示名
     */
    public String getDisplayName() {
        return switch (this) {
            case SYSTEM -> "システム";
            case EVENT -> "イベント";
            case TICKET -> "チケット";
            case HOUSING -> "住居";
            case JOB -> "求人";
            case FORUM -> "フォーラム";
            case COMMENT -> "コメント";
            case LIKE -> "いいね";
            case FOLLOW -> "フォロー";
            case MESSAGE -> "メッセージ";
        };
    }

    /**
     * 重要な通知かどうかを判定
     * 
     * @return 重要な通知の場合true
     */
    public boolean isImportant() {
        return this == SYSTEM || this == TICKET;
    }
}