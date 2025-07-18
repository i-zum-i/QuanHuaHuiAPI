package com.rihua.api.domain.enums;

/**
 * 求人ステータス列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum JobStatus {
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
    ACTIVE,
    
    /**
     * 一時停止
     */
    PAUSED,
    
    /**
     * 募集終了
     */
    FILLED,
    
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
        return this == ACTIVE;
    }

    /**
     * 応募可能かどうかを判定
     * 
     * @return 応募可能な場合true
     */
    public boolean canApply() {
        return this == ACTIVE;
    }

    /**
     * 編集可能かどうかを判定
     * 
     * @return 編集可能な場合true
     */
    public boolean canEdit() {
        return this == DRAFT || this == PENDING_APPROVAL || this == ACTIVE || this == PAUSED;
    }
}