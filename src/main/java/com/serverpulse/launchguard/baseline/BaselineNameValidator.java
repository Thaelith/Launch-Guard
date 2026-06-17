package com.serverpulse.launchguard.baseline;

import java.util.regex.Pattern;

public final class BaselineNameValidator {

    private static final Pattern VALID_NAME = Pattern.compile("^[A-Za-z0-9_-]{1,32}$");

    private BaselineNameValidator() {}

    public static boolean isValid(String name) {
        return name != null && VALID_NAME.matcher(name).matches();
    }

    public static String validate(String name) {
        if (!isValid(name)) {
            return "Invalid baseline name. Use 1-32 characters: letters, numbers, underscore, or dash.";
        }
        return null;
    }
}
