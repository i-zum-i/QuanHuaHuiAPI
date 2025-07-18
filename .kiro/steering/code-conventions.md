# コーディング規約

## 1. 命名規則

### 1.1. クラス命名規則

- **基本原則**: PascalCase（各単語の最初を大文字）を使用
- **レイヤー別命名**:
  - **Controller**: `*Controller` (例: `UserController`, `EventController`)
  - **Service**: `*Service` (例: `UserService`, `EventService`)
  - **Repository**: `*Repository` (例: `UserRepository`, `EventRepository`)
  - **Entity**: 単数形の名詞 (例: `User`, `Event`, `HousingPost`)
  - **DTO**: 
    - リクエスト: `*Request` (例: `CreateUserRequest`, `UpdateEventRequest`)
    - レスポンス: `*Response` (例: `UserResponse`, `EventListResponse`)
    - 汎用: `*Dto` (例: `UserProfileDto`)
  - **Exception**: `*Exception` (例: `UserNotFoundException`, `InvalidTokenException`)
  - **Enum**: `*Status`, `*Type`, `*Category` (例: `UserStatus`, `EventType`)
  - **Configuration**: `*Config` (例: `SecurityConfig`, `DatabaseConfig`)
  - **Utility**: `*Utils` または `*Helper` (例: `DateUtils`, `ValidationHelper`)

### 1.2. メソッド命名規則

- **基本原則**: camelCase（最初は小文字、以降の単語の最初を大文字）
- **CRUD操作**:
  - 作成: `create*`, `add*` (例: `createUser`, `addEvent`)
  - 取得: `get*`, `find*`, `fetch*` (例: `getUserById`, `findActiveEvents`)
  - 更新: `update*`, `modify*` (例: `updateUserProfile`, `modifyEventStatus`)
  - 削除: `delete*`, `remove*` (例: `deleteUser`, `removeEvent`)
- **検証メソッド**: `validate*`, `check*`, `verify*` (例: `validateEmail`, `checkPermission`)
- **変換メソッド**: `convert*`, `transform*`, `map*` (例: `convertToDto`, `mapToEntity`)
- **boolean返却**: `is*`, `has*`, `can*` (例: `isActive`, `hasPermission`, `canAccess`)

### 1.3. 変数・フィールド命名規則

- **基本原則**: camelCase
- **定数**: UPPER_SNAKE_CASE (例: `MAX_FILE_SIZE`, `DEFAULT_PAGE_SIZE`)
- **コレクション**: 複数形を使用 (例: `users`, `events`, `housingPosts`)
- **boolean変数**: `is*`, `has*`, `can*` で始める (例: `isEnabled`, `hasAccess`)

### 1.4. パッケージ命名規則

- **基本構造**: `jp.co.protosoft.rihua.api.*`
- **レイヤー別パッケージ**:
  ```
  jp.co.protosoft.rihua.api.controller     # REST Controller
  jp.co.protosoft.rihua.api.service        # ビジネスロジック
  jp.co.protosoft.rihua.api.repository     # データアクセス
  jp.co.protosoft.rihua.api.domain         # Entity
  jp.co.protosoft.rihua.api.dto            # Data Transfer Object
  jp.co.protosoft.rihua.api.config         # 設定クラス
  jp.co.protosoft.rihua.api.exception      # カスタム例外
  jp.co.protosoft.rihua.api.util           # ユーティリティ
  jp.co.protosoft.rihua.api.security       # セキュリティ関連
  jp.co.protosoft.rihua.api.validation     # バリデーション
  ```

## 2. コード構造とスタイル

### 2.1. クラス構造

```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    // 1. 定数
    private static final int DEFAULT_PAGE_SIZE = 20;
    
    // 2. 依存性注入フィールド（final）
    private final UserService userService;
    private final ValidationService validationService;
    
    // 3. パブリックメソッド（APIエンドポイント）
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        // 実装
    }
    
    // 4. プライベートメソッド（ヘルパーメソッド）
    private void validateUserAccess(UUID userId) {
        // 実装
    }
}
```

### 2.2. メソッド構造

```java
@Transactional
public UserResponse createUser(CreateUserRequest request) {
    // 1. バリデーション
    validateCreateUserRequest(request);
    
    // 2. ビジネスロジック
    User user = User.builder()
        .email(request.getEmail())
        .username(request.getUsername())
        .status(UserStatus.ACTIVE)
        .build();
    
    // 3. データ保存
    User savedUser = userRepository.save(user);
    
    // 4. レスポンス変換
    return convertToUserResponse(savedUser);
}
```

### 2.3. インデントとフォーマット

- **インデント**: 4スペース（タブ文字は使用しない）
- **行の長さ**: 120文字以内
- **改行**: 
  - メソッドチェーンは各メソッドで改行
  - 長いパラメータリストは各パラメータで改行
- **空行**: 
  - メソッド間に1行
  - 論理的なブロック間に1行

```java
// 良い例
User user = User.builder()
    .email(request.getEmail())
    .username(request.getUsername())
    .status(UserStatus.ACTIVE)
    .createdAt(LocalDateTime.now())
    .build();

// 悪い例
User user = User.builder().email(request.getEmail()).username(request.getUsername()).status(UserStatus.ACTIVE).createdAt(LocalDateTime.now()).build();
```

## 3. アノテーション使用規則

### 3.1. Spring アノテーション

```java
// Controller
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

// Service
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

// Repository
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

// Entity
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
```

### 3.2. バリデーションアノテーション

```java
public class CreateUserRequest {
    
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    @Size(max = 100, message = "メールアドレスは100文字以内で入力してください")
    private String email;
    
    @NotBlank(message = "ユーザー名は必須です")
    @Size(min = 3, max = 50, message = "ユーザー名は3文字以上50文字以内で入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "ユーザー名は英数字とアンダースコアのみ使用可能です")
    private String username;
    
    @NotBlank(message = "パスワードは必須です")
    @Size(min = 8, max = 100, message = "パスワードは8文字以上100文字以内で入力してください")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "パスワードは大文字、小文字、数字を含む必要があります")
    private String password;
}
```

## 4. エラーハンドリング規約

### 4.1. カスタム例外クラス

```java
// 基底例外クラス
public abstract class BusinessException extends RuntimeException {
    private final String errorCode;
    
    protected BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}

// 具体的な例外クラス
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(UUID userId) {
        super("USER_NOT_FOUND", "ユーザーが見つかりません: " + userId);
    }
}

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "認証情報が正しくありません");
    }
}
```

### 4.2. グローバル例外ハンドラー

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.warn("Business exception occurred: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(ex.getErrorCode())
            .message(ex.getMessage())
            .build();
            
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Map<String, String> errors = fieldErrors.stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage
            ));
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("VALIDATION_ERROR")
            .message("入力データに問題があります")
            .details(errors)
            .build();
            
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
```

## 5. ログ出力規約

### 5.1. ログレベル使い分け

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    public UserResponse createUser(CreateUserRequest request) {
        // INFO: 重要な業務処理の開始・完了
        log.info("ユーザー作成開始: email={}", request.getEmail());
        
        try {
            // DEBUG: 詳細な処理情報（開発時のみ）
            log.debug("ユーザー情報検証中: {}", request);
            
            User user = createUserEntity(request);
            User savedUser = userRepository.save(user);
            
            // INFO: 正常完了
            log.info("ユーザー作成完了: userId={}, email={}", 
                     savedUser.getId(), savedUser.getEmail());
            
            return convertToUserResponse(savedUser);
            
        } catch (DataIntegrityViolationException ex) {
            // WARN: 予期される例外（重複登録など）
            log.warn("ユーザー作成失敗 - データ整合性エラー: email={}, error={}", 
                     request.getEmail(), ex.getMessage());
            throw new UserAlreadyExistsException(request.getEmail());
            
        } catch (Exception ex) {
            // ERROR: 予期しない例外
            log.error("ユーザー作成中に予期しないエラーが発生: email={}", 
                      request.getEmail(), ex);
            throw new UserCreationException("ユーザー作成に失敗しました");
        }
    }
}
```

### 5.2. ログメッセージフォーマット

- **構造化ログ**: キー=値の形式で情報を記録
- **個人情報の保護**: メールアドレスやIDは記録するが、パスワードなどの機密情報は記録しない
- **トレーサビリティ**: リクエストIDやユーザーIDを含める

```java
// 良い例
log.info("ユーザーログイン成功: userId={}, email={}, loginTime={}", 
         user.getId(), user.getEmail(), LocalDateTime.now());

// 悪い例
log.info("User " + user.getEmail() + " logged in successfully");
```

## 6. テストコード規約

### 6.1. テストクラス命名

```java
// 単体テスト
public class UserServiceTest {

// 統合テスト
public class UserControllerIntegrationTest {

// Repository テスト
public class UserRepositoryTest {
```

### 6.2. テストメソッド命名

```java
@Test
void createUser_ValidRequest_ReturnsUserResponse() {
    // Given-When-Then パターン
}

@Test
void createUser_DuplicateEmail_ThrowsUserAlreadyExistsException() {
    // 例外テスト
}

@Test
void getUserById_NonExistentId_ThrowsUserNotFoundException() {
    // 異常系テスト
}
```

### 6.3. テストコード構造

```java
@Test
void createUser_ValidRequest_ReturnsUserResponse() {
    // Given (準備)
    CreateUserRequest request = CreateUserRequest.builder()
        .email("test@example.com")
        .username("testuser")
        .password("Password123")
        .build();
    
    User expectedUser = User.builder()
        .id(UUID.randomUUID())
        .email(request.getEmail())
        .username(request.getUsername())
        .status(UserStatus.ACTIVE)
        .build();
    
    when(userRepository.save(any(User.class))).thenReturn(expectedUser);
    
    // When (実行)
    UserResponse result = userService.createUser(request);
    
    // Then (検証)
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo(request.getEmail());
    assertThat(result.getUsername()).isEqualTo(request.getUsername());
    assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    
    verify(userRepository).save(any(User.class));
}
```

## 7. コメント規約

### 7.1. JavaDoc

```java
/**
 * ユーザー管理サービス
 * 
 * <p>ユーザーの作成、更新、削除、検索などの業務処理を提供します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Service
public class UserService {
    
    /**
     * 新規ユーザーを作成します
     * 
     * @param request ユーザー作成リクエスト
     * @return 作成されたユーザー情報
     * @throws UserAlreadyExistsException 同じメールアドレスのユーザーが既に存在する場合
     * @throws ValidationException リクエストデータが不正な場合
     */
    public UserResponse createUser(CreateUserRequest request) {
        // 実装
    }
}
```

### 7.2. インラインコメント

```java
public UserResponse createUser(CreateUserRequest request) {
    // メールアドレスの重複チェック
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new UserAlreadyExistsException(request.getEmail());
    }
    
    // パスワードのハッシュ化
    String hashedPassword = passwordEncoder.encode(request.getPassword());
    
    // TODO: メール認証機能の実装（Phase 2で対応予定）
    
    User user = User.builder()
        .email(request.getEmail())
        .username(request.getUsername())
        .passwordHash(hashedPassword)
        .status(UserStatus.PENDING_VERIFICATION) // 認証待ち状態で作成
        .build();
    
    return convertToUserResponse(userRepository.save(user));
}
```

## 8. 多言語対応コーディング規約

### 8.1. メッセージキー命名

```properties
# messages.properties
user.validation.email.required=メールアドレスは必須です
user.validation.email.invalid=有効なメールアドレスを入力してください
user.validation.password.weak=パスワードが弱すぎます
user.creation.success=ユーザーが正常に作成されました
user.not.found=指定されたユーザーが見つかりません

# messages_zh_CN.properties
user.validation.email.required=邮箱地址是必填项
user.validation.email.invalid=请输入有效的邮箱地址
user.validation.password.weak=密码强度不够
user.creation.success=用户创建成功
user.not.found=找不到指定的用户
```

### 8.2. メッセージ使用方法

```java
@Component
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageSource messageSource;
    
    public String getMessage(String code, Object[] args, Locale locale) {
        return messageSource.getMessage(code, args, locale);
    }
    
    public String getMessage(String code, Locale locale) {
        return getMessage(code, null, locale);
    }
}

// 使用例
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final MessageService messageService;
    
    public void validateUser(User user, Locale locale) {
        if (user.getEmail() == null) {
            String message = messageService.getMessage(
                "user.validation.email.required", locale);
            throw new ValidationException(message);
        }
    }
}
```

この規約に従うことで、保守性が高く、読みやすく、国際化に対応したコードを作成できます。