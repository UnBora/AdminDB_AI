package com.platform.modules.translation.entity;

import com.platform.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "translations", indexes = {
    @Index(name = "idx_translations_locale_key", columnList = "locale,translation_key"),
    @Index(name = "idx_translations_category", columnList = "category")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_translations_locale_key", columnNames = {"locale", "translation_key"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Translation extends BaseEntity {

    @Column(name = "locale", nullable = false, length = 20)
    private String locale;

    @Column(name = "translation_key", nullable = false, length = 255)
    private String translationKey;

    @Column(name = "translation_value", nullable = false, columnDefinition = "text")
    private String translationValue;

    @Column(name = "category", length = 100)
    private String category;
}
