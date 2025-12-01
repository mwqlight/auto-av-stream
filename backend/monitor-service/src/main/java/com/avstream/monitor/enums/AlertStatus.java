package com.avstream.monitor.enums;

/**
 * 告警状态枚举
 */
public enum AlertStatus {
    TRIGGERED("已触发"),
    ACTIVE("活跃"),
    CONFIRMED("已确认"),
    ACKNOWLEDGED("已确认"),
    RESOLVED("已解决");

    private final String description;

    AlertStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}