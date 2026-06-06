package com.platform.modules.file.entity;

import com.platform.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "file_metadata", indexes = {
        @Index(name = "idx_file_metadata_user", columnList = "created_by"),
        @Index(name = "idx_file_metadata_type", columnList = "file_type"),
        @Index(name = "idx_file_metadata_name", columnList = "file_name")
})
@EqualsAndHashCode(callSuper = true)
public class FileMetadata extends BaseEntity {

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "storage_type")
    private String storageType; // LOCAL, S3, AZURE

    @Column(name = "storage_path")
    private String storagePath; // S3 bucket path, Azure container, etc.

    @Column(name = "description")
    private String description;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "download_count")
    private Long downloadCount;

    @Column(name = "module")
    private String module; // user, project, portfolio, etc.

    @Column(name = "entity_id")
    private UUID entityId; // Reference to the entity this file belongs to

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (downloadCount == null) {
            downloadCount = 0L;
        }
        if (isPublic == null) {
            isPublic = false;
        }
        if (storageType == null) {
            storageType = "LOCAL";
        }
    }
}
