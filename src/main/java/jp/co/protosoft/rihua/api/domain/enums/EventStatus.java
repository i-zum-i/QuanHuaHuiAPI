package jp.co.protosoft.rihua.api.domain.enums;

/**
 * イベントステータス列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum EventStatus {
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
    PUBLISHED,
    
    /**
     * 満席
     */
    SOLD_OUT,
    
    /**
     * キャンセル
     */
    CANCELLED,
    
    /**
     * 終了
     */
    COMPLETED,
    
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
        return this == PUBLISHED || this == SOLD_OUT;
    }

    /**
     * チケット購入可能かどうかを判定
     * 
     * @return 購入可能な場合true
     */
    public boolean canPurchaseTickets() {
        return this == PUBLISHED;
    }

    /**
     * 編集可能かどうかを判定
     * 
     * @return 編集可能な場合true
     */
    public boolean canEdit() {
        return this == DRAFT || this == PENDING_APPROVAL;
    }
}