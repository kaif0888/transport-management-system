package com.tms.util;

public class FilePathUtil {

    public static String toPublicUrl(String fullPath) {
        if (fullPath == null) return null;

        // Normalize slashes
        String path = fullPath.replace("\\", "/");

        // Remove local disk root
        if (path.contains("/xampp/htdocs")) {
            path = path.substring(path.indexOf("/TransportFiles"));
        }

        // Ensure leading slash
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        return path;
    }
}
