package com.rihua.api.domain.enums;

/**
 * 監査アクション列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum AuditAction {
    /**
     * 作成
     */
    CREATE,
    
    /**
     * 更新
     */
    UPDATE,
    
    /**
     * 削除
     */
    DELETE,
    
    /**
     * ログイン
     */
    LOGIN,
    
    /**
     * ログアウト
     */
    LOGOUT,
    
    /**
     * パスワード変更
     */
    PASSWORD_CHANGE,
    
    /**
     * ステータス変更
     */
    STATUS_CHANGE,
    
    /**
     * 承認
     */
    APPROVE,
    
    /**
     * 拒否
     */
    REJECT,
    
    /**
     * 公開
     */
    PUBLISH,
    
    /**
     * 非公開
     */
    UNPUBLISH,
    
    /**
     * アクセス
     */
    ACCESS,
    
    /**
     * エクスポート
     */
    EXPORT,
    
    /**
     * インポート
     */
    IMPORT;

    /**
     * アクションの表示名を取得
     * 
     * @return 表示名
     */
    public String getDisplayName() {
        return switch (this) {
            case CREATE -> "作成";
            case UPDATE -> "更新";
            case DELETE -> "削除";
            case LOGIN -> "ログイン";
            case LOGOUT -> "ログアウト";
            case PASSWORD_CHANGE -> "パスワード変更";
            case STATUS_CHANGE -> "ステータス変更";
            case APPROVE -> "承認";
            case REJECT -> "拒否";
            case PUBLISH -> "公開";
            case UNPUBLISH -> "非公開";
            case ACCESS -> "アクセス";
            case EXPORT -> "エクスポート";
            case IMPORT -> "インポート";
        };
    }

    /**
     * 重要なアクションかどうかを判定
     * 
     * @return 重要なアクションの場合true
     */
    public boolean isImportant() {
        return this == DELETE || this == PASSWORD_CHANGE || this == STATUS_CHANGE || 
               this == APPROVE || this == REJECT;
    }
}