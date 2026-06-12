package com.serverpulse.launchguard.util;

public final class TextUtil {

    private TextUtil() {
    }

    public static String stripLegacyColors(String text) {
        if (text == null) return "";
        return text.replaceAll("(?i)&[0-9a-fklmnor]", "");
    }

    public static String replacePlaceholder(String template, String placeholder, String value) {
        if (template == null) return "";
        return template.replace("%" + placeholder + "%", value != null ? value : "");
    }
}
