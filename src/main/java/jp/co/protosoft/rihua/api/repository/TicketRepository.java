package jp.co.protosoft.rihua.api.repository;

import jp.co.protosoft.rihua.api.domain.Ticket;
import jp.co.protosoft.rihua.api.domain.Event;
import jp.co.protosoft.rihua.api.domain.User;
import jp.co.protosoft.rihua.api.domain.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * チケットリポジトリ
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    /**
     * IDでチケットを検索（削除済み除外）
     * 
     * @param id チケットID
     * @return チケット
     */
    Optional<Ticket> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * QRコードでチケットを検索（削除済み除外）
     * 
     * @param qrCode QRコード
     * @return チケット
     */
    Optional<Ticket> findByQrCodeAndDeletedAtIsNull(String qrCode);

    /**
     * 購入者でチケットを検索（削除済み除外）
     * 
     * @param purchaser 購入者
     * @param pageable ページング情報
     * @return チケットページ
     */
    Page<Ticket> findByPurchaserAndDeletedAtIsNull(User purchaser, Pageable pageable);

    /**
     * イベントでチケットを検索（削除済み除外）
     * 
     * @param event イベント
     * @param pageable ページング情報
     * @return チケットページ
     */
    Page<Ticket> findByEventAndDeletedAtIsNull(Event event, Pageable pageable);

    /**
     * ステータスでチケットを検索（削除済み除外）
     * 
     * @param status ステータス
     * @param pageable ページング情報
     * @return チケットページ
     */
    Page<Ticket> findByStatusAndDeletedAtIsNull(TicketStatus status, Pageable pageable);

    /**
     * 購入者とイベントでチケットを検索（削除済み除外）
     * 
     * @param purchaser 購入者
     * @param event イベント
     * @param pageable ページング情報
     * @return チケットページ
     */
    Page<Ticket> findByPurchaserAndEventAndDeletedAtIsNull(User purchaser, Event event, Pageable pageable);

    /**
     * 有効なチケットを検索（削除済み除外）
     * 
     * @param pageable ページング情報
     * @return チケットページ
     */
    @Query("SELECT t FROM Ticket t WHERE t.status = 'VALID' AND t.deletedAt IS NULL ORDER BY t.createdAt DESC")
    Page<Ticket> findValidTickets(Pageable pageable);

    /**
     * 使用済みチケットを検索（削除済み除外）
     * 
     * @param pageable ページング情報
     * @return チケットページ
     */
    @Query("SELECT t FROM Ticket t WHERE t.status = 'USED' AND t.deletedAt IS NULL ORDER BY t.usedAt DESC")
    Page<Ticket> findUsedTickets(Pageable pageable);

    /**
     * キャンセル済みチケットを検索（削除済み除外）
     * 
     * @param pageable ページング情報
     * @return チケットページ
     */
    @Query("SELECT t FROM Ticket t WHERE t.status = 'CANCELLED' AND t.deletedAt IS NULL ORDER BY t.updatedAt DESC")
    Page<Ticket> findCancelledTickets(Pageable pageable);

    /**
     * 期限切れチケットを検索
     * 
     * @return 期限切れチケットリスト
     */
    @Query("SELECT t FROM Ticket t WHERE t.event.endTime < CURRENT_TIMESTAMP AND t.status = 'VALID' AND t.deletedAt IS NULL")
    List<Ticket> findExpiredTickets();

    /**
     * イベントの販売済みチケット数を取得
     * 
     * @param event イベント
     * @return 販売済みチケット数
     */
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.event = :event AND t.status IN ('VALID', 'USED') AND t.deletedAt IS NULL")
    long countSoldTicketsByEvent(@Param("event") Event event);

    /**
     * イベントの有効チケット数を取得
     * 
     * @param event イベント
     * @return 有効チケット数
     */
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.event = :event AND t.status = 'VALID' AND t.deletedAt IS NULL")
    long countValidTicketsByEvent(@Param("event") Event event);

    /**
     * イベントの使用済みチケット数を取得
     * 
     * @param event イベント
     * @return 使用済みチケット数
     */
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.event = :event AND t.status = 'USED' AND t.deletedAt IS NULL")
    long countUsedTicketsByEvent(@Param("event") Event event);

    /**
     * 購入者のチケット数を取得
     * 
     * @param purchaser 購入者
     * @return チケット数
     */
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.purchaser = :purchaser AND t.status IN ('VALID', 'USED') AND t.deletedAt IS NULL")
    long countTicketsByPurchaser(@Param("purchaser") User purchaser);

    /**
     * 指定期間内に購入されたチケットを検索
     * 
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @param pageable ページング情報
     * @return チケットページ
     */
    @Query("SELECT t FROM Ticket t WHERE t.createdAt BETWEEN :startDate AND :endDate AND t.deletedAt IS NULL")
    Page<Ticket> findByPurchaseDateBetweenAndDeletedAtIsNull(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 指定期間内に使用されたチケットを検索
     * 
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @param pageable ページング情報
     * @return チケットページ
     */
    @Query("SELECT t FROM Ticket t WHERE t.usedAt BETWEEN :startDate AND :endDate AND t.status = 'USED' AND t.deletedAt IS NULL")
    Page<Ticket> findByUsedDateBetweenAndStatusAndDeletedAtIsNull(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 価格範囲でチケットを検索
     * 
     * @param minPrice 最低価格
     * @param maxPrice 最高価格
     * @param pageable ページング情報
     * @return チケットページ
     */
    @Query("SELECT t FROM Ticket t WHERE t.price BETWEEN :minPrice AND :maxPrice AND t.deletedAt IS NULL")
    Page<Ticket> findByPriceBetweenAndDeletedAtIsNull(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    /**
     * 複合条件でチケットを検索
     * 
     * @param purchaser 購入者（null可）
     * @param event イベント（null可）
     * @param status ステータス（null可）
     * @param startDate 開始日時（null可）
     * @param endDate 終了日時（null可）
     * @param pageable ページング情報
     * @return チケットページ
     */
    @Query("SELECT t FROM Ticket t WHERE " +
           "(:purchaser IS NULL OR t.purchaser = :purchaser) AND " +
           "(:event IS NULL OR t.event = :event) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:startDate IS NULL OR t.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR t.createdAt <= :endDate) AND " +
           "t.deletedAt IS NULL " +
           "ORDER BY t.createdAt DESC")
    Page<Ticket> findByComplexCriteria(
            @Param("purchaser") User purchaser,
            @Param("event") Event event,
            @Param("status") TicketStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 統計: 月別チケット販売数
     * 
     * @param year 年
     * @return 月別チケット販売数
     */
    @Query("SELECT MONTH(t.createdAt), COUNT(t) FROM Ticket t WHERE YEAR(t.createdAt) = :year AND t.status IN ('VALID', 'USED') AND t.deletedAt IS NULL GROUP BY MONTH(t.createdAt)")
    List<Object[]> countTicketSalesByMonth(@Param("year") int year);

    /**
     * 統計: 月別売上
     * 
     * @param year 年
     * @return 月別売上
     */
    @Query("SELECT MONTH(t.createdAt), SUM(t.price) FROM Ticket t WHERE YEAR(t.createdAt) = :year AND t.status IN ('VALID', 'USED') AND t.deletedAt IS NULL GROUP BY MONTH(t.createdAt)")
    List<Object[]> sumRevenueByMonth(@Param("year") int year);

    /**
     * 統計: イベント別チケット販売数
     * 
     * @return イベント別チケット販売数
     */
    @Query("SELECT t.event, COUNT(t) FROM Ticket t WHERE t.status IN ('VALID', 'USED') AND t.deletedAt IS NULL GROUP BY t.event ORDER BY COUNT(t) DESC")
    List<Object[]> countTicketSalesByEvent();

    /**
     * 最近購入されたチケットを検索
     * 
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return チケットページ
     */
    @Query("SELECT t FROM Ticket t WHERE t.createdAt >= :sinceDate AND t.deletedAt IS NULL ORDER BY t.createdAt DESC")
    Page<Ticket> findRecentTickets(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);

    /**
     * 今日使用されたチケットを検索
     * 
     * @param startOfDay 今日の開始時刻
     * @param endOfDay 今日の終了時刻
     * @param pageable ページング情報
     * @return チケットページ
     */
    @Query("SELECT t FROM Ticket t WHERE t.usedAt BETWEEN :startOfDay AND :endOfDay AND t.status = 'USED' AND t.deletedAt IS NULL ORDER BY t.usedAt DESC")
    Page<Ticket> findTodayUsedTickets(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable);
}