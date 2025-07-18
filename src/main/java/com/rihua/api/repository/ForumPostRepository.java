package com.rihua.api.repository;

import com.rihua.api.domain.ForumPost;
import com.rihua.api.domain.User;
import com.rihua.api.domain.enums.ForumCategory;
import com.rihua.api.domain.enums.PostStatus;
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
 * フォーラム投稿リポジトリ
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, UUID> {

    /**
     * IDでフォーラム投稿を検索（削除済み除外）
     * 
     * @param id 投稿ID
     * @return フォーラム投稿
     */
    Optional<ForumPost> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * 公開されているフォーラム投稿を検索
     * 
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    @Query("SELECT f FROM ForumPost f WHERE f.status = 'PUBLISHED' AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    Page<ForumPost> findPublishedPosts(Pageable pageable);

    /**
     * カテゴリでフォーラム投稿を検索（削除済み除外）
     * 
     * @param category カテゴリ
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    Page<ForumPost> findByCategoryAndDeletedAtIsNull(ForumCategory category, Pageable pageable);

    /**
     * ステータスでフォーラム投稿を検索（削除済み除外）
     * 
     * @param status ステータス
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    Page<ForumPost> findByStatusAndDeletedAtIsNull(PostStatus status, Pageable pageable);

    /**
     * 作成者でフォーラム投稿を検索（削除済み除外）
     * 
     * @param author 作成者
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    Page<ForumPost> findByAuthorAndDeletedAtIsNull(User author, Pageable pageable);

    /**
     * キーワードでフォーラム投稿を検索
     * 
     * @param keyword 検索キーワード
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    @Query("SELECT f FROM ForumPost f WHERE " +
           "(LOWER(f.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "f.status = 'PUBLISHED' AND f.deletedAt IS NULL")
    Page<ForumPost> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 人気フォーラム投稿を検索（いいね数順）
     * 
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    @Query("SELECT f FROM ForumPost f WHERE f.status = 'PUBLISHED' AND f.deletedAt IS NULL ORDER BY f.likeCount DESC")
    Page<ForumPost> findPopularPosts(Pageable pageable);

    /**
     * 最新のフォーラム投稿を検索
     * 
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    @Query("SELECT f FROM ForumPost f WHERE f.status = 'PUBLISHED' AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    Page<ForumPost> findLatestPosts(Pageable pageable);

    /**
     * コメント数が多いフォーラム投稿を検索
     * 
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    @Query("SELECT f FROM ForumPost f WHERE f.status = 'PUBLISHED' AND f.deletedAt IS NULL ORDER BY f.commentCount DESC")
    Page<ForumPost> findMostCommentedPosts(Pageable pageable);

    /**
     * 固定投稿を検索
     * 
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    @Query("SELECT f FROM ForumPost f WHERE f.pinned = true AND f.status = 'PUBLISHED' AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    Page<ForumPost> findPinnedPosts(Pageable pageable);

    /**
     * 複合条件でフォーラム投稿を検索
     * 
     * @param category カテゴリ（null可）
     * @param keyword 検索キーワード（null可）
     * @param author 作成者（null可）
     * @param startDate 開始日時（null可）
     * @param endDate 終了日時（null可）
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    @Query("SELECT f FROM ForumPost f WHERE " +
           "(:category IS NULL OR f.category = :category) AND " +
           "(:keyword IS NULL OR LOWER(f.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:author IS NULL OR f.author = :author) AND " +
           "(:startDate IS NULL OR f.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR f.createdAt <= :endDate) AND " +
           "f.status = 'PUBLISHED' AND f.deletedAt IS NULL " +
           "ORDER BY f.createdAt DESC")
    Page<ForumPost> findByComplexCriteria(
            @Param("category") ForumCategory category,
            @Param("keyword") String keyword,
            @Param("author") User author,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 最近投稿されたフォーラム投稿を検索
     * 
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    @Query("SELECT f FROM ForumPost f WHERE f.createdAt >= :sinceDate AND f.status = 'PUBLISHED' AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    Page<ForumPost> findRecentPosts(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);

    /**
     * 統計: カテゴリ別投稿数
     * 
     * @return カテゴリ別投稿数
     */
    @Query("SELECT f.category, COUNT(f) FROM ForumPost f WHERE f.status = 'PUBLISHED' AND f.deletedAt IS NULL GROUP BY f.category")
    List<Object[]> countPostsByCategory();

    /**
     * 統計: 月別投稿数
     * 
     * @param year 年
     * @return 月別投稿数
     */
    @Query("SELECT MONTH(f.createdAt), COUNT(f) FROM ForumPost f WHERE YEAR(f.createdAt) = :year AND f.deletedAt IS NULL GROUP BY MONTH(f.createdAt)")
    List<Object[]> countPostsByMonth(@Param("year") int year);

    /**
     * 作成者の公開投稿数を取得
     * 
     * @param author 作成者
     * @return 公開投稿数
     */
    @Query("SELECT COUNT(f) FROM ForumPost f WHERE f.author = :author AND f.status = 'PUBLISHED' AND f.deletedAt IS NULL")
    long countPublishedPostsByAuthor(@Param("author") User author);

    /**
     * 承認待ち投稿を検索
     * 
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    @Query("SELECT f FROM ForumPost f WHERE f.status = 'PENDING_APPROVAL' AND f.deletedAt IS NULL ORDER BY f.createdAt ASC")
    Page<ForumPost> findPendingApprovalPosts(Pageable pageable);

    /**
     * 報告された投稿を検索
     * 
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    @Query("SELECT f FROM ForumPost f WHERE f.reportCount > 0 AND f.status = 'PUBLISHED' AND f.deletedAt IS NULL ORDER BY f.reportCount DESC")
    Page<ForumPost> findReportedPosts(Pageable pageable);

    /**
     * トレンド投稿を検索（最近のいいね数が多い）
     * 
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return フォーラム投稿ページ
     */
    @Query("SELECT f FROM ForumPost f WHERE f.createdAt >= :sinceDate AND f.status = 'PUBLISHED' AND f.deletedAt IS NULL ORDER BY f.likeCount DESC")
    Page<ForumPost> findTrendingPosts(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);
}