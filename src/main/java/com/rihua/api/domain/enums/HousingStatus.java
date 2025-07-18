package com.rihua.api.domain.enums;

/**
 * 住居ステータス列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum HousingStatus {
    /**
     * 下書き
     */
    DRAFT,
    
    /**
     * 承認待ち
     */
    PENDING_APPROVAL,
    
    /**
     * 公開中
     */
    AVAILABLE,
    
    /**
     * 交渉中
     */
    UNDER_NEGOTIATION,
    
    /**
     * 成約済み
     */
    RENTED_SOLD,
    
    /**
     * 期限切れ
     */
    EXPIRED,
    
    /**
     * 削除済み
     */
    DELETED;

    /**
     * 公開されているかどうかを判定
     * 
     * @return 公開されている場合true
     */
    public boolean isPublic() {
        return this == AVAILABLE || this == UNDER_NEGOTIATION;
    }

    /**
     * 問い合わせ可能かどうかを判定
     * 
     * @return 問い合わせ可能な場合true
     */
    public boolean canInquire() {
        return this == AVAILABLE;
    }

    /**
     * 編集可能かどうかを判定
     * 
     * @return 編集可能な場合true
     */
    public boolean canEdit() {
        return this == DRAFT || this == PENDING_APPROVAL || this == AVAILABLE;
    }
}