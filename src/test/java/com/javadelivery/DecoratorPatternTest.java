package com.javadelivery;

/**
 * Test class for Phase 3 Decorator Pattern.
 * Tests topping decorators and their combinations.
 */
public class DecoratorPatternTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("=== Phase 3: Decorator Pattern Tests ===\n");

        testSingleToppings();
        testMultipleToppings();
        testToppingChaining();
        testDecoratorMethods();
        testNullBaseItem();

        System.out.println("\n" + "=".repeat(40));
        System.out.printf("Results: %d/%d tests passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All tests passed!");
        } else {
            System.out.println("❌ Some tests failed!");
            System.exit(1);
        }
    }

    private static void testSingleToppings() {
        System.out.println("Testing single toppings...");
        
        // Base pizza
        Dish pizza = new Pizza("Margherita", 10.00, DishCategory.VEG, "Classic");
        assertTest("Base pizza price", pizza.getPrice() == 10.00);
        assertTest("Base pizza name", "Margherita".equals(pizza.getName()));
        
        // Cheese topping (+1.00)
        Item withCheese = new CheeseTopping(pizza);
        assertTest("With cheese price", withCheese.getPrice() == 11.00);
        assertTest("With cheese name", "Margherita + Cheese".equals(withCheese.getName()));
        
        // Extra sauce topping (+0.50)
        Item withSauce = new ExtraSauceTopping(pizza);
        assertTest("With sauce price", withSauce.getPrice() == 10.50);
        assertTest("With sauce name", "Margherita + Extra Sauce".equals(withSauce.getName()));
        
        // Bacon topping (+2.00)
        Item withBacon = new BaconTopping(pizza);
        assertTest("With bacon price", withBacon.getPrice() == 12.00);
        assertTest("With bacon name", "Margherita + Bacon".equals(withBacon.getName()));
        
        // Jalapeno topping (+0.75)
        Item withJalapeno = new JalapenoTopping(pizza);
        assertTest("With jalapeno price", withJalapeno.getPrice() == 10.75);
        assertTest("With jalapeno name", "Margherita + Jalapeños".equals(withJalapeno.getName()));
    }

    private static void testMultipleToppings() {
        System.out.println("\nTesting multiple toppings...");
        
        // Base burger
        Dish burger = new Burger("Classic", 8.00, DishCategory.NON_VEG, "Beef");
        
        // Add cheese (+1.00) then bacon (+2.00)
        Item cheeseAndBacon = new BaconTopping(new CheeseTopping(burger));
        assertTest("Cheese + Bacon price", cheeseAndBacon.getPrice() == 11.00);
        assertTest("Cheese + Bacon name", "Classic + Cheese + Bacon".equals(cheeseAndBacon.getName()));
        
        // Add all toppings: cheese (+1.00) + sauce (+0.50) + bacon (+2.00) + jalapeno (+0.75)
        Item allToppings = new JalapenoTopping(
            new BaconTopping(
                new ExtraSauceTopping(
                    new CheeseTopping(burger)
                )
            )
        );
        double expectedPrice = 8.00 + 1.00 + 0.50 + 2.00 + 0.75; // 12.25
        assertTest("All toppings price", Math.abs(allToppings.getPrice() - 12.25) < 0.01);
        assertTest("All toppings name contains cheese", allToppings.getName().contains("Cheese"));
        assertTest("All toppings name contains bacon", allToppings.getName().contains("Bacon"));
        assertTest("All toppings name contains sauce", allToppings.getName().contains("Extra Sauce"));
        assertTest("All toppings name contains jalapeno", allToppings.getName().contains("Jalapeños"));
    }

    private static void testToppingChaining() {
        System.out.println("\nTesting topping chaining with different dishes...");
        
        // Sushi with sauce
        Dish sushi = new Sushi("Salmon Roll", 9.00, DishCategory.NON_VEG, "Fresh");
        Item sushiWithSauce = new ExtraSauceTopping(sushi);
        assertTest("Sushi with sauce", sushiWithSauce.getPrice() == 9.50);
        
        // Double cheese on pizza
        Dish pizza = new Pizza("Four Cheese", 14.00, DishCategory.VEG, "Cheesy");
        Item doubleCheese = new CheeseTopping(new CheeseTopping(pizza));
        assertTest("Double cheese price", doubleCheese.getPrice() == 16.00);
        assertTest("Double cheese name", doubleCheese.getName().contains("Cheese + Cheese"));
    }

    private static void testDecoratorMethods() {
        System.out.println("\nTesting decorator methods...");
        
        Dish pizza = new Pizza("Test", 10.00, DishCategory.VEG, "Test");
        
        // Test getToppingName and getToppingPrice
        CheeseTopping cheese = new CheeseTopping(pizza);
        assertTest("Cheese topping name", "Cheese".equals(cheese.getToppingName()));
        assertTest("Cheese topping price", cheese.getToppingPrice() == 1.00);
        
        ExtraSauceTopping sauce = new ExtraSauceTopping(pizza);
        assertTest("Sauce topping name", "Extra Sauce".equals(sauce.getToppingName()));
        assertTest("Sauce topping price", sauce.getToppingPrice() == 0.50);
        
        BaconTopping bacon = new BaconTopping(pizza);
        assertTest("Bacon topping name", "Bacon".equals(bacon.getToppingName()));
        assertTest("Bacon topping price", bacon.getToppingPrice() == 2.00);
        
        JalapenoTopping jalapeno = new JalapenoTopping(pizza);
        assertTest("Jalapeno topping name", "Jalapeños".equals(jalapeno.getToppingName()));
        assertTest("Jalapeno topping price", jalapeno.getToppingPrice() == 0.75);
        
        // Test getBaseItem
        assertTest("getBaseItem returns original", cheese.getBaseItem() == pizza);
    }

    private static void testNullBaseItem() {
        System.out.println("\nTesting null base item handling...");
        
        boolean threwException = false;
        try {
            new CheeseTopping(null);
        } catch (IllegalArgumentException e) {
            threwException = true;
        }
        assertTest("Throws exception for null base item", threwException);
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
