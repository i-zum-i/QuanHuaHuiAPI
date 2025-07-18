package com.rihua.api.repository;

import com.rihua.api.domain.Event;
import com.rihua.api.domain.User;
import com.rihua.api.domain.enums.EventCategory;
import com.rihua.api.domain.enums.EventStatus;
import com.rihua.api.domain.enums.UserRole;
import com.rihua.api.domain.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EventRepositoryの統合テスト
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
class EventRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("IDでイベントを検索できる")
    void findByIdAndDeletedAtIsNull_ExistingId_ReturnsEvent() {
        // Given
        User organizer = createTestUser("organizer@example.com", "organizer");
        Event event = createTestEvent("テストイベント", organizer, EventStatus.PUBLISHED);
        persistAndFlush(event);

        // When
        Optional<Event> result = eventRepository.findByIdAndDeletedAtIsNull(event.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("テストイベント");
    }

    @Test
    @DisplayName("公開されているイベントを検索できる")
    void findPublishedEvents_ReturnsOnlyPublishedEvents() {
        // Given
        User organizer = createTestUser("organizer@example.com", "organizer");
        Event publishedEvent = createTestEvent("公開イベント", organizer, EventStatus.PUBLISHED);
        Event draftEvent = createTestEvent("下書きイベント", organizer, EventStatus.DRAFT);
        
        persistAndFlush(publishedEvent);
        persistAndFlush(draftEvent);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Event> result = eventRepository.findPublishedEvents(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(EventStatus.PUBLISHED);
    }

    @Test
    @DisplayName("カテゴリでイベントを検索できる")
    void findByCategoryAndDeletedAtIsNull_SpecificCategory_ReturnsEventsInCategory() {
        // Given
        User organizer = createTestUser("organizer@example.com", "organizer");
        Event culturalEvent = createTestEvent("文化イベント", organizer, EventStatus.PUBLISHED);
        culturalEvent.setCategory(EventCategory.CULTURAL);
        
        Event businessEvent = createTestEvent("ビジネスイベント", organizer, EventStatus.PUBLISHED);
        businessEvent.setCategory(EventCategory.BUSINESS);
        
        persistAndFlush(culturalEvent);
        persistAndFlush(businessEvent);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Event> result = eventRepository.findByCategoryAndDeletedAtIsNull(EventCategory.CULTURAL, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo(EventCategory.CULTURAL);
    }

    @Test
    @DisplayName("主催者でイベントを検索できる")
    void findByOrganizerAndDeletedAtIsNull_SpecificOrganizer_ReturnsOrganizerEvents() {
        // Given
        User organizer1 = createTestUser("organizer1@example.com", "organizer1");
        User organizer2 = createTestUser("organizer2@example.com", "organizer2");
        
        Event event1 = createTestEvent("イベント1", organizer1, EventStatus.PUBLISHED);
        Event event2 = createTestEvent("イベント2", organizer1, EventStatus.PUBLISHED);
        Event event3 = createTestEvent("イベント3", organizer2, EventStatus.PUBLISHED);
        
        persistAndFlush(event1);
        persistAndFlush(event2);
        persistAndFlush(event3);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Event> result = eventRepository.findByOrganizerAndDeletedAtIsNull(organizer1, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Event::getOrganizer)
                .containsOnly(organizer1);
    }

    @Test
    @DisplayName("今後開催予定のイベントを検索できる")
    void findUpcomingEvents_ReturnsFutureEvents() {
        // Given
        User organizer = createTestUser("organizer@example.com", "organizer");
        
        Event pastEvent = createTestEvent("過去のイベント", organizer, EventStatus.PUBLISHED);
        pastEvent.setStartTime(LocalDateTime.now().minusDays(1));
        
        Event futureEvent = createTestEvent("未来のイベント", organizer, EventStatus.PUBLISHED);
        futureEvent.setStartTime(LocalDateTime.now().plusDays(1));
        
        persistAndFlush(pastEvent);
        persistAndFlush(futureEvent);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Event> result = eventRepository.findUpcomingEvents(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("未来のイベント");
    }

    @Test
    @DisplayName("無料イベントを検索できる")
    void findFreeEvents_ReturnsFreeEvents() {
        // Given
        User organizer = createTestUser("organizer@example.com", "organizer");
        
        Event freeEvent = createTestEvent("無料イベント", organizer, EventStatus.PUBLISHED);
        freeEvent.setPrice(BigDecimal.ZERO);
        
        Event paidEvent = createTestEvent("有料イベント", organizer, EventStatus.PUBLISHED);
        paidEvent.setPrice(new BigDecimal("1000"));
        
        persistAndFlush(freeEvent);
        persistAndFlush(paidEvent);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Event> result = eventRepository.findFreeEvents(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("価格範囲でイベントを検索できる")
    void findByPriceBetweenAndStatusAndDeletedAtIsNull_PriceRange_ReturnsEventsInRange() {
        // Given
        User organizer = createTestUser("organizer@example.com", "organizer");
        
        Event cheapEvent = createTestEvent("安いイベント", organizer, EventStatus.PUBLISHED);
        cheapEvent.setPrice(new BigDecimal("500"));
        
        Event moderateEvent = createTestEvent("普通のイベント", organizer, EventStatus.PUBLISHED);
        moderateEvent.setPrice(new BigDecimal("1500"));
        
        Event expensiveEvent = createTestEvent("高いイベント", organizer, EventStatus.PUBLISHED);
        expensiveEvent.setPrice(new BigDecimal("5000"));
        
        persistAndFlush(cheapEvent);
        persistAndFlush(moderateEvent);
        persistAndFlush(expensiveEvent);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Event> result = eventRepository.findByPriceBetweenAndStatusAndDeletedAtIsNull(
                new BigDecimal("1000"), new BigDecimal("2000"), pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("普通のイベント");
    }

    @Test
    @DisplayName("キーワードでイベントを検索できる")
    void searchByKeyword_MatchingKeyword_ReturnsMatchingEvents() {
        // Given
        User organizer = createTestUser("organizer@example.com", "organizer");
        
        Event event1 = createTestEvent("春祭りイベント", organizer, EventStatus.PUBLISHED);
        event1.setDescription("桜の季節に開催される春のお祭りです");
        
        Event event2 = createTestEvent("夏祭りイベント", organizer, EventStatus.PUBLISHED);
        event2.setDescription("花火大会もある夏のお祭りです");
        
        persistAndFlush(event1);
        persistAndFlush(event2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Event> result = eventRepository.searchByKeyword("春", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains("春");
    }

    @Test
    @DisplayName("複合条件でイベントを検索できる")
    void findByComplexCriteria_MultipleConditions_ReturnsMatchingEvents() {
        // Given
        User organizer = createTestUser("organizer@example.com", "organizer");
        
        Event event1 = createTestEvent("文化イベント1", organizer, EventStatus.PUBLISHED);
        event1.setCategory(EventCategory.CULTURAL);
        event1.setLocation("東京");
        event1.setPrice(new BigDecimal("1000"));
        event1.setStartTime(LocalDateTime.now().plusDays(1));
        
        Event event2 = createTestEvent("ビジネスイベント", organizer, EventStatus.PUBLISHED);
        event2.setCategory(EventCategory.BUSINESS);
        event2.setLocation("東京");
        event2.setPrice(new BigDecimal("1000"));
        event2.setStartTime(LocalDateTime.now().plusDays(1));
        
        persistAndFlush(event1);
        persistAndFlush(event2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Event> result = eventRepository.findByComplexCriteria(
                EventCategory.CULTURAL, "東京", 
                new BigDecimal("500"), new BigDecimal("1500"),
                LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo(EventCategory.CULTURAL);
    }

    @Test
    @DisplayName("期限切れイベントを検索できる")
    void findExpiredEvents_ReturnsExpiredEvents() {
        // Given
        User organizer = createTestUser("organizer@example.com", "organizer");
        
        Event expiredEvent = createTestEvent("期限切れイベント", organizer, EventStatus.PUBLISHED);
        expiredEvent.setEndTime(LocalDateTime.now().minusDays(1));
        
        Event activeEvent = createTestEvent("アクティブイベント", organizer, EventStatus.PUBLISHED);
        activeEvent.setEndTime(LocalDateTime.now().plusDays(1));
        
        persistAndFlush(expiredEvent);
        persistAndFlush(activeEvent);

        // When
        List<Event> result = eventRepository.findExpiredEvents();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("期限切れイベント");
    }

    @Test
    @DisplayName("主催者の公開イベント数を取得できる")
    void countPublishedEventsByOrganizer_ReturnsPublishedCount() {
        // Given
        User organizer = createTestUser("organizer@example.com", "organizer");
        
        Event publishedEvent1 = createTestEvent("公開イベント1", organizer, EventStatus.PUBLISHED);
        Event publishedEvent2 = createTestEvent("公開イベント2", organizer, EventStatus.PUBLISHED);
        Event draftEvent = createTestEvent("下書きイベント", organizer, EventStatus.DRAFT);
        
        persistAndFlush(publishedEvent1);
        persistAndFlush(publishedEvent2);
        persistAndFlush(draftEvent);

        // When
        long count = eventRepository.countPublishedEventsByOrganizer(organizer);

        // Then
        assertThat(count).isEqualTo(2);
    }

    private User createTestUser(String email, String username) {
        return User.builder()
                .email(email)
                .passwordHash("hashedPassword")
                .firstName("テスト")
                .lastName("ユーザー")
                .roles(Set.of(UserRole.USER))
                .status(UserStatus.ACTIVE)
                .build();
    }

    private Event createTestEvent(String title, User organizer, EventStatus status) {
        return Event.builder()
                .title(title)
                .description("テストイベントの説明")
                .organizer(organizer)
                .status(status)
                .category(EventCategory.CULTURAL)
                .location("テスト会場")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .capacity(100)
                .price(new BigDecimal("1000"))
                .build();
    }
}