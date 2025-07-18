# パッケージ構造とアーキテクチャ

## 基本パッケージ情報

### ベースパッケージ
- **企業ドメイン**: `jp.co.protosoft` (株式会社プロトソフト)
- **プロジェクト名**: `rihua` (日华プロジェクト)
- **アプリケーション種別**: `api` (REST API)
- **完全パッケージ名**: `jp.co.protosoft.rihua.api`

## パッケージ構造

### 全体構造
```
jp.co.protosoft.rihua.api/
├── RihuaApiApplication.java          # メインアプリケーションクラス
├── config/                           # 設定クラス
├── controller/                       # REST APIコントローラー
├── service/                          # ビジネスロジック層
├── repository/                       # データアクセス層
├── domain/                          # エンティティクラス
├── dto/                             # データ転送オブジェクト
├── security/                        # セキュリティ関連
├── exception/                       # カスタム例外
└── util/                           # ユーティリティクラス
```

### 詳細パッケージ構造

#### 1. 設定クラス (`config`)
```
jp.co.protosoft.rihua.api.config/
├── SecurityConfig.java              # Spring Security設定
├── JacksonConfig.java               # JSON設定
├── WebConfig.java                   # Web設定
├── DatabaseConfig.java              # データベース設定
└── CacheConfig.java                 # キャッシュ設定
```

#### 2. コントローラー層 (`controller`)
```
jp.co.protosoft.rihua.api.controller/
├── AuthController.java              # 認証関連API
├── UserController.java              # ユーザー管理API
├── EventController.java             # イベント管理API
├── HousingController.java           # 住居管理API
├── JobController.java               # 求人管理API
├── ForumController.java             # フォーラムAPI
├── TicketController.java            # チケット管理API
├── NotificationController.java      # 通知管理API
├── FileController.java              # ファイル管理API
└── AdminController.java             # 管理者API
```

#### 3. サービス層 (`service`)
```
jp.co.protosoft.rihua.api.service/
├── UserService.java                 # ユーザービジネスロジック
├── EventService.java                # イベントビジネスロジック
├── HousingService.java              # 住居ビジネスロジック
├── JobService.java                  # 求人ビジネスロジック
├── ForumService.java                # フォーラムビジネスロジック
├── TicketService.java               # チケットビジネスロジック
├── NotificationService.java         # 通知ビジネスロジック
├── FileService.java                 # ファイル管理ビジネスロジック
├── CustomUserDetailsService.java   # Spring Security連携
└── EmailService.java                # メール送信サービス
```

#### 4. リポジトリ層 (`repository`)
```
jp.co.protosoft.rihua.api.repository/
├── UserRepository.java              # ユーザーデータアクセス
├── EventRepository.java             # イベントデータアクセス
├── HousingRepository.java           # 住居データアクセス
├── JobRepository.java               # 求人データアクセス
├── ForumPostRepository.java         # フォーラム投稿データアクセス
├── CommentRepository.java           # コメントデータアクセス
├── TicketRepository.java            # チケットデータアクセス
├── NotificationRepository.java      # 通知データアクセス
├── FileMetadataRepository.java      # ファイルメタデータアクセス
├── LikeRepository.java              # いいねデータアクセス
├── ReportRepository.java            # 報告データアクセス
└── AuditLogRepository.java          # 監査ログデータアクセス
```

#### 5. ドメイン層 (`domain`)
```
jp.co.protosoft.rihua.api.domain/
├── User.java                        # ユーザーエンティティ
├── Event.java                       # イベントエンティティ
├── Housing.java                     # 住居エンティティ
├── Job.java                         # 求人エンティティ
├── ForumPost.java                   # フォーラム投稿エンティティ
├── Comment.java                     # コメントエンティティ
├── Ticket.java                      # チケットエンティティ
├── Notification.java                # 通知エンティティ
├── FileMetadata.java                # ファイルメタデータエンティティ
├── Like.java                        # いいねエンティティ
├── Report.java                      # 報告エンティティ
├── AuditLog.java                    # 監査ログエンティティ
└── enums/                          # 列挙型
    ├── UserStatus.java              # ユーザーステータス
    ├── UserRole.java                # ユーザー権限
    ├── EventCategory.java           # イベントカテゴリ
    ├── EventStatus.java             # イベントステータス
    ├── HousingType.java             # 住居タイプ
    ├── HousingStatus.java           # 住居ステータス
    ├── JobType.java                 # 求人タイプ
    ├── JobStatus.java               # 求人ステータス
    ├── ForumCategory.java           # フォーラムカテゴリ
    ├── PostStatus.java              # 投稿ステータス
    ├── CommentStatus.java           # コメントステータス
    ├── TicketStatus.java            # チケットステータス
    ├── NotificationType.java        # 通知タイプ
    ├── NotificationStatus.java      # 通知ステータス
    ├── FileType.java                # ファイルタイプ
    ├── LikeableType.java            # いいね対象タイプ
    ├── ReportableType.java          # 報告対象タイプ
    ├── ReportReason.java            # 報告理由
    ├── ReportStatus.java            # 報告ステータス
    └── AuditAction.java             # 監査アクション
```

#### 6. DTO層 (`dto`)
```
jp.co.protosoft.rihua.api.dto/
├── request/                         # リクエストDTO
│   ├── CreateUserRequest.java       # ユーザー作成リクエスト
│   ├── UpdateUserRequest.java       # ユーザー更新リクエスト
│   ├── CreateEventRequest.java      # イベント作成リクエスト
│   ├── UpdateEventRequest.java      # イベント更新リクエスト
│   ├── LoginRequest.java            # ログインリクエスト
│   └── PasswordResetRequest.java    # パスワードリセットリクエスト
└── response/                        # レスポンスDTO
    ├── UserResponse.java            # ユーザーレスポンス
    ├── EventResponse.java           # イベントレスポンス
    ├── HousingResponse.java         # 住居レスポンス
    ├── JobResponse.java             # 求人レスポンス
    ├── AuthResponse.java            # 認証レスポンス
    ├── ErrorResponse.java           # エラーレスポンス
    └── ApiResponse.java             # 汎用APIレスポンス
```

#### 7. セキュリティ層 (`security`)
```
jp.co.protosoft.rihua.api.security/
├── JwtTokenProvider.java            # JWT生成・検証
├── JwtAuthenticationFilter.java     # JWT認証フィルター
├── JwtAuthenticationEntryPoint.java # JWT認証エントリーポイント
├── UserPrincipal.java               # ユーザープリンシパル
├── SecurityUtils.java               # セキュリティユーティリティ
└── PasswordValidator.java           # パスワード検証
```

#### 8. 例外層 (`exception`)
```
jp.co.protosoft.rihua.api.exception/
├── BusinessException.java           # ビジネス例外基底クラス
├── UserNotFoundException.java       # ユーザー未発見例外
├── InvalidCredentialsException.java # 認証情報無効例外
├── ValidationException.java         # バリデーション例外
├── ResourceConflictException.java   # リソース競合例外
├── UnauthorizedException.java       # 認可失敗例外
├── GlobalExceptionHandler.java      # グローバル例外ハンドラー
└── ErrorCode.java                   # エラーコード定義
```

#### 9. ユーティリティ層 (`util`)
```
jp.co.protosoft.rihua.api.util/
├── DateUtils.java                   # 日付ユーティリティ
├── ValidationUtils.java             # バリデーションユーティリティ
├── MessageUtils.java                # メッセージユーティリティ
├── FileUtils.java                   # ファイルユーティリティ
├── EncryptionUtils.java             # 暗号化ユーティリティ
├── QRCodeUtils.java                 # QRコードユーティリティ
└── Constants.java                   # 定数定義
```

## アーキテクチャ原則

### レイヤー分離
- **Controller**: HTTPリクエスト処理、バリデーション、レスポンス生成
- **Service**: ビジネスロジック、トランザクション管理
- **Repository**: データアクセス、クエリ実行
- **Domain**: エンティティ定義、ドメインロジック

### 依存関係の方向
```
Controller → Service → Repository → Domain
     ↓         ↓          ↓
    DTO    Exception    Util
```

### 命名規則
- **パッケージ**: 小文字、ドット区切り
- **クラス**: PascalCase
- **メソッド**: camelCase
- **定数**: UPPER_SNAKE_CASE
- **変数**: camelCase

## 設定ファイルでの参照

### ログ設定
```yaml
logging:
  level:
    jp.co.protosoft.rihua.api: INFO
    jp.co.protosoft.rihua.api.security: DEBUG
    jp.co.protosoft.rihua.api.repository: DEBUG
```

### コンポーネントスキャン
```java
@SpringBootApplication
@ComponentScan(basePackages = "jp.co.protosoft.rihua.api")
public class RihuaApiApplication {
    // ...
}
```

### JPA設定
```yaml
spring:
  jpa:
    packages-to-scan: jp.co.protosoft.rihua.api.domain
```

## テストパッケージ構造

テストクラスは対応するソースクラスと同じパッケージ構造を持ちます：

```
src/test/java/jp/co/protosoft/rihua/api/
├── RihuaApiApplicationTest.java
├── domain/
│   ├── UserTest.java
│   └── EventTest.java
├── repository/
│   ├── BaseRepositoryTest.java
│   ├── UserRepositoryTest.java
│   └── EventRepositoryTest.java
├── service/
│   ├── UserServiceTest.java
│   └── EventServiceTest.java
├── controller/
│   ├── UserControllerTest.java
│   └── EventControllerTest.java
└── security/
    ├── JwtTokenProviderTest.java
    └── JwtAuthenticationFilterTest.java
```

## 変更履歴

- **2025-07-18**: パッケージ名を`com.rihua.api`から`jp.co.protosoft.rihua.api`に変更
- **2025-07-18**: 全ソースファイルのパッケージ宣言とimport文を更新
- **2025-07-18**: 設定ファイル（application.yml等）のパッケージ参照を更新
- **2025-07-18**: ドキュメントファイルのパッケージ参照を更新