package jp.co.protosoft.rihua.api.domain;

import jp.co.protosoft.rihua.api.domain.enums.AuditAction;
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
 * 監査ログエンティティ
 * 
 * <p>システムの重要な操作を記録します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AuditAction action;

    @NotBlank
    @Size(max = 100)
    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Size(max = 36)
    @Column(name = "entity_id", length = 36)
    private String entityId;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues;

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues;

    @Size(max = 45)
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Size(max = 500)
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Size(max = 100)
    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "is_successful", nullable = false)
    @Builder.Default
    private Boolean isSuccessful = true;

    @Size(max = 1000)
    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 成功した操作かどうかを確認
     * 
     * @return 成功した場合true
     */
    public boolean isSuccessful() {
        return Boolean.TRUE.equals(isSuccessful);
    }

    /**
     * 重要な操作かどうかを確認
     * 
     * @return 重要な操作の場合true
     */
    public boolean isImportant() {
        return action.isImportant();
    }

    /**
     * 最近の操作かどうかを確認（24時間以内）
     * 
     * @return 最近の操作の場合true
     */
    public boolean isRecent() {
        return createdAt.isAfter(LocalDateTime.now().minusHours(24));
    }

    /**
     * エンティティIDをUUIDで取得
     * 
     * @return エンティティID
     */
    public UUID getEntityIdAsUUID() {
        if (entityId == null) {
            return null;
        }
        try {
            return UUID.fromString(entityId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * エンティティ情報を設定
     * 
     * @param entityType エンティティタイプ
     * @param entityId エンティティID
     */
    public void setEntity(String entityType, UUID entityId) {
        this.entityType = entityType;
        this.entityId = entityId != null ? entityId.toString() : null;
    }

    /**
     * 失敗した操作として記録
     * 
     * @param errorMessage エラーメッセージ
     */
    public void markAsFailed(String errorMessage) {
        this.isSuccessful = false;
        this.errorMessage = errorMessage;
    }
}