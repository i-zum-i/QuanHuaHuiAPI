# API標準とガイドライン

## 1. REST API設計原則

### 1.1. RESTful設計規約

- **リソース指向**: URIはリソースを表現する名詞（複数形）を使用
- **HTTPメソッド**: 操作はHTTPメソッドで表現
- **ステートレス**: 各リクエストは独立して処理可能
- **統一インターフェース**: 一貫したAPI設計パターン

### 1.2. URI設計規約

#### 基本パターン
```
/api/v{version}/{resource}
/api/v{version}/{resource}/{id}
/api/v{version}/{resource}/{id}/{sub-resource}
```

#### 具体例
```
GET    /api/v1/users                    # ユーザー一覧取得
GET    /api/v1/users/{id}               # 特定ユーザー取得
POST   /api/v1/users                    # ユーザー作成
PUT    /api/v1/users/{id}               # ユーザー更新（全体）
PATCH  /api/v1/users/{id}               # ユーザー更新（部分）
DELETE /api/v1/users/{id}               # ユーザー削除

GET    /api/v1/users/{id}/events        # ユーザーのイベント一覧
POST   /api/v1/events/{id}/tickets      # イベントのチケット購入
```

#### 命名規則
- **リソース名**: 複数形の名詞（`users`, `events`, `housing-posts`）
- **ケバブケース**: 複数単語はハイフンで区切り（`housing-posts`, `job-applications`）
- **階層構造**: 親子関係は最大2階層まで
- **動詞の禁止**: URIに動詞を含めない（`/api/v1/users/create` ❌）

### 1.3. HTTPメソッド使用規約

| メソッド | 用途 | 冪等性 | 安全性 | 例 |
|---------|------|--------|--------|-----|
| GET | リソース取得 | ✓ | ✓ | `GET /api/v1/users` |
| POST | リソース作成 | ✗ | ✗ | `POST /api/v1/users` |
| PUT | リソース更新（全体） | ✓ | ✗ | `PUT /api/v1/users/{id}` |
| PATCH | リソース更新（部分） | ✗ | ✗ | `PATCH /api/v1/users/{id}` |
| DELETE | リソース削除 | ✓ | ✗ | `DELETE /api/v1/users/{id}` |

## 2. APIバージョン管理

### 2.1. バージョニング戦略

- **URIパスバージョニング**: `/api/v1/`, `/api/v2/`
- **セマンティックバージョニング**: メジャー.マイナー.パッチ（例: 1.2.3）
- **後方互換性**: マイナーバージョンアップでは後方互換性を維持

### 2.2. バージョンアップ指針

#### メジャーバージョンアップ（v1 → v2）
- 破壊的変更を含む場合
- レスポンス構造の大幅な変更
- 認証方式の変更

#### マイナーバージョンアップ（v1.1 → v1.2）
- 新機能追加（後方互換性あり）
- 新しいエンドポイント追加
- レスポンスフィールド追加

#### パッチバージョンアップ（v1.1.1 → v1.1.2）
- バグ修正
- パフォーマンス改善
- セキュリティ修正

### 2.3. 廃止予定API管理

```http
HTTP/1.1 200 OK
Deprecation: true
Sunset: Sat, 31 Dec 2025 23:59:59 GMT
Link: </api/v2/users>; rel="successor-version"
```

## 3. 認証・認可フロー

### 3.1. JWT認証フロー

```mermaid
sequenceDiagram
    participant C as クライアント
    participant A as 認証API
    participant R as リソースAPI
    participant D as データベース
    
    C->>A: POST /api/v1/auth/login
    A->>D: ユーザー認証
    D-->>A: 認証結果
    A-->>C: JWT (access + refresh)
    
    C->>R: GET /api/v1/users/me (Bearer token)
    R->>R: JWT検証
    R->>D: データ取得
    D-->>R: ユーザーデータ
    R-->>C: レスポンス
```

### 3.2. 認証エンドポイント

```
POST   /api/v1/auth/register           # 新規登録
POST   /api/v1/auth/login              # ログイン
POST   /api/v1/auth/refresh            # トークン更新
POST   /api/v1/auth/logout             # ログアウト
POST   /api/v1/auth/forgot-password    # パスワードリセット要求
POST   /api/v1/auth/reset-password     # パスワードリセット実行
POST   /api/v1/auth/verify-email       # メール認証
```

### 3.3. 認証ヘッダー

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 3.4. 権限レベル

| 権限 | 説明 | アクセス範囲 |
|------|------|-------------|
| USER | 一般ユーザー | 基本機能、自分のデータ |
| BUSINESS | 事業者 | 投稿作成、イベント管理 |
| MODERATOR | モデレーター | コンテンツ管理 |
| ADMIN | 管理者 | ユーザー管理、システム設定 |
| SUPER_ADMIN | 最高管理者 | 全機能アクセス |

## 4. HTTPステータスコード

### 4.1. 成功レスポンス（2xx）

| コード | 意味 | 使用場面 |
|--------|------|----------|
| 200 | OK | GET、PUT、PATCH成功 |
| 201 | Created | POST成功（リソース作成） |
| 202 | Accepted | 非同期処理受付 |
| 204 | No Content | DELETE成功、レスポンスボディなし |

### 4.2. クライアントエラー（4xx）

| コード | 意味 | 使用場面 |
|--------|------|----------|
| 400 | Bad Request | リクエスト形式エラー |
| 401 | Unauthorized | 認証失敗 |
| 403 | Forbidden | 権限不足 |
| 404 | Not Found | リソース不存在 |
| 409 | Conflict | リソース競合（重複登録等） |
| 422 | Unprocessable Entity | バリデーションエラー |
| 429 | Too Many Requests | レート制限 |

### 4.3. サーバーエラー（5xx）

| コード | 意味 | 使用場面 |
|--------|------|----------|
| 500 | Internal Server Error | サーバー内部エラー |
| 502 | Bad Gateway | 外部サービスエラー |
| 503 | Service Unavailable | サービス一時停止 |
| 504 | Gateway Timeout | 外部サービスタイムアウト |

## 5. エラーレスポンス形式

### 5.1. 標準エラーレスポンス

```json
{
  "timestamp": "2025-07-17T14:30:00.123Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "入力データに問題があります",
  "path": "/api/v1/users",
  "details": {
    "email": "有効なメールアドレスを入力してください",
    "password": "パスワードは8文字以上である必要があります"
  }
}
```

### 5.2. エラーコード体系

#### 認証・認可エラー（1xxx）
- `1001`: INVALID_CREDENTIALS - 認証情報が正しくありません
- `1002`: TOKEN_EXPIRED - トークンの有効期限が切れています
- `1003`: INSUFFICIENT_PERMISSIONS - 権限が不足しています
- `1004`: ACCOUNT_SUSPENDED - アカウントが停止されています

#### バリデーションエラー（2xxx）
- `2001`: VALIDATION_ERROR - 入力データに問題があります
- `2002`: REQUIRED_FIELD_MISSING - 必須フィールドが不足しています
- `2003`: INVALID_FORMAT - データ形式が正しくありません
- `2004`: VALUE_OUT_OF_RANGE - 値が範囲外です

#### ビジネスロジックエラー（3xxx）
- `3001`: RESOURCE_NOT_FOUND - リソースが見つかりません
- `3002`: DUPLICATE_RESOURCE - 重複するリソースが存在します
- `3003`: OPERATION_NOT_ALLOWED - この操作は許可されていません
- `3004`: QUOTA_EXCEEDED - 制限を超過しました

#### 外部サービスエラー（4xxx）
- `4001`: EXTERNAL_SERVICE_ERROR - 外部サービスエラー
- `4002`: PAYMENT_FAILED - 決済処理に失敗しました
- `4003`: EMAIL_SEND_FAILED - メール送信に失敗しました
- `4004`: FILE_UPLOAD_FAILED - ファイルアップロードに失敗しました

#### システムエラー（5xxx）
- `5001`: INTERNAL_SERVER_ERROR - サーバー内部エラー
- `5002`: DATABASE_ERROR - データベースエラー
- `5003`: CONFIGURATION_ERROR - 設定エラー
- `5004`: MAINTENANCE_MODE - メンテナンス中です

## 6. リクエスト・レスポンス例

### 6.1. ユーザー登録

#### リクエスト
```http
POST /api/v1/auth/register
Content-Type: application/json
Accept-Language: zh-CN

{
  "email": "zhang.san@example.com",
  "username": "zhangsan",
  "password": "SecurePass123",
  "firstName": "三",
  "lastName": "张",
  "preferredLanguage": "zh-CN"
}
```

#### 成功レスポンス
```http
HTTP/1.1 201 Created
Content-Type: application/json
Location: /api/v1/users/123e4567-e89b-12d3-a456-426614174000

{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "email": "zhang.san@example.com",
  "username": "zhangsan",
  "firstName": "三",
  "lastName": "张",
  "status": "PENDING_VERIFICATION",
  "preferredLanguage": "zh-CN",
  "createdAt": "2025-07-17T14:30:00.123Z"
}
```

#### エラーレスポンス
```http
HTTP/1.1 422 Unprocessable Entity
Content-Type: application/json

{
  "timestamp": "2025-07-17T14:30:00.123Z",
  "status": 422,
  "error": "VALIDATION_ERROR",
  "message": "输入数据有问题",
  "path": "/api/v1/auth/register",
  "details": {
    "email": "邮箱地址已被使用",
    "password": "密码必须包含大小写字母和数字"
  }
}
```

### 6.2. イベント一覧取得

#### リクエスト
```http
GET /api/v1/events?category=CULTURAL&city=東京&page=0&size=20&sort=startDate,desc
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Accept-Language: ja
```

#### レスポンス
```http
HTTP/1.1 200 OK
Content-Type: application/json
X-Total-Count: 45
X-Page-Number: 0
X-Page-Size: 20
X-Total-Pages: 3

{
  "content": [
    {
      "id": "456e7890-e89b-12d3-a456-426614174001",
      "title": "春节联欢晚会",
      "description": "2025年春节庆祝活动",
      "category": "CULTURAL",
      "startDateTime": "2025-02-01T19:00:00+09:00",
      "endDateTime": "2025-02-01T22:00:00+09:00",
      "venue": {
        "name": "東京国際フォーラム",
        "address": "東京都千代田区丸の内3-5-1",
        "latitude": 35.6762,
        "longitude": 139.7633
      },
      "organizer": {
        "id": "789e0123-e89b-12d3-a456-426614174002",
        "name": "在日中国文化协会",
        "verified": true
      },
      "ticketTypes": [
        {
          "id": "abc12345-e89b-12d3-a456-426614174003",
          "name": "一般席",
          "price": 3000.00,
          "currency": "JPY",
          "availableQuantity": 150
        }
      ],
      "imageUrl": "https://cdn.rihua.com/events/spring-festival-2025.jpg",
      "status": "PUBLISHED",
      "createdAt": "2025-01-15T10:00:00+09:00"
    }
  ],
  "pageable": {
    "page": 0,
    "size": 20,
    "sort": ["startDate,desc"]
  },
  "totalElements": 45,
  "totalPages": 3,
  "first": true,
  "last": false
}
```

### 6.3. チケット購入

#### リクエスト
```http
POST /api/v1/tickets/purchase
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Content-Type: application/json

{
  "ticketTypeId": "abc12345-e89b-12d3-a456-426614174003",
  "quantity": 2,
  "paymentMethod": "stripe",
  "customerInfo": {
    "name": "张三",
    "email": "zhang.san@example.com",
    "phone": "+81-90-1234-5678"
  }
}
```

#### レスポンス
```http
HTTP/1.1 201 Created
Content-Type: application/json
Location: /api/v1/tickets/def67890-e89b-12d3-a456-426614174004

{
  "purchaseId": "def67890-e89b-12d3-a456-426614174004",
  "ticketTypeId": "abc12345-e89b-12d3-a456-426614174003",
  "quantity": 2,
  "unitPrice": 3000.00,
  "totalAmount": 6000.00,
  "currency": "JPY",
  "status": "PENDING_PAYMENT",
  "paymentIntentId": "pi_1J5K6l2eZvKYlo2C...",
  "clientSecret": "pi_1J5K6l2eZvKYlo2C..._secret_...",
  "expiresAt": "2025-07-17T15:00:00+09:00",
  "createdAt": "2025-07-17T14:30:00+09:00"
}
```

## 7. ページネーション

### 7.1. ページベースページネーション

#### リクエストパラメータ
```
GET /api/v1/users?page=0&size=20&sort=createdAt,desc
```

#### レスポンスヘッダー
```http
X-Total-Count: 1250
X-Page-Number: 0
X-Page-Size: 20
X-Total-Pages: 63
```

#### レスポンスボディ
```json
{
  "content": [...],
  "pageable": {
    "page": 0,
    "size": 20,
    "sort": ["createdAt,desc"]
  },
  "totalElements": 1250,
  "totalPages": 63,
  "first": true,
  "last": false
}
```

### 7.2. カーソルベースページネーション（大量データ用）

#### リクエスト
```
GET /api/v1/events?cursor=eyJjcmVhdGVkQXQiOiIyMDI1LTA3LTE3VDEwOjAwOjAwWiJ9&limit=20
```

#### レスポンス
```json
{
  "data": [...],
  "pagination": {
    "nextCursor": "eyJjcmVhdGVkQXQiOiIyMDI1LTA3LTE3VDA5OjMwOjAwWiJ9",
    "hasNext": true,
    "limit": 20
  }
}
```

## 8. 検索・フィルタリング

### 8.1. 検索パラメータ

```
GET /api/v1/housing?
  type=RENT&
  minPrice=50000&
  maxPrice=150000&
  location=東京&
  rooms=2&
  foreignerFriendly=true&
  keyword=新宿&
  sort=price,asc
```

### 8.2. 高度な検索（POST）

```http
POST /api/v1/housing/search
Content-Type: application/json

{
  "filters": {
    "type": ["RENT", "SHARE"],
    "priceRange": {
      "min": 50000,
      "max": 150000
    },
    "location": {
      "prefecture": "東京都",
      "city": "新宿区",
      "radius": 2000
    },
    "features": ["foreignerFriendly", "petAllowed"],
    "rooms": {
      "min": 1,
      "max": 3
    }
  },
  "sort": [
    {"field": "price", "direction": "asc"},
    {"field": "createdAt", "direction": "desc"}
  ],
  "page": 0,
  "size": 20
}
```

## 9. 多言語対応

### 9.1. 言語指定

#### Accept-Languageヘッダー
```http
Accept-Language: zh-CN,zh;q=0.9,ja;q=0.8,en;q=0.7
```

#### クエリパラメータ
```
GET /api/v1/events?lang=zh-CN
```

### 9.2. 多言語レスポンス

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "title": {
    "zh-CN": "春节联欢晚会",
    "ja": "春節パーティー"
  },
  "description": {
    "zh-CN": "欢迎参加2025年春节庆祝活动",
    "ja": "2025年春節お祝いイベントにご参加ください"
  },
  "currentLanguage": "zh-CN"
}
```

## 10. レート制限

### 10.1. レート制限ヘッダー

```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1642694400
Retry-After: 3600
```

### 10.2. レート制限エラー

```http
HTTP/1.1 429 Too Many Requests
Content-Type: application/json

{
  "timestamp": "2025-07-17T14:30:00.123Z",
  "status": 429,
  "error": "RATE_LIMIT_EXCEEDED",
  "message": "API呼び出し制限を超過しました",
  "path": "/api/v1/users",
  "retryAfter": 3600
}
```

## 11. セキュリティヘッダー

### 11.1. 必須セキュリティヘッダー

```http
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Content-Security-Policy: default-src 'self'
```

### 11.2. CORS設定

```http
Access-Control-Allow-Origin: https://app.rihua.com
Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Access-Control-Allow-Headers: Authorization, Content-Type, Accept-Language
Access-Control-Max-Age: 86400
```

## 12. API文書化

### 12.1. OpenAPI仕様

- **OpenAPI 3.1**準拠
- **Swagger UI**での対話的ドキュメント
- **自動生成**によるドキュメント同期

### 12.2. エンドポイント文書化例

```yaml
/api/v1/users/{id}:
  get:
    summary: ユーザー情報取得
    description: 指定されたIDのユーザー情報を取得します
    parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
          format: uuid
        example: "123e4567-e89b-12d3-a456-426614174000"
    responses:
      '200':
        description: ユーザー情報
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserResponse'
      '404':
        description: ユーザーが見つかりません
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ErrorResponse'
```

この標準に従うことで、一貫性があり、保守性の高い、国際化対応のREST APIを構築できます。