package jp.co.protosoft.rihua.api.repository;

import jp.co.protosoft.rihua.api.domain.Job;
import jp.co.protosoft.rihua.api.domain.User;
import jp.co.protosoft.rihua.api.domain.enums.JobType;
import jp.co.protosoft.rihua.api.domain.enums.JobStatus;
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
 * 求人リポジトリ
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

    /**
     * IDで求人を検索（削除済み除外）
     * 
     * @param id 求人ID
     * @return 求人
     */
    Optional<Job> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * 公開されている求人を検索
     * 
     * @param pageable ページング情報
     * @return 求人ページ
     */
    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' AND j.deletedAt IS NULL ORDER BY j.createdAt DESC")
    Page<Job> findActiveJobs(Pageable pageable);

    /**
     * タイプで求人を検索（削除済み除外）
     * 
     * @param type 求人タイプ
     * @param pageable ページング情報
     * @return 求人ページ
     */
    Page<Job> findByTypeAndDeletedAtIsNull(JobType type, Pageable pageable);

    /**
     * ステータスで求人を検索（削除済み除外）
     * 
     * @param status ステータス
     * @param pageable ページング情報
     * @return 求人ページ
     */
    Page<Job> findByStatusAndDeletedAtIsNull(JobStatus status, Pageable pageable);

    /**
     * 投稿者で求人を検索（削除済み除外）
     * 
     * @param employer 投稿者
     * @param pageable ページング情報
     * @return 求人ページ
     */
    Page<Job> findByEmployerAndDeletedAtIsNull(User employer, Pageable pageable);

    /**
     * 給与範囲で求人を検索
     * 
     * @param minSalary 最低給与
     * @param maxSalary 最高給与
     * @param pageable ページング情報
     * @return 求人ページ
     */
    @Query("SELECT j FROM Job j WHERE j.salaryMin >= :minSalary AND j.salaryMax <= :maxSalary AND j.status = 'ACTIVE' AND j.deletedAt IS NULL")
    Page<Job> findBySalaryRangeAndStatusAndDeletedAtIsNull(
            @Param("minSalary") BigDecimal minSalary,
            @Param("maxSalary") BigDecimal maxSalary,
            Pageable pageable);

    /**
     * 地域で求人を検索
     * 
     * @param location 地域キーワード
     * @param pageable ページング情報
     * @return 求人ページ
     */
    @Query("SELECT j FROM Job j WHERE " +
           "(LOWER(j.prefecture) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(j.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "j.status = 'ACTIVE' AND j.deletedAt IS NULL")
    Page<Job> findByLocationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(
            @Param("location") String location,
            Pageable pageable);

    /**
     * ビザサポート可能な求人を検索
     * 
     * @param pageable ページング情報
     * @return 求人ページ
     */
    @Query("SELECT j FROM Job j WHERE j.visaSupport = true AND j.status = 'ACTIVE' AND j.deletedAt IS NULL")
    Page<Job> findVisaSupportJobs(Pageable pageable);

    /**
     * リモートワーク可能な求人を検索
     * 
     * @param pageable ページング情報
     * @return 求人ページ
     */
    @Query("SELECT j FROM Job j WHERE j.remoteWorkAvailable = true AND j.status = 'ACTIVE' AND j.deletedAt IS NULL")
    Page<Job> findRemoteWorkJobs(Pageable pageable);

    /**
     * 経験年数で求人を検索
     * 
     * @param maxExperience 最大必要経験年数
     * @param pageable ページング情報
     * @return 求人ページ
     */
    @Query("SELECT j FROM Job j WHERE (j.experienceRequired IS NULL OR j.experienceRequired <= :maxExperience) AND j.status = 'ACTIVE' AND j.deletedAt IS NULL")
    Page<Job> findByExperienceRequiredLessThanEqualAndStatusAndDeletedAtIsNull(
            @Param("maxExperience") Integer maxExperience,
            Pageable pageable);

    /**
     * キーワードで求人を検索
     * 
     * @param keyword 検索キーワード
     * @param pageable ページング情報
     * @return 求人ページ
     */
    @Query("SELECT j FROM Job j WHERE " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.requirements) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "j.status = 'ACTIVE' AND j.deletedAt IS NULL")
    Page<Job> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 複合条件で求人を検索
     * 
     * @param type 求人タイプ（null可）
     * @param prefecture 都道府県（null可）
     * @param city 市区町村（null可）
     * @param minSalary 最低給与（null可）
     * @param maxSalary 最高給与（null可）
     * @param maxExperience 最大必要経験年数（null可）
     * @param visaSupport ビザサポート（null可）
     * @param remoteWork リモートワーク（null可）
     * @param pageable ページング情報
     * @return 求人ページ
     */
    @Query("SELECT j FROM Job j WHERE " +
           "(:type IS NULL OR j.type = :type) AND " +
           "(:prefecture IS NULL OR LOWER(j.prefecture) LIKE LOWER(CONCAT('%', :prefecture, '%'))) AND " +
           "(:city IS NULL OR LOWER(j.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:minSalary IS NULL OR j.salaryMin >= :minSalary) AND " +
           "(:maxSalary IS NULL OR j.salaryMax <= :maxSalary) AND " +
           "(:maxExperience IS NULL OR j.experienceRequired IS NULL OR j.experienceRequired <= :maxExperience) AND " +
           "(:visaSupport IS NULL OR j.visaSupport = :visaSupport) AND " +
           "(:remoteWork IS NULL OR j.remoteWorkAvailable = :remoteWork) AND " +
           "j.status = 'ACTIVE' AND j.deletedAt IS NULL " +
           "ORDER BY j.createdAt DESC")
    Page<Job> findByComplexCriteria(
            @Param("type") JobType type,
            @Param("prefecture") String prefecture,
            @Param("city") String city,
            @Param("minSalary") BigDecimal minSalary,
            @Param("maxSalary") BigDecimal maxSalary,
            @Param("maxExperience") Integer maxExperience,
            @Param("visaSupport") Boolean visaSupport,
            @Param("remoteWork") Boolean remoteWork,
            Pageable pageable);

    /**
     * 人気求人を検索（閲覧数順）
     * 
     * @param pageable ページング情報
     * @return 求人ページ
     */
    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' AND j.deletedAt IS NULL ORDER BY j.viewCount DESC")
    Page<Job> findPopularJobs(Pageable pageable);

    /**
     * 最近投稿された求人を検索
     * 
     * @param sinceDate 基準日時
     * @param pageable ページング情報
     * @return 求人ページ
     */
    @Query("SELECT j FROM Job j WHERE j.createdAt >= :sinceDate AND j.status = 'ACTIVE' AND j.deletedAt IS NULL ORDER BY j.createdAt DESC")
    Page<Job> findRecentJobs(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);

    /**
     * 期限切れ求人を検索
     * 
     * @return 期限切れ求人リスト
     */
    @Query("SELECT j FROM Job j WHERE j.applicationDeadline < CURRENT_TIMESTAMP AND j.status = 'ACTIVE' AND j.deletedAt IS NULL")
    List<Job> findExpiredJobs();

    /**
     * 統計: タイプ別求人数
     * 
     * @return タイプ別求人数
     */
    @Query("SELECT j.type, COUNT(j) FROM Job j WHERE j.status = 'ACTIVE' AND j.deletedAt IS NULL GROUP BY j.type")
    List<Object[]> countJobsByType();

    /**
     * 統計: 都道府県別求人数
     * 
     * @return 都道府県別求人数
     */
    @Query("SELECT j.prefecture, COUNT(j) FROM Job j WHERE j.status = 'ACTIVE' AND j.deletedAt IS NULL GROUP BY j.prefecture ORDER BY COUNT(j) DESC")
    List<Object[]> countJobsByPrefecture();

    /**
     * 投稿者の公開求人数を取得
     * 
     * @param employer 投稿者
     * @return 公開求人数
     */
    @Query("SELECT COUNT(j) FROM Job j WHERE j.employer = :employer AND j.status = 'ACTIVE' AND j.deletedAt IS NULL")
    long countActiveJobsByEmployer(@Param("employer") User employer);

    /**
     * 高給与求人を検索
     * 
     * @param minSalary 最低給与
     * @param pageable ページング情報
     * @return 求人ページ
     */
    @Query("SELECT j FROM Job j WHERE j.salaryMax >= :minSalary AND j.status = 'ACTIVE' AND j.deletedAt IS NULL ORDER BY j.salaryMax DESC")
    Page<Job> findHighSalaryJobs(@Param("minSalary") BigDecimal minSalary, Pageable pageable);
}