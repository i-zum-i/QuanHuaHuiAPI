package jp.co.protosoft.rihua.api.repository;

import jp.co.protosoft.rihua.api.domain.Notification;
import jp.co.protosoft.rihua.api.domain.User;
import jp.co.protosoft.rihua.api.domain.enums.NotificationType;
import jp.co.protosoft.rihua.api.domain.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 通知リポジトリ
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * IDで通知を検索（削除済み除外）
     * 
     * @param id 通知ID
     * @return 通知
     */
    Optional<Notification> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * ユーザーの通知を検索（削除済み除外）
     * 
     * @param user ユーザー
     * @param pageable ページング情報
     * @return 通知ページ
     */
    Page<Notification> findByUserAndDeletedAtIsNull(User user, Pageable pageable);

    /**
     * ユーザーの未読通知を検索（削除済み除外）
     * 
     * @param user ユーザー
     * @param pageable ページング情報
     * @return 通知ページ
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.status = 'UNREAD' AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    Page<Notification> findUnreadNotificationsByUser(@Param("user") User user, Pageable pageable);

    /**
     * ユーザーの既読通知を検索（削除済み除外）
     * 
     * @param user ユーザー
     * @param pageable ページング情報
     * @return 通知ページ
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.status = 'READ' AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    Page<Notification> findReadNotificationsByUser(@Param("user") User user, Pageable pageable);

    /**
     * タイプで通知を検索（削除済み除外）
     * 
     * @param type 通知タイプ
     * @param pageable ページング情報
     * @return 通知ページ
     */
    Page<Notification> findByTypeAndDeletedAtIsNull(NotificationType type, Pageable pageable);

    /**
     * ステータスで通知を検索（削除済み除外）
     * 
     * @param status ステータス
     * @param pageable ページング情報
     * @return 通知ページ
     */
    Page<Notification> findByStatusAndDeletedAtIsNull(NotificationStatus status, Pageable pageable);

    /**
     * ユーザーとタイプで通知を検索（削除済み除外）
     * 
     * @param user ユーザー
     * @param type 通知タイプ
     * @param pageable ページング情報
     * @return 通知ページ
     */
    Page<Notification> findByUserAndTypeAndDeletedAtIsNull(User user, NotificationType type, Pageable pageable);

    /**
     * ユーザーの未読通知数を取得
     * 
     * @param user ユーザー
     * @return 未読通知数
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.status = 'UNREAD' AND n.deletedAt IS NULL")
    long countUnreadNotificationsByUser(@Param("user") User user);

    /**
     * ユーザーの通知数を取得
     * 
     * @param user ユーザー
     * @return 通知数
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.deletedAt IS NULL")
    long countNotificationsByUser(@Param("user") User user);

    /**
     * 指定期間内の通知を検索
     * 
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @param pageable ページング情報
     * @return 通知ページ
     */
    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startDate AND :endDate AND n.deletedAt IS NULL")
    Page<Notification> findByCreatedAtBetweenAndDeletedAtIsNull(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 最近の通知を検索
     * 
     * @param user ユーザー
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return 通知ページ
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.createdAt >= :sinceDate AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    Page<Notification> findRecentNotificationsByUser(
            @Param("user") User user,
            @Param("sinceDate") LocalDateTime sinceDate,
            Pageable pageable);

    /**
     * 重要な通知を検索
     * 
     * @param user ユーザー
     * @param pageable ページング情報
     * @return 通知ページ
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.priority = 'HIGH' AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    Page<Notification> findHighPriorityNotificationsByUser(@Param("user") User user, Pageable pageable);

    /**
     * 複合条件で通知を検索
     * 
     * @param user ユーザー（null可）
     * @param type 通知タイプ（null可）
     * @param status ステータス（null可）
     * @param startDate 開始日時（null可）
     * @param endDate 終了日時（null可）
     * @param pageable ページング情報
     * @return 通知ページ
     */
    @Query("SELECT n FROM Notification n WHERE " +
           "(:user IS NULL OR n.user = :user) AND " +
           "(:type IS NULL OR n.type = :type) AND " +
           "(:status IS NULL OR n.status = :status) AND " +
           "(:startDate IS NULL OR n.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR n.createdAt <= :endDate) AND " +
           "n.deletedAt IS NULL " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findByComplexCriteria(
            @Param("user") User user,
            @Param("type") NotificationType type,
            @Param("status") NotificationStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 送信失敗した通知を検索
     * 
     * @param pageable ページング情報
     * @return 通知ページ
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.deletedAt IS NULL ORDER BY n.createdAt ASC")
    Page<Notification> findFailedNotifications(Pageable pageable);

    /**
     * 送信待ちの通知を検索
     * 
     * @param pageable ページング情報
     * @return 通知ページ
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'PENDING' AND n.deletedAt IS NULL ORDER BY n.createdAt ASC")
    Page<Notification> findPendingNotifications(Pageable pageable);

    /**
     * 古い通知を検索（削除対象）
     * 
     * @param cutoffDate 削除基準日時
     * @return 古い通知リスト
     */
    @Query("SELECT n FROM Notification n WHERE n.createdAt < :cutoffDate AND n.deletedAt IS NULL")
    List<Notification> findOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 統計: タイプ別通知数
     * 
     * @return タイプ別通知数
     */
    @Query("SELECT n.type, COUNT(n) FROM Notification n WHERE n.deletedAt IS NULL GROUP BY n.type")
    List<Object[]> countNotificationsByType();

    /**
     * 統計: 月別通知数
     * 
     * @param year 年
     * @return 月別通知数
     */
    @Query("SELECT MONTH(n.createdAt), COUNT(n) FROM Notification n WHERE YEAR(n.createdAt) = :year AND n.deletedAt IS NULL GROUP BY MONTH(n.createdAt)")
    List<Object[]> countNotificationsByMonth(@Param("year") int year);

    /**
     * 統計: ステータス別通知数
     * 
     * @return ステータス別通知数
     */
    @Query("SELECT n.status, COUNT(n) FROM Notification n WHERE n.deletedAt IS NULL GROUP BY n.status")
    List<Object[]> countNotificationsByStatus();

    /**
     * 今日の通知を検索
     * 
     * @param startOfDay 今日の開始時刻
     * @param endOfDay 今日の終了時刻
     * @param pageable ページング情報
     * @return 通知ページ
     */
    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startOfDay AND :endOfDay AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    Page<Notification> findTodayNotifications(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable);

    /**
     * リトライ対象の通知を検索
     * 
     * @param maxRetryCount 最大リトライ回数
     * @param pageable ページング情報
     * @return 通知ページ
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount < :maxRetryCount AND n.deletedAt IS NULL ORDER BY n.createdAt ASC")
    Page<Notification> findRetryableNotifications(@Param("maxRetryCount") int maxRetryCount, Pageable pageable);
}