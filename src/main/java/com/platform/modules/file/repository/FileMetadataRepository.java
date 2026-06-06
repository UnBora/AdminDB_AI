package com.platform.modules.file.repository;

import com.platform.core.base.BaseRepository;
import com.platform.modules.file.entity.FileMetadata;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileMetadataRepository extends BaseRepository<FileMetadata> {
    
    List<FileMetadata> findByModuleAndEntityId(String module, UUID entityId);
    
    List<FileMetadata> findByModule(String module);
    
    List<FileMetadata> findByCreatedBy(UUID userId);
}
