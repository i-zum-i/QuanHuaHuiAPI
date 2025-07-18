package jp.co.protosoft.rihua.api.domain;

import jp.co.protosoft.rihua.api.domain.enums.TicketStatus;
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
 * チケットエンティティ
 * 
 * <p>イベントチケット情報を管理します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchaser_id", nullable = false)
    private User purchaser;

    @NotBlank
    @Size(max = 100)
    @Column(name = "ticket_code", nullable = false, unique = true, length = 100)
    private String ticketCode;

    @NotBlank
    @Size(max = 100)
    @Column(name = "purchaser_name", nullable = false, length = 100)
    private String purchaserName;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "purchaser_email", nullable = false, length = 100)
    private String purchaserEmail;

    @Size(max = 20)
    @Column(name = "purchaser_phone", length = 20)
    private String purchaserPhone;

    @NotNull
    @Min(1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Size(max = 100)
    @Column(name = "payment_intent_id", length = 100)
    private String paymentIntentId;

    @Size(max = 50)
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_completed_at")
    private LocalDateTime paymentCompletedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private TicketStatus status = TicketStatus.PENDING_PAYMENT;

    @Size(max = 1000)
    @Column(name = "qr_code_url", length = 1000)
    private String qrCodeUrl;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 有効なチケットかどうかを確認
     * 
     * @return 有効な場合true
     */
    public boolean isValid() {
        return status.isValid() && !isExpired();
    }

    /**
     * 使用可能なチケットかどうかを確認
     * 
     * @return 使用可能な場合true
     */
    public boolean canUse() {
        return status.canUse() && !isExpired() && !isUsed();
    }

    /**
     * キャンセル可能なチケットかどうかを確認
     * 
     * @return キャンセル可能な場合true
     */
    public boolean canCancel() {
        return status.canCancel() && !isExpired() && !isUsed();
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
     * 使用済みかどうかを確認
     * 
     * @return 使用済みの場合true
     */
    public boolean isUsed() {
        return status == TicketStatus.USED;
    }

    /**
     * 決済完了済みかどうかを確認
     * 
     * @return 決済完了済みの場合true
     */
    public boolean isPaid() {
        return paymentCompletedAt != null && status == TicketStatus.CONFIRMED;
    }

    /**
     * 決済完了処理
     */
    public void confirmPayment() {
        this.status = TicketStatus.CONFIRMED;
        this.paymentCompletedAt = LocalDateTime.now();
    }

    /**
     * チケット使用処理
     */
    public void use() {
        if (!canUse()) {
            throw new IllegalStateException("このチケットは使用できません");
        }
        this.status = TicketStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    /**
     * チケットキャンセル処理
     */
    public void cancel() {
        if (!canCancel()) {
            throw new IllegalStateException("このチケットはキャンセルできません");
        }
        this.status = TicketStatus.CANCELLED;
    }

    /**
     * チケット返金処理
     */
    public void refund() {
        if (this.status != TicketStatus.CONFIRMED && this.status != TicketStatus.CANCELLED) {
            throw new IllegalStateException("このチケットは返金できません");
        }
        this.status = TicketStatus.REFUNDED;
    }

    /**
     * チケット期限切れ処理
     */
    public void expire() {
        if (this.status == TicketStatus.PENDING_PAYMENT || this.status == TicketStatus.CONFIRMED) {
            this.status = TicketStatus.EXPIRED;
        }
    }

    /**
     * QRコードURLを設定
     * 
     * @param qrCodeUrl QRコードURL
     */
    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    /**
     * 合計金額を計算して設定
     */
    public void calculateTotalAmount() {
        if (unitPrice != null && quantity != null) {
            this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * チケットの表示名を取得
     * 
     * @return チケット表示名
     */
    public String getDisplayName() {
        if (event != null) {
            return event.getTitle() + " - " + quantity + "枚";
        }
        return "チケット - " + quantity + "枚";
    }
}