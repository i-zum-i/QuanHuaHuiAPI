package com.rihua.api.domain;

import com.rihua.api.domain.enums.ForumCategory;
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
 * フォーラム投稿エンティティ
 * 
 * <p>フォーラムの投稿情報を管理します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "forum_posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank
    @Size(max = 10000)
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    @Builder.Default
    private ForumCategory category = ForumCategory.GENERAL;

    @Min(0)
    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    @Min(0)
    @Column(name = "comment_count", nullable = false)
    @Builder.Default
    private Integer commentCount = 0;

    @Min(0)
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "is_pinned", nullable = false)
    @Builder.Default
    private Boolean isPinned = false;

    @Column(name = "is_locked", nullable = false)
    @Builder.Default
    private Boolean isLocked = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PostStatus status = PostStatus.PUBLISHED;

    @Size(max = 10)
    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "zh-CN";

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

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
     * コメント可能かどうかを確認
     * 
     * @return コメント可能な場合true
     */
    public boolean canComment() {
        return status.canComment() && !isDeleted() && !isLocked();
    }

    /**
     * 編集可能かどうかを確認
     * 
     * @return 編集可能な場合true
     */
    public boolean canEdit() {
        return status.canEdit() && !isDeleted();
    }

    /**
     * ピン留めされているかどうかを確認
     * 
     * @return ピン留めされている場合true
     */
    public boolean isPinned() {
        return Boolean.TRUE.equals(isPinned);
    }

    /**
     * ロックされているかどうかを確認
     * 
     * @return ロックされている場合true
     */
    public boolean isLocked() {
        return Boolean.TRUE.equals(isLocked);
    }

    /**
     * 人気投稿かどうかを確認（いいね数とコメント数で判定）
     * 
     * @return 人気投稿の場合true
     */
    public boolean isPopular() {
        return likeCount >= 10 || commentCount >= 5;
    }

    /**
     * 最近の投稿かどうかを確認（24時間以内）
     * 
     * @return 最近の投稿の場合true
     */
    public boolean isRecent() {
        return createdAt.isAfter(LocalDateTime.now().minusHours(24));
    }

    /**
     * アクティブな投稿かどうかを確認（最近活動があった）
     * 
     * @return アクティブな投稿の場合true
     */
    public boolean isActive() {
        if (lastActivityAt == null) {
            return isRecent();
        }
        return lastActivityAt.isAfter(LocalDateTime.now().minusHours(24));
    }

    /**
     * いいね数を増加
     */
    public void incrementLikeCount() {
        this.likeCount++;
        updateLastActivity();
    }

    /**
     * いいね数を減少
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
        updateLastActivity();
    }

    /**
     * コメント数を増加
     */
    public void incrementCommentCount() {
        this.commentCount++;
        updateLastActivity();
    }

    /**
     * コメント数を減少
     */
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
        updateLastActivity();
    }

    /**
     * 閲覧数を増加
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * ピン留め設定
     * 
     * @param pinned ピン留めするかどうか
     */
    public void setPinned(boolean pinned) {
        this.isPinned = pinned;
    }

    /**
     * ロック設定
     * 
     * @param locked ロックするかどうか
     */
    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }

    /**
     * 投稿を非表示にする
     */
    public void hide() {
        this.status = PostStatus.HIDDEN;
    }

    /**
     * 投稿を再表示する
     */
    public void unhide() {
        this.status = PostStatus.PUBLISHED;
    }

    /**
     * 最終活動時刻を更新
     */
    private void updateLastActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }

    /**
     * エンゲージメント率を計算（いいね数 + コメント数 / 閲覧数）
     * 
     * @return エンゲージメント率
     */
    public double getEngagementRate() {
        if (viewCount == 0) {
            return 0.0;
        }
        return (double) (likeCount + commentCount) / viewCount;
    }

    /**
     * 投稿の人気度スコアを計算
     * 
     * @return 人気度スコア
     */
    public double getPopularityScore() {
        // いいね数 * 2 + コメント数 * 3 + 閲覧数 * 0.1
        return (likeCount * 2.0) + (commentCount * 3.0) + (viewCount * 0.1);
    }
}