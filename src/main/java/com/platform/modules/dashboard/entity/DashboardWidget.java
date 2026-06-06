package com.platform.modules.dashboard.entity;

import com.platform.core.base.BaseEntity;
import com.platform.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dashboard_widgets", indexes = {
    @Index(name = "idx_dashboard_widgets_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardWidget extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @Column(name = "widget_type", nullable = false, length = 100)
    private String widgetType;

    @Column(name = "widget_title", length = 255)
    private String widgetTitle;

    @Column(name = "position")
    private Integer position;

    @Column(name = "visible")
    private Boolean visible = true;

    @Column(name = "configuration", columnDefinition = "jsonb")
    private String configuration;
}
