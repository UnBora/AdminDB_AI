package com.platform.modules.file.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataResponse {
    private UUID id;
    private String fileName;
    private String originalName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private String mimeType;
    private String storageType;
    private String description;
    private Boolean isPublic;
    private Long downloadCount;
    private String module;
    private UUID entityId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public String getFileSizeFormatted() {
        if (fileSize == null) return "0 B";
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        if (fileSize < 1024 * 1024 * 1024) return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
    }

    public String getFileExtension() {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
    }
}
