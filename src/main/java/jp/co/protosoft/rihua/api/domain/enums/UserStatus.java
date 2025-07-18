package jp.co.protosoft.rihua.api.domain.enums;

/**
 * ユーザーステータス列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum UserStatus {
    /**
     * メール認証待ち
     */
    PENDING_VERIFICATION,
    
    /**
     * アクティブ
     */
    ACTIVE,
    
    /**
     * 一時停止
     */
    SUSPENDED,
    
    /**
     * 禁止
     */
    BANNED,
    
    /**
     * 削除済み
     */
    DELETED,
    
    /**
     * 非アクティブ
     */
    INACTIVE;

    /**
     * ユーザーが有効かどうかを判定
     * 
     * @return 有効な場合true
     */
    public boolean isEnabled() {
        return this == ACTIVE || this == PENDING_VERIFICATION;
    }

    /**
     * ユーザーがロックされているかどうかを判定
     * 
     * @return ロックされている場合true
     */
    public boolean isLocked() {
        return this == SUSPENDED || this == BANNED || this == DELETED || this == INACTIVE;
    }

    /**
     * ログイン可能かどうかを判定
     * 
     * @return ログイン可能な場合true
     */
    public boolean canLogin() {
        return this == ACTIVE;
    }
}