package jp.co.protosoft.rihua.api.domain;

import jp.co.protosoft.rihua.api.domain.enums.UserRole;
import jp.co.protosoft.rihua.api.domain.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Userエンティティのテスト
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
class UserTest {

    @Test
    @DisplayName("ユーザーの基本情報が正しく設定される")
    void createUser_ValidData_SetsPropertiesCorrectly() {
        // Given
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String firstName = "太郎";
        String lastName = "田中";
        
        // When
        User user = User.builder()
            .id(userId)
            .email(email)
            .firstName(firstName)
            .lastName(lastName)
            .status(UserStatus.ACTIVE)
            .roles(Set.of(UserRole.USER))
            .preferredLanguage("ja")
            .build();
        
        // Then
        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getFirstName()).isEqualTo(firstName);
        assertThat(user.getLastName()).isEqualTo(lastName);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getRoles()).containsExactly(UserRole.USER);
        assertThat(user.getPreferredLanguage()).isEqualTo("ja");
    }

    @Test
    @DisplayName("デフォルト値が正しく設定される")
    void createUser_DefaultValues_SetsCorrectDefaults() {
        // When
        User user = User.builder()
            .email("test@example.com")
            .build();
        
        // Then
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);
        assertThat(user.getRoles()).containsExactly(UserRole.USER);
        assertThat(user.getPreferredLanguage()).isEqualTo("zh-CN");
    }

    @Test
    @DisplayName("アクティブなユーザーかどうかを正しく判定する")
    void isActive_ActiveUser_ReturnsTrue() {
        // Given
        User user = User.builder()
            .email("test@example.com")
            .status(UserStatus.ACTIVE)
            .build();
        
        // When & Then
        assertThat(user.isActive()).isTrue();
    }

    @Test
    @DisplayName("削除済みユーザーはアクティブではない")
    void isActive_DeletedUser_ReturnsFalse() {
        // Given
        User user = User.builder()
            .email("test@example.com")
            .status(UserStatus.ACTIVE)
            .deletedAt(LocalDateTime.now())
            .build();
        
        // When & Then
        assertThat(user.isActive()).isFalse();
    }

    @Test
    @DisplayName("停止中のユーザーはアクティブではない")
    void isActive_SuspendedUser_ReturnsFalse() {
        // Given
        User user = User.builder()
            .email("test@example.com")
            .status(UserStatus.SUSPENDED)
            .build();
        
        // When & Then
        assertThat(user.isActive()).isFalse();
    }

    @Test
    @DisplayName("特定の権限を持っているかどうかを正しく判定する")
    void hasRole_UserWithRole_ReturnsTrue() {
        // Given
        User user = User.builder()
            .email("test@example.com")
            .roles(Set.of(UserRole.USER, UserRole.BUSINESS))
            .build();
        
        // When & Then
        assertThat(user.hasRole(UserRole.USER)).isTrue();
        assertThat(user.hasRole(UserRole.BUSINESS)).isTrue();
        assertThat(user.hasRole(UserRole.ADMIN)).isFalse();
    }

    @Test
    @DisplayName("管理者権限を持っているかどうかを正しく判定する")
    void isAdmin_AdminUser_ReturnsTrue() {
        // Given
        User adminUser = User.builder()
            .email("admin@example.com")
            .roles(Set.of(UserRole.ADMIN))
            .build();
        
        User superAdminUser = User.builder()
            .email("superadmin@example.com")
            .roles(Set.of(UserRole.SUPER_ADMIN))
            .build();
        
        User regularUser = User.builder()
            .email("user@example.com")
            .roles(Set.of(UserRole.USER))
            .build();
        
        // When & Then
        assertThat(adminUser.isAdmin()).isTrue();
        assertThat(superAdminUser.isAdmin()).isTrue();
        assertThat(regularUser.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("ビジネスユーザー権限を持っているかどうかを正しく判定する")
    void isBusiness_BusinessUser_ReturnsTrue() {
        // Given
        User businessUser = User.builder()
            .email("business@example.com")
            .roles(Set.of(UserRole.BUSINESS))
            .build();
        
        User adminUser = User.builder()
            .email("admin@example.com")
            .roles(Set.of(UserRole.ADMIN))
            .build();
        
        User regularUser = User.builder()
            .email("user@example.com")
            .roles(Set.of(UserRole.USER))
            .build();
        
        // When & Then
        assertThat(businessUser.isBusiness()).isTrue();
        assertThat(adminUser.isBusiness()).isTrue(); // 管理者はビジネス権限も持つ
        assertThat(regularUser.isBusiness()).isFalse();
    }

    @Test
    @DisplayName("ソフトデリートが正しく実行される")
    void softDelete_ValidUser_SetsDeletedAtAndStatus() {
        // Given
        User user = User.builder()
            .email("test@example.com")
            .status(UserStatus.ACTIVE)
            .build();
        
        // When
        user.softDelete();
        
        // Then
        assertThat(user.getDeletedAt()).isNotNull();
        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(user.isDeleted()).isTrue();
        assertThat(user.isActive()).isFalse();
    }

    @Test
    @DisplayName("削除済みかどうかを正しく判定する")
    void isDeleted_DeletedUser_ReturnsTrue() {
        // Given
        User user = User.builder()
            .email("test@example.com")
            .deletedAt(LocalDateTime.now())
            .build();
        
        // When & Then
        assertThat(user.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("削除されていないユーザーは削除済みではない")
    void isDeleted_ActiveUser_ReturnsFalse() {
        // Given
        User user = User.builder()
            .email("test@example.com")
            .status(UserStatus.ACTIVE)
            .build();
        
        // When & Then
        assertThat(user.isDeleted()).isFalse();
    }
}