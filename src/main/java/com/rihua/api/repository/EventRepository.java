package com.rihua.api.repository;

import com.rihua.api.domain.Event;
import com.rihua.api.domain.User;
import com.rihua.api.domain.enums.EventCategory;
import com.rihua.api.domain.enums.EventStatus;
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
 * イベントリポジトリ
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    /**
     * IDでイベントを検索（削除済み除外）
     * 
     * @param id イベントID
     * @return イベント
     */
    Optional<Event> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * 公開されているイベントを検索
     * 
     * @param pageable ページング情報
     * @return イベントページ
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.deletedAt IS NULL ORDER BY e.startTime ASC")
    Page<Event> findPublishedEvents(Pageable pageable);

    /**
     * カテゴリでイベントを検索（削除済み除外）
     * 
     * @param category カテゴリ
     * @param pageable ページング情報
     * @return イベントページ
     */
    Page<Event> findByCategoryAndDeletedAtIsNull(EventCategory category, Pageable pageable);

    /**
     * ステータスでイベントを検索（削除済み除外）
     * 
     * @param status ステータス
     * @param pageable ページング情報
     * @return イベントページ
     */
    Page<Event> findByStatusAndDeletedAtIsNull(EventStatus status, Pageable pageable);

    /**
     * 主催者でイベントを検索（削除済み除外）
     * 
     * @param organizer 主催者
     * @param pageable ページング情報
     * @return イベントページ
     */
    Page<Event> findByOrganizerAndDeletedAtIsNull(User organizer, Pageable pageable);

    /**
     * 指定期間内のイベントを検索
     * 
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @param pageable ページング情報
     * @return イベントページ
     */
    @Query("SELECT e FROM Event e WHERE e.startTime BETWEEN :startDate AND :endDate AND e.deletedAt IS NULL")
    Page<Event> findByStartTimeBetweenAndDeletedAtIsNull(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 今後開催予定のイベントを検索
     * 
     * @param pageable ページング情報
     * @return イベントページ
     */
    @Query("SELECT e FROM Event e WHERE e.startTime > CURRENT_TIMESTAMP AND e.status = 'PUBLISHED' AND e.deletedAt IS NULL ORDER BY e.startTime ASC")
    Page<Event> findUpcomingEvents(Pageable pageable);

    /**
     * 無料イベントを検索
     * 
     * @param pageable ページング情報
     * @return イベントページ
     */
    @Query("SELECT e FROM Event e WHERE (e.price IS NULL OR e.price = 0) AND e.status = 'PUBLISHED' AND e.deletedAt IS NULL")
    Page<Event> findFreeEvents(Pageable pageable);

    /**
     * 価格範囲でイベントを検索
     * 
     * @param minPrice 最低価格
     * @param maxPrice 最高価格
     * @param pageable ページング情報
     * @return イベントページ
     */
    @Query("SELECT e FROM Event e WHERE e.price BETWEEN :minPrice AND :maxPrice AND e.status = 'PUBLISHED' AND e.deletedAt IS NULL")
    Page<Event> findByPriceBetweenAndStatusAndDeletedAtIsNull(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    /**
     * 地域でイベントを検索
     * 
     * @param location 地域キーワード
     * @param pageable ページング情報
     * @return イベントページ
     */
    @Query("SELECT e FROM Event e WHERE LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%')) AND e.status = 'PUBLISHED' AND e.deletedAt IS NULL")
    Page<Event> findByLocationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(
            @Param("location") String location,
            Pageable pageable);

    /**
     * キーワードでイベントを検索
     * 
     * @param keyword 検索キーワード
     * @param pageable ページング情報
     * @return イベントページ
     */
    @Query("SELECT e FROM Event e WHERE " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "e.status = 'PUBLISHED' AND e.deletedAt IS NULL")
    Page<Event> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 複合条件でイベントを検索
     * 
     * @param category カテゴリ（null可）
     * @param location 地域（null可）
     * @param minPrice 最低価格（null可）
     * @param maxPrice 最高価格（null可）
     * @param startDate 開始日時（null可）
     * @param endDate 終了日時（null可）
     * @param pageable ページング情報
     * @return イベントページ
     */
    @Query("SELECT e FROM Event e WHERE " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:minPrice IS NULL OR e.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR e.price <= :maxPrice) AND " +
           "(:startDate IS NULL OR e.startTime >= :startDate) AND " +
           "(:endDate IS NULL OR e.startTime <= :endDate) AND " +
           "e.status = 'PUBLISHED' AND e.deletedAt IS NULL " +
           "ORDER BY e.startTime ASC")
    Page<Event> findByComplexCriteria(
            @Param("category") EventCategory category,
            @Param("location") String location,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 人気イベントを検索（チケット販売数順）
     * 
     * @param pageable ページング情報
     * @return イベントページ
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.deletedAt IS NULL ORDER BY e.soldTickets DESC")
    Page<Event> findPopularEvents(Pageable pageable);

    /**
     * 満席に近いイベントを検索
     * 
     * @param pageable ページング情報
     * @return イベントページ
     */
    @Query("SELECT e FROM Event e WHERE e.capacity IS NOT NULL AND e.soldTickets > 0 AND " +
           "(CAST(e.soldTickets AS double) / CAST(e.capacity AS double)) >= 0.8 AND " +
           "e.status = 'PUBLISHED' AND e.deletedAt IS NULL ORDER BY (CAST(e.soldTickets AS double) / CAST(e.capacity AS double)) DESC")
    Page<Event> findNearlyFullEvents(Pageable pageable);

    /**
     * 期限切れイベントを検索
     * 
     * @return 期限切れイベントリスト
     */
    @Query("SELECT e FROM Event e WHERE e.endTime < CURRENT_TIMESTAMP AND e.status != 'COMPLETED' AND e.deletedAt IS NULL")
    List<Event> findExpiredEvents();

    /**
     * 統計: カテゴリ別イベント数
     * 
     * @return カテゴリ別イベント数
     */
    @Query("SELECT e.category, COUNT(e) FROM Event e WHERE e.status = 'PUBLISHED' AND e.deletedAt IS NULL GROUP BY e.category")
    List<Object[]> countEventsByCategory();

    /**
     * 統計: 月別イベント数
     * 
     * @param year 年
     * @return 月別イベント数
     */
    @Query("SELECT MONTH(e.startTime), COUNT(e) FROM Event e WHERE YEAR(e.startTime) = :year AND e.deletedAt IS NULL GROUP BY MONTH(e.startTime)")
    List<Object[]> countEventsByMonth(@Param("year") int year);

    /**
     * 主催者の公開イベント数を取得
     * 
     * @param organizer 主催者
     * @return 公開イベント数
     */
    @Query("SELECT COUNT(e) FROM Event e WHERE e.organizer = :organizer AND e.status = 'PUBLISHED' AND e.deletedAt IS NULL")
    long countPublishedEventsByOrganizer(@Param("organizer") User organizer);
}