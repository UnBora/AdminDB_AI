package com.platform.modules.user.entity;

import com.platform.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions", indexes = {
    @Index(name = "idx_permissions_name", columnList = "name"),
    @Index(name = "idx_permissions_module", columnList = "module")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "module", length = 100)
    private String module;
}
