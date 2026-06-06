package com.platform.modules.menu.entity;

import com.platform.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "menus", indexes = {
    @Index(name = "idx_menus_parent_id", columnList = "parent_id"),
    @Index(name = "idx_menus_order_number", columnList = "order_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "translation_key", length = 255)
    private String translationKey;

    @Column(name = "url", length = 255)
    private String url;

    @Column(name = "icon", length = 100)
    private String icon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Menu parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Menu> children = new HashSet<>();

    @Column(name = "order_number")
    private Integer orderNumber = 0;

    @Column(name = "active")
    private Boolean active = true;
}
