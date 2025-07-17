
# バックエンド API 要件定義書

## 概要
中国系住民向けコミュニティアプリ「Rihua（日华）」のバックエンドAPIシステムです。
フロントエンド（iOS/Android）アプリと管理者Webアプリの両方に対してRESTful APIを提供します。

## 目的
- **統合API**: モバイルアプリと管理画面への統一されたAPIインターフェース
- **認証・認可**: JWT + Role-based Access Control による安全なアクセス制御
- **スケーラビリティ**: 高トラフィックに対応できる拡張性
- **データ整合性**: トランザクション管理による信頼性の高いデータ操作

## 技術スタック

### フレームワーク・言語
- **フレームワーク**: Spring Boot 3.x
- **言語**: Java 17
- **アーキテクチャ**: Layered Architecture (Controller → Service → Repository)
- **API仕様**: OpenAPI 3.1 (springdoc-openapi)

### データベース
- **メインDB**: Amazon Aurora MySQL 8.0
- **キャッシュ**: Redis 7.x（セッション・頻繁なクエリ）
- **ファイルストレージ**: Amazon S3（画像・動画・ドキュメント）

### セキュリティ
- **認証**: Spring Security + JWT
- **認可**: Role-based Access Control (RBAC)
- **暗号化**: HTTPS、データベース暗号化
- **監査**: Spring Data Envers（データ変更履歴）

### 外部連携
- **決済**: Stripe API（チケット購入・掲載料）
- **通知**: Expo Push Notifications API
- **メール**: Amazon SES
- **地図**: Google Maps API

### インフラ・運用
- **デプロイ**: Replit（開発・本番）
- **監視**: Micrometer + Prometheus
- **ログ**: Logback + CloudWatch
- **CI/CD**: GitHub Actions

## データベース設計

### 主要エンティティ

#### ユーザー管理
```sql
-- ユーザー基本情報
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('USER', 'BUSINESS', 'ADMIN', 'SUPER_ADMIN') DEFAULT 'USER',
    status ENUM('ACTIVE', 'SUSPENDED', 'BANNED') DEFAULT 'ACTIVE',
    language_preference ENUM('zh-CN', 'ja-JP') DEFAULT 'zh-CN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- プロフィール詳細
CREATE TABLE user_profiles (
    user_id BIGINT PRIMARY KEY,
    display_name VARCHAR(100),
    avatar_url VARCHAR(500),
    phone VARCHAR(20),
    wechat_id VARCHAR(50),
    bio TEXT,
    verification_status ENUM('UNVERIFIED', 'PENDING', 'VERIFIED') DEFAULT 'UNVERIFIED',
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### イベント・チケット
```sql
-- イベント
CREATE TABLE events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category ENUM('CULTURAL', 'BUSINESS', 'EDUCATION', 'SOCIAL') NOT NULL,
    start_datetime DATETIME NOT NULL,
    end_datetime DATETIME NOT NULL,
    venue_name VARCHAR(200),
    venue_address TEXT,
    venue_latitude DECIMAL(10, 8),
    venue_longitude DECIMAL(11, 8),
    organizer_id BIGINT NOT NULL,
    max_capacity INT,
    status ENUM('DRAFT', 'PUBLISHED', 'CANCELLED', 'COMPLETED') DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer_id) REFERENCES users(id)
);

-- チケット種別
CREATE TABLE ticket_types (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    max_per_user INT DEFAULT 5,
    sale_start_datetime DATETIME,
    sale_end_datetime DATETIME,
    FOREIGN KEY (event_id) REFERENCES events(id)
);

-- チケット購入
CREATE TABLE ticket_purchases (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    ticket_type_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'PAID', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING',
    payment_id VARCHAR(100),
    qr_code VARCHAR(500),
    purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (ticket_type_id) REFERENCES ticket_types(id)
);
```

#### 住まい・求人
```sql
-- 住まい投稿
CREATE TABLE housing_posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type ENUM('RENT', 'SALE', 'SHARE') NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    deposit DECIMAL(10, 2),
    size_sqm DECIMAL(6, 2),
    rooms INT,
    address TEXT NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    nearest_station VARCHAR(100),
    status ENUM('DRAFT', 'PUBLISHED', 'RENTED', 'SOLD') DEFAULT 'DRAFT',
    published_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 求人投稿
CREATE TABLE job_posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    company_name VARCHAR(200),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    employment_type ENUM('FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERN') NOT NULL,
    salary_min DECIMAL(10, 2),
    salary_max DECIMAL(10, 2),
    location VARCHAR(200),
    visa_support BOOLEAN DEFAULT FALSE,
    chinese_required BOOLEAN DEFAULT FALSE,
    japanese_level ENUM('N5', 'N4', 'N3', 'N2', 'N1', 'NATIVE', 'NOT_REQUIRED'),
    status ENUM('DRAFT', 'PUBLISHED', 'CLOSED') DEFAULT 'DRAFT',
    published_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### 投稿・コミュニティ
```sql
-- 掲示板投稿
CREATE TABLE posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category ENUM('GENERAL', 'QUESTION', 'NEWS', 'LIFE', 'BUSINESS') NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    status ENUM('PUBLISHED', 'HIDDEN', 'DELETED') DEFAULT 'PUBLISHED',
    views_count INT DEFAULT 0,
    likes_count INT DEFAULT 0,
    comments_count INT DEFAULT 0,
    is_pinned BOOLEAN DEFAULT FALSE,
    published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- コメント
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    parent_comment_id BIGINT,
    status ENUM('PUBLISHED', 'HIDDEN', 'DELETED') DEFAULT 'PUBLISHED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (parent_comment_id) REFERENCES comments(id)
);
```

#### 通知・システム
```sql
-- 通知
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type ENUM('EVENT', 'COMMENT', 'LIKE', 'SYSTEM', 'PROMOTION') NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    related_id BIGINT,
    related_type VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ファイル管理
CREATE TABLE file_uploads (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    related_type VARCHAR(50),
    related_id BIGINT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## API エンドポイント設計

### 1. 認証・ユーザー管理

#### 認証
```
POST   /api/auth/register          # 新規登録
POST   /api/auth/login             # ログイン
POST   /api/auth/refresh           # トークン更新
POST   /api/auth/logout            # ログアウト
POST   /api/auth/forgot-password   # パスワードリセット要求
POST   /api/auth/reset-password    # パスワードリセット実行
```

#### ユーザー管理
```
GET    /api/users/me               # 自分の情報取得
PUT    /api/users/me               # プロフィール更新
POST   /api/users/me/avatar        # アバター画像アップロード
GET    /api/users/{id}             # 他ユーザー情報取得（公開情報のみ）
POST   /api/users/verification     # 本人確認申請
```

### 2. イベント・チケット

#### フロントエンド向け
```
GET    /api/events                 # イベント一覧（検索・フィルタ）
GET    /api/events/{id}            # イベント詳細
POST   /api/events                 # イベント作成（認証企業のみ）
PUT    /api/events/{id}            # イベント更新
DELETE /api/events/{id}            # イベント削除

GET    /api/events/{id}/tickets    # チケット種別一覧
POST   /api/tickets/purchase       # チケット購入
GET    /api/tickets/my-tickets     # 購入チケット一覧
GET    /api/tickets/{id}/qr        # チケットQRコード取得
```

#### 管理者向け
```
GET    /api/admin/events                    # 全イベント管理
PUT    /api/admin/events/{id}/status        # イベント状態変更
GET    /api/admin/events/{id}/participants  # 参加者一覧
POST   /api/admin/events/{id}/notify        # 参加者通知送信
GET    /api/admin/tickets/sales-report      # 売上レポート
```

### 3. 住まい検索

#### フロントエンド向け
```
GET    /api/housing                # 物件一覧（検索・フィルタ）
GET    /api/housing/{id}           # 物件詳細
POST   /api/housing                # 物件投稿
PUT    /api/housing/{id}           # 物件更新
DELETE /api/housing/{id}           # 物件削除
POST   /api/housing/{id}/inquiry   # 物件問い合わせ
```

#### 管理者向け
```
GET    /api/admin/housing                # 全物件管理
PUT    /api/admin/housing/{id}/status    # 物件承認・却下
GET    /api/admin/housing/pending        # 承認待ち物件
```

### 4. 求人検索

#### フロントエンド向け
```
GET    /api/jobs                   # 求人一覧（検索・フィルタ）
GET    /api/jobs/{id}              # 求人詳細
POST   /api/jobs                   # 求人投稿
PUT    /api/jobs/{id}              # 求人更新
DELETE /api/jobs/{id}              # 求人削除
POST   /api/jobs/{id}/apply        # 求人応募
```

#### 管理者向け
```
GET    /api/admin/jobs                # 全求人管理
PUT    /api/admin/jobs/{id}/status    # 求人承認・却下
GET    /api/admin/jobs/pending        # 承認待ち求人
```

### 5. 掲示板・コミュニティ

#### フロントエンド向け
```
GET    /api/posts                  # 投稿一覧（カテゴリ・検索）
GET    /api/posts/{id}             # 投稿詳細
POST   /api/posts                  # 新規投稿
PUT    /api/posts/{id}             # 投稿編集
DELETE /api/posts/{id}             # 投稿削除
POST   /api/posts/{id}/like        # いいね
DELETE /api/posts/{id}/like        # いいね取消

GET    /api/posts/{id}/comments    # コメント一覧
POST   /api/posts/{id}/comments    # コメント投稿
PUT    /api/comments/{id}          # コメント編集
DELETE /api/comments/{id}          # コメント削除
```

#### 管理者向け
```
GET    /api/admin/posts                 # 全投稿管理
PUT    /api/admin/posts/{id}/status     # 投稿状態変更
GET    /api/admin/posts/reports         # 通報された投稿
PUT    /api/admin/posts/{id}/pin        # 投稿ピン留め
```

### 6. 通知システム

#### フロントエンド向け
```
GET    /api/notifications          # 通知一覧
PUT    /api/notifications/{id}/read # 通知既読
PUT    /api/notifications/read-all # 全通知既読
GET    /api/notifications/settings # 通知設定取得
PUT    /api/notifications/settings # 通知設定更新
```

#### 管理者向け
```
POST   /api/admin/notifications/broadcast # 一斉通知送信
GET    /api/admin/notifications/stats     # 通知統計
```

### 7. ファイル管理

```
POST   /api/files/upload           # ファイルアップロード
GET    /api/files/{id}             # ファイル取得
DELETE /api/files/{id}             # ファイル削除
```

### 8. 管理者専用API

#### ユーザー管理
```
GET    /api/admin/users                 # ユーザー一覧
GET    /api/admin/users/{id}            # ユーザー詳細
PUT    /api/admin/users/{id}/status     # アカウント状態変更
POST   /api/admin/users/{id}/warning    # 警告送信
GET    /api/admin/users/{id}/activities # ユーザー活動履歴
PUT    /api/admin/users/{id}/role       # 役割変更
```

#### 分析・レポート
```
GET    /api/admin/analytics/overview    # KPI概要
GET    /api/admin/analytics/users       # ユーザー分析
GET    /api/admin/analytics/revenue     # 収益分析
GET    /api/admin/analytics/content     # コンテンツ分析
GET    /api/admin/analytics/events      # イベント分析
```

#### システム設定
```
GET    /api/admin/settings              # システム設定取得
PUT    /api/admin/settings              # システム設定更新
GET    /api/admin/audit-logs            # 操作ログ
```

## リクエスト・レスポンス例

### 認証
```json
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "success": true,
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIs...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIs...",
    "expires_in": 3600,
    "user": {
      "id": 1,
      "username": "user123",
      "email": "user@example.com",
      "role": "USER",
      "profile": {
        "display_name": "张三",
        "avatar_url": "https://s3.../avatar.jpg"
      }
    }
  }
}
```

### イベント一覧
```json
GET /api/events?category=CULTURAL&city=東京&page=1&size=20

Response:
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "春节联欢晚会",
        "description": "2025年春节庆祝活动",
        "category": "CULTURAL",
        "start_datetime": "2025-02-01T19:00:00",
        "end_datetime": "2025-02-01T22:00:00",
        "venue": {
          "name": "東京国際フォーラム",
          "address": "東京都千代田区丸の内3-5-1",
          "latitude": 35.6762,
          "longitude": 139.7633
        },
        "organizer": {
          "id": 5,
          "name": "在日中国文化协会",
          "verified": true
        },
        "tickets": [
          {
            "id": 1,
            "name": "一般席",
            "price": 3000.00,
            "available": 150
          },
          {
            "id": 2,
            "name": "VIP席",
            "price": 8000.00,
            "available": 20
          }
        ],
        "image_url": "https://s3.../event1.jpg",
        "status": "PUBLISHED"
      }
    ],
    "page": 1,
    "size": 20,
    "total_elements": 45,
    "total_pages": 3
  }
}
```

### チケット購入
```json
POST /api/tickets/purchase
{
  "ticket_type_id": 1,
  "quantity": 2,
  "payment_method": "stripe"
}

Response:
{
  "success": true,
  "data": {
    "purchase_id": 123,
    "payment_intent_id": "pi_1J5K6l2eZvKYlo2C...",
    "client_secret": "pi_1J5K6l2eZvKYlo2C..._secret_...",
    "total_amount": 6000.00,
    "status": "PENDING"
  }
}
```

## セキュリティ仕様

### 認証・認可
- **JWT有効期限**: Access Token（1時間）、Refresh Token（30日）
- **Role-based Access Control**: USER < BUSINESS < ADMIN < SUPER_ADMIN
- **Rate Limiting**: API毎に適切な制限を設定
- **CORS**: フロントエンドドメインのみ許可

### データ保護
- **暗号化**: 保存時（Database）・転送時（HTTPS）
- **個人情報**: マスキング・仮名化処理
- **ファイルスキャン**: S3アップロード時のウイルス・マルウェア検知
- **入力検証**: SQLインジェクション・XSS対策

### 監査ログ
```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "user_id": 123,
  "role": "ADMIN",
  "action": "UPDATE_USER_STATUS",
  "resource": "/api/admin/users/456/status",
  "ip_address": "192.168.1.100",
  "user_agent": "Mozilla/5.0...",
  "details": {
    "target_user_id": 456,
    "old_status": "ACTIVE",
    "new_status": "SUSPENDED",
    "reason": "不適切な投稿"
  }
}
```

## エラーハンドリング

### エラーレスポンス形式
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "入力データに誤りがあります",
    "details": [
      {
        "field": "email",
        "message": "有効なメールアドレスを入力してください"
      }
    ]
  },
  "timestamp": "2025-01-15T10:30:00Z",
  "path": "/api/auth/register"
}
```

### HTTPステータスコード
- **200**: 成功
- **201**: 作成成功
- **400**: リクエスト不正
- **401**: 認証失敗
- **403**: 権限不足
- **404**: リソース不存在
- **409**: 重複エラー
- **422**: バリデーションエラー
- **429**: レート制限
- **500**: サーバーエラー

## パフォーマンス要件

### レスポンス時間
- **API平均レスポンス**: 200ms以下
- **データベースクエリ**: 100ms以下
- **ファイルアップロード**: 10MB/30秒以内
- **検索API**: 1秒以内

### スループット
- **同時接続数**: 1,000ユーザー
- **API呼び出し**: 10,000 req/min
- **データベース接続**: Pool最大50接続

### キャッシュ戦略
- **Redis**: セッション、頻繁なクエリ結果
- **CDN**: 静的ファイル（画像・動画）
- **データベース**: クエリキャッシュ有効

## 開発・運用

### 開発環境セットアップ
```bash
# プロジェクト作成
spring init --dependencies=web,security,data-jpa,redis,validation \
  --java-version=17 --type=maven-project rihua-api

# 必要な依存関係追加
# pom.xml に springdoc-openapi, jwt, aws-sdk など追加

# 開発サーバー起動
mvn spring-boot:run
```

### 環境変数
```properties
# データベース
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/rihua
SPRING_DATASOURCE_USERNAME=rihua_user
SPRING_DATASOURCE_PASSWORD=your_password

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=3600

# AWS
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_S3_BUCKET=rihua-files

# Stripe
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...

# Expo Push
EXPO_ACCESS_TOKEN=your-expo-token
```

### API ドキュメント
- **OpenAPI 3.1**: `http://localhost:8080/swagger-ui.html`
- **JSON仕様**: `http://localhost:8080/v3/api-docs`

### テスト
```bash
# 単体テスト
mvn test

# 統合テスト
mvn integration-test

# API テスト（Newman）
newman run postman/rihua-api.postman_collection.json
```

### デプロイ（Replit）
```bash
# Replit での実行
java -jar target/rihua-api-1.0.0.jar --server.port=5000
```

## 今後の拡張

### フェーズ1（MVP）
- 基本CRUD API
- 認証・認可
- ファイルアップロード
- 基本通知

### フェーズ2（機能拡張）
- リアルタイム通知（WebSocket）
- 高度な検索（Elasticsearch）
- AI コンテンツモデレーション
- 決済機能完全統合

### フェーズ3（スケールアップ）
- マイクロサービス化
- GraphQL API
- 機械学習推薦システム
- 多言語自動翻訳

---

## 参考リンク
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [JWT.io](https://jwt.io/)
- [Stripe API Documentation](https://stripe.com/docs/api)
