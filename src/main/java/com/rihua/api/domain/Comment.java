package com.rihua.api.domain;

import com.rihua.api.domain.enums.PostStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * コメントエンティティ
 * 
 * <p>フォーラム投稿のコメント情報を管理します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private ForumPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @NotBlank
    @Size(max = 2000)
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Min(0)
    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PostStatus status = PostStatus.PUBLISHED;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * ソフトデリート実行
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.status = PostStatus.DELETED;
    }

    /**
     * 削除済みかどうかを確認
     * 
     * @return 削除済みの場合true
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * 公開されているかどうかを確認
     * 
     * @return 公開されている場合true
     */
    public boolean isPublic() {
        return status.isPublic() && !isDeleted();
    }

    /**
     * 返信コメントかどうかを確認
     * 
     * @return 返信コメントの場合true
     */
    public boolean isReply() {
        return parent != null;
    }

    /**
     * トップレベルコメントかどうかを確認
     * 
     * @return トップレベルコメントの場合true
     */
    public boolean isTopLevel() {
        return parent == null;
    }

    /**
     * いいね数を増加
     */
    public void incrementLikeCount() {
        this.likeCount++;
    }

    /**
     * いいね数を減少
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /**
     * コメントを非表示にする
     */
    public void hide() {
        this.status = PostStatus.HIDDEN;
    }

    /**
     * コメントを再表示する
     */
    public void unhide() {
        this.status = PostStatus.PUBLISHED;
    }
}