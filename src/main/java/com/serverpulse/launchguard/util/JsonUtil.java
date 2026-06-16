package com.serverpulse.launchguard.util;

public final class JsonUtil {

    private JsonUtil() {
    }

    public static String escape(String value) {
        if (value == null) return "null";
        StringBuilder sb = new StringBuilder(value.length() + 8);
        sb.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    public static String jsonString(String key, String value) {
        return "\"" + key + "\": " + escape(value);
    }

    public static String jsonNumber(String key, long value) {
        return "\"" + key + "\": " + value;
    }

    public static String jsonInt(String key, int value) {
        return "\"" + key + "\": " + value;
    }

    public static String jsonBoolean(String key, boolean value) {
        return "\"" + key + "\": " + value;
    }

    public static String jsonNull(String key) {
        return "\"" + key + "\": null";
    }
}
