package jp.co.protosoft.rihua.api.domain.enums;

/**
 * 報告ステータス列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum ReportStatus {
    /**
     * 未処理
     */
    PENDING,
    
    /**
     * 処理中
     */
    IN_PROGRESS,
    
    /**
     * 承認（違反あり）
     */
    APPROVED,
    
    /**
     * 却下（違反なし）
     */
    REJECTED,
    
    /**
     * 解決済み
     */
    RESOLVED;

    /**
     * ステータスの表示名を取得
     * 
     * @return 表示名
     */
    public String getDisplayName() {
        return switch (this) {
            case PENDING -> "未処理";
            case IN_PROGRESS -> "処理中";
            case APPROVED -> "承認";
            case REJECTED -> "却下";
            case RESOLVED -> "解決済み";
        };
    }

    /**
     * 処理が必要なステータスかどうかを判定
     * 
     * @return 処理が必要な場合true
     */
    public boolean needsAction() {
        return this == PENDING || this == IN_PROGRESS;
    }

    /**
     * 完了したステータスかどうかを判定
     * 
     * @return 完了した場合true
     */
    public boolean isCompleted() {
        return this == APPROVED || this == REJECTED || this == RESOLVED;
    }
}