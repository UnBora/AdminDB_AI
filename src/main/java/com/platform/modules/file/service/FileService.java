package com.platform.modules.file.service;

import com.platform.modules.file.dto.request.FileUploadRequest;
import com.platform.modules.file.dto.response.FileMetadataResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileService {
    
    /**
     * Upload a file
     */
    FileMetadataResponse uploadFile(FileUploadRequest request);
    
    /**
     * Get file metadata
     */
    FileMetadataResponse getFileMetadata(UUID fileId);
    
    /**
     * List files for a module/entity
     */
    List<FileMetadataResponse> getFilesByEntity(String module, UUID entityId);
    
    /**
     * Download file
     */
    Resource downloadFile(UUID fileId);
    
    /**
     * Delete file
     */
    void deleteFile(UUID fileId);
    
    /**
     * Get all files (paginated)
     */
    List<FileMetadataResponse> getAllFiles(int pageSize);
    
    /**
     * Increment download count
     */
    void incrementDownloadCount(UUID fileId);
}
