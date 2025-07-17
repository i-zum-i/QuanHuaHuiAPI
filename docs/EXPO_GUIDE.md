
# Expo 開発・ビルドガイド

## Expo の概要

Expoは React Native アプリケーションの開発・ビルド・配布を効率化するプラットフォームです。このプロジェクトでは Expo SDK 53.0.9 を使用しています。

## 開発環境

### Expo の開発方式

1. **Expo Go での開発**
   - 最も簡単な開発方法
   - QRコードでデバイスに即座にアプリを配信
   - 一部のネイティブ機能に制限あり

2. **Development Build**
   - カスタムネイティブコードを含む開発版
   - 全てのネイティブ機能にアクセス可能
   - EAS Build を使用してビルド

3. **Expo Dev Client**
   - 開発専用のカスタムクライアント
   - 本番環境に近い環境でのテスト

## EAS（Expo Application Services）

### EAS Build
ネイティブアプリケーションをクラウドでビルドするサービス

```bash
# EAS プロジェクトの初期化
npx eas init

# iOS アプリのビルド
npx eas build --platform ios

# Android アプリのビルド
npx eas build --platform android

# 両プラットフォーム同時ビルド
npx eas build --platform all
```

### EAS Update
JavaScript/アセットの差分配信（OTA Update）

```bash
# 本番環境への更新配信
npx eas update --branch production

# 開発環境への更新配信
npx eas update --branch development

# 自動更新
npx eas update --auto
```

### EAS Submit
App Store / Google Play Store への自動提出

```bash
# iOS App Store への提出
npx eas submit --platform ios

# Google Play Store への提出
npx eas submit --platform android
```

## ネイティブアプリへの変換

### 1. Expo Managed Workflow
- **特徴**: Expo が管理するワークフロー
- **メリット**: 設定が簡単、Expo サービスを最大限活用
- **デメリット**: カスタムネイティブコードに制限

### 2. Expo Bare Workflow
- **特徴**: React Native CLI プロジェクトに Expo SDK を統合
- **メリット**: 完全なネイティブコード制御
- **デメリット**: 設定が複雑

### 3. Expo Prebuild
現在推奨されるアプローチ

```bash
# ネイティブプロジェクトの生成
npx expo prebuild

# iOS プロジェクトの生成
npx expo prebuild --platform ios

# Android プロジェクトの生成
npx expo prebuild --platform android
```

## ビルド設定

### eas.json の設定例

```json
{
  "build": {
    "development": {
      "developmentClient": true,
      "distribution": "internal"
    },
    "preview": {
      "distribution": "internal"
    },
    "production": {
      "distribution": "store"
    }
  },
  "submit": {
    "production": {
      "ios": {
        "appleId": "your-apple-id@example.com",
        "ascAppId": "1234567890"
      },
      "android": {
        "serviceAccountKeyPath": "path/to/api-key.json",
        "track": "internal"
      }
    }
  }
}
```

## プラットフォーム固有の設定

### iOS 設定 (app.json)
```json
{
  "expo": {
    "ios": {
      "bundleIdentifier": "com.yourcompany.yourapp",
      "buildNumber": "1.0.0",
      "supportsTablet": true,
      "infoPlist": {
        "NSCameraUsageDescription": "カメラへのアクセスが必要です"
      }
    }
  }
}
```

### Android 設定 (app.json)
```json
{
  "expo": {
    "android": {
      "package": "com.yourcompany.yourapp",
      "versionCode": 1,
      "permissions": [
        "CAMERA",
        "WRITE_EXTERNAL_STORAGE"
      ]
    }
  }
}
```

## 配布方法

### 1. 内部配布
```bash
# TestFlight (iOS) / Internal Testing (Android)
npx eas build --platform all --profile preview
```

### 2. ストア配布
```bash
# 本番ビルド
npx eas build --platform all --profile production

# ストア提出
npx eas submit --platform all
```

### 3. OTA Update
```bash
# 即座に更新配信
npx eas update --branch production --message "バグ修正"
```

## 重要な技術的考慮事項

### 1. バイナリサイズ
- Expo SDK は多くの機能を含むため、バイナリサイズが大きくなる
- 不要な機能は除外することを推奨

### 2. パフォーマンス
- JavaScript ブリッジを経由するため、計算集約的な処理は避ける
- 必要に応じてネイティブモジュールを使用

### 3. アップデート戦略
- 重要な修正: OTA Update で即座に配信
- 新機能: ストア経由でのアップデート
- App Store Review Guidelines に準拠

### 4. セキュリティ
- 機密情報は環境変数で管理
- App Store Connect / Google Play Console でのセキュリティ設定

## トラブルシューティング

### よくある問題と解決策

1. **Metro bundler エラー**
   ```bash
   npx expo start --clear
   ```

2. **キャッシュ問題**
   ```bash
   npx expo start --clear
   rm -rf node_modules
   npm install
   ```

3. **ビルドエラー**
   ```bash
   npx eas build --platform ios --clear-cache
   ```

## 本番環境へのデプロイ

### 1. 環境設定
```bash
# 本番環境変数の設定
npx eas secret:push --scope project --env-file .env.production
```

### 2. 本番ビルド
```bash
# 本番ビルドの実行
npx eas build --platform all --profile production
```

### 3. ストア提出
```bash
# 自動提出
npx eas submit --platform all --profile production
```

## 監視とメンテナンス

### 1. アプリケーション監視
- Expo Application Services ダッシュボードでビルド状況確認
- クラッシュレポートの監視

### 2. 更新管理
- OTA Update の使用状況確認
- ユーザーの更新状況追跡

### 3. パフォーマンス最適化
- バンドルサイズの最適化
- 不要な依存関係の削除
- JavaScript パフォーマンスの改善

## 参考リンク

- [Expo Documentation](https://docs.expo.dev/)
- [EAS Documentation](https://docs.expo.dev/eas/)
- [React Native Documentation](https://reactnative.dev/)
- [Expo GitHub Repository](https://github.com/expo/expo)
