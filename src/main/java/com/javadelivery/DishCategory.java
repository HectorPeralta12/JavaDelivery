package com.javadelivery;

/**
 * Enum representing the dietary category of a dish.
 * Used to classify dishes for users with specific dietary requirements.
 */
public enum DishCategory {
    VEG("Vegetarian"),
    NON_VEG("Non-Vegetarian"),
    VEGAN("Vegan");

    private final String displayName;

    DishCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
