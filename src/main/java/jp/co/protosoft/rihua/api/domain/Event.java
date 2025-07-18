package jp.co.protosoft.rihua.api.domain;

import jp.co.protosoft.rihua.api.domain.enums.EventCategory;
import jp.co.protosoft.rihua.api.domain.enums.EventStatus;
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
import java.util.UUID;

/**
 * イベントエンティティ
 * 
 * <p>イベント情報を管理します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank
    @Size(max = 5000)
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    @Builder.Default
    private EventCategory category = EventCategory.OTHER;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @NotBlank
    @Size(max = 500)
    @Column(name = "location", nullable = false, length = 500)
    private String location;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "price", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Min(1)
    @Column(name = "capacity")
    private Integer capacity;

    @Min(0)
    @Column(name = "sold_tickets", nullable = false)
    @Builder.Default
    private Integer soldTickets = 0;

    @Size(max = 1000)
    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private EventStatus status = EventStatus.DRAFT;

    @Size(max = 10)
    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "zh-CN";

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
        this.status = EventStatus.DELETED;
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
        return status.isPublic() && !isDeleted();
    }

    /**
     * チケット購入可能かどうかを確認
     * 
     * @return 購入可能な場合true
     */
    public boolean canPurchaseTickets() {
        return status.canPurchaseTickets() && !isDeleted() && !isSoldOut();
    }

    /**
     * 満席かどうかを確認
     * 
     * @return 満席の場合true
     */
    public boolean isSoldOut() {
        return capacity != null && soldTickets >= capacity;
    }

    /**
     * 残りチケット数を取得
     * 
     * @return 残りチケット数
     */
    public Integer getRemainingTickets() {
        if (capacity == null) {
            return null;
        }
        return Math.max(0, capacity - soldTickets);
    }

    /**
     * チケット販売率を取得
     * 
     * @return 販売率（0.0-1.0）
     */
    public Double getSalesRate() {
        if (capacity == null || capacity == 0) {
            return 0.0;
        }
        return (double) soldTickets / capacity;
    }

    /**
     * イベントが開始されているかどうかを確認
     * 
     * @return 開始されている場合true
     */
    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(startTime);
    }

    /**
     * イベントが終了しているかどうかを確認
     * 
     * @return 終了している場合true
     */
    public boolean hasEnded() {
        return LocalDateTime.now().isAfter(endTime);
    }

    /**
     * 無料イベントかどうかを確認
     * 
     * @return 無料の場合true
     */
    public boolean isFree() {
        return price == null || price.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * チケット購入処理
     * 
     * @param quantity 購入数量
     * @throws IllegalStateException チケット購入できない場合
     */
    public void purchaseTickets(int quantity) {
        if (!canPurchaseTickets()) {
            throw new IllegalStateException("チケットを購入できません");
        }
        
        if (capacity != null && soldTickets + quantity > capacity) {
            throw new IllegalStateException("チケットの在庫が不足しています");
        }
        
        this.soldTickets += quantity;
        
        if (isSoldOut()) {
            this.status = EventStatus.SOLD_OUT;
        }
    }
}