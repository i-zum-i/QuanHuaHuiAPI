package com.rihua.api.repository;

import com.rihua.api.domain.Report;
import com.rihua.api.domain.User;
import com.rihua.api.domain.enums.ReportableType;
import com.rihua.api.domain.enums.ReportStatus;
import com.rihua.api.domain.enums.ReportReason;
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
 * 報告リポジトリ
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    /**
     * IDで報告を検索（削除済み除外）
     * 
     * @param id 報告ID
     * @return 報告
     */
    Optional<Report> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * 報告者で報告を検索（削除済み除外）
     * 
     * @param reporter 報告者
     * @param pageable ページング情報
     * @return 報告ページ
     */
    Page<Report> findByReporterAndDeletedAtIsNull(User reporter, Pageable pageable);

    /**
     * 報告対象タイプで報告を検索（削除済み除外）
     * 
     * @param reportableType 報告対象タイプ
     * @param pageable ページング情報
     * @return 報告ページ
     */
    Page<Report> findByReportableTypeAndDeletedAtIsNull(ReportableType reportableType, Pageable pageable);

    /**
     * 報告対象で報告を検索（削除済み除外）
     * 
     * @param reportableType 報告対象タイプ
     * @param reportableId 報告対象ID
     * @param pageable ページング情報
     * @return 報告ページ
     */
    Page<Report> findByReportableTypeAndReportableIdAndDeletedAtIsNull(
            ReportableType reportableType, 
            UUID reportableId, 
            Pageable pageable);

    /**
     * ステータスで報告を検索（削除済み除外）
     * 
     * @param status ステータス
     * @param pageable ページング情報
     * @return 報告ページ
     */
    Page<Report> findByStatusAndDeletedAtIsNull(ReportStatus status, Pageable pageable);

    /**
     * 理由で報告を検索（削除済み除外）
     * 
     * @param reason 理由
     * @param pageable ページング情報
     * @return 報告ページ
     */
    Page<Report> findByReasonAndDeletedAtIsNull(ReportReason reason, Pageable pageable);

    /**
     * 報告者と対象で報告を検索（削除済み除外）
     * 
     * @param reporter 報告者
     * @param reportableType 報告対象タイプ
     * @param reportableId 報告対象ID
     * @return 報告
     */
    Optional<Report> findByReporterAndReportableTypeAndReportableIdAndDeletedAtIsNull(
            User reporter, 
            ReportableType reportableType, 
            UUID reportableId);

    /**
     * 報告者が特定の対象を報告しているかチェック
     * 
     * @param reporter 報告者
     * @param reportableType 報告対象タイプ
     * @param reportableId 報告対象ID
     * @return 報告している場合true
     */
    boolean existsByReporterAndReportableTypeAndReportableIdAndDeletedAtIsNull(
            User reporter, 
            ReportableType reportableType, 
            UUID reportableId);

    /**
     * 特定対象の報告数を取得
     * 
     * @param reportableType 報告対象タイプ
     * @param reportableId 報告対象ID
     * @return 報告数
     */
    @Query("SELECT COUNT(r) FROM Report r WHERE r.reportableType = :reportableType AND r.reportableId = :reportableId AND r.deletedAt IS NULL")
    long countByReportableTypeAndReportableIdAndDeletedAtIsNull(
            @Param("reportableType") ReportableType reportableType,
            @Param("reportableId") UUID reportableId);

    /**
     * 報告者の報告数を取得
     * 
     * @param reporter 報告者
     * @return 報告数
     */
    @Query("SELECT COUNT(r) FROM Report r WHERE r.reporter = :reporter AND r.deletedAt IS NULL")
    long countByReporterAndDeletedAtIsNull(@Param("reporter") User reporter);

    /**
     * 未処理の報告を検索
     * 
     * @param pageable ページング情報
     * @return 報告ページ
     */
    @Query("SELECT r FROM Report r WHERE r.status = 'PENDING' AND r.deletedAt IS NULL ORDER BY r.createdAt ASC")
    Page<Report> findPendingReports(Pageable pageable);

    /**
     * 処理済みの報告を検索
     * 
     * @param pageable ページング情報
     * @return 報告ページ
     */
    @Query("SELECT r FROM Report r WHERE r.status IN ('APPROVED', 'REJECTED') AND r.deletedAt IS NULL ORDER BY r.updatedAt DESC")
    Page<Report> findProcessedReports(Pageable pageable);

    /**
     * 承認された報告を検索
     * 
     * @param pageable ページング情報
     * @return 報告ページ
     */
    @Query("SELECT r FROM Report r WHERE r.status = 'APPROVED' AND r.deletedAt IS NULL ORDER BY r.updatedAt DESC")
    Page<Report> findApprovedReports(Pageable pageable);

    /**
     * 指定期間内の報告を検索
     * 
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @param pageable ページング情報
     * @return 報告ページ
     */
    @Query("SELECT r FROM Report r WHERE r.createdAt BETWEEN :startDate AND :endDate AND r.deletedAt IS NULL")
    Page<Report> findByCreatedAtBetweenAndDeletedAtIsNull(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 最近の報告を検索
     * 
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return 報告ページ
     */
    @Query("SELECT r FROM Report r WHERE r.createdAt >= :sinceDate AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    Page<Report> findRecentReports(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);

    /**
     * 複合条件で報告を検索
     * 
     * @param reporter 報告者（null可）
     * @param reportableType 報告対象タイプ（null可）
     * @param reportableId 報告対象ID（null可）
     * @param status ステータス（null可）
     * @param reason 理由（null可）
     * @param startDate 開始日時（null可）
     * @param endDate 終了日時（null可）
     * @param pageable ページング情報
     * @return 報告ページ
     */
    @Query("SELECT r FROM Report r WHERE " +
           "(:reporter IS NULL OR r.reporter = :reporter) AND " +
           "(:reportableType IS NULL OR r.reportableType = :reportableType) AND " +
           "(:reportableId IS NULL OR r.reportableId = :reportableId) AND " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:reason IS NULL OR r.reason = :reason) AND " +
           "(:startDate IS NULL OR r.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR r.createdAt <= :endDate) AND " +
           "r.deletedAt IS NULL " +
           "ORDER BY r.createdAt DESC")
    Page<Report> findByComplexCriteria(
            @Param("reporter") User reporter,
            @Param("reportableType") ReportableType reportableType,
            @Param("reportableId") UUID reportableId,
            @Param("status") ReportStatus status,
            @Param("reason") ReportReason reason,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 統計: タイプ別報告数
     * 
     * @return タイプ別報告数
     */
    @Query("SELECT r.reportableType, COUNT(r) FROM Report r WHERE r.deletedAt IS NULL GROUP BY r.reportableType")
    List<Object[]> countReportsByType();

    /**
     * 統計: 理由別報告数
     * 
     * @return 理由別報告数
     */
    @Query("SELECT r.reason, COUNT(r) FROM Report r WHERE r.deletedAt IS NULL GROUP BY r.reason")
    List<Object[]> countReportsByReason();

    /**
     * 統計: ステータス別報告数
     * 
     * @return ステータス別報告数
     */
    @Query("SELECT r.status, COUNT(r) FROM Report r WHERE r.deletedAt IS NULL GROUP BY r.status")
    List<Object[]> countReportsByStatus();

    /**
     * 統計: 月別報告数
     * 
     * @param year 年
     * @return 月別報告数
     */
    @Query("SELECT MONTH(r.createdAt), COUNT(r) FROM Report r WHERE YEAR(r.createdAt) = :year AND r.deletedAt IS NULL GROUP BY MONTH(r.createdAt)")
    List<Object[]> countReportsByMonth(@Param("year") int year);

    /**
     * 多数報告されたコンテンツを検索
     * 
     * @param minReportCount 最小報告数
     * @param pageable ページング情報
     * @return 報告対象別報告数
     */
    @Query("SELECT r.reportableType, r.reportableId, COUNT(r) FROM Report r WHERE r.deletedAt IS NULL GROUP BY r.reportableType, r.reportableId HAVING COUNT(r) >= :minReportCount ORDER BY COUNT(r) DESC")
    Page<Object[]> findHighlyReportedContent(@Param("minReportCount") long minReportCount, Pageable pageable);

    /**
     * 今日の報告を検索
     * 
     * @param startOfDay 今日の開始時刻
     * @param endOfDay 今日の終了時刻
     * @param pageable ページング情報
     * @return 報告ページ
     */
    @Query("SELECT r FROM Report r WHERE r.createdAt BETWEEN :startOfDay AND :endOfDay AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    Page<Report> findTodayReports(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable);

    /**
     * 処理者で報告を検索
     * 
     * @param processor 処理者
     * @param pageable ページング情報
     * @return 報告ページ
     */
    Page<Report> findByProcessorAndDeletedAtIsNull(User processor, Pageable pageable);

    /**
     * 緊急度の高い報告を検索
     * 
     * @param pageable ページング情報
     * @return 報告ページ
     */
    @Query("SELECT r FROM Report r WHERE r.priority = 'HIGH' AND r.status = 'PENDING' AND r.deletedAt IS NULL ORDER BY r.createdAt ASC")
    Page<Report> findHighPriorityReports(Pageable pageable);
}