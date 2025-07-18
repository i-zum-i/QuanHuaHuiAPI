package com.rihua.api.domain.enums;

/**
 * 通知ステータス列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum NotificationStatus {
    
    /**
     * 未読
     */
    UNREAD,
    
    /**
     * 既読
     */
    READ,
    
    /**
     * 送信待機中
     */
    PENDING,
    
    /**
     * 送信完了
     */
    SENT,
    
    /**
     * 送信失敗
     */
    FAILED
}