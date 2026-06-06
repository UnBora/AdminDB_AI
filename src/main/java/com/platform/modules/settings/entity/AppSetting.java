package com.platform.modules.settings.entity;

import com.platform.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_settings", indexes = {
    @Index(name = "idx_app_settings_key", columnList = "setting_key"),
    @Index(name = "idx_app_settings_category", columnList = "category")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_app_settings_key", columnNames = "setting_key")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppSetting extends BaseEntity {

    @Column(name = "setting_key", nullable = false, unique = true, length = 255)
    private String settingKey;

    @Column(name = "setting_value", columnDefinition = "jsonb")
    private String settingValue;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "description", length = 500)
    private String description;
}
