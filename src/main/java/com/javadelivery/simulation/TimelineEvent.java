package com.javadelivery.simulation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event in the order timeline.
 */
public class TimelineEvent {
    private final LocalDateTime timestamp;
    private final String description;
    private final String icon;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public TimelineEvent(String description, String icon) {
        this.timestamp = LocalDateTime.now();
        this.description = description;
        this.icon = icon;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getFormattedTime() {
        return timestamp.format(TIME_FORMAT);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", getFormattedTime(), icon, description);
    }
}
