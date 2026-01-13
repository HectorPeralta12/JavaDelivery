package com.javadelivery;

/**
 * Abstract decorator class for adding toppings to dishes.
 * 
 * Design Pattern: Decorator
 * - Allows adding responsibilities to objects dynamically
 * - Provides flexible alternative to subclassing for extending functionality
 * - Each decorator wraps an Item and can modify its behavior
 * 
 * Example usage:
 * <pre>
 * Item pizza = new Pizza("Margherita", 10.99, DishCategory.VEG, "Classic");
 * Item withCheese = new CheeseTopping(pizza);
 * Item withCheeseAndBacon = new BaconTopping(withCheese);
 * // Price now includes pizza + cheese + bacon
 * </pre>
 */
public abstract class ToppingDecorator implements Item {
    protected final Item baseItem;

    protected ToppingDecorator(Item baseItem) {
        if (baseItem == null) {
            throw new IllegalArgumentException("Base item cannot be null");
        }
        this.baseItem = baseItem;
    }

    @Override
    public String getName() {
        return baseItem.getName();
    }

    @Override
    public double getPrice() {
        return baseItem.getPrice();
    }

    /**
     * Returns the original item without decorators.
     */
    public Item getBaseItem() {
        return baseItem;
    }

    /**
     * Returns the name of this topping.
     */
    public abstract String getToppingName();

    /**
     * Returns the price of this topping alone.
     */
    public abstract double getToppingPrice();
}