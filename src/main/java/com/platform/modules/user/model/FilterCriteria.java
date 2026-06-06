package com.platform.modules.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterCriteria {

    private String filterKey;
    
    private FilterOperation operation;
    
    private Object value;

    public enum FilterOperation {
        EQUAL,
        NOT_EQUAL,
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN_OR_EQUAL,
        LIKE,
        IN,
        BETWEEN,
        IS_NULL,
        IS_NOT_NULL
    }
}
