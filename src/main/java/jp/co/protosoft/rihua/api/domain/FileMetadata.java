package jp.co.protosoft.rihua.api.domain;

import jp.co.protosoft.rihua.api.domain.enums.FileType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ファイルメタデータエンティティ
 * 
 * <p>アップロードされたファイルのメタデータを管理します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "file_metadata")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @NotBlank
    @Size(max = 255)
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @NotBlank
    @Size(max = 255)
    @Column(name = "stored_filename", nullable = false, length = 255)
    private String storedFilename;

    @NotBlank
    @Size(max = 1000)
    @Column(name = "file_path", nullable = false, length = 1000)
    private String filePath;

    @NotBlank
    @Size(max = 1000)
    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @NotNull
    @Min(0)
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Size(max = 100)
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Size(max = 32)
    @Column(name = "file_hash", length = 32)
    private String fileHash;

    @Size(max = 100)
    @Column(name = "related_entity_type", length = 100)
    private String relatedEntityType;

    @Size(max = 36)
    @Column(name = "related_entity_id", length = 36)
    private String relatedEntityId;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @Column(name = "is_temporary", nullable = false)
    @Builder.Default
    private Boolean isTemporary = false;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 削除済みかどうかを確認
     * 
     * @return 削除済みの場合true
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * 公開ファイルかどうかを確認
     * 
     * @return 公開ファイルの場合true
     */
    public boolean isPublic() {
        return Boolean.TRUE.equals(isPublic);
    }

    /**
     * 一時ファイルかどうかを確認
     * 
     * @return 一時ファイルの場合true
     */
    public boolean isTemporary() {
        return Boolean.TRUE.equals(isTemporary);
    }

    /**
     * 期限切れかどうかを確認
     * 
     * @return 期限切れの場合true
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * 画像ファイルかどうかを確認
     * 
     * @return 画像ファイルの場合true
     */
    public boolean isImage() {
        return fileType.isImage();
    }

    /**
     * ドキュメントファイルかどうかを確認
     * 
     * @return ドキュメントファイルの場合true
     */
    public boolean isDocument() {
        return fileType.isDocument();
    }

    /**
     * ファイルサイズを人間が読みやすい形式で取得
     * 
     * @return ファイルサイズ文字列
     */
    public String getHumanReadableFileSize() {
        if (fileSize == null) {
            return "0 B";
        }
        
        long size = fileSize;
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%d %s", size, units[unitIndex]);
    }

    /**
     * ファイル拡張子を取得
     * 
     * @return ファイル拡張子
     */
    public String getFileExtension() {
        if (originalFilename == null) {
            return null;
        }
        
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == originalFilename.length() - 1) {
            return null;
        }
        
        return originalFilename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 関連エンティティ情報を設定
     * 
     * @param entityType エンティティタイプ
     * @param entityId エンティティID
     */
    public void setRelatedEntity(String entityType, UUID entityId) {
        this.relatedEntityType = entityType;
        this.relatedEntityId = entityId.toString();
    }

    /**
     * 関連エンティティIDをUUIDで取得
     * 
     * @return 関連エンティティID
     */
    public UUID getRelatedEntityIdAsUUID() {
        if (relatedEntityId == null) {
            return null;
        }
        try {
            return UUID.fromString(relatedEntityId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * ソフトデリート実行
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 一時ファイルを永続化
     */
    public void makePermanent() {
        this.isTemporary = false;
        this.expiresAt = null;
    }
}