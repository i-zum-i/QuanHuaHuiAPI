package com.rihua.api.domain;

import com.rihua.api.domain.enums.ReportReason;
import com.rihua.api.domain.enums.ReportStatus;
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
 * 報告エンティティ
 * 
 * <p>不適切なコンテンツの報告情報を管理します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private ForumPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private ReportReason reason;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Size(max = 1000)
    @Column(name = "review_notes", length = 1000)
    private String reviewNotes;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 投稿の報告かどうかを確認
     * 
     * @return 投稿の報告の場合true
     */
    public boolean isPostReport() {
        return post != null;
    }

    /**
     * コメントの報告かどうかを確認
     * 
     * @return コメントの報告の場合true
     */
    public boolean isCommentReport() {
        return comment != null;
    }

    /**
     * ユーザーの報告かどうかを確認
     * 
     * @return ユーザーの報告の場合true
     */
    public boolean isUserReport() {
        return reportedUser != null;
    }

    /**
     * 緊急度の高い報告かどうかを確認
     * 
     * @return 緊急度が高い場合true
     */
    public boolean isUrgent() {
        return reason.isUrgent();
    }

    /**
     * 処理が必要かどうかを確認
     * 
     * @return 処理が必要な場合true
     */
    public boolean needsAction() {
        return status.needsAction();
    }

    /**
     * 完了しているかどうかを確認
     * 
     * @return 完了している場合true
     */
    public boolean isCompleted() {
        return status.isCompleted();
    }

    /**
     * レビュー済みかどうかを確認
     * 
     * @return レビュー済みの場合true
     */
    public boolean isReviewed() {
        return reviewedAt != null && reviewedBy != null;
    }

    /**
     * 最近の報告かどうかを確認（24時間以内）
     * 
     * @return 最近の報告の場合true
     */
    public boolean isRecent() {
        return createdAt.isAfter(LocalDateTime.now().minusHours(24));
    }

    /**
     * 報告を承認する
     * 
     * @param reviewer レビュアー
     * @param notes レビューノート
     */
    public void approve(User reviewer, String notes) {
        this.status = ReportStatus.APPROVED;
        this.reviewedBy = reviewer;
        this.reviewNotes = notes;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * 報告を却下する
     * 
     * @param reviewer レビュアー
     * @param notes レビューノート
     */
    public void reject(User reviewer, String notes) {
        this.status = ReportStatus.REJECTED;
        this.reviewedBy = reviewer;
        this.reviewNotes = notes;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * 報告を解決済みにする
     * 
     * @param reviewer レビュアー
     * @param notes レビューノート
     */
    public void resolve(User reviewer, String notes) {
        this.status = ReportStatus.RESOLVED;
        this.reviewedBy = reviewer;
        this.reviewNotes = notes;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * レビューを開始する
     * 
     * @param reviewer レビュアー
     */
    public void startReview(User reviewer) {
        this.status = ReportStatus.IN_PROGRESS;
        this.reviewedBy = reviewer;
    }

    /**
     * 報告対象の表示名を取得
     * 
     * @return 報告対象の表示名
     */
    public String getReportedContentDisplayName() {
        if (isPostReport()) {
            return "投稿: " + post.getTitle();
        } else if (isCommentReport()) {
            return "コメント";
        } else if (isUserReport()) {
            return "ユーザー: " + reportedUser.getFirstName() + " " + reportedUser.getLastName();
        }
        return "不明";
    }
}