package com.platform.modules.menu.entity;

import com.platform.modules.user.entity.Permission;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "menu_permissions", indexes = {
    @Index(name = "idx_menu_permissions_menu_id", columnList = "menu_id"),
    @Index(name = "idx_menu_permissions_permission_id", columnList = "permission_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(MenuPermissionId.class)
public class MenuPermission {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", referencedColumnName = "id", nullable = false)
    private Menu menu;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", referencedColumnName = "id", nullable = false)
    private Permission permission;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
