package com.rihua.api.domain;

import com.rihua.api.domain.enums.HousingStatus;
import com.rihua.api.domain.enums.HousingType;
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
 * 住居エンティティ
 * 
 * <p>住居情報を管理します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "housing")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Housing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank
    @Size(max = 5000)
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private HousingType type;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotBlank
    @Size(max = 200)
    @Column(name = "prefecture", nullable = false, length = 200)
    private String prefecture;

    @NotBlank
    @Size(max = 200)
    @Column(name = "city", nullable = false, length = 200)
    private String city;

    @NotBlank
    @Size(max = 500)
    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Min(0)
    @Column(name = "rooms")
    private Integer rooms;

    @Min(0)
    @Column(name = "bathrooms")
    private Integer bathrooms;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "area", precision = 8, scale = 2)
    private BigDecimal area;

    @Min(1)
    @Max(20)
    @Column(name = "floor")
    private Integer floor;

    @Min(1)
    @Max(50)
    @Column(name = "total_floors")
    private Integer totalFloors;

    @Column(name = "foreigner_friendly", nullable = false)
    @Builder.Default
    private Boolean foreignerFriendly = false;

    @Column(name = "pet_allowed", nullable = false)
    @Builder.Default
    private Boolean petAllowed = false;

    @Column(name = "furnished", nullable = false)
    @Builder.Default
    private Boolean furnished = false;

    @Column(name = "parking_available", nullable = false)
    @Builder.Default
    private Boolean parkingAvailable = false;

    @ElementCollection
    @CollectionTable(name = "housing_images", joinColumns = @JoinColumn(name = "housing_id"))
    @Column(name = "image_url", length = 1000)
    private List<String> imageUrls;

    @Size(max = 50)
    @Column(name = "nearest_station", length = 50)
    private String nearestStation;

    @Min(0)
    @Column(name = "walk_minutes_to_station")
    private Integer walkMinutesToStation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private HousingStatus status = HousingStatus.DRAFT;

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
        this.status = HousingStatus.DELETED;
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
     * 問い合わせ可能かどうかを確認
     * 
     * @return 問い合わせ可能な場合true
     */
    public boolean canInquire() {
        return status.canInquire() && !isDeleted() && !isExpired();
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
     * 賃貸物件かどうかを確認
     * 
     * @return 賃貸物件の場合true
     */
    public boolean isRental() {
        return type.isRental();
    }

    /**
     * 売買物件かどうかを確認
     * 
     * @return 売買物件の場合true
     */
    public boolean isForSale() {
        return type == HousingType.SALE;
    }

    /**
     * 外国人対応可能かどうかを確認
     * 
     * @return 外国人対応可能な場合true
     */
    public boolean isForeignerFriendly() {
        return Boolean.TRUE.equals(foreignerFriendly);
    }

    /**
     * ペット可かどうかを確認
     * 
     * @return ペット可の場合true
     */
    public boolean isPetAllowed() {
        return Boolean.TRUE.equals(petAllowed);
    }

    /**
     * 家具付きかどうかを確認
     * 
     * @return 家具付きの場合true
     */
    public boolean isFurnished() {
        return Boolean.TRUE.equals(furnished);
    }

    /**
     * 駐車場ありかどうかを確認
     * 
     * @return 駐車場ありの場合true
     */
    public boolean hasParkingAvailable() {
        return Boolean.TRUE.equals(parkingAvailable);
    }

    /**
     * 完全な住所を取得
     * 
     * @return 完全な住所
     */
    public String getFullAddress() {
        return prefecture + city + address;
    }

    /**
     * 最寄り駅情報を取得
     * 
     * @return 最寄り駅情報
     */
    public String getStationInfo() {
        if (nearestStation == null) {
            return null;
        }
        
        if (walkMinutesToStation != null) {
            return nearestStation + " 徒歩" + walkMinutesToStation + "分";
        }
        
        return nearestStation;
    }

    /**
     * 期限切れ処理
     */
    public void expire() {
        if (this.status == HousingStatus.AVAILABLE) {
            this.status = HousingStatus.EXPIRED;
        }
    }

    /**
     * 成約処理
     */
    public void markAsRentedOrSold() {
        if (this.status == HousingStatus.AVAILABLE || this.status == HousingStatus.UNDER_NEGOTIATION) {
            this.status = HousingStatus.RENTED_SOLD;
        }
    }
}