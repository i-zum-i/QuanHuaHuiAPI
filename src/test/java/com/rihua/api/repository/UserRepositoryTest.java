package com.rihua.api.repository;

import com.rihua.api.domain.User;
import com.rihua.api.domain.enums.UserRole;
import com.rihua.api.domain.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepositoryの統合テスト
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("メールアドレスでユーザーを検索できる")
    void findByEmailAndDeletedAtIsNull_ExistingEmail_ReturnsUser() {
        // Given
        User user = User.builder()
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .firstName("太郎")
                .lastName("田中")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .build();
        persistAndFlush(user);

        // When
        Optional<User> result = userRepository.findByEmailAndDeletedAtIsNull("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("存在しないメールアドレスで検索すると空のOptionalが返される")
    void findByEmailAndDeletedAtIsNull_NonExistentEmail_ReturnsEmpty() {
        // When
        Optional<User> result = userRepository.findByEmailAndDeletedAtIsNull("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("削除済みユーザーは検索結果に含まれない")
    void findByEmailAndDeletedAtIsNull_DeletedUser_ReturnsEmpty() {
        // Given
        User user = User.builder()
                .email("deleted@example.com")
                .passwordHash("hashedPassword")
                .firstName("削除")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.INACTIVE)
                .deletedAt(LocalDateTime.now())
                .build();
        persistAndFlush(user);

        // When
        Optional<User> result = userRepository.findByEmailAndDeletedAtIsNull("deleted@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("メールアドレスの存在確認ができる")
    void existsByEmailAndDeletedAtIsNull_ExistingEmail_ReturnsTrue() {
        // Given
        User user = User.builder()
                .email("exists@example.com")
                .passwordHash("hashedPassword")
                .firstName("存在")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .build();
        persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByEmailAndDeletedAtIsNull("exists@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("ステータスでユーザーを検索できる")
    void findByStatusAndDeletedAtIsNull_ActiveStatus_ReturnsActiveUsers() {
        // Given
        User activeUser1 = User.builder()
                .email("active1@example.com")
                .passwordHash("hashedPassword")
                .firstName("アクティブ1")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .build();

        User activeUser2 = User.builder()
                .email("active2@example.com")
                .passwordHash("hashedPassword")
                .firstName("アクティブ2")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .build();

        User inactiveUser = User.builder()
                .email("inactive@example.com")
                .passwordHash("hashedPassword")
                .firstName("非アクティブ")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.INACTIVE)
                .build();

        persistAndFlush(activeUser1);
        persistAndFlush(activeUser2);
        persistAndFlush(inactiveUser);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByStatusAndDeletedAtIsNull(UserStatus.ACTIVE, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(User::getStatus)
                .containsOnly(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("アクティブなユーザーを検索できる")
    void findActiveUsers_ReturnsOnlyActiveUsers() {
        // Given
        User activeUser = User.builder()
                .email("active@example.com")
                .passwordHash("hashedPassword")
                .firstName("アクティブ")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .build();

        User pendingUser = User.builder()
                .email("pending@example.com")
                .passwordHash("hashedPassword")
                .firstName("保留")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.PENDING_VERIFICATION)
                .build();

        persistAndFlush(activeUser);
        persistAndFlush(pendingUser);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findActiveUsers(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("キーワードでユーザーを検索できる")
    void searchByKeyword_MatchingKeyword_ReturnsMatchingUsers() {
        // Given
        User user1 = User.builder()
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .build();

        User user2 = User.builder()
                .email("jane.smith@example.com")
                .passwordHash("hashedPassword")
                .firstName("Jane")
                .lastName("Smith")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .build();

        persistAndFlush(user1);
        persistAndFlush(user2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.searchByKeyword("john", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("指定期間内に作成されたユーザーを検索できる")
    void findByCreatedAtBetweenAndDeletedAtIsNull_DateRange_ReturnsUsersInRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        LocalDateTime middleDate = LocalDateTime.now().minusDays(3);

        User oldUser = User.builder()
                .email("old@example.com")
                .passwordHash("hashedPassword")
                .firstName("古い")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now().minusDays(10))
                .build();

        User middleUser = User.builder()
                .email("middle@example.com")
                .passwordHash("hashedPassword")
                .firstName("中間")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .createdAt(middleDate)
                .build();

        User newUser = User.builder()
                .email("new@example.com")
                .passwordHash("hashedPassword")
                .firstName("新しい")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        persistAndFlush(oldUser);
        persistAndFlush(middleUser);
        persistAndFlush(newUser);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByCreatedAtBetweenAndDeletedAtIsNull(startDate, endDate, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("middle@example.com");
    }

    @Test
    @DisplayName("認証待ちユーザー数を取得できる")
    void countPendingVerificationUsers_ReturnsPendingCount() {
        // Given
        User activeUser = User.builder()
                .email("active@example.com")
                .passwordHash("hashedPassword")
                .firstName("アクティブ")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .build();

        User pendingUser1 = User.builder()
                .email("pending1@example.com")
                .passwordHash("hashedPassword")
                .firstName("保留1")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.PENDING_VERIFICATION)
                .build();

        User pendingUser2 = User.builder()
                .email("pending2@example.com")
                .passwordHash("hashedPassword")
                .firstName("保留2")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.PENDING_VERIFICATION)
                .build();

        persistAndFlush(activeUser);
        persistAndFlush(pendingUser1);
        persistAndFlush(pendingUser2);

        // When
        long count = userRepository.countPendingVerificationUsers();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("アクティブユーザー数を取得できる")
    void countActiveUsers_ReturnsActiveCount() {
        // Given
        User activeUser1 = User.builder()
                .email("active1@example.com")
                .passwordHash("hashedPassword")
                .firstName("アクティブ1")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .build();

        User activeUser2 = User.builder()
                .email("active2@example.com")
                .passwordHash("hashedPassword")
                .firstName("アクティブ2")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .build();

        User inactiveUser = User.builder()
                .email("inactive@example.com")
                .passwordHash("hashedPassword")
                .firstName("非アクティブ")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.INACTIVE)
                .build();

        persistAndFlush(activeUser1);
        persistAndFlush(activeUser2);
        persistAndFlush(inactiveUser);

        // When
        long count = userRepository.countActiveUsers();

        // Then
        assertThat(count).isEqualTo(2);
    }
}