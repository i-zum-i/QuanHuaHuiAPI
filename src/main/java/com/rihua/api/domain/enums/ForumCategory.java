package com.rihua.api.domain.enums;

/**
 * フォーラムカテゴリ列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum ForumCategory {
    /**
     * 一般質問
     */
    GENERAL,
    
    /**
     * 住居・不動産
     */
    HOUSING,
    
    /**
     * 求人・転職
     */
    JOBS,
    
    /**
     * 生活情報
     */
    LIFESTYLE,
    
    /**
     * 法律・ビザ
     */
    LEGAL,
    
    /**
     * 教育・学習
     */
    EDUCATION,
    
    /**
     * 健康・医療
     */
    HEALTH,
    
    /**
     * 買い物・売買
     */
    MARKETPLACE,
    
    /**
     * イベント・交流
     */
    EVENTS,
    
    /**
     * 技術・IT
     */
    TECHNOLOGY,
    
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
            case GENERAL -> "一般質問";
            case HOUSING -> "住居・不動産";
            case JOBS -> "求人・転職";
            case LIFESTYLE -> "生活情報";
            case LEGAL -> "法律・ビザ";
            case EDUCATION -> "教育・学習";
            case HEALTH -> "健康・医療";
            case MARKETPLACE -> "買い物・売買";
            case EVENTS -> "イベント・交流";
            case TECHNOLOGY -> "技術・IT";
            case OTHER -> "その他";
        };
    }
}