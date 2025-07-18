package jp.co.protosoft.rihua.api.domain.enums;

/**
 * ユーザー権限列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum UserRole {
    /**
     * 一般ユーザー
     */
    USER,
    
    /**
     * ビジネスユーザー（イベント・求人投稿可能）
     */
    BUSINESS,
    
    /**
     * モデレーター（コンテンツ管理）
     */
    MODERATOR,
    
    /**
     * 管理者（全権限）
     */
    ADMIN,
    
    /**
     * 最高管理者（システム管理）
     */
    SUPER_ADMIN;

    /**
     * 管理者権限を持っているかチェック
     * 
     * @return 管理者権限を持っている場合true
     */
    public boolean isAdmin() {
        return this == ADMIN || this == SUPER_ADMIN;
    }

    /**
     * モデレーション権限を持っているかチェック
     * 
     * @return モデレーション権限を持っている場合true
     */
    public boolean canModerate() {
        return this == MODERATOR || isAdmin();
    }

    /**
     * ビジネス機能を使用できるかチェック
     * 
     * @return ビジネス機能を使用できる場合true
     */
    public boolean canUseBusiness() {
        return this == BUSINESS || canModerate();
    }
}