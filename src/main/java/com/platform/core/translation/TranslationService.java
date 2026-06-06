package com.platform.core.translation;

import com.platform.core.constants.AppConstants;
import com.platform.modules.translation.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationService {

    private final TranslationRepository translationRepository;
    private final Map<String, Map<String, String>> translationCache = new ConcurrentHashMap<>();

    public void loadTranslations() {
        log.info("Loading translations into cache...");
        for (String locale : AppConstants.SUPPORTED_LOCALES) {
            Map<String, String> localeTranslations = new ConcurrentHashMap<>();
            translationRepository.findByLocale(locale).forEach(translation ->
                    localeTranslations.put(translation.getTranslationKey(), translation.getTranslationValue())
            );
            translationCache.put(locale, localeTranslations);
            log.info("Loaded {} translations for locale: {}", localeTranslations.size(), locale);
        }
    }

    public String get(String key) {
        return get(key, AppConstants.DEFAULT_LOCALE);
    }

    public String get(String key, String locale) {
        if (locale == null || locale.isEmpty()) {
            locale = AppConstants.DEFAULT_LOCALE;
        }

        Map<String, String> localeTranslations = translationCache.getOrDefault(locale, new ConcurrentHashMap<>());
        String value = localeTranslations.get(key);

        if (value == null) {
            log.warn("Translation not found for key: {} in locale: {}", key, locale);
            return key;
        }

        return value;
    }

    public void refreshCache() {
        log.info("Refreshing translation cache...");
        translationCache.clear();
        loadTranslations();
    }

    public void clearCache() {
        translationCache.clear();
    }
}
