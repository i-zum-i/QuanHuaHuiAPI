package com.rihua.api.domain.enums;

/**
 * 投稿ステータス列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum PostStatus {
    /**
     * 下書き
     */
    DRAFT,
    
    /**
     * 公開中
     */
    PUBLISHED,
    
    /**
     * 非表示（モデレーション）
     */
    HIDDEN,
    
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
        return this == PUBLISHED;
    }

    /**
     * 編集可能かどうかを判定
     * 
     * @return 編集可能な場合true
     */
    public boolean canEdit() {
        return this == DRAFT || this == PUBLISHED;
    }

    /**
     * コメント可能かどうかを判定
     * 
     * @return コメント可能な場合true
     */
    public boolean canComment() {
        return this == PUBLISHED;
    }
}