package com.rihua.api.domain.enums;

/**
 * イベントカテゴリ列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum EventCategory {
    /**
     * 文化イベント
     */
    CULTURAL,
    
    /**
     * ビジネス・ネットワーキング
     */
    BUSINESS,
    
    /**
     * 教育・セミナー
     */
    EDUCATION,
    
    /**
     * エンターテイメント
     */
    ENTERTAINMENT,
    
    /**
     * スポーツ・フィットネス
     */
    SPORTS,
    
    /**
     * 料理・グルメ
     */
    FOOD,
    
    /**
     * 旅行・観光
     */
    TRAVEL,
    
    /**
     * 技術・IT
     */
    TECHNOLOGY,
    
    /**
     * 健康・ウェルネス
     */
    HEALTH,
    
    /**
     * その他
     */
    OTHER;

    /**
     * カテゴリの表示名を取得
     * 
     * @return 表示名
     */
    public String getDisplayName() {
        return switch (this) {
            case CULTURAL -> "文化イベント";
            case BUSINESS -> "ビジネス・ネットワーキング";
            case EDUCATION -> "教育・セミナー";
            case ENTERTAINMENT -> "エンターテイメント";
            case SPORTS -> "スポーツ・フィットネス";
            case FOOD -> "料理・グルメ";
            case TRAVEL -> "旅行・観光";
            case TECHNOLOGY -> "技術・IT";
            case HEALTH -> "健康・ウェルネス";
            case OTHER -> "その他";
        };
    }
}