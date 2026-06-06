package com.platform.modules.audit.util;

import jakarta.servlet.http.HttpServletRequest;

public class IPAddressExtractor {

    private static final String[] HEADERS = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "X-Real-IP"
    };

    public static String extractClientIp(HttpServletRequest request) {
        for (String header : HEADERS) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    public static boolean isValidIpAddress(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return isValidIpv4(ip) || isValidIpv6(ip);
    }

    private static boolean isValidIpv4(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidIpv6(String ip) {
        return ip.contains(":") && ip.length() <= 39;
    }
}
