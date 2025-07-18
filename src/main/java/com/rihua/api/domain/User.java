package com.rihua.api.domain;

import com.rihua.api.domain.enums.UserRole;
import com.rihua.api.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * ユーザーエンティティ
 * 
 * <p>システムユーザーの基本情報を管理します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private Set<UserRole> roles = Set.of(UserRole.USER);

    @Column(name = "preferred_language", length = 10)
    @Builder.Default
    private String preferredLanguage = "zh-CN";

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
        this.status = UserStatus.DELETED;
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
     * アクティブなユーザーかどうかを確認
     * 
     * @return アクティブな場合true
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE && !isDeleted();
    }

    /**
     * 特定の権限を持っているかチェック
     * 
     * @param role 権限
     * @return 権限を持っている場合true
     */
    public boolean hasRole(UserRole role) {
        return roles.contains(role);
    }

    /**
     * 管理者権限を持っているかチェック
     * 
     * @return 管理者権限を持っている場合true
     */
    public boolean isAdmin() {
        return hasRole(UserRole.ADMIN) || hasRole(UserRole.SUPER_ADMIN);
    }

    /**
     * ビジネスユーザー権限を持っているかチェック
     * 
     * @return ビジネスユーザー権限を持っている場合true
     */
    public boolean isBusiness() {
        return hasRole(UserRole.BUSINESS) || isAdmin();
    }
}