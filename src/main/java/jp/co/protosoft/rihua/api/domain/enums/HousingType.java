package jp.co.protosoft.rihua.api.domain.enums;

/**
 * 住居タイプ列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum HousingType {
    /**
     * 賃貸
     */
    RENT,
    
    /**
     * 売買
     */
    SALE,
    
    /**
     * シェアハウス
     */
    SHARE,
    
    /**
     * 短期賃貸
     */
    SHORT_TERM,
    
    /**
     * 学生寮
     */
    DORMITORY;

    /**
     * タイプの表示名を取得
     * 
     * @return 表示名
     */
    public String getDisplayName() {
        return switch (this) {
            case RENT -> "賃貸";
            case SALE -> "売買";
            case SHARE -> "シェアハウス";
            case SHORT_TERM -> "短期賃貸";
            case DORMITORY -> "学生寮";
        };
    }

    /**
     * 賃貸系かどうかを判定
     * 
     * @return 賃貸系の場合true
     */
    public boolean isRental() {
        return this == RENT || this == SHARE || this == SHORT_TERM || this == DORMITORY;
    }
}