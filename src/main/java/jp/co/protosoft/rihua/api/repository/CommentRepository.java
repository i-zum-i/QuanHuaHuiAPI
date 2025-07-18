package jp.co.protosoft.rihua.api.repository;

import jp.co.protosoft.rihua.api.domain.Comment;
import jp.co.protosoft.rihua.api.domain.ForumPost;
import jp.co.protosoft.rihua.api.domain.User;
import jp.co.protosoft.rihua.api.domain.enums.CommentStatus;
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
 * コメントリポジトリ
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    /**
     * IDでコメントを検索（削除済み除外）
     * 
     * @param id コメントID
     * @return コメント
     */
    Optional<Comment> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * フォーラム投稿のコメントを検索（削除済み除外）
     * 
     * @param post フォーラム投稿
     * @param pageable ページング情報
     * @return コメントページ
     */
    Page<Comment> findByPostAndDeletedAtIsNull(ForumPost post, Pageable pageable);

    /**
     * フォーラム投稿の公開コメントを検索
     * 
     * @param post フォーラム投稿
     * @param pageable ページング情報
     * @return コメントページ
     */
    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.status = 'PUBLISHED' AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    Page<Comment> findPublishedCommentsByPost(@Param("post") ForumPost post, Pageable pageable);

    /**
     * 作成者でコメントを検索（削除済み除外）
     * 
     * @param author 作成者
     * @param pageable ページング情報
     * @return コメントページ
     */
    Page<Comment> findByAuthorAndDeletedAtIsNull(User author, Pageable pageable);

    /**
     * ステータスでコメントを検索（削除済み除外）
     * 
     * @param status ステータス
     * @param pageable ページング情報
     * @return コメントページ
     */
    Page<Comment> findByStatusAndDeletedAtIsNull(CommentStatus status, Pageable pageable);

    /**
     * 親コメントの返信を検索（削除済み除外）
     * 
     * @param parent 親コメント
     * @param pageable ページング情報
     * @return コメントページ
     */
    Page<Comment> findByParentAndDeletedAtIsNull(Comment parent, Pageable pageable);

    /**
     * 親コメントの公開返信を検索
     * 
     * @param parent 親コメント
     * @param pageable ページング情報
     * @return コメントページ
     */
    @Query("SELECT c FROM Comment c WHERE c.parent = :parent AND c.status = 'PUBLISHED' AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    Page<Comment> findPublishedRepliesByParentComment(@Param("parent") Comment parent, Pageable pageable);

    /**
     * トップレベルコメントを検索（返信ではないコメント）
     * 
     * @param post フォーラム投稿
     * @param pageable ページング情報
     * @return コメントページ
     */
    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.parent IS NULL AND c.status = 'PUBLISHED' AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    Page<Comment> findTopLevelCommentsByPost(@Param("post") ForumPost post, Pageable pageable);

    /**
     * キーワードでコメントを検索
     * 
     * @param keyword 検索キーワード
     * @param pageable ページング情報
     * @return コメントページ
     */
    @Query("SELECT c FROM Comment c WHERE LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%')) AND c.status = 'PUBLISHED' AND c.deletedAt IS NULL")
    Page<Comment> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 人気コメントを検索（いいね数順）
     * 
     * @param post フォーラム投稿
     * @param pageable ページング情報
     * @return コメントページ
     */
    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.status = 'PUBLISHED' AND c.deletedAt IS NULL ORDER BY c.likeCount DESC")
    Page<Comment> findPopularCommentsByPost(@Param("post") ForumPost post, Pageable pageable);

    /**
     * 最新コメントを検索
     * 
     * @param post フォーラム投稿
     * @param pageable ページング情報
     * @return コメントページ
     */
    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.status = 'PUBLISHED' AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findLatestCommentsByPost(@Param("post") ForumPost post, Pageable pageable);

    /**
     * 最近投稿されたコメントを検索
     * 
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return コメントページ
     */
    @Query("SELECT c FROM Comment c WHERE c.createdAt >= :sinceDate AND c.status = 'PUBLISHED' AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findRecentComments(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);

    /**
     * フォーラム投稿のコメント数を取得
     * 
     * @param post フォーラム投稿
     * @return コメント数
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post = :post AND c.status = 'PUBLISHED' AND c.deletedAt IS NULL")
    long countPublishedCommentsByPost(@Param("post") ForumPost post);

    /**
     * 作成者のコメント数を取得
     * 
     * @param author 作成者
     * @return コメント数
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.author = :author AND c.status = 'PUBLISHED' AND c.deletedAt IS NULL")
    long countPublishedCommentsByAuthor(@Param("author") User author);

    /**
     * 承認待ちコメントを検索
     * 
     * @param pageable ページング情報
     * @return コメントページ
     */
    @Query("SELECT c FROM Comment c WHERE c.status = 'PENDING_APPROVAL' AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    Page<Comment> findPendingApprovalComments(Pageable pageable);

    /**
     * 報告されたコメントを検索
     * 
     * @param pageable ページング情報
     * @return コメントページ
     */
    @Query("SELECT c FROM Comment c WHERE c.reportCount > 0 AND c.status = 'PUBLISHED' AND c.deletedAt IS NULL ORDER BY c.reportCount DESC")
    Page<Comment> findReportedComments(Pageable pageable);

    /**
     * 親コメントの返信数を取得
     * 
     * @param parent 親コメント
     * @return 返信数
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parent = :parent AND c.status = 'PUBLISHED' AND c.deletedAt IS NULL")
    long countRepliesByParentComment(@Param("parent") Comment parent);

    /**
     * 統計: 月別コメント数
     * 
     * @param year 年
     * @return 月別コメント数
     */
    @Query("SELECT MONTH(c.createdAt), COUNT(c) FROM Comment c WHERE YEAR(c.createdAt) = :year AND c.deletedAt IS NULL GROUP BY MONTH(c.createdAt)")
    List<Object[]> countCommentsByMonth(@Param("year") int year);

    /**
     * 複合条件でコメントを検索
     * 
     * @param post フォーラム投稿（null可）
     * @param author 作成者（null可）
     * @param keyword 検索キーワード（null可）
     * @param startDate 開始日時（null可）
     * @param endDate 終了日時（null可）
     * @param pageable ページング情報
     * @return コメントページ
     */
    @Query("SELECT c FROM Comment c WHERE " +
           "(:post IS NULL OR c.post = :post) AND " +
           "(:author IS NULL OR c.author = :author) AND " +
           "(:keyword IS NULL OR LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:startDate IS NULL OR c.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR c.createdAt <= :endDate) AND " +
           "c.status = 'PUBLISHED' AND c.deletedAt IS NULL " +
           "ORDER BY c.createdAt DESC")
    Page<Comment> findByComplexCriteria(
            @Param("post") ForumPost post,
            @Param("author") User author,
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}