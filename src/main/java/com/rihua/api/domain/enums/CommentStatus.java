package com.rihua.api.domain.enums;

/**
 * コメントステータス列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum CommentStatus {
    
    /**
     * 公開中
     */
    PUBLISHED,
    
    /**
     * 非表示
     */
    HIDDEN,
    
    /**
     * 承認待ち
     */
    PENDING_APPROVAL,
    
    /**
     * 削除済み
     */
    DELETED
}