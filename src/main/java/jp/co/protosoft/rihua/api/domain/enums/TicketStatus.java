package jp.co.protosoft.rihua.api.domain.enums;

/**
 * チケットステータス列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum TicketStatus {
    /**
     * 決済待ち
     */
    PENDING_PAYMENT,
    
    /**
     * 決済完了・有効
     */
    CONFIRMED,
    
    /**
     * 使用済み
     */
    USED,
    
    /**
     * キャンセル済み
     */
    CANCELLED,
    
    /**
     * 返金済み
     */
    REFUNDED,
    
    /**
     * 期限切れ
     */
    EXPIRED;

    /**
     * 有効なチケットかどうかを判定
     * 
     * @return 有効な場合true
     */
    public boolean isValid() {
        return this == CONFIRMED;
    }

    /**
     * 使用可能なチケットかどうかを判定
     * 
     * @return 使用可能な場合true
     */
    public boolean canUse() {
        return this == CONFIRMED;
    }

    /**
     * キャンセル可能なチケットかどうかを判定
     * 
     * @return キャンセル可能な場合true
     */
    public boolean canCancel() {
        return this == CONFIRMED || this == PENDING_PAYMENT;
    }
}