package com.platform.modules.audit.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChangeExtractor {

    public static Map<String, Map<String, Object>> extractChanges(
            Map<String, Object> oldData,
            Map<String, Object> newData) {

        Map<String, Map<String, Object>> changes = new HashMap<>();

        if (oldData == null || newData == null) {
            return changes;
        }

        // Check for changed and new fields
        newData.forEach((key, newValue) -> {
            Object oldValue = oldData.get(key);
            if (!Objects.equals(oldValue, newValue)) {
                Map<String, Object> change = new HashMap<>();
                change.put("old", oldValue);
                change.put("new", newValue);
                changes.put(key, change);
            }
        });

        // Check for deleted fields
        oldData.forEach((key, oldValue) -> {
            if (!newData.containsKey(key)) {
                Map<String, Object> change = new HashMap<>();
                change.put("old", oldValue);
                change.put("new", null);
                changes.put(key, change);
            }
        });

        return changes;
    }

    public static boolean hasChanges(Map<String, Object> oldData, Map<String, Object> newData) {
        return !extractChanges(oldData, newData).isEmpty();
    }
}
