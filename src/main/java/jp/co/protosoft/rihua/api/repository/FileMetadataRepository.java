package jp.co.protosoft.rihua.api.repository;

import jp.co.protosoft.rihua.api.domain.FileMetadata;
import jp.co.protosoft.rihua.api.domain.User;
import jp.co.protosoft.rihua.api.domain.enums.FileType;
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
 * ファイルメタデータリポジトリ
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {

    /**
     * IDでファイルメタデータを検索（削除済み除外）
     * 
     * @param id ファイルメタデータID
     * @return ファイルメタデータ
     */
    Optional<FileMetadata> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * ファイルパスでファイルメタデータを検索（削除済み除外）
     * 
     * @param filePath ファイルパス
     * @return ファイルメタデータ
     */
    Optional<FileMetadata> findByFilePathAndDeletedAtIsNull(String filePath);

    /**
     * アップロード者でファイルメタデータを検索（削除済み除外）
     * 
     * @param uploader アップロード者
     * @param pageable ページング情報
     * @return ファイルメタデータページ
     */
    Page<FileMetadata> findByUploaderAndDeletedAtIsNull(User uploader, Pageable pageable);

    /**
     * ファイルタイプでファイルメタデータを検索（削除済み除外）
     * 
     * @param fileType ファイルタイプ
     * @param pageable ページング情報
     * @return ファイルメタデータページ
     */
    Page<FileMetadata> findByFileTypeAndDeletedAtIsNull(FileType fileType, Pageable pageable);

    /**
     * アップロード者とファイルタイプでファイルメタデータを検索（削除済み除外）
     * 
     * @param uploader アップロード者
     * @param fileType ファイルタイプ
     * @param pageable ページング情報
     * @return ファイルメタデータページ
     */
    Page<FileMetadata> findByUploaderAndFileTypeAndDeletedAtIsNull(User uploader, FileType fileType, Pageable pageable);

    /**
     * ファイル名で検索（削除済み除外）
     * 
     * @param fileName ファイル名（部分一致）
     * @param pageable ページング情報
     * @return ファイルメタデータページ
     */
    @Query("SELECT f FROM FileMetadata f WHERE LOWER(f.originalFileName) LIKE LOWER(CONCAT('%', :fileName, '%')) AND f.deletedAt IS NULL")
    Page<FileMetadata> findByFileNameContainingIgnoreCaseAndDeletedAtIsNull(@Param("fileName") String fileName, Pageable pageable);

    /**
     * ファイルサイズ範囲で検索（削除済み除外）
     * 
     * @param minSize 最小サイズ
     * @param maxSize 最大サイズ
     * @param pageable ページング情報
     * @return ファイルメタデータページ
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.fileSize BETWEEN :minSize AND :maxSize AND f.deletedAt IS NULL")
    Page<FileMetadata> findByFileSizeBetweenAndDeletedAtIsNull(
            @Param("minSize") Long minSize,
            @Param("maxSize") Long maxSize,
            Pageable pageable);

    /**
     * 指定期間内にアップロードされたファイルを検索
     * 
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @param pageable ページング情報
     * @return ファイルメタデータページ
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.createdAt BETWEEN :startDate AND :endDate AND f.deletedAt IS NULL")
    Page<FileMetadata> findByUploadDateBetweenAndDeletedAtIsNull(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 最近アップロードされたファイルを検索
     * 
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return ファイルメタデータページ
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.createdAt >= :sinceDate AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    Page<FileMetadata> findRecentFiles(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);

    /**
     * 大きなファイルを検索
     * 
     * @param minSize 最小サイズ
     * @param pageable ページング情報
     * @return ファイルメタデータページ
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.fileSize >= :minSize AND f.deletedAt IS NULL ORDER BY f.fileSize DESC")
    Page<FileMetadata> findLargeFiles(@Param("minSize") Long minSize, Pageable pageable);

    /**
     * 画像ファイルを検索
     * 
     * @param pageable ページング情報
     * @return ファイルメタデータページ
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.fileType = 'IMAGE' AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    Page<FileMetadata> findImageFiles(Pageable pageable);

    /**
     * ドキュメントファイルを検索
     * 
     * @param pageable ページング情報
     * @return ファイルメタデータページ
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.fileType = 'DOCUMENT' AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    Page<FileMetadata> findDocumentFiles(Pageable pageable);

    /**
     * 複合条件でファイルメタデータを検索
     * 
     * @param uploader アップロード者（null可）
     * @param fileType ファイルタイプ（null可）
     * @param fileName ファイル名（null可）
     * @param minSize 最小サイズ（null可）
     * @param maxSize 最大サイズ（null可）
     * @param startDate 開始日時（null可）
     * @param endDate 終了日時（null可）
     * @param pageable ページング情報
     * @return ファイルメタデータページ
     */
    @Query("SELECT f FROM FileMetadata f WHERE " +
           "(:uploader IS NULL OR f.uploader = :uploader) AND " +
           "(:fileType IS NULL OR f.fileType = :fileType) AND " +
           "(:fileName IS NULL OR LOWER(f.originalFileName) LIKE LOWER(CONCAT('%', :fileName, '%'))) AND " +
           "(:minSize IS NULL OR f.fileSize >= :minSize) AND " +
           "(:maxSize IS NULL OR f.fileSize <= :maxSize) AND " +
           "(:startDate IS NULL OR f.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR f.createdAt <= :endDate) AND " +
           "f.deletedAt IS NULL " +
           "ORDER BY f.createdAt DESC")
    Page<FileMetadata> findByComplexCriteria(
            @Param("uploader") User uploader,
            @Param("fileType") FileType fileType,
            @Param("fileName") String fileName,
            @Param("minSize") Long minSize,
            @Param("maxSize") Long maxSize,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * アップロード者のファイル数を取得
     * 
     * @param uploader アップロード者
     * @return ファイル数
     */
    @Query("SELECT COUNT(f) FROM FileMetadata f WHERE f.uploader = :uploader AND f.deletedAt IS NULL")
    long countFilesByUploader(@Param("uploader") User uploader);

    /**
     * アップロード者の総ファイルサイズを取得
     * 
     * @param uploader アップロード者
     * @return 総ファイルサイズ
     */
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileMetadata f WHERE f.uploader = :uploader AND f.deletedAt IS NULL")
    long sumFileSizeByUploader(@Param("uploader") User uploader);

    /**
     * 統計: ファイルタイプ別ファイル数
     * 
     * @return ファイルタイプ別ファイル数
     */
    @Query("SELECT f.fileType, COUNT(f) FROM FileMetadata f WHERE f.deletedAt IS NULL GROUP BY f.fileType")
    List<Object[]> countFilesByType();

    /**
     * 統計: 月別アップロード数
     * 
     * @param year 年
     * @return 月別アップロード数
     */
    @Query("SELECT MONTH(f.createdAt), COUNT(f) FROM FileMetadata f WHERE YEAR(f.createdAt) = :year AND f.deletedAt IS NULL GROUP BY MONTH(f.createdAt)")
    List<Object[]> countUploadsByMonth(@Param("year") int year);

    /**
     * 統計: 月別アップロードサイズ
     * 
     * @param year 年
     * @return 月別アップロードサイズ
     */
    @Query("SELECT MONTH(f.createdAt), SUM(f.fileSize) FROM FileMetadata f WHERE YEAR(f.createdAt) = :year AND f.deletedAt IS NULL GROUP BY MONTH(f.createdAt)")
    List<Object[]> sumUploadSizeByMonth(@Param("year") int year);

    /**
     * 古いファイルを検索（削除対象）
     * 
     * @param cutoffDate 削除基準日時
     * @return 古いファイルリスト
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.createdAt < :cutoffDate AND f.deletedAt IS NULL")
    List<FileMetadata> findOldFiles(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 孤立ファイルを検索（参照されていないファイル）
     * 
     * @return 孤立ファイルリスト
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.referenceCount = 0 AND f.deletedAt IS NULL")
    List<FileMetadata> findOrphanedFiles();

    /**
     * 今日アップロードされたファイルを検索
     * 
     * @param startOfDay 今日の開始時刻
     * @param endOfDay 今日の終了時刻
     * @param pageable ページング情報
     * @return ファイルメタデータページ
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.createdAt BETWEEN :startOfDay AND :endOfDay AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    Page<FileMetadata> findTodayFiles(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable);

    /**
     * MIMEタイプでファイルを検索
     * 
     * @param mimeType MIMEタイプ
     * @param pageable ページング情報
     * @return ファイルメタデータページ
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.mimeType = :mimeType AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    Page<FileMetadata> findByMimeType(@Param("mimeType") String mimeType, Pageable pageable);
}