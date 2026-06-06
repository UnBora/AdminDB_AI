package com.platform.modules.translation.repository;

import com.platform.core.base.BaseRepository;
import com.platform.modules.translation.entity.Translation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranslationRepository extends BaseRepository<Translation> {
    Optional<Translation> findByLocaleAndTranslationKey(String locale, String translationKey);
    List<Translation> findByLocale(String locale);
    List<Translation> findByCategory(String category);
    List<Translation> findByLocaleAndCategory(String locale, String category);
}
