package com.rihua.api.repository;

import com.rihua.api.domain.AuditLog;
import com.rihua.api.domain.User;
import com.rihua.api.domain.enums.AuditAction;
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
 * 監査ログリポジトリ
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    /**
     * IDで監査ログを検索
     * 
     * @param id 監査ログID
     * @return 監査ログ
     */
    Optional<AuditLog> findById(UUID id);

    /**
     * ユーザーで監査ログを検索
     * 
     * @param user ユーザー
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    Page<AuditLog> findByUser(User user, Pageable pageable);

    /**
     * アクションで監査ログを検索
     * 
     * @param action アクション
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);

    /**
     * エンティティタイプで監査ログを検索
     * 
     * @param entityType エンティティタイプ
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);

    /**
     * エンティティIDで監査ログを検索
     * 
     * @param entityId エンティティID
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    Page<AuditLog> findByEntityId(String entityId, Pageable pageable);

    /**
     * 指定期間内の監査ログを検索
     * 
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    Page<AuditLog> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * ユーザーとアクションで監査ログを検索
     * 
     * @param user ユーザー
     * @param action アクション
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    Page<AuditLog> findByUserAndAction(User user, AuditAction action, Pageable pageable);

    /**
     * エンティティタイプとアクションで監査ログを検索
     * 
     * @param entityType エンティティタイプ
     * @param action アクション
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    Page<AuditLog> findByEntityTypeAndAction(String entityType, AuditAction action, Pageable pageable);

    /**
     * 特定エンティティの監査ログを検索
     * 
     * @param entityType エンティティタイプ
     * @param entityId エンティティID
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    Page<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId, Pageable pageable);

    /**
     * IPアドレスで監査ログを検索
     * 
     * @param ipAddress IPアドレス
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    Page<AuditLog> findByIpAddress(String ipAddress, Pageable pageable);

    /**
     * 複合条件で監査ログを検索
     * 
     * @param user ユーザー（null可）
     * @param action アクション（null可）
     * @param entityType エンティティタイプ（null可）
     * @param entityId エンティティID（null可）
     * @param startDate 開始日時（null可）
     * @param endDate 終了日時（null可）
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:user IS NULL OR a.user = :user) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:entityType IS NULL OR a.entityType = :entityType) AND " +
           "(:entityId IS NULL OR a.entityId = :entityId) AND " +
           "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR a.createdAt <= :endDate) " +
           "ORDER BY a.createdAt DESC")
    Page<AuditLog> findByComplexCriteria(
            @Param("user") User user,
            @Param("action") AuditAction action,
            @Param("entityType") String entityType,
            @Param("entityId") String entityId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 最近の監査ログを検索
     * 
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt >= :sinceDate ORDER BY a.createdAt DESC")
    Page<AuditLog> findRecentAuditLogs(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);

    /**
     * 統計: アクション別ログ数
     * 
     * @return アクション別ログ数
     */
    @Query("SELECT a.action, COUNT(a) FROM AuditLog a GROUP BY a.action")
    List<Object[]> countLogsByAction();

    /**
     * 統計: エンティティタイプ別ログ数
     * 
     * @return エンティティタイプ別ログ数
     */
    @Query("SELECT a.entityType, COUNT(a) FROM AuditLog a GROUP BY a.entityType")
    List<Object[]> countLogsByEntityType();

    /**
     * 統計: 月別ログ数
     * 
     * @param year 年
     * @return 月別ログ数
     */
    @Query("SELECT MONTH(a.createdAt), COUNT(a) FROM AuditLog a WHERE YEAR(a.createdAt) = :year GROUP BY MONTH(a.createdAt)")
    List<Object[]> countLogsByMonth(@Param("year") int year);

    /**
     * 統計: ユーザー別ログ数
     * 
     * @param pageable ページング情報
     * @return ユーザー別ログ数
     */
    @Query("SELECT a.user, COUNT(a) FROM AuditLog a GROUP BY a.user ORDER BY COUNT(a) DESC")
    Page<Object[]> countLogsByUser(Pageable pageable);

    /**
     * 古い監査ログを検索（削除対象）
     * 
     * @param cutoffDate 削除基準日時
     * @return 古い監査ログリスト
     */
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt < :cutoffDate")
    List<AuditLog> findOldAuditLogs(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 今日の監査ログを検索
     * 
     * @param startOfDay 今日の開始時刻
     * @param endOfDay 今日の終了時刻
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startOfDay AND :endOfDay ORDER BY a.createdAt DESC")
    Page<AuditLog> findTodayAuditLogs(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable);

    /**
     * 失敗したアクションのログを検索
     * 
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    @Query("SELECT a FROM AuditLog a WHERE a.success = false ORDER BY a.createdAt DESC")
    Page<AuditLog> findFailedActions(Pageable pageable);

    /**
     * 特定IPアドレスからの最近のアクティビティを検索
     * 
     * @param ipAddress IPアドレス
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return 監査ログページ
     */
    @Query("SELECT a FROM AuditLog a WHERE a.ipAddress = :ipAddress AND a.createdAt >= :sinceDate ORDER BY a.createdAt DESC")
    Page<AuditLog> findRecentActivityByIpAddress(
            @Param("ipAddress") String ipAddress,
            @Param("sinceDate") LocalDateTime sinceDate,
            Pageable pageable);
}