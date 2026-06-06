package com.platform.modules.menu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuReorderRequest {

    @NotNull(message = "Menu ID is required")
    private UUID menuId;

    @NotNull(message = "Order number is required")
    private Integer orderNumber;
}
