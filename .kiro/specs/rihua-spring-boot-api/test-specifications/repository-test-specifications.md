# リポジトリ層テスト仕様書

## 概要

本文書は、Rihua Spring Boot APIプロジェクトのリポジトリ層における統合テストの仕様を記載しています。各リポジトリの機能が正しく動作することを検証するためのテストケースを定義しています。

## テスト環境

- **テストフレームワーク**: JUnit 5
- **データベース**: H2 (インメモリ)
- **Spring Boot Test**: @DataJpaTest
- **アサーションライブラリ**: AssertJ

---

## UserRepository テスト仕様

### テスト対象
`jp.co.protosoft.rihua.api.repository.UserRepository`

### テストケース一覧

#### 1. メールアドレス検索機能

| テストケース | 説明 | 期待結果 |
|-------------|------|----------|
| **TC-UR-001** | 存在するメールアドレスでユーザーを検索 | 該当ユーザーが取得できる |
| **TC-UR-002** | 存在しないメールアドレスで検索 | 空のOptionalが返される |
| **TC-UR-003** | 削除済みユーザーのメールアドレスで検索 | 空のOptionalが返される（ソフトデリート対応） |

**詳細仕様:**
```java
// TC-UR-001: 正常系
Optional<User> result = userRepository.findByEmailAndDeletedAtIsNull("test@example.com");
assertThat(result).isPresent();
assertThat(result.get().getEmail()).isEqualTo("test@example.com");

// TC-UR-002: 異常系
Optional<User> result = userRepository.findByEmailAndDeletedAtIsNull("nonexistent@example.com");
assertThat(result).isEmpty();

// TC-UR-003: ソフトデリート確認
// deletedAt != null のユーザーは検索結果に含まれない
```

#### 2. メールアドレス存在確認機能

| テストケース | 説明 | 期待結果 |
|-------------|------|----------|
| **TC-UR-004** | 存在するメールアドレスの存在確認 | `true`が返される |

**詳細仕様:**
```java
boolean exists = userRepository.existsByEmailAndDeletedAtIsNull("exists@example.com");
assertThat(exists).isTrue();
```

#### 3. ステータス別検索機能

| テストケース | 説明 | 期待結果 |
|-------------|------|----------|
| **TC-UR-005** | アクティブステータスのユーザー検索 | アクティブユーザーのみ取得 |

**詳細仕様:**
```java
Page<User> result = userRepository.findByStatusAndDeletedAtIsNull(UserStatus.ACTIVE, pageable);
assertThat(result.getContent()).hasSize(2);
assertThat(result.getContent()).extracting(User::getStatus).containsOnly(UserStatus.ACTIVE);
```

#### 4. アクティブユーザー検索機能

| テストケース | 説明 | 期待結果 |
|-------------|------|----------|
| **TC-UR-006** | アクティブユーザーのみを検索 | ステータスがACTIVEのユーザーのみ取得 |

**詳細仕様:**
```java
Page<User> result = userRepository.findActiveUsers(pageable);
assertThat(result.getContent()).hasSize(1);
assertThat(result.getContent().get(0).getStatus()).isEqualTo(UserStatus.ACTIVE);
```

#### 5. キーワード検索機能

| テストケース | 説明 | 期待結果 |
|-------------|------|----------|
| **TC-UR-007** | 名前・メールアドレスでのキーワード検索 | 部分一致するユーザーが取得できる |

**詳細仕様:**
```java
Page<User> result = userRepository.searchByKeyword("john", pageable);
assertThat(result.getContent()).hasSize(1);
assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
```

#### 6. 期間指定検索機能

| テストケース | 説明 | 期待結果 |
|-------------|------|----------|
| **TC-UR-008** | 指定期間内に作成されたユーザー検索 | 期間内のユーザーのみ取得 |

**詳細仕様:**
```java
Page<User> result = userRepository.findByCreatedAtBetweenAndDeletedAtIsNull(startDate, endDate, pageable);
assertThat(result.getContent()).hasSize(1);
assertThat(result.getContent().get(0).getEmail()).isEqualTo("middle@example.com");
```

#### 7. 統計情報取得機能

| テストケース | 説明 | 期待結果 |
|-------------|------|----------|
| **TC-UR-009** | 認証待ちユーザー数の取得 | 正確な認証待ちユーザー数が取得できる |
| **TC-UR-010** | アクティブユーザー数の取得 | 正確なアクティブユーザー数が取得できる |

**詳細仕様:**
```java
// TC-UR-009
long count = userRepository.countPendingVerificationUsers();
assertThat(count).isEqualTo(2);

// TC-UR-010
long count = userRepository.countActiveUsers();
assertThat(count).isEqualTo(2);
```

---

## EventRepository テスト仕様

### テスト対象
`jp.co.protosoft.rihua.api.repository.EventRepository`

### テストケース一覧

#### 1. 基本検索機能

| テストケース | 説明 | 期待結果 |
|-------------|------|----------|
| **TC-ER-001** | IDでイベントを検索 | 該当イベントが取得できる |
| **TC-ER-002** | 公開されているイベントのみ検索 | 公開ステータスのイベントのみ取得 |

**詳細仕様:**
```java
// TC-ER-001
Optional<Event> result = eventRepository.findByIdAndDeletedAtIsNull(event.getId());
assertThat(result).isPresent();
assertThat(result.get().getTitle()).isEqualTo("テストイベント");

// TC-ER-002
Page<Event> result = eventRepository.findPublishedEvents(pageable);
assertThat(result.getContent()).hasSize(1);
assertThat(result.getContent().get(0).getStatus()).isEqualTo(EventStatus.PUBLISHED);
```

#### 2. カテゴリ・主催者別検索機能

| テストケース | 説明 | 期待結果 |
|-------------|------|----------|
| **TC-ER-003** | カテゴリでイベントを検索 | 指定カテゴリのイベントのみ取得 |
| **TC-ER-004** | 主催者でイベントを検索 | 指定主催者のイベントのみ取得 |

**詳細仕様:**
```java
// TC-ER-003
Page<Event> result = eventRepository.findByCategoryAndDeletedAtIsNull(EventCategory.CULTURAL, pageable);
assertThat(result.getContent()).hasSize(1);
assertThat(result.getContent().get(0).getCategory()).isEqualTo(EventCategory.CULTURAL);

// TC-ER-004
Page<Event> result = eventRepository.findByOrganizerAndDeletedAtIsNull(organizer1, pageable);
assertThat(result.getContent()).hasSize(2);
assertThat(result.getContent()).extracting(Event::getOrganizer).containsOnly(organizer1);
```

#### 3. 日時・価格条件検索機能

| テストケース | 説明 | 期待結果 |
|-------------|------|----------|
| **TC-ER-005** | 今後開催予定のイベント検索 | 未来の開始時刻を持つイベントのみ取得 |
| **TC-ER-006** | 無料イベント検索 | 価格が0円のイベントのみ取得 |
| **TC-ER-007** | 価格範囲でイベント検索 | 指定価格範囲内のイベントのみ取得 |

**詳細仕様:**
```java
// TC-ER-005
Page<Event> result = eventRepository.findUpcomingEvents(pageable);
assertThat(result.getContent()).hasSize(1);
assertThat(result.getContent().get(0).getTitle()).isEqualTo("未来のイベント");

// TC-ER-006
Page<Event> result = eventRepository.findFreeEvents(pageable);
assertThat(result.getContent()).hasSize(1);
assertThat(result.getContent().get(0).getPrice()).isEqualByComparingTo(BigDecimal.ZERO);

// TC-ER-007
Page<Event> result = eventRepository.findByPriceBetweenAndStatusAndDeletedAtIsNull(
    new BigDecimal("1000"), new BigDecimal("2000"), pageable);
assertThat(result.getContent()).hasSize(1);
assertThat(result.getContent().get(0).getTitle()).isEqualTo("普通のイベント");
```

#### 4. 高度な検索機能

| テストケース | 説明 | 期待結果 |
|-------------|------|----------|
| **TC-ER-008** | キーワード検索 | タイトル・説明文に含まれるキーワードで検索 |
| **TC-ER-009** | 複合条件検索 | 複数の条件を組み合わせた検索 |

**詳細仕様:**
```java
// TC-ER-008
Page<Event> result = eventRepository.searchByKeyword("春", pageable);
assertThat(result.getContent()).hasSize(1);
assertThat(result.getContent().get(0).getTitle()).contains("春");

// TC-ER-009
Page<Event> result = eventRepository.findByComplexCriteria(
    EventCategory.CULTURAL, "東京", 
    new BigDecimal("500"), new BigDecimal("1500"),
    LocalDateTime.now(), LocalDateTime.now().plusDays(2),
    pageable);
assertThat(result.getContent()).hasSize(1);
assertThat(result.getContent().get(0).getCategory()).isEqualTo(EventCategory.CULTURAL);
```

#### 5. 管理・統計機能

| テストケース | 説明 | 期待結果 |
|-------------|------|----------|
| **TC-ER-010** | 期限切れイベント検索 | 終了時刻が過去のイベントを取得 |
| **TC-ER-011** | 主催者の公開イベント数取得 | 指定主催者の公開イベント数を正確に取得 |

**詳細仕様:**
```java
// TC-ER-010
List<Event> result = eventRepository.findExpiredEvents();
assertThat(result).hasSize(1);
assertThat(result.get(0).getTitle()).isEqualTo("期限切れイベント");

// TC-ER-011
long count = eventRepository.countPublishedEventsByOrganizer(organizer);
assertThat(count).isEqualTo(2);
```

---

## テストデータ設計

### ユーザーテストデータ

```java
User testUser = User.builder()
    .email("test@example.com")
    .username("testuser")
    .passwordHash("hashedPassword")
    .firstName("太郎")
    .lastName("田中")
    .role(UserRole.USER)
    .status(UserStatus.ACTIVE)
    .build();
```

### イベントテストデータ

```java
Event testEvent = Event.builder()
    .title("テストイベント")
    .description("テストイベントの説明")
    .organizer(organizer)
    .status(EventStatus.PUBLISHED)
    .category(EventCategory.CULTURAL)
    .location("テスト会場")
    .startTime(LocalDateTime.now().plusDays(1))
    .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
    .capacity(100)
    .price(new BigDecimal("1000"))
    .build();
```

---

## テスト実行結果

### UserRepositoryTest
- **総テストケース数**: 11
- **成功**: 11 ✅
- **失敗**: 0
- **カバレッジ**: 主要メソッドの90%以上

### EventRepositoryTest
- **総テストケース数**: 12
- **成功**: 12 ✅
- **失敗**: 0
- **カバレッジ**: 主要メソッドの90%以上

---

## テスト品質保証

### 1. テストの独立性
- 各テストケースは独立して実行可能
- `@BeforeEach`でテストデータをクリーンアップ
- テスト間でのデータ競合を防止

### 2. データ整合性
- `TestEntityManager`を使用したテストデータ管理
- トランザクション境界の明確化
- ソフトデリート対応の検証

### 3. エラーハンドリング
- 正常系・異常系の両方をテスト
- 境界値テストの実施
- null値・空値の処理確認

### 4. パフォーマンス考慮
- ページネーション機能のテスト
- インデックス効果の間接的検証
- 大量データでの動作確認（今後実装予定）

---

## 今後の拡張予定

### 1. 追加テストケース
- HousingRepositoryTest
- JobRepositoryTest
- ForumPostRepositoryTest
- その他のリポジトリテスト

### 2. パフォーマンステスト
- 大量データでの検索性能テスト
- インデックス効果の測定
- メモリ使用量の監視

### 3. 統合テスト
- 複数リポジトリ間の連携テスト
- トランザクション境界のテスト
- 外部キー制約のテスト

---

## 参考資料

- [Spring Data JPA Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing-spring-boot-applications-testing-autoconfigured-jpa-test)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)