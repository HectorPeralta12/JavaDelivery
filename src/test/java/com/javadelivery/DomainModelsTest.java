package com.javadelivery;

/**
 * Simple test class for Phase 1 Domain Models.
 * Tests basic functionality without external testing frameworks.
 */
public class DomainModelsTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("=== Phase 1: Domain Models Tests ===\n");

        testDishCategory();
        testDishCreation();
        testRestaurantMenu();
        testRestaurantCategoryFilter();

        System.out.println("\n" + "=".repeat(40));
        System.out.printf("Results: %d/%d tests passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All tests passed!");
        } else {
            System.out.println("❌ Some tests failed!");
            System.exit(1);
        }
    }

    private static void testDishCategory() {
        System.out.println("Testing DishCategory...");
        
        // Test enum values exist
        assertTest("DishCategory has VEG", DishCategory.VEG != null);
        assertTest("DishCategory has NON_VEG", DishCategory.NON_VEG != null);
        assertTest("DishCategory has VEGAN", DishCategory.VEGAN != null);
        
        // Test display names
        assertTest("VEG display name", "Vegetarian".equals(DishCategory.VEG.getDisplayName()));
        assertTest("NON_VEG display name", "Non-Vegetarian".equals(DishCategory.NON_VEG.getDisplayName()));
        assertTest("VEGAN display name", "Vegan".equals(DishCategory.VEGAN.getDisplayName()));
    }

    private static void testDishCreation() {
        System.out.println("\nTesting Dish creation...");
        
        // Test Pizza
        Pizza pizza = new Pizza("Margherita", 12.99, DishCategory.VEG, "Classic tomato and mozzarella");
        assertTest("Pizza name", "Margherita".equals(pizza.getName()));
        assertTest("Pizza price", pizza.getPrice() == 12.99);
        assertTest("Pizza category", pizza.getCategory() == DishCategory.VEG);
        assertTest("Pizza type", "Pizza".equals(pizza.getDishType()));
        
        // Test Burger
        Burger burger = new Burger("Classic Burger", 9.99, DishCategory.NON_VEG, "Beef patty with cheese");
        assertTest("Burger name", "Classic Burger".equals(burger.getName()));
        assertTest("Burger type", "Burger".equals(burger.getDishType()));
        
        // Test Sushi
        Sushi sushi = new Sushi("Avocado Roll", 8.50, DishCategory.VEGAN, "Fresh avocado roll");
        assertTest("Sushi name", "Avocado Roll".equals(sushi.getName()));
        assertTest("Sushi type", "Sushi".equals(sushi.getDishType()));
        assertTest("Sushi is vegan", sushi.getCategory() == DishCategory.VEGAN);
    }

    private static void testRestaurantMenu() {
        System.out.println("\nTesting Restaurant menu...");
        
        Restaurant restaurant = new Restaurant("R001", "Pizza Palace", "Italian");
        
        assertTest("Restaurant name", "Pizza Palace".equals(restaurant.getName()));
        assertTest("Restaurant cuisine", "Italian".equals(restaurant.getCuisineType()));
        assertTest("Restaurant ID", "R001".equals(restaurant.getId()));
        assertTest("Empty menu initially", restaurant.getMenuSize() == 0);
        
        // Add dishes
        Pizza pizza = new Pizza("Margherita", 12.99, DishCategory.VEG, "Classic");
        restaurant.addDish(pizza);
        assertTest("Menu size after add", restaurant.getMenuSize() == 1);
        
        // Find dish
        Dish found = restaurant.findDishByName("Margherita");
        assertTest("Find dish by name", found != null && "Margherita".equals(found.getName()));
        
        // Find non-existent dish
        Dish notFound = restaurant.findDishByName("NonExistent");
        assertTest("Non-existent dish returns null", notFound == null);
        
        // Remove dish
        boolean removed = restaurant.removeDish("Margherita");
        assertTest("Remove dish success", removed);
        assertTest("Menu size after remove", restaurant.getMenuSize() == 0);
    }

    private static void testRestaurantCategoryFilter() {
        System.out.println("\nTesting Restaurant category filter...");
        
        Restaurant restaurant = new Restaurant("R002", "Test Restaurant", "Mixed");
        restaurant.addDish(new Pizza("Veggie Pizza", 11.99, DishCategory.VEG, "Vegetable pizza"));
        restaurant.addDish(new Burger("Beef Burger", 13.99, DishCategory.NON_VEG, "Beef burger"));
        restaurant.addDish(new Sushi("Vegan Roll", 9.99, DishCategory.VEGAN, "Vegan sushi"));
        restaurant.addDish(new Pizza("Pepperoni", 14.99, DishCategory.NON_VEG, "Pepperoni pizza"));
        
        assertTest("Total menu size", restaurant.getMenuSize() == 4);
        assertTest("VEG dishes count", restaurant.getDishesByCategory(DishCategory.VEG).size() == 1);
        assertTest("NON_VEG dishes count", restaurant.getDishesByCategory(DishCategory.NON_VEG).size() == 2);
        assertTest("VEGAN dishes count", restaurant.getDishesByCategory(DishCategory.VEGAN).size() == 1);
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
