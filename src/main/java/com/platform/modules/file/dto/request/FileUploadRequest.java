package com.platform.modules.file.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {
    private MultipartFile file;
    private String module;
    private UUID entityId;
    private String description;
    private Boolean isPublic;
    private String storageType; // LOCAL or S3

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final String[] ALLOWED_TYPES = {"pdf", "doc", "docx", "xls", "xlsx", "txt", "jpg", "jpeg", "png", "gif", "zip"};

    public boolean isValid() {
        if (file == null || file.isEmpty()) return false;
        if (file.getSize() > MAX_FILE_SIZE) return false;
        
        String extension = getFileExtension();
        for (String allowed : ALLOWED_TYPES) {
            if (extension.equalsIgnoreCase(allowed)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension() {
        String filename = file.getOriginalFilename();
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return "";
    }
}
