# 技術標準とベストプラクティス

## 1. アーキテクチャ原則

### 1.1. Spring Boot開発標準

- **Javaバージョン**: **Java 17+** を使用します。`record`、`sealed class`、パターンマッチング for `switch` などのモダンな機能を積極的に活用し、コードの簡潔性と安全性を高めます。
    
- **Spring Bootバージョン**: **Spring Boot 3.x** の最新安定版を利用します。依存関係は原則として **Spring Boot Starter** に準拠し、BOM (Bill of Materials) を通じてバージョン管理を一元化します。
    
- **レイヤードアーキテクチャ**: アプリケーションは以下の4層構造を基本とします。
    
    - `Controller` (API層): HTTPリクエストの受付、リクエストデータの検証、Service層の呼び出し、レスポンスの返却に責務を持ちます。**Entityを直接返却せず、必ずDTOに変換します。**
        
    - `Service` (ビジネスロジック層): ドメイン固有のビジネスルールを実装します。複数のRepositoryを跨ぐトランザクション管理もこの層で行います。
        
    - `Repository` (データアクセス層): データベースとのやり取りに特化します。Spring Data JPAのインターフェースを継承して実装します。
        
    - `Domain` (Entity層): データベースのテーブル構造とマッピングされるオブジェクト。ビジネスロジックは含めず、データの構造を定義します。
        
- **DTO (Data Transfer Object)**: レイヤー間のデータ転送にはDTOを使用します。
    
    - `Request DTO`: Controllerが受け取るリクエストボディ。Bean Validationによる検証アノテーションを付与します。
        
    - `Response DTO`: Controllerがクライアントに返すレスポンス。Entityから必要なフィールドのみを抽出して構成します。
        
- **依存性注入 (DI)**: **コンストラクタインジェクション** を使用します。これにより、依存関係の不変性 (Immutability) が保証され、テストが容易になります。Lombokの `@RequiredArgsConstructor` の利用を推奨します。
    
- **設定の外部化**: `application.yml` を使用し、Spring Profiles (`dev`, `stg`, `prod`など) を活用して環境ごとに設定を切り替えます。機密情報（APIキー、DBパスワードなど）は環境変数またはSecret Managerから読み込むようにし、リポジトリに含めません。`@ConfigurationProperties` を使用して、設定値を型安全なオブジェクトとして扱います。
    

### 1.2. データベース設計原則

- **主キー**: 全てのエンティティの主キーには、推測困難な **UUID v7** を使用します。これは時系列ソートが可能で、パフォーマンス上の利点があります。
    
- **監査フィールド**: Spring Data JPA Auditing (`@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`) を利用して、全テーブルに以下のフィールドを自動で設定します。
    
    - `created_at` (作成日時)
        
    - `updated_at` (更新日時)
        
    - `created_by` (作成者ID)
        
    - `updated_by` (更新者ID)
        
- **ソフトデリート**: ユーザー情報や決済履歴などの重要データは物理削除せず、論理削除を行います。
    
    - `deleted_at` (TIMESTAMP) フィールドを追加します。`null` の場合は有効、日時が入っている場合は削除済みと見なします。
        
    - JPAの `@SQLDelete` と `@Where` アノテーションを用いて、アプリケーションレベルで論理削除を透過的に扱います。
        
- **インデックス最適化**: `WHERE`句で頻繁に使用されるカラム、および外部キーには必ずインデックスを設定します。複合インデックスの利用も積極的に検討し、クエリの実行計画 (EXPLAIN) を確認して効果を測定します。
    
- **多言語対応**: 多言語対応が必要なテーブル（例: `products`）には、コンテンツを管理する別テーブル（例: `product_translations`）を作成し、`product_id` と `language_code` をキーとして関連付けます。
    

### 1.3. セキュリティ標準

- **認証**: 認証方式として **JWT (JSON Web Token)** を採用します。
    
    - **アクセストークン**: 有効期間は **1時間**。ペイロードには `user_id`, `roles` (権限) を含めます。
        
    - **リフレッシュトークン**: 有効期間は **30日**。データベースに保存し、アクセストークンの再発行にのみ使用します。
        
- **通信の暗号化**: ロードバランサーまたはリバースプロキシでSSL終端を行い、全ての通信を **HTTPS** に強制します。Spring Securityで `requires-secure()` を設定することも可能です。
    
- **入力検証**: Controllerのメソッド引数に `@Valid` アノテーションを付与し、**Bean Validation** を徹底します。リクエストボディ、パス変数、クエリパラメータの全てが検証対象です。
    
- **パスワード管理**: パスワードは **bcrypt** (ストレングス10以上) を使用してハッシュ化し、データベースに保存します。
    
- **レート制限**: Resilience4j などのライブラリを導入し、APIエンドポイントごとに秒間・分間あたりのリクエスト数を制限します。
    
- **CORS**: `@CrossOrigin` アノテーションは開発中の利用に留め、本番環境では `WebMvcConfigurer` を用いて許可するオリジン、メソッド、ヘッダーをグローバルに設定します。
    

---

## 2. コーディング規約

詳細なコーディング規約については、[code-conventions.md](./code-conventions.md) を参照してください。

### 主要なポイント

- **命名規則**: PascalCase（クラス）、camelCase（メソッド・変数）、UPPER_SNAKE_CASE（定数）
- **パッケージ構造**: レイヤー別の明確な分離（controller, service, repository, domain, dto等）
- **エラーハンドリング**: 統一されたエラーレスポンス形式とグローバル例外ハンドラー
- **ログ出力**: SLF4J + Logback による構造化ログ
- **テスト戦略**: 単体テスト70%、統合テスト20%、E2Eテスト10%のテストピラミッド
- **多言語対応**: MessageSourceを使用した国際化対応

### 2.1. パッケージ構造標準

**ベースパッケージ**: `jp.co.protosoft.rihua.api`

```
jp.co.protosoft.rihua.api/
├── RihuaApiApplication.java          # メインアプリケーションクラス
├── config/                           # 設定クラス
│   ├── SecurityConfig.java
│   ├── JacksonConfig.java
│   └── WebConfig.java
├── controller/                       # REST APIコントローラー
│   ├── AuthController.java
│   ├── UserController.java
│   └── EventController.java
├── service/                          # ビジネスロジック層
│   ├── UserService.java
│   ├── EventService.java
│   └── CustomUserDetailsService.java
├── repository/                       # データアクセス層
│   ├── UserRepository.java
│   ├── EventRepository.java
│   └── HousingRepository.java
├── domain/                          # エンティティクラス
│   ├── User.java
│   ├── Event.java
│   ├── Housing.java
│   └── enums/                       # 列挙型
│       ├── UserStatus.java
│       ├── EventCategory.java
│       └── HousingType.java
├── dto/                             # データ転送オブジェクト
│   ├── request/                     # リクエストDTO
│   │   ├── CreateUserRequest.java
│   │   └── UpdateEventRequest.java
│   └── response/                    # レスポンスDTO
│       ├── UserResponse.java
│       ├── EventResponse.java
│       └── ErrorResponse.java
├── security/                        # セキュリティ関連
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtAuthenticationEntryPoint.java
│   └── UserPrincipal.java
├── exception/                       # カスタム例外
│   ├── BusinessException.java
│   ├── UserNotFoundException.java
│   └── ValidationException.java
└── util/                           # ユーティリティクラス
    ├── DateUtils.java
    ├── ValidationUtils.java
    └── MessageUtils.java
```

**パッケージ命名規則**:
- 企業ドメイン: `jp.co.protosoft` (株式会社プロトソフト)
- プロジェクト名: `rihua` (日华プロジェクト)
- アプリケーション種別: `api` (REST API)
    

---

## 3. パフォーマンス要件

### 3.1. レスポンスとクエリ

- **API平均レスポンス**: **200ms** 以下を目指します。
    
- **データベースクエリ**: **N+1問題**を撲滅します。JPAの **Fetch Join** や `@EntityGraph` を活用して、必要な関連データを一度のクエリで取得します。クエリの実行時間は **100ms** 以下を目標とします。
    
- **ファイルアップロード**: 10MBのファイルを **30秒** 以内に処理完了することを目標とします。
    

### 3.2. キャッシュ戦略

- **Redis**: ユーザーセッション、頻繁に参照されるが更新頻度の低いマスタデータ、JWTのリフレッシュトークンなどのキャッシュに利用します。
    
- **アプリケーションキャッシュ**: **Spring Cache** (`@Cacheable`, `@CachePut`, `@CacheEvict`) を活用し、Service層のメソッド結果をキャッシュします。
    
- **CDN (Content Delivery Network)**: 静的なアセット（画像、CSS、JSファイルなど）はCDN経由で配信します。
    

### 3.3. 非同期処理と接続数

- **非同期処理**: メールの送信、プッシュ通知、時間のかかるデータ処理などは `@Async` を用いて非同期化し、APIのレスポンスタイムを向上させます。
    
- **同時接続**: 1,000ユーザーからの同時アクセス、**10,000 req/min** のAPIコールに耐えうる設計とします。
    
- **データベース接続プール**: HikariCPを使用し、最大接続数を **50** に設定します。負荷テストに基づき、適切な値に調整します。
    

---

## 4. 外部サービス統合

### 4.1. APIクライアント

- **HTTPクライアント**: 外部APIとの通信には、ノンブロッキングI/Oをサポートする **`WebClient`** の使用を推奨します。`RestTemplate` も利用可能ですが、新規実装では `WebClient` を優先します。
    
- **リトライとタイムアウト**: 全ての外部API呼び出しには、適切な接続・読み取りタイムアウトと、冪等性の高い処理に対するリトライ機構（例: Resilience4j）を実装します。
    

### 4.2. ファイルストレージ (AWS S3)

- **アップロードフロー**: クライアントから直接S3にアップロードするための **署名付きURL (Presigned URL)** を発行するAPIを実装し、サーバーの負荷を軽減します。
    
- **ファイル検証**: アップロードされたファイルは、サーバーサイドでファイルサイズ、MIMEタイプを検証します。必要に応じてマルウェアスキャンも実施します。
    
- **アクセス制御**: S3バケットポリシーとIAMロールを適切に設定し、最小権限の原則に従います。
    

### 4.3. 通知システム

- **プッシュ通知 (Expo Push) / メール送信 (AWS SES)**: 通知処理は、`@Async` を利用するか、SQSなどのメッセージキューを介して完全に非同期化し、APIの応答性に影響を与えないようにします。
    

---

## 5. 多言語対応 (i18n)

### 5.1. 国際化の実装

- **メッセージ管理**: Springの **`MessageSource`** を使用します。言語ごとのメッセージファイル (`messages.properties`, `messages_zh_CN.properties`, `messages_ja.properties` など) を用意します。
    
- **言語の検出**: HTTPリクエストの **`Accept-Language` ヘッダー** を解析する `AcceptHeaderLocaleResolver` を設定し、ユーザーの言語設定に応じて自動でロケールを切り替えます。
    
- **デフォルト言語**: **中国語（簡体字: `zh-CN`）** をデフォルトの言語とします。
    
- **エンコーディング**: 全てのファイル、データベース、APIレスポンスの文字エンコーディングは **UTF-8** に統一します。
    

### 5.2. コンテンツ管理

- **言語別コンテンツ**: データベースで多言語コンテンツを管理する際は、前述の通りコンテンツテーブルを分離します。
    
- **フォールバック**: リクエストされた言語の翻訳が存在しない場合は、デフォルト言語（中国語簡体字）のコンテンツを返却するフォールバック機構を実装します。