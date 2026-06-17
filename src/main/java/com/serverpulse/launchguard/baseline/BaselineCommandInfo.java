package com.serverpulse.launchguard.baseline;

import java.util.List;

public record BaselineCommandInfo(
        String name,
        String label,
        String plugin,
        List<String> aliases
) {}
