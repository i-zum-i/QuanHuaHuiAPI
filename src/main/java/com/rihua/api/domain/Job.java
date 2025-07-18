package com.rihua.api.domain;

import com.rihua.api.domain.enums.JobStatus;
import com.rihua.api.domain.enums.JobType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 求人エンティティ
 * 
 * <p>求人情報を管理します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank
    @Size(max = 5000)
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @Size(max = 200)
    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private JobType type;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "salary_min", precision = 10, scale = 2)
    private BigDecimal salaryMin;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "salary_max", precision = 10, scale = 2)
    private BigDecimal salaryMax;

    @Size(max = 10)
    @Column(name = "salary_type", length = 10)
    @Builder.Default
    private String salaryType = "monthly"; // monthly, yearly, hourly

    @NotBlank
    @Size(max = 200)
    @Column(name = "prefecture", nullable = false, length = 200)
    private String prefecture;

    @NotBlank
    @Size(max = 200)
    @Column(name = "city", nullable = false, length = 200)
    private String city;

    @Size(max = 500)
    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "remote_work_available", nullable = false)
    @Builder.Default
    private Boolean remoteWorkAvailable = false;

    @Column(name = "visa_support", nullable = false)
    @Builder.Default
    private Boolean visaSupport = false;

    @Column(name = "japanese_required", nullable = false)
    @Builder.Default
    private Boolean japaneseRequired = false;

    @Size(max = 50)
    @Column(name = "japanese_level", length = 50)
    private String japaneseLevel; // N1, N2, N3, N4, N5, Native, Conversational

    @Column(name = "chinese_preferred", nullable = false)
    @Builder.Default
    private Boolean chinesePreferred = false;

    @ElementCollection
    @CollectionTable(name = "job_requirements", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "requirement", length = 500)
    private List<String> requirements;

    @ElementCollection
    @CollectionTable(name = "job_benefits", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "benefit", length = 500)
    private List<String> benefits;

    @ElementCollection
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill", length = 100)
    private List<String> requiredSkills;

    @Size(max = 100)
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Size(max = 20)
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "application_deadline")
    private LocalDateTime applicationDeadline;

    @Min(0)
    @Column(name = "application_count", nullable = false)
    @Builder.Default
    private Integer applicationCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private JobStatus status = JobStatus.DRAFT;

    @Size(max = 10)
    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "zh-CN";

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * ソフトデリート実行
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.status = JobStatus.DELETED;
    }

    /**
     * 削除済みかどうかを確認
     * 
     * @return 削除済みの場合true
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * 公開されているかどうかを確認
     * 
     * @return 公開されている場合true
     */
    public boolean isPublic() {
        return status.isPublic() && !isDeleted() && !isExpired();
    }

    /**
     * 応募可能かどうかを確認
     * 
     * @return 応募可能な場合true
     */
    public boolean canApply() {
        return status.canApply() && !isDeleted() && !isExpired() && !isApplicationDeadlinePassed();
    }

    /**
     * 期限切れかどうかを確認
     * 
     * @return 期限切れの場合true
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * 応募締切が過ぎているかどうかを確認
     * 
     * @return 応募締切が過ぎている場合true
     */
    public boolean isApplicationDeadlinePassed() {
        return applicationDeadline != null && LocalDateTime.now().isAfter(applicationDeadline);
    }

    /**
     * ビザサポートありかどうかを確認
     * 
     * @return ビザサポートありの場合true
     */
    public boolean hasVisaSupport() {
        return Boolean.TRUE.equals(visaSupport);
    }

    /**
     * 日本語必須かどうかを確認
     * 
     * @return 日本語必須の場合true
     */
    public boolean isJapaneseRequired() {
        return Boolean.TRUE.equals(japaneseRequired);
    }

    /**
     * 中国語優遇かどうかを確認
     * 
     * @return 中国語優遇の場合true
     */
    public boolean isChinesePreferred() {
        return Boolean.TRUE.equals(chinesePreferred);
    }

    /**
     * リモートワーク可能かどうかを確認
     * 
     * @return リモートワーク可能な場合true
     */
    public boolean isRemoteWorkAvailable() {
        return Boolean.TRUE.equals(remoteWorkAvailable);
    }

    /**
     * 正社員かどうかを確認
     * 
     * @return 正社員の場合true
     */
    public boolean isFullTime() {
        return type == JobType.FULL_TIME;
    }

    /**
     * 学生向けかどうかを確認
     * 
     * @return 学生向けの場合true
     */
    public boolean isStudentFriendly() {
        return type.isStudentFriendly();
    }

    /**
     * 給与範囲を取得
     * 
     * @return 給与範囲の文字列
     */
    public String getSalaryRange() {
        if (salaryMin == null && salaryMax == null) {
            return "応相談";
        }
        
        String unit = getSalaryUnit();
        
        if (salaryMin != null && salaryMax != null) {
            return String.format("¥%,d - ¥%,d %s", salaryMin.intValue(), salaryMax.intValue(), unit);
        } else if (salaryMin != null) {
            return String.format("¥%,d以上 %s", salaryMin.intValue(), unit);
        } else {
            return String.format("¥%,d以下 %s", salaryMax.intValue(), unit);
        }
    }

    /**
     * 給与単位を取得
     * 
     * @return 給与単位
     */
    private String getSalaryUnit() {
        return switch (salaryType) {
            case "yearly" -> "年収";
            case "hourly" -> "時給";
            default -> "月給";
        };
    }

    /**
     * 完全な勤務地を取得
     * 
     * @return 完全な勤務地
     */
    public String getFullLocation() {
        if (isRemoteWorkAvailable()) {
            return "リモートワーク可";
        }
        
        if (address != null && !address.trim().isEmpty()) {
            return prefecture + city + address;
        }
        
        return prefecture + city;
    }

    /**
     * 応募処理
     */
    public void incrementApplicationCount() {
        this.applicationCount++;
    }

    /**
     * 期限切れ処理
     */
    public void expire() {
        if (this.status == JobStatus.ACTIVE) {
            this.status = JobStatus.EXPIRED;
        }
    }

    /**
     * 募集終了処理
     */
    public void markAsFilled() {
        if (this.status == JobStatus.ACTIVE || this.status == JobStatus.PAUSED) {
            this.status = JobStatus.FILLED;
        }
    }
}