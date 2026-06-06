package com.platform.modules.audit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeFieldResponse {
    private String fieldName;
    private Object oldValue;
    private Object newValue;
}
