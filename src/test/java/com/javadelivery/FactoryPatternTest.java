package com.javadelivery;

import java.util.List;

/**
 * Test class for Phase 2 Factory Pattern.
 * Tests DishFactory and RestaurantFactory functionality.
 */
public class FactoryPatternTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("=== Phase 2: Factory Pattern Tests ===\n");

        // Reset factory before tests
        RestaurantFactory.reset();

        testDishFactory();
        testDishFactoryErrors();
        testRestaurantFactory();
        testRestaurantSearch();

        System.out.println("\n" + "=".repeat(40));
        System.out.printf("Results: %d/%d tests passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All tests passed!");
        } else {
            System.out.println("❌ Some tests failed!");
            System.exit(1);
        }
    }

    private static void testDishFactory() {
        System.out.println("Testing DishFactory...");
        
        // Test creating Pizza
        Dish pizza = DishFactory.createDish("pizza", "Test Pizza", 9.99, DishCategory.VEG, "Test");
        assertTest("Create pizza type", pizza instanceof Pizza);
        assertTest("Pizza name correct", "Test Pizza".equals(pizza.getName()));
        assertTest("Pizza price correct", pizza.getPrice() == 9.99);
        
        // Test creating Burger
        Dish burger = DishFactory.createDish("burger", "Test Burger", 8.99, DishCategory.NON_VEG, "Test");
        assertTest("Create burger type", burger instanceof Burger);
        assertTest("Burger category correct", burger.getCategory() == DishCategory.NON_VEG);
        
        // Test creating Sushi
        Dish sushi = DishFactory.createDish("sushi", "Test Sushi", 7.99, DishCategory.VEGAN, "Test");
        assertTest("Create sushi type", sushi instanceof Sushi);
        assertTest("Sushi getDishType", "Sushi".equals(sushi.getDishType()));
        
        // Test case insensitivity
        Dish pizzaUpper = DishFactory.createDish("PIZZA", "Upper", 5.0, DishCategory.VEG, "Test");
        assertTest("Case insensitive (PIZZA)", pizzaUpper instanceof Pizza);
        
        Dish pizzaMixed = DishFactory.createDish("PiZzA", "Mixed", 5.0, DishCategory.VEG, "Test");
        assertTest("Case insensitive (PiZzA)", pizzaMixed instanceof Pizza);
        
        // Test specific factory methods
        Pizza specificPizza = DishFactory.createPizza("Specific", 10.0, DishCategory.VEG, "Desc");
        assertTest("createPizza returns Pizza", specificPizza instanceof Pizza);
        
        Burger specificBurger = DishFactory.createBurger("Specific", 10.0, DishCategory.NON_VEG, "Desc");
        assertTest("createBurger returns Burger", specificBurger instanceof Burger);
        
        Sushi specificSushi = DishFactory.createSushi("Specific", 10.0, DishCategory.VEGAN, "Desc");
        assertTest("createSushi returns Sushi", specificSushi instanceof Sushi);
    }

    private static void testDishFactoryErrors() {
        System.out.println("\nTesting DishFactory error handling...");
        
        // Test invalid type
        boolean threwException = false;
        try {
            DishFactory.createDish("invalid", "Test", 5.0, DishCategory.VEG, "Test");
        } catch (IllegalArgumentException e) {
            threwException = true;
        }
        assertTest("Throws exception for invalid type", threwException);
        
        // Test null type
        threwException = false;
        try {
            DishFactory.createDish(null, "Test", 5.0, DishCategory.VEG, "Test");
        } catch (IllegalArgumentException e) {
            threwException = true;
        }
        assertTest("Throws exception for null type", threwException);
        
        // Test empty type
        threwException = false;
        try {
            DishFactory.createDish("  ", "Test", 5.0, DishCategory.VEG, "Test");
        } catch (IllegalArgumentException e) {
            threwException = true;
        }
        assertTest("Throws exception for empty type", threwException);
    }

    private static void testRestaurantFactory() {
        System.out.println("\nTesting RestaurantFactory...");
        
        List<Restaurant> restaurants = RestaurantFactory.getAllRestaurants();
        assertTest("Has 3 sample restaurants", restaurants.size() == 3);
        
        // Check Pizza Palace
        Restaurant pizzaPalace = RestaurantFactory.findById("R001");
        assertTest("Pizza Palace exists", pizzaPalace != null);
        assertTest("Pizza Palace name", "Pizza Palace".equals(pizzaPalace.getName()));
        assertTest("Pizza Palace cuisine", "Italian".equals(pizzaPalace.getCuisineType()));
        assertTest("Pizza Palace has 5 dishes", pizzaPalace.getMenuSize() == 5);
        
        // Check Burger Town
        Restaurant burgerTown = RestaurantFactory.findById("R002");
        assertTest("Burger Town exists", burgerTown != null);
        assertTest("Burger Town has 5 dishes", burgerTown.getMenuSize() == 5);
        
        // Check Sushi Master
        Restaurant sushiMaster = RestaurantFactory.findById("R003");
        assertTest("Sushi Master exists", sushiMaster != null);
        assertTest("Sushi Master has 5 dishes", sushiMaster.getMenuSize() == 5);
        
        // Test restaurant count
        assertTest("Restaurant count is 3", RestaurantFactory.getRestaurantCount() == 3);
    }

    private static void testRestaurantSearch() {
        System.out.println("\nTesting RestaurantFactory search...");
        
        // Find by name
        Restaurant found = RestaurantFactory.findByName("pizza palace");
        assertTest("Find by name (case insensitive)", found != null && "R001".equals(found.getId()));
        
        // Find non-existent
        Restaurant notFound = RestaurantFactory.findByName("NonExistent");
        assertTest("Returns null for non-existent", notFound == null);
        
        // Find by cuisine
        List<Restaurant> italian = RestaurantFactory.findByCuisine("Italian");
        assertTest("Find by cuisine", italian.size() == 1);
        
        // Create custom restaurant
        Restaurant custom = RestaurantFactory.createRestaurant("R004", "Test Restaurant", "Mexican");
        assertTest("Create custom restaurant", custom != null);
        assertTest("Custom restaurant added", RestaurantFactory.getRestaurantCount() == 4);
        
        // Reset and verify
        RestaurantFactory.reset();
        assertTest("Reset returns to 3 restaurants", RestaurantFactory.getRestaurantCount() == 3);
    }

    private static void assertTest(String testName, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("  ✓ " + testName);
        } else {
            System.out.println("  ✗ " + testName + " - FAILED");
        }
    }
}
