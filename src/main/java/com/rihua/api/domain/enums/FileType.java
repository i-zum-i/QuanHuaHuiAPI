package com.rihua.api.domain.enums;

/**
 * ファイルタイプ列挙型
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
public enum FileType {
    /**
     * 画像ファイル
     */
    IMAGE,
    
    /**
     * ドキュメントファイル
     */
    DOCUMENT,
    
    /**
     * 動画ファイル
     */
    VIDEO,
    
    /**
     * 音声ファイル
     */
    AUDIO,
    
    /**
     * その他
     */
    OTHER;

    /**
     * MIMEタイプから判定
     * 
     * @param mimeType MIMEタイプ
     * @return ファイルタイプ
     */
    public static FileType fromMimeType(String mimeType) {
        if (mimeType == null) {
            return OTHER;
        }
        
        String type = mimeType.toLowerCase();
        
        if (type.startsWith("image/")) {
            return IMAGE;
        } else if (type.startsWith("video/")) {
            return VIDEO;
        } else if (type.startsWith("audio/")) {
            return AUDIO;
        } else if (type.equals("application/pdf") || 
                   type.startsWith("application/msword") ||
                   type.startsWith("application/vnd.openxmlformats-officedocument") ||
                   type.equals("text/plain")) {
            return DOCUMENT;
        }
        
        return OTHER;
    }

    /**
     * 画像ファイルかどうかを判定
     * 
     * @return 画像ファイルの場合true
     */
    public boolean isImage() {
        return this == IMAGE;
    }

    /**
     * ドキュメントファイルかどうかを判定
     * 
     * @return ドキュメントファイルの場合true
     */
    public boolean isDocument() {
        return this == DOCUMENT;
    }
}