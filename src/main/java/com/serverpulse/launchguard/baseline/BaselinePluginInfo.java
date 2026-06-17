package com.serverpulse.launchguard.baseline;

import java.util.List;

public record BaselinePluginInfo(
        String name,
        String version,
        boolean enabled,
        List<String> authors,
        List<String> depend,
        List<String> softDepend
) {}
