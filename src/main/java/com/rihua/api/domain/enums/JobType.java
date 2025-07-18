package com.rihua.api.domain.enums;

/**
 * 求人タイプ列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum JobType {
    /**
     * 正社員
     */
    FULL_TIME,
    
    /**
     * パートタイム
     */
    PART_TIME,
    
    /**
     * 契約社員
     */
    CONTRACT,
    
    /**
     * アルバイト
     */
    PART_TIME_JOB,
    
    /**
     * インターンシップ
     */
    INTERNSHIP,
    
    /**
     * フリーランス
     */
    FREELANCE,
    
    /**
     * リモートワーク
     */
    REMOTE;

    /**
     * タイプの表示名を取得
     * 
     * @return 表示名
     */
    public String getDisplayName() {
        return switch (this) {
            case FULL_TIME -> "正社員";
            case PART_TIME -> "パートタイム";
            case CONTRACT -> "契約社員";
            case PART_TIME_JOB -> "アルバイト";
            case INTERNSHIP -> "インターンシップ";
            case FREELANCE -> "フリーランス";
            case REMOTE -> "リモートワーク";
        };
    }

    /**
     * 正規雇用かどうかを判定
     * 
     * @return 正規雇用の場合true
     */
    public boolean isRegular() {
        return this == FULL_TIME;
    }

    /**
     * 学生向けかどうかを判定
     * 
     * @return 学生向けの場合true
     */
    public boolean isStudentFriendly() {
        return this == PART_TIME_JOB || this == INTERNSHIP || this == PART_TIME;
    }
}