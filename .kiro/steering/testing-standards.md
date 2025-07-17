# テスト標準とアプローチ

## 1. テスト戦略概要

### 1.1. テストピラミッド

Rihua Spring Boot APIプロジェクトでは、以下のテストピラミッドに従います：

- **単体テスト (70%)**: 個別のクラス・メソッドの動作検証
- **統合テスト (20%)**: コンポーネント間の連携検証
- **E2Eテスト (10%)**: エンドツーエンドのユーザーシナリオ検証

### 1.2. 品質目標

- **コードカバレッジ**: 最低80%、目標90%
- **テスト実行時間**: 単体テスト < 30秒、統合テスト < 5分
- **テスト安定性**: フレーキーテスト率 < 1%
- **テスト保守性**: 明確で読みやすいテストコード

### 1.3. テスト実行戦略

- **継続的テスト**: 全てのコミットで単体テストを実行
- **統合テスト**: プルリクエスト時に実行
- **E2Eテスト**: リリース前とステージング環境で実行
- **パフォーマンステスト**: 週次で実行

## 2. 推奨テストライブラリ

### 2.1. 基本フレームワーク

```xml
<dependencies>
    <!-- JUnit 5 - テストフレームワーク -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito - モックフレームワーク -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- AssertJ - アサーションライブラリ -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Spring Boot Test Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Testcontainers - 統合テスト用 -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- WireMock - 外部API モック -->
    <dependency>
        <groupId>com.github.tomakehurst</groupId>
        <artifactId>wiremock-jre8</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- JsonPath - JSON レスポンステスト -->
    <dependency>
        <groupId>com.jayway.jsonpath</groupId>
        <artifactId>json-path</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 2.2. テストプロファイル設定

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  redis:
    host: localhost
    port: 6379
  mail:
    host: localhost
    port: 1025

logging:
  level:
    com.rihua.api: DEBUG
    org.springframework.web: DEBUG
```
#
# 3. 単体テストパターン

### 3.1. Service層テストパターン

#### 基本的なServiceテスト構造

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private MessageService messageService;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("有効なリクエストでユーザー作成が成功する")
    void createUser_ValidRequest_ReturnsUserResponse() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .email("test@example.com")
            .username("testuser")
            .password("Password123")
            .firstName("太郎")
            .lastName("田中")
            .build();
        
        User savedUser = User.builder()
            .id(UUID.randomUUID())
            .email(request.getEmail())
            .username(request.getUsername())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .build();
        
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        UserResponse result = userService.createUser(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedUser.getId());
        assertThat(result.getEmail()).isEqualTo(request.getEmail());
        assertThat(result.getUsername()).isEqualTo(request.getUsername());
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        
        verify(userRepository).existsByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(argThat(user -> 
            user.getEmail().equals(request.getEmail()) &&
            user.getUsername().equals(request.getUsername()) &&
            user.getPasswordHash().equals("hashedPassword")
        ));
    }
    
    @Test
    @DisplayName("重複するメールアドレスでユーザー作成が失敗する")
    void createUser_DuplicateEmail_ThrowsUserAlreadyExistsException() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .email("existing@example.com")
            .username("testuser")
            .password("Password123")
            .build();
        
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessage("ユーザーが既に存在します: existing@example.com");
        
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "invalid-email", "@example.com"})
    @DisplayName("無効なメールアドレスでバリデーションエラーが発生する")
    void createUser_InvalidEmail_ThrowsValidationException(String invalidEmail) {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .email(invalidEmail)
            .username("testuser")
            .password("Password123")
            .build();
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(ValidationException.class);
    }
}
```

### 3.2. Repository層テストパターン

```java
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    @DisplayName("メールアドレスでユーザーを検索できる")
    void findByEmail_ExistingEmail_ReturnsUser() {
        // Given
        User user = User.builder()
            .email("test@example.com")
            .username("testuser")
            .passwordHash("hashedPassword")
            .status(UserStatus.ACTIVE)
            .build();
        
        entityManager.persistAndFlush(user);
        
        // When
        Optional<User> result = userRepository.findByEmail("test@example.com");
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }
    
    @Test
    @DisplayName("存在しないメールアドレスで検索すると空のOptionalが返される")
    void findByEmail_NonExistentEmail_ReturnsEmpty() {
        // When
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("アクティブなユーザーのみを取得できる")
    void findByStatus_ActiveUsers_ReturnsOnlyActiveUsers() {
        // Given
        User activeUser = User.builder()
            .email("active@example.com")
            .username("activeuser")
            .status(UserStatus.ACTIVE)
            .build();
        
        User inactiveUser = User.builder()
            .email("inactive@example.com")
            .username("inactiveuser")
            .status(UserStatus.INACTIVE)
            .build();
        
        entityManager.persist(activeUser);
        entityManager.persist(inactiveUser);
        entityManager.flush();
        
        // When
        List<User> result = userRepository.findByStatus(UserStatus.ACTIVE);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.get(0).getEmail()).isEqualTo("active@example.com");
    }
}
```

### 3.3. Controller層テストパターン

```java
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    
    @Test
    @DisplayName("有効なリクエストでユーザー作成が成功する")
    void createUser_ValidRequest_ReturnsCreated() throws Exception {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .email("test@example.com")
            .username("testuser")
            .password("Password123")
            .firstName("太郎")
            .lastName("田中")
            .build();
        
        UserResponse response = UserResponse.builder()
            .id(UUID.randomUUID())
            .email(request.getEmail())
            .username(request.getUsername())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .build();
        
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "test@example.com",
                        "username": "testuser",
                        "password": "Password123",
                        "firstName": "太郎",
                        "lastName": "田中"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("太郎"))
                .andExpect(jsonPath("$.lastName").value("田中"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
        
        verify(userService).createUser(argThat(req -> 
            req.getEmail().equals("test@example.com") &&
            req.getUsername().equals("testuser")
        ));
    }
    
    @Test
    @DisplayName("無効なリクエストでバリデーションエラーが返される")
    void createUser_InvalidRequest_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "invalid-email",
                        "username": "",
                        "password": "123"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.email").exists())
                .andExpect(jsonPath("$.details.username").exists())
                .andExpect(jsonPath("$.details.password").exists());
        
        verify(userService, never()).createUser(any(CreateUserRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("認証済みユーザーが自分の情報を取得できる")
    void getCurrentUser_AuthenticatedUser_ReturnsUserInfo() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        UserResponse response = UserResponse.builder()
            .id(userId)
            .email("test@example.com")
            .username("testuser")
            .status(UserStatus.ACTIVE)
            .build();
        
        when(userService.getCurrentUser()).thenReturn(response);
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer valid-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
```

## 4. 統合テストパターン

### 4.1. Spring Boot統合テスト

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
class UserIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    @DisplayName("ユーザー作成から取得までの完全なフロー")
    void userCreationAndRetrievalFlow() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .email("integration@example.com")
            .username("integrationuser")
            .password("Password123")
            .firstName("統合")
            .lastName("テスト")
            .build();
        
        // When - ユーザー作成
        ResponseEntity<UserResponse> createResponse = restTemplate.postForEntity(
            "/api/v1/users", request, UserResponse.class);
        
        // Then - 作成成功を確認
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getEmail()).isEqualTo(request.getEmail());
        
        UUID userId = createResponse.getBody().getId();
        
        // When - ユーザー取得
        ResponseEntity<UserResponse> getResponse = restTemplate.getForEntity(
            "/api/v1/users/" + userId, UserResponse.class);
        
        // Then - 取得成功を確認
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(userId);
        assertThat(getResponse.getBody().getEmail()).isEqualTo(request.getEmail());
        
        // データベース確認
        Optional<User> savedUser = userRepository.findById(userId);
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo(request.getEmail());
    }
    
    @Test
    @DisplayName("重複メールアドレスでユーザー作成が失敗する")
    void createUser_DuplicateEmail_ReturnsConflict() {
        // Given - 既存ユーザーを作成
        User existingUser = User.builder()
            .email("duplicate@example.com")
            .username("existing")
            .passwordHash(passwordEncoder.encode("password"))
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(existingUser);
        
        CreateUserRequest request = CreateUserRequest.builder()
            .email("duplicate@example.com")
            .username("newuser")
            .password("Password123")
            .build();
        
        // When
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
            "/api/v1/users", request, ErrorResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("USER_ALREADY_EXISTS");
    }
}
```

### 4.2. 外部サービス統合テスト（WireMock使用）

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExternalServiceIntegrationTest {
    
    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8089))
            .build();
    
    @Autowired
    private EmailService emailService;
    
    @Test
    @DisplayName("メール送信サービスとの統合テスト")
    void sendEmail_ValidRequest_SendsEmailSuccessfully() {
        // Given
        wireMock.stubFor(post(urlEqualTo("/send-email"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "messageId": "msg-12345",
                                "status": "sent"
                            }
                            """)));
        
        EmailRequest request = EmailRequest.builder()
            .to("test@example.com")
            .subject("テストメール")
            .body("これはテストメールです")
            .build();
        
        // When
        EmailResponse response = emailService.sendEmail(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMessageId()).isEqualTo("msg-12345");
        assertThat(response.getStatus()).isEqualTo("sent");
        
        // WireMockの呼び出し確認
        wireMock.verify(postRequestedFor(urlEqualTo("/send-email"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(containing("test@example.com")));
    }
}
```

## 5. モックアプローチとベストプラクティス

### 5.1. モック使用指針

#### 使用すべき場面
- **外部依存関係**: データベース、外部API、ファイルシステム
- **複雑な依存関係**: 設定が困難または時間がかかる依存関係
- **副作用のある操作**: メール送信、決済処理など

#### 使用を避けるべき場面
- **値オブジェクト**: DTOやEntityなどの単純なデータオブジェクト
- **単純なユーティリティ**: 計算ロジックなどの純粋関数
- **Spring管理Bean**: 可能な限り実際のBeanを使用

### 5.2. モックパターン例

```java
// 良い例: 外部依存関係のモック
@Mock
private UserRepository userRepository;

@Mock
private EmailService emailService;

// 悪い例: 値オブジェクトのモック（実際のオブジェクトを使用すべき）
// @Mock
// private CreateUserRequest request; // ❌

// 良い例: 実際のオブジェクトを使用
CreateUserRequest request = CreateUserRequest.builder()
    .email("test@example.com")
    .build(); // ✅
```

### 5.3. Stubbing パターン

```java
// 基本的なStubbing
when(userRepository.findById(userId)).thenReturn(Optional.of(user));

// 例外のStubbing
when(userRepository.save(any(User.class)))
    .thenThrow(new DataIntegrityViolationException("Duplicate key"));

// 引数マッチャーの使用
when(userRepository.findByEmail(argThat(email -> email.endsWith("@example.com"))))
    .thenReturn(Optional.of(user));

// 複数回呼び出しのStubbing
when(userRepository.findAll())
    .thenReturn(Arrays.asList(user1, user2))
    .thenReturn(Arrays.asList(user1, user2, user3));
```

## 6. アサーションスタイルとパターン

### 6.1. AssertJ推奨パターン

```java
// 基本的なアサーション
assertThat(result).isNotNull();
assertThat(result.getId()).isEqualTo(expectedId);
assertThat(result.getEmail()).isEqualTo("test@example.com");

// コレクションのアサーション
assertThat(users)
    .hasSize(3)
    .extracting(User::getEmail)
    .containsExactly("user1@example.com", "user2@example.com", "user3@example.com");

// 例外のアサーション
assertThatThrownBy(() -> userService.deleteUser(nonExistentId))
    .isInstanceOf(UserNotFoundException.class)
    .hasMessage("ユーザーが見つかりません: " + nonExistentId);

// 条件付きアサーション
assertThat(users)
    .filteredOn(user -> user.getStatus() == UserStatus.ACTIVE)
    .hasSize(2);

// オブジェクトのフィールドアサーション
assertThat(user)
    .extracting(User::getEmail, User::getUsername, User::getStatus)
    .containsExactly("test@example.com", "testuser", UserStatus.ACTIVE);

// カスタムアサーション
assertThat(user).satisfies(u -> {
    assertThat(u.getEmail()).isEqualTo("test@example.com");
    assertThat(u.getCreatedAt()).isBefore(LocalDateTime.now());
    assertThat(u.getStatus()).isIn(UserStatus.ACTIVE, UserStatus.PENDING_VERIFICATION);
});
```

### 6.2. JSON レスポンスアサーション

```java
// MockMvc でのJSONアサーション
mockMvc.perform(get("/api/v1/users/{id}", userId))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.id").value(userId.toString()))
    .andExpect(jsonPath("$.email").value("test@example.com"))
    .andExpect(jsonPath("$.status").value("ACTIVE"))
    .andExpect(jsonPath("$.createdAt").exists())
    .andExpect(jsonPath("$.password").doesNotExist()); // パスワードは返されない

// 配列のアサーション
mockMvc.perform(get("/api/v1/users"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.content").isArray())
    .andExpect(jsonPath("$.content", hasSize(2)))
    .andExpect(jsonPath("$.content[0].email").value("user1@example.com"))
    .andExpect(jsonPath("$.content[1].email").value("user2@example.com"));
```

## 7. テストファイル構成とディレクトリ構造

### 7.1. 推奨ディレクトリ構造

```
src/test/java/
├── com/rihua/api/
│   ├── controller/
│   │   ├── UserControllerTest.java
│   │   ├── EventControllerTest.java
│   │   └── integration/
│   │       ├── UserControllerIntegrationTest.java
│   │       └── EventControllerIntegrationTest.java
│   ├── service/
│   │   ├── UserServiceTest.java
│   │   ├── EventServiceTest.java
│   │   └── integration/
│   │       └── UserServiceIntegrationTest.java
│   ├── repository/
│   │   ├── UserRepositoryTest.java
│   │   └── EventRepositoryTest.java
│   ├── util/
│   │   ├── DateUtilsTest.java
│   │   └── ValidationUtilsTest.java
│   └── testutil/
│       ├── TestDataBuilder.java
│       ├── TestContainerConfig.java
│       └── MockDataFactory.java
└── resources/
    ├── application-test.yml
    ├── data.sql
    └── test-data/
        ├── users.json
        └── events.json
```

### 7.2. テストデータビルダーパターン

```java
// TestDataBuilder.java
public class TestDataBuilder {
    
    public static class UserBuilder {
        private String email = "test@example.com";
        private String username = "testuser";
        private String firstName = "太郎";
        private String lastName = "田中";
        private UserStatus status = UserStatus.ACTIVE;
        
        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }
        
        public UserBuilder inactive() {
            this.status = UserStatus.INACTIVE;
            return this;
        }
        
        public User build() {
            return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
        }
        
        public CreateUserRequest buildRequest() {
            return CreateUserRequest.builder()
                .email(email)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .password("Password123")
                .build();
        }
    }
    
    public static UserBuilder user() {
        return new UserBuilder();
    }
}

// 使用例
@Test
void testUserCreation() {
    // Given
    User user = TestDataBuilder.user()
        .email("specific@example.com")
        .username("specificuser")
        .inactive()
        .build();
    
    CreateUserRequest request = TestDataBuilder.user()
        .email("request@example.com")
        .buildRequest();
    
    // テストロジック...
}
```

## 8. カバレッジ測定と品質管理

### 8.1. JaCoCo設定

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                            <limit>
                                <counter>BRANCH</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.75</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 8.2. カバレッジ除外設定

```xml
<configuration>
    <excludes>
        <exclude>**/dto/**</exclude>
        <exclude>**/config/**</exclude>
        <exclude>**/Application.class</exclude>
        <exclude>**/exception/**</exclude>
    </excludes>
</configuration>
```

### 8.3. 品質ゲート基準

| メトリクス | 最低基準 | 目標 |
|-----------|---------|------|
| 行カバレッジ | 80% | 90% |
| ブランチカバレッジ | 75% | 85% |
| 複雑度 | 10以下/メソッド | 5以下/メソッド |
| テスト実行時間 | 5分以内 | 2分以内 |

## 9. パフォーマンステストとベンチマーク

### 9.1. JMH（Java Microbenchmark Harness）

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class UserServiceBenchmark {
    
    private UserService userService;
    private CreateUserRequest request;
    
    @Setup
    public void setup() {
        // セットアップロジック
        userService = new UserService(mockRepository, mockEncoder);
        request = TestDataBuilder.user().buildRequest();
    }
    
    @Benchmark
    public UserResponse benchmarkCreateUser() {
        return userService.createUser(request);
    }
}
```

### 9.2. 負荷テスト指針

- **単体テスト**: 個別メソッドのパフォーマンス測定
- **統合テスト**: エンドポイント単位の応答時間測定
- **負荷テスト**: 同時接続数とスループット測定
- **ストレステスト**: 限界値での動作確認

## 10. 継続的テストとCI/CD統合

### 10.1. GitHub Actions設定例

```yaml
name: Test Suite
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
    
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Run unit tests
      run: mvn test
    
    - name: Run integration tests
      run: mvn verify -P integration-test
    
    - name: Generate test report
      run: mvn jacoco:report
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
```

### 10.2. テスト実行戦略

- **プルリクエスト**: 単体テスト + 統合テスト
- **マージ後**: 全テストスイート実行
- **リリース前**: E2Eテスト + パフォーマンステスト
- **定期実行**: 週次でフルテストスイート

この包括的なテスト標準に従うことで、高品質で保守性の高いSpring Boot APIを構築し、継続的に品質を維持できます。