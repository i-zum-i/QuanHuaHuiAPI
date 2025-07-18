package com.rihua.api.repository;

import com.rihua.api.domain.User;
import com.rihua.api.domain.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ユーザーリポジトリ
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * メールアドレスでユーザーを検索（削除済み除外）
     * 
     * @param email メールアドレス
     * @return ユーザー
     */
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    /**
     * IDでユーザーを検索（削除済み除外）
     * 
     * @param id ユーザーID
     * @return ユーザー
     */
    Optional<User> findByIdAndDeletedAtIsNull(String id);

    /**
     * メールアドレスの存在確認（削除済み除外）
     * 
     * @param email メールアドレス
     * @return 存在する場合true
     */
    boolean existsByEmailAndDeletedAtIsNull(String email);

    /**
     * ステータスでユーザーを検索（削除済み除外）
     * 
     * @param status ユーザーステータス
     * @param pageable ページング情報
     * @return ユーザーページ
     */
    Page<User> findByStatusAndDeletedAtIsNull(UserStatus status, Pageable pageable);

    /**
     * アクティブなユーザーを検索
     * 
     * @param pageable ページング情報
     * @return ユーザーページ
     */
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND u.deletedAt IS NULL")
    Page<User> findActiveUsers(Pageable pageable);

    /**
     * 指定期間内に作成されたユーザーを検索
     * 
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @param pageable ページング情報
     * @return ユーザーページ
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.deletedAt IS NULL")
    Page<User> findByCreatedAtBetweenAndDeletedAtIsNull(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * メールアドレスまたは名前で検索（削除済み除外）
     * 
     * @param keyword 検索キーワード
     * @param pageable ページング情報
     * @return ユーザーページ
     */
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "u.deletedAt IS NULL")
    Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 認証待ちユーザーの数を取得
     * 
     * @return 認証待ちユーザー数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'PENDING_VERIFICATION' AND u.deletedAt IS NULL")
    long countPendingVerificationUsers();

    /**
     * アクティブユーザーの数を取得
     * 
     * @return アクティブユーザー数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ACTIVE' AND u.deletedAt IS NULL")
    long countActiveUsers();
}