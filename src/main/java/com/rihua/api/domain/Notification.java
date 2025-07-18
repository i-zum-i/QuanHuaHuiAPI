package com.rihua.api.domain;

import com.rihua.api.domain.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 通知エンティティ
 * 
 * <p>ユーザー通知情報を管理します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank
    @Size(max = 1000)
    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Size(max = 100)
    @Column(name = "related_entity_type", length = 100)
    private String relatedEntityType;

    @Size(max = 36)
    @Column(name = "related_entity_id", length = 36)
    private String relatedEntityId;

    @Size(max = 500)
    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "is_sent", nullable = false)
    @Builder.Default
    private Boolean isSent = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 既読かどうかを確認
     * 
     * @return 既読の場合true
     */
    public boolean isRead() {
        return Boolean.TRUE.equals(isRead);
    }

    /**
     * 送信済みかどうかを確認
     * 
     * @return 送信済みの場合true
     */
    public boolean isSent() {
        return Boolean.TRUE.equals(isSent);
    }

    /**
     * 期限切れかどうかを確認
     * 
     * @return 期限切れの場合true
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * 重要な通知かどうかを確認
     * 
     * @return 重要な通知の場合true
     */
    public boolean isImportant() {
        return type.isImportant();
    }

    /**
     * 新しい通知かどうかを確認（24時間以内）
     * 
     * @return 新しい通知の場合true
     */
    public boolean isNew() {
        return createdAt.isAfter(LocalDateTime.now().minusHours(24));
    }

    /**
     * 既読処理
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * 未読処理
     */
    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;
    }

    /**
     * 送信完了処理
     */
    public void markAsSent() {
        this.isSent = true;
        this.sentAt = LocalDateTime.now();
    }

    /**
     * 関連エンティティ情報を設定
     * 
     * @param entityType エンティティタイプ
     * @param entityId エンティティID
     */
    public void setRelatedEntity(String entityType, UUID entityId) {
        this.relatedEntityType = entityType;
        this.relatedEntityId = entityId.toString();
    }

    /**
     * 関連エンティティIDをUUIDで取得
     * 
     * @return 関連エンティティID
     */
    public UUID getRelatedEntityIdAsUUID() {
        if (relatedEntityId == null) {
            return null;
        }
        try {
            return UUID.fromString(relatedEntityId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}