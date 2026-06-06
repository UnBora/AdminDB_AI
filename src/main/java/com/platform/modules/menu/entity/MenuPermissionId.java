package com.platform.modules.menu.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuPermissionId implements Serializable {

    private UUID menu;
    private UUID permission;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuPermissionId that = (MenuPermissionId) o;
        return Objects.equals(menu, that.menu) &&
                Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menu, permission);
    }
}
