package com.agen.autopat.client;

public class AutoPatClientConfig {
    private static boolean patPlayersEnabled = false;
    private static boolean patAllEntitiesEnabled = false;
    private static int patInterval = 20; // 默认20刻

    public static boolean isPatPlayersEnabled() {
        return patPlayersEnabled;
    }

    public static void setPatPlayersEnabled(boolean enabled) {
        patPlayersEnabled = enabled;
    }

    public static boolean isPatAllEntitiesEnabled() {
        return patAllEntitiesEnabled;
    }

    public static void setPatAllEntitiesEnabled(boolean enabled) {
        patAllEntitiesEnabled = enabled;
    }

    public static int getPatInterval() {
        return patInterval;
    }

    public static void setPatInterval(int interval) {
        // 限制最小值为1，最大值为200
        if (interval < 1) interval = 1;
        if (interval > 200) interval = 200;
        patInterval = interval;
    }
}
