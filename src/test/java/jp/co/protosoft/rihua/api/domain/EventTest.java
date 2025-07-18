package jp.co.protosoft.rihua.api.domain;

import jp.co.protosoft.rihua.api.domain.enums.EventCategory;
import jp.co.protosoft.rihua.api.domain.enums.EventStatus;
import jp.co.protosoft.rihua.api.domain.enums.UserRole;
import jp.co.protosoft.rihua.api.domain.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Eventエンティティのテスト
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
class EventTest {

    private User organizer;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        organizer = User.builder()
            .id(UUID.randomUUID())
            .email("organizer@example.com")
            .status(UserStatus.ACTIVE)
            .roles(Set.of(UserRole.BUSINESS))
            .build();
        
        startTime = LocalDateTime.now().plusDays(7);
        endTime = startTime.plusHours(3);
    }

    @Test
    @DisplayName("イベントの基本情報が正しく設定される")
    void createEvent_ValidData_SetsPropertiesCorrectly() {
        // Given
        String title = "春節パーティー";
        String description = "2025年春節を祝うコミュニティイベント";
        String location = "東京国際フォーラム";
        BigDecimal price = new BigDecimal("3000.00");
        Integer capacity = 100;
        
        // When
        Event event = Event.builder()
            .organizer(organizer)
            .title(title)
            .description(description)
            .category(EventCategory.CULTURAL)
            .startTime(startTime)
            .endTime(endTime)
            .location(location)
            .price(price)
            .capacity(capacity)
            .status(EventStatus.PUBLISHED)
            .build();
        
        // Then
        assertThat(event.getOrganizer()).isEqualTo(organizer);
        assertThat(event.getTitle()).isEqualTo(title);
        assertThat(event.getDescription()).isEqualTo(description);
        assertThat(event.getCategory()).isEqualTo(EventCategory.CULTURAL);
        assertThat(event.getStartTime()).isEqualTo(startTime);
        assertThat(event.getEndTime()).isEqualTo(endTime);
        assertThat(event.getLocation()).isEqualTo(location);
        assertThat(event.getPrice()).isEqualTo(price);
        assertThat(event.getCapacity()).isEqualTo(capacity);
        assertThat(event.getStatus()).isEqualTo(EventStatus.PUBLISHED);
    }

    @Test
    @DisplayName("デフォルト値が正しく設定される")
    void createEvent_DefaultValues_SetsCorrectDefaults() {
        // When
        Event event = Event.builder()
            .organizer(organizer)
            .title("テストイベント")
            .description("テスト用のイベントです")
            .startTime(startTime)
            .endTime(endTime)
            .location("テスト会場")
            .build();
        
        // Then
        assertThat(event.getCategory()).isEqualTo(EventCategory.OTHER);
        assertThat(event.getPrice()).isEqualTo(BigDecimal.ZERO);
        assertThat(event.getSoldTickets()).isEqualTo(0);
        assertThat(event.getStatus()).isEqualTo(EventStatus.DRAFT);
        assertThat(event.getLanguage()).isEqualTo("zh-CN");
    }

    @Test
    @DisplayName("公開されているかどうかを正しく判定する")
    void isPublic_PublishedEvent_ReturnsTrue() {
        // Given
        Event event = Event.builder()
            .organizer(organizer)
            .title("公開イベント")
            .description("公開されているイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .status(EventStatus.PUBLISHED)
            .build();
        
        // When & Then
        assertThat(event.isPublic()).isTrue();
    }

    @Test
    @DisplayName("削除済みイベントは公開されていない")
    void isPublic_DeletedEvent_ReturnsFalse() {
        // Given
        Event event = Event.builder()
            .organizer(organizer)
            .title("削除済みイベント")
            .description("削除されたイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .status(EventStatus.PUBLISHED)
            .deletedAt(LocalDateTime.now())
            .build();
        
        // When & Then
        assertThat(event.isPublic()).isFalse();
    }

    @Test
    @DisplayName("チケット購入可能かどうかを正しく判定する")
    void canPurchaseTickets_PublishedEvent_ReturnsTrue() {
        // Given
        Event event = Event.builder()
            .organizer(organizer)
            .title("チケット販売中イベント")
            .description("チケットを購入できるイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .status(EventStatus.PUBLISHED)
            .capacity(100)
            .soldTickets(50)
            .build();
        
        // When & Then
        assertThat(event.canPurchaseTickets()).isTrue();
    }

    @Test
    @DisplayName("満席のイベントはチケット購入できない")
    void canPurchaseTickets_SoldOutEvent_ReturnsFalse() {
        // Given
        Event event = Event.builder()
            .organizer(organizer)
            .title("満席イベント")
            .description("満席のイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .status(EventStatus.PUBLISHED)
            .capacity(100)
            .soldTickets(100)
            .build();
        
        // When & Then
        assertThat(event.canPurchaseTickets()).isFalse();
        assertThat(event.isSoldOut()).isTrue();
    }

    @Test
    @DisplayName("残りチケット数を正しく計算する")
    void getRemainingTickets_EventWithCapacity_ReturnsCorrectCount() {
        // Given
        Event event = Event.builder()
            .organizer(organizer)
            .title("テストイベント")
            .description("テスト用のイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .capacity(100)
            .soldTickets(30)
            .build();
        
        // When & Then
        assertThat(event.getRemainingTickets()).isEqualTo(70);
    }

    @Test
    @DisplayName("定員なしのイベントは残りチケット数がnull")
    void getRemainingTickets_EventWithoutCapacity_ReturnsNull() {
        // Given
        Event event = Event.builder()
            .organizer(organizer)
            .title("定員なしイベント")
            .description("定員制限のないイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .soldTickets(50)
            .build();
        
        // When & Then
        assertThat(event.getRemainingTickets()).isNull();
    }

    @Test
    @DisplayName("販売率を正しく計算する")
    void getSalesRate_EventWithSales_ReturnsCorrectRate() {
        // Given
        Event event = Event.builder()
            .organizer(organizer)
            .title("テストイベント")
            .description("テスト用のイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .capacity(100)
            .soldTickets(25)
            .build();
        
        // When & Then
        assertThat(event.getSalesRate()).isEqualTo(0.25);
    }

    @Test
    @DisplayName("無料イベントかどうかを正しく判定する")
    void isFree_FreeEvent_ReturnsTrue() {
        // Given
        Event freeEvent1 = Event.builder()
            .organizer(organizer)
            .title("無料イベント1")
            .description("価格が0のイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .price(BigDecimal.ZERO)
            .build();
        
        Event freeEvent2 = Event.builder()
            .organizer(organizer)
            .title("無料イベント2")
            .description("価格がnullのイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .build();
        
        Event paidEvent = Event.builder()
            .organizer(organizer)
            .title("有料イベント")
            .description("価格が設定されたイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .price(new BigDecimal("1000"))
            .build();
        
        // When & Then
        assertThat(freeEvent1.isFree()).isTrue();
        assertThat(freeEvent2.isFree()).isTrue();
        assertThat(paidEvent.isFree()).isFalse();
    }

    @Test
    @DisplayName("チケット購入処理が正しく実行される")
    void purchaseTickets_ValidPurchase_UpdatesSoldTickets() {
        // Given
        Event event = Event.builder()
            .organizer(organizer)
            .title("チケット販売イベント")
            .description("チケットを販売するイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .status(EventStatus.PUBLISHED)
            .capacity(100)
            .soldTickets(20)
            .build();
        
        // When
        event.purchaseTickets(5);
        
        // Then
        assertThat(event.getSoldTickets()).isEqualTo(25);
        assertThat(event.getRemainingTickets()).isEqualTo(75);
    }

    @Test
    @DisplayName("在庫不足の場合チケット購入が失敗する")
    void purchaseTickets_InsufficientCapacity_ThrowsException() {
        // Given
        Event event = Event.builder()
            .organizer(organizer)
            .title("在庫不足イベント")
            .description("在庫が不足しているイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .status(EventStatus.PUBLISHED)
            .capacity(100)
            .soldTickets(95)
            .build();
        
        // When & Then
        assertThatThrownBy(() -> event.purchaseTickets(10))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("チケットの在庫が不足しています");
    }

    @Test
    @DisplayName("満席になった場合ステータスがSOLD_OUTに変更される")
    void purchaseTickets_BecomeSoldOut_UpdatesStatus() {
        // Given
        Event event = Event.builder()
            .organizer(organizer)
            .title("満席になるイベント")
            .description("満席になるイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .status(EventStatus.PUBLISHED)
            .capacity(100)
            .soldTickets(95)
            .build();
        
        // When
        event.purchaseTickets(5);
        
        // Then
        assertThat(event.getSoldTickets()).isEqualTo(100);
        assertThat(event.getStatus()).isEqualTo(EventStatus.SOLD_OUT);
        assertThat(event.isSoldOut()).isTrue();
    }

    @Test
    @DisplayName("ソフトデリートが正しく実行される")
    void softDelete_ValidEvent_SetsDeletedAtAndStatus() {
        // Given
        Event event = Event.builder()
            .organizer(organizer)
            .title("削除対象イベント")
            .description("削除されるイベント")
            .startTime(startTime)
            .endTime(endTime)
            .location("会場")
            .status(EventStatus.PUBLISHED)
            .build();
        
        // When
        event.softDelete();
        
        // Then
        assertThat(event.getDeletedAt()).isNotNull();
        assertThat(event.getStatus()).isEqualTo(EventStatus.DELETED);
        assertThat(event.isDeleted()).isTrue();
        assertThat(event.isPublic()).isFalse();
    }
}