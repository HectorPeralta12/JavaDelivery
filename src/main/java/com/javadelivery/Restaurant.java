package com.javadelivery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a restaurant in the food delivery system.
 * Each restaurant has a name, cuisine type, and a menu of dishes.
 */
public class Restaurant {
    private final String id;
    private final String name;
    private final String cuisineType;
    private final List<Dish> menu;

    public Restaurant(String id, String name, String cuisineType) {
        this.id = id;
        this.name = name;
        this.cuisineType = cuisineType;
        this.menu = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    /**
     * Returns an unmodifiable view of the menu.
     */
    public List<Dish> getMenu() {
        return Collections.unmodifiableList(menu);
    }

    /**
     * Adds a dish to the restaurant's menu.
     */
    public void addDish(Dish dish) {
        menu.add(dish);
    }

    /**
     * Removes a dish from the menu by name.
     */
    public boolean removeDish(String dishName) {
        return menu.removeIf(dish -> dish.getName().equalsIgnoreCase(dishName));
    }

    /**
     * Finds a dish by name (case-insensitive).
     */
    public Dish findDishByName(String dishName) {
        return menu.stream()
                .filter(dish -> dish.getName().equalsIgnoreCase(dishName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Filters menu by dietary category.
     */
    public List<Dish> getDishesByCategory(DishCategory category) {
        return menu.stream()
                .filter(dish -> dish.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * Returns the number of dishes in the menu.
     */
    public int getMenuSize() {
        return menu.size();
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %d dishes available", name, cuisineType, menu.size());
    }

    /**
     * Displays the full menu with all dishes.
     */
    public void displayMenu() {
        System.out.println("\n=== " + name + " Menu ===");
        System.out.println("Cuisine: " + cuisineType);
        System.out.println("-".repeat(40));
        
        if (menu.isEmpty()) {
            System.out.println("No dishes available.");
            return;
        }

        for (int i = 0; i < menu.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, menu.get(i));
        }
    }
}
