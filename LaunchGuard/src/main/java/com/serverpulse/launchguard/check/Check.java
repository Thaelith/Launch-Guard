package com.serverpulse.launchguard.check;

import java.util.List;

public interface Check {

    String id();

    String displayName();

    List<CheckResult> run(CheckContext context);
}
