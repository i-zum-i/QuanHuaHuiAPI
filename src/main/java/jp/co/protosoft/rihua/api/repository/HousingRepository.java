package jp.co.protosoft.rihua.api.repository;

import jp.co.protosoft.rihua.api.domain.Housing;
import jp.co.protosoft.rihua.api.domain.User;
import jp.co.protosoft.rihua.api.domain.enums.HousingType;
import jp.co.protosoft.rihua.api.domain.enums.HousingStatus;
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
 * 住居リポジトリ
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Repository
public interface HousingRepository extends JpaRepository<Housing, UUID> {

    /**
     * IDで住居を検索（削除済み除外）
     * 
     * @param id 住居ID
     * @return 住居
     */
    Optional<Housing> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * 公開されている住居を検索
     * 
     * @param pageable ページング情報
     * @return 住居ページ
     */
    @Query("SELECT h FROM Housing h WHERE h.status = 'AVAILABLE' AND h.deletedAt IS NULL ORDER BY h.createdAt DESC")
    Page<Housing> findAvailableHousing(Pageable pageable);

    /**
     * タイプで住居を検索（削除済み除外）
     * 
     * @param type 住居タイプ
     * @param pageable ページング情報
     * @return 住居ページ
     */
    Page<Housing> findByTypeAndDeletedAtIsNull(HousingType type, Pageable pageable);

    /**
     * ステータスで住居を検索（削除済み除外）
     * 
     * @param status ステータス
     * @param pageable ページング情報
     * @return 住居ページ
     */
    Page<Housing> findByStatusAndDeletedAtIsNull(HousingStatus status, Pageable pageable);

    /**
     * 投稿者で住居を検索（削除済み除外）
     * 
     * @param owner 投稿者
     * @param pageable ページング情報
     * @return 住居ページ
     */
    Page<Housing> findByOwnerAndDeletedAtIsNull(User owner, Pageable pageable);

    /**
     * 価格範囲で住居を検索
     * 
     * @param minPrice 最低価格
     * @param maxPrice 最高価格
     * @param pageable ページング情報
     * @return 住居ページ
     */
    @Query("SELECT h FROM Housing h WHERE h.price BETWEEN :minPrice AND :maxPrice AND h.status = 'AVAILABLE' AND h.deletedAt IS NULL")
    Page<Housing> findByPriceBetweenAndStatusAndDeletedAtIsNull(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    /**
     * 地域で住居を検索
     * 
     * @param location 地域キーワード
     * @param pageable ページング情報
     * @return 住居ページ
     */
    @Query("SELECT h FROM Housing h WHERE " +
           "(LOWER(h.prefecture) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(h.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(h.address) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "h.status = 'AVAILABLE' AND h.deletedAt IS NULL")
    Page<Housing> findByLocationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(
            @Param("location") String location,
            Pageable pageable);

    /**
     * 外国人対応可能な住居を検索
     * 
     * @param pageable ページング情報
     * @return 住居ページ
     */
    @Query("SELECT h FROM Housing h WHERE h.foreignerFriendly = true AND h.status = 'AVAILABLE' AND h.deletedAt IS NULL")
    Page<Housing> findForeignerFriendlyHousing(Pageable pageable);

    /**
     * ペット可の住居を検索
     * 
     * @param pageable ページング情報
     * @return 住居ページ
     */
    @Query("SELECT h FROM Housing h WHERE h.petAllowed = true AND h.status = 'AVAILABLE' AND h.deletedAt IS NULL")
    Page<Housing> findPetAllowedHousing(Pageable pageable);

    /**
     * 部屋数で住居を検索
     * 
     * @param minRooms 最小部屋数
     * @param maxRooms 最大部屋数
     * @param pageable ページング情報
     * @return 住居ページ
     */
    @Query("SELECT h FROM Housing h WHERE h.rooms BETWEEN :minRooms AND :maxRooms AND h.status = 'AVAILABLE' AND h.deletedAt IS NULL")
    Page<Housing> findByRoomsBetweenAndStatusAndDeletedAtIsNull(
            @Param("minRooms") Integer minRooms,
            @Param("maxRooms") Integer maxRooms,
            Pageable pageable);

    /**
     * キーワードで住居を検索
     * 
     * @param keyword 検索キーワード
     * @param pageable ページング情報
     * @return 住居ページ
     */
    @Query("SELECT h FROM Housing h WHERE " +
           "(LOWER(h.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(h.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(h.prefecture) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(h.city) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "h.status = 'AVAILABLE' AND h.deletedAt IS NULL")
    Page<Housing> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 複合条件で住居を検索
     * 
     * @param type 住居タイプ（null可）
     * @param prefecture 都道府県（null可）
     * @param city 市区町村（null可）
     * @param minPrice 最低価格（null可）
     * @param maxPrice 最高価格（null可）
     * @param minRooms 最小部屋数（null可）
     * @param maxRooms 最大部屋数（null可）
     * @param foreignerFriendly 外国人対応（null可）
     * @param petAllowed ペット可（null可）
     * @param pageable ページング情報
     * @return 住居ページ
     */
    @Query("SELECT h FROM Housing h WHERE " +
           "(:type IS NULL OR h.type = :type) AND " +
           "(:prefecture IS NULL OR LOWER(h.prefecture) LIKE LOWER(CONCAT('%', :prefecture, '%'))) AND " +
           "(:city IS NULL OR LOWER(h.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:minPrice IS NULL OR h.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR h.price <= :maxPrice) AND " +
           "(:minRooms IS NULL OR h.rooms >= :minRooms) AND " +
           "(:maxRooms IS NULL OR h.rooms <= :maxRooms) AND " +
           "(:foreignerFriendly IS NULL OR h.foreignerFriendly = :foreignerFriendly) AND " +
           "(:petAllowed IS NULL OR h.petAllowed = :petAllowed) AND " +
           "h.status = 'AVAILABLE' AND h.deletedAt IS NULL " +
           "ORDER BY h.createdAt DESC")
    Page<Housing> findByComplexCriteria(
            @Param("type") HousingType type,
            @Param("prefecture") String prefecture,
            @Param("city") String city,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minRooms") Integer minRooms,
            @Param("maxRooms") Integer maxRooms,
            @Param("foreignerFriendly") Boolean foreignerFriendly,
            @Param("petAllowed") Boolean petAllowed,
            Pageable pageable);

    /**
     * 人気住居を検索（閲覧数順）
     * 
     * @param pageable ページング情報
     * @return 住居ページ
     */
    @Query("SELECT h FROM Housing h WHERE h.status = 'AVAILABLE' AND h.deletedAt IS NULL ORDER BY h.viewCount DESC")
    Page<Housing> findPopularHousing(Pageable pageable);

    /**
     * 最近投稿された住居を検索
     * 
     * @param days 日数
     * @param pageable ページング情報
     * @return 住居ページ
     */
    @Query("SELECT h FROM Housing h WHERE h.createdAt >= :sinceDate AND h.status = 'AVAILABLE' AND h.deletedAt IS NULL ORDER BY h.createdAt DESC")
    Page<Housing> findRecentHousing(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);

    /**
     * 統計: タイプ別住居数
     * 
     * @return タイプ別住居数
     */
    @Query("SELECT h.type, COUNT(h) FROM Housing h WHERE h.status = 'AVAILABLE' AND h.deletedAt IS NULL GROUP BY h.type")
    List<Object[]> countHousingByType();

    /**
     * 統計: 都道府県別住居数
     * 
     * @return 都道府県別住居数
     */
    @Query("SELECT h.prefecture, COUNT(h) FROM Housing h WHERE h.status = 'AVAILABLE' AND h.deletedAt IS NULL GROUP BY h.prefecture ORDER BY COUNT(h) DESC")
    List<Object[]> countHousingByPrefecture();

    /**
     * 投稿者の公開住居数を取得
     * 
     * @param owner 投稿者
     * @return 公開住居数
     */
    @Query("SELECT COUNT(h) FROM Housing h WHERE h.owner = :owner AND h.status = 'AVAILABLE' AND h.deletedAt IS NULL")
    long countAvailableHousingByOwner(@Param("owner") User owner);

    /**
     * 期限切れ住居を検索
     * 
     * @return 期限切れ住居リスト
     */
    @Query("SELECT h FROM Housing h WHERE h.availableUntil < CURRENT_TIMESTAMP AND h.status = 'AVAILABLE' AND h.deletedAt IS NULL")
    List<Housing> findExpiredHousing();
}