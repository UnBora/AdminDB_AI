package com.platform.modules.audit.util;

public class UserAgentParser {

    public static String parseUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }

        if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("Edge")) {
            return "Edge";
        } else if (userAgent.contains("Opera")) {
            return "Opera";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "Internet Explorer";
        } else if (userAgent.contains("Mobile")) {
            return "Mobile Browser";
        }
        return "Other";
    }

    public static String getBrowserInfo(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown Browser";
        }

        StringBuilder info = new StringBuilder();
        info.append(parseUserAgent(userAgent));

        if (userAgent.contains("Windows")) {
            info.append(" (Windows)");
        } else if (userAgent.contains("Mac")) {
            info.append(" (macOS)");
        } else if (userAgent.contains("Linux")) {
            info.append(" (Linux)");
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            info.append(" (iOS)");
        } else if (userAgent.contains("Android")) {
            info.append(" (Android)");
        }

        return info.toString();
    }
}
