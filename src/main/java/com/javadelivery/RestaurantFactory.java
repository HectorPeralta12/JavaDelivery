package com.javadelivery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory class for creating and managing Restaurant instances.
 * Provides pre-configured sample restaurants for the food delivery system.
 * 
 * Design Pattern: Factory Method
 * - Centralizes restaurant creation logic
 * - Provides sample data for demonstration
 * - Easy to extend with new restaurants
 */
public class RestaurantFactory {

    private static final List<Restaurant> restaurants = new ArrayList<>();

    static {
        // Initialize sample restaurants on class load
        initializeSampleRestaurants();
    }

    /**
     * Creates sample restaurants with pre-populated menus.
     */
    private static void initializeSampleRestaurants() {
        restaurants.clear();
        
        // Pizza Palace - Italian Restaurant
        Restaurant pizzaPalace = new Restaurant("R001", "Pizza Palace", "Italian");
        pizzaPalace.addDish(DishFactory.createPizza("Margherita", 10.99, DishCategory.VEG, 
            "Classic tomato sauce and mozzarella"));
        pizzaPalace.addDish(DishFactory.createPizza("Pepperoni", 13.99, DishCategory.NON_VEG, 
            "Loaded with spicy pepperoni"));
        pizzaPalace.addDish(DishFactory.createPizza("Veggie Supreme", 12.99, DishCategory.VEGAN, 
            "Grilled vegetables with vegan cheese"));
        pizzaPalace.addDish(DishFactory.createPizza("Hawaiian", 12.49, DishCategory.NON_VEG, 
            "Ham and pineapple classic"));
        pizzaPalace.addDish(DishFactory.createPizza("Four Cheese", 14.99, DishCategory.VEG, 
            "Mozzarella, gorgonzola, parmesan, ricotta"));
        restaurants.add(pizzaPalace);

        // Burger Town - American Restaurant
        Restaurant burgerTown = new Restaurant("R002", "Burger Town", "American");
        burgerTown.addDish(DishFactory.createBurger("Classic Burger", 9.99, DishCategory.NON_VEG, 
            "Beef patty with lettuce, tomato, onion"));
        burgerTown.addDish(DishFactory.createBurger("Cheese Burger", 10.99, DishCategory.NON_VEG, 
            "Classic with melted cheddar"));
        burgerTown.addDish(DishFactory.createBurger("Veggie Burger", 9.49, DishCategory.VEG, 
            "Grilled vegetable patty"));
        burgerTown.addDish(DishFactory.createBurger("Beyond Burger", 12.99, DishCategory.VEGAN, 
            "Plant-based patty, 100% vegan"));
        burgerTown.addDish(DishFactory.createBurger("BBQ Bacon Burger", 13.99, DishCategory.NON_VEG, 
            "Crispy bacon with BBQ sauce"));
        restaurants.add(burgerTown);

        // Sushi Master - Japanese Restaurant
        Restaurant sushiMaster = new Restaurant("R003", "Sushi Master", "Japanese");
        sushiMaster.addDish(DishFactory.createSushi("Salmon Roll", 8.99, DishCategory.NON_VEG, 
            "Fresh salmon with avocado"));
        sushiMaster.addDish(DishFactory.createSushi("Tuna Nigiri", 9.99, DishCategory.NON_VEG, 
            "Premium tuna on seasoned rice"));
        sushiMaster.addDish(DishFactory.createSushi("Avocado Roll", 6.99, DishCategory.VEGAN, 
            "Creamy avocado roll"));
        sushiMaster.addDish(DishFactory.createSushi("California Roll", 8.49, DishCategory.NON_VEG, 
            "Crab, avocado, cucumber"));
        sushiMaster.addDish(DishFactory.createSushi("Veggie Tempura Roll", 7.99, DishCategory.VEG, 
            "Crispy vegetable tempura"));
        restaurants.add(sushiMaster);
    }

    /**
     * Returns an unmodifiable list of all available restaurants.
     */
    public static List<Restaurant> getAllRestaurants() {
        return Collections.unmodifiableList(restaurants);
    }

    /**
     * Finds a restaurant by its ID.
     */
    public static Restaurant findById(String id) {
        return restaurants.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a restaurant by name (case-insensitive).
     */
    public static Restaurant findByName(String name) {
        return restaurants.stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds restaurants by cuisine type.
     */
    public static List<Restaurant> findByCuisine(String cuisineType) {
        return restaurants.stream()
                .filter(r -> r.getCuisineType().equalsIgnoreCase(cuisineType))
                .toList();
    }

    /**
     * Creates a new custom restaurant.
     */
    public static Restaurant createRestaurant(String id, String name, String cuisineType) {
        Restaurant restaurant = new Restaurant(id, name, cuisineType);
        restaurants.add(restaurant);
        return restaurant;
    }

    /**
     * Returns the total number of restaurants.
     */
    public static int getRestaurantCount() {
        return restaurants.size();
    }

    /**
     * Resets the factory to its initial state with sample restaurants.
     */
    public static void reset() {
        initializeSampleRestaurants();
    }

    /**
     * Displays all available restaurants.
     */
    public static void displayAllRestaurants() {
        System.out.println("\n=== Available Restaurants ===");
        System.out.println("-".repeat(40));
        
        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant r = restaurants.get(i);
            System.out.printf("%d. %s%n", i + 1, r);
        }
    }
}
