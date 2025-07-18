package jp.co.protosoft.rihua.api.repository;

import jp.co.protosoft.rihua.api.domain.Like;
import jp.co.protosoft.rihua.api.domain.User;
import jp.co.protosoft.rihua.api.domain.enums.LikeableType;
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
 * いいねリポジトリ
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {

    /**
     * IDでいいねを検索（削除済み除外）
     * 
     * @param id いいねID
     * @return いいね
     */
    Optional<Like> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * ユーザーでいいねを検索（削除済み除外）
     * 
     * @param user ユーザー
     * @param pageable ページング情報
     * @return いいねページ
     */
    Page<Like> findByUserAndDeletedAtIsNull(User user, Pageable pageable);

    /**
     * いいね対象タイプでいいねを検索（削除済み除外）
     * 
     * @param likeableType いいね対象タイプ
     * @param pageable ページング情報
     * @return いいねページ
     */
    Page<Like> findByLikeableTypeAndDeletedAtIsNull(LikeableType likeableType, Pageable pageable);

    /**
     * いいね対象でいいねを検索（削除済み除外）
     * 
     * @param likeableType いいね対象タイプ
     * @param likeableId いいね対象ID
     * @param pageable ページング情報
     * @return いいねページ
     */
    Page<Like> findByLikeableTypeAndLikeableIdAndDeletedAtIsNull(
            LikeableType likeableType, 
            UUID likeableId, 
            Pageable pageable);

    /**
     * ユーザーと対象でいいねを検索（削除済み除外）
     * 
     * @param user ユーザー
     * @param likeableType いいね対象タイプ
     * @param likeableId いいね対象ID
     * @return いいね
     */
    Optional<Like> findByUserAndLikeableTypeAndLikeableIdAndDeletedAtIsNull(
            User user, 
            LikeableType likeableType, 
            UUID likeableId);

    /**
     * ユーザーが特定の対象にいいねしているかチェック
     * 
     * @param user ユーザー
     * @param likeableType いいね対象タイプ
     * @param likeableId いいね対象ID
     * @return いいねしている場合true
     */
    boolean existsByUserAndLikeableTypeAndLikeableIdAndDeletedAtIsNull(
            User user, 
            LikeableType likeableType, 
            UUID likeableId);

    /**
     * 特定対象のいいね数を取得
     * 
     * @param likeableType いいね対象タイプ
     * @param likeableId いいね対象ID
     * @return いいね数
     */
    @Query("SELECT COUNT(l) FROM Like l WHERE l.likeableType = :likeableType AND l.likeableId = :likeableId AND l.deletedAt IS NULL")
    long countByLikeableTypeAndLikeableIdAndDeletedAtIsNull(
            @Param("likeableType") LikeableType likeableType,
            @Param("likeableId") UUID likeableId);

    /**
     * ユーザーのいいね数を取得
     * 
     * @param user ユーザー
     * @return いいね数
     */
    @Query("SELECT COUNT(l) FROM Like l WHERE l.user = :user AND l.deletedAt IS NULL")
    long countByUserAndDeletedAtIsNull(@Param("user") User user);

    /**
     * ユーザーが特定タイプにいいねした数を取得
     * 
     * @param user ユーザー
     * @param likeableType いいね対象タイプ
     * @return いいね数
     */
    @Query("SELECT COUNT(l) FROM Like l WHERE l.user = :user AND l.likeableType = :likeableType AND l.deletedAt IS NULL")
    long countByUserAndLikeableTypeAndDeletedAtIsNull(
            @Param("user") User user,
            @Param("likeableType") LikeableType likeableType);

    /**
     * 指定期間内のいいねを検索
     * 
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @param pageable ページング情報
     * @return いいねページ
     */
    @Query("SELECT l FROM Like l WHERE l.createdAt BETWEEN :startDate AND :endDate AND l.deletedAt IS NULL")
    Page<Like> findByCreatedAtBetweenAndDeletedAtIsNull(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 最近のいいねを検索
     * 
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return いいねページ
     */
    @Query("SELECT l FROM Like l WHERE l.createdAt >= :sinceDate AND l.deletedAt IS NULL ORDER BY l.createdAt DESC")
    Page<Like> findRecentLikes(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);

    /**
     * 複合条件でいいねを検索
     * 
     * @param user ユーザー（null可）
     * @param likeableType いいね対象タイプ（null可）
     * @param likeableId いいね対象ID（null可）
     * @param startDate 開始日時（null可）
     * @param endDate 終了日時（null可）
     * @param pageable ページング情報
     * @return いいねページ
     */
    @Query("SELECT l FROM Like l WHERE " +
           "(:user IS NULL OR l.user = :user) AND " +
           "(:likeableType IS NULL OR l.likeableType = :likeableType) AND " +
           "(:likeableId IS NULL OR l.likeableId = :likeableId) AND " +
           "(:startDate IS NULL OR l.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR l.createdAt <= :endDate) AND " +
           "l.deletedAt IS NULL " +
           "ORDER BY l.createdAt DESC")
    Page<Like> findByComplexCriteria(
            @Param("user") User user,
            @Param("likeableType") LikeableType likeableType,
            @Param("likeableId") UUID likeableId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 統計: タイプ別いいね数
     * 
     * @return タイプ別いいね数
     */
    @Query("SELECT l.likeableType, COUNT(l) FROM Like l WHERE l.deletedAt IS NULL GROUP BY l.likeableType")
    List<Object[]> countLikesByType();

    /**
     * 統計: 月別いいね数
     * 
     * @param year 年
     * @return 月別いいね数
     */
    @Query("SELECT MONTH(l.createdAt), COUNT(l) FROM Like l WHERE YEAR(l.createdAt) = :year AND l.deletedAt IS NULL GROUP BY MONTH(l.createdAt)")
    List<Object[]> countLikesByMonth(@Param("year") int year);

    /**
     * 人気コンテンツを検索（いいね数順）
     * 
     * @param likeableType いいね対象タイプ
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return いいね対象ID別いいね数
     */
    @Query("SELECT l.likeableId, COUNT(l) FROM Like l WHERE l.likeableType = :likeableType AND l.createdAt >= :sinceDate AND l.deletedAt IS NULL GROUP BY l.likeableId ORDER BY COUNT(l) DESC")
    Page<Object[]> findPopularContentByType(
            @Param("likeableType") LikeableType likeableType,
            @Param("sinceDate") LocalDateTime sinceDate,
            Pageable pageable);

    /**
     * 今日のいいねを検索
     * 
     * @param startOfDay 今日の開始時刻
     * @param endOfDay 今日の終了時刻
     * @param pageable ページング情報
     * @return いいねページ
     */
    @Query("SELECT l FROM Like l WHERE l.createdAt BETWEEN :startOfDay AND :endOfDay AND l.deletedAt IS NULL ORDER BY l.createdAt DESC")
    Page<Like> findTodayLikes(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable);

    /**
     * ユーザーの最近のいいねを検索
     * 
     * @param user ユーザー
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return いいねページ
     */
    @Query("SELECT l FROM Like l WHERE l.user = :user AND l.createdAt >= :sinceDate AND l.deletedAt IS NULL ORDER BY l.createdAt DESC")
    Page<Like> findRecentLikesByUser(
            @Param("user") User user,
            @Param("sinceDate") LocalDateTime sinceDate,
            Pageable pageable);

    /**
     * 特定対象への最近のいいねを検索
     * 
     * @param likeableType いいね対象タイプ
     * @param likeableId いいね対象ID
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return いいねページ
     */
    @Query("SELECT l FROM Like l WHERE l.likeableType = :likeableType AND l.likeableId = :likeableId AND l.createdAt >= :sinceDate AND l.deletedAt IS NULL ORDER BY l.createdAt DESC")
    Page<Like> findRecentLikesForTarget(
            @Param("likeableType") LikeableType likeableType,
            @Param("likeableId") UUID likeableId,
            @Param("sinceDate") LocalDateTime sinceDate,
            Pageable pageable);
}