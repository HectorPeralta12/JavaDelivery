package com.javadelivery.tui.render;

import com.googlecode.lanterna.TextColor;
import com.javadelivery.OrderStatus;

/**
 * Renders status badges with appropriate colors and icons.
 */
public class StatusBadgeRenderer {

    public static String getIcon(OrderStatus status) {
        return switch (status) {
            case PLACED -> "⏳";
            case CONFIRMED -> "✅";
            case PREPARING -> "🟡";
            case READY -> "📦";
            case OUT_FOR_DELIVERY -> "🔵";
            case DELIVERED -> "✔";
            case CANCELLED -> "❌";
        };
    }

    public static TextColor getColor(OrderStatus status) {
        return switch (status) {
            case PLACED -> TextColor.ANSI.YELLOW;
            case CONFIRMED -> TextColor.ANSI.GREEN;
            case PREPARING -> TextColor.ANSI.YELLOW_BRIGHT;
            case READY -> TextColor.ANSI.CYAN;
            case OUT_FOR_DELIVERY -> TextColor.ANSI.BLUE_BRIGHT;
            case DELIVERED -> TextColor.ANSI.GREEN_BRIGHT;
            case CANCELLED -> TextColor.ANSI.RED;
        };
    }

    public static String getShortName(OrderStatus status) {
        return switch (status) {
            case PLACED -> "Placed";
            case CONFIRMED -> "Confirmed";
            case PREPARING -> "Cooking";
            case READY -> "Ready";
            case OUT_FOR_DELIVERY -> "On Way";
            case DELIVERED -> "Delivered";
            case CANCELLED -> "Cancelled";
        };
    }

    /**
     * Gets progress percentage for the order status (0-100)
     */
    public static int getProgressPercent(OrderStatus status) {
        return switch (status) {
            case PLACED -> 10;
            case CONFIRMED -> 25;
            case PREPARING -> 45;
            case READY -> 65;
            case OUT_FOR_DELIVERY -> 85;
            case DELIVERED -> 100;
            case CANCELLED -> 0;
        };
    }
}
