package com.javadelivery.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.javadelivery.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Interactive wizard for creating new orders.
 * Guides user through: Restaurant → Dishes → Toppings → Payment
 */
public class NewOrderWizard {
    
    private final Screen screen;
    private final TextGraphics graphics;
    
    private enum WizardStep {
        SELECT_RESTAURANT,
        SELECT_DISHES,
        SELECT_TOPPINGS,
        ENTER_NAME,
        SELECT_PAYMENT,
        CONFIRM
    }
    
    private WizardStep currentStep = WizardStep.SELECT_RESTAURANT;
    private int selectedIndex = 0;
    private Restaurant selectedRestaurant = null;
    private List<Item> selectedItems = new ArrayList<>();
    private Dish currentDishForTopping = null;
    private int currentToppingIndex = 0;
    private String customerName = "";
    private PaymentStrategy selectedPayment = null;
    private boolean cancelled = false;
    
    private static final String[] TOPPING_OPTIONS = {"None", "Bacon (+2.00€)", "Cheese (+1.50€)", "Extra Sauce (+1.00€)", "Jalapeño (+1.25€)"};
    private static final String[] PAYMENT_OPTIONS = {"Credit Card", "PayPal", "Cash on Delivery"};

    public NewOrderWizard(Screen screen, TextGraphics graphics) {
        this.screen = screen;
        this.graphics = graphics;
    }

    /**
     * Runs the wizard and returns the created order, or null if cancelled.
     */
    public Order run() throws IOException {
        while (!cancelled && currentStep != null) {
            render();
            screen.refresh();
            
            KeyStroke key = screen.readInput();
            if (key == null) continue;
            
            handleInput(key);
            
            if (currentStep == WizardStep.CONFIRM && !cancelled) {
                return createOrder();
            }
        }
        return null;
    }

    private void handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            cancelled = true;
            return;
        }

        switch (currentStep) {
            case SELECT_RESTAURANT -> handleRestaurantSelection(key);
            case SELECT_DISHES -> handleDishSelection(key);
            case SELECT_TOPPINGS -> handleToppingSelection(key);
            case ENTER_NAME -> handleNameEntry(key);
            case SELECT_PAYMENT -> handlePaymentSelection(key);
        }
    }

    private void handleRestaurantSelection(KeyStroke key) {
        List<Restaurant> restaurants = RestaurantFactory.getAllRestaurants();
        
        if (key.getKeyType() == KeyType.ArrowUp && selectedIndex > 0) {
            selectedIndex--;
        } else if (key.getKeyType() == KeyType.ArrowDown && selectedIndex < restaurants.size() - 1) {
            selectedIndex++;
        } else if (key.getKeyType() == KeyType.Enter) {
            selectedRestaurant = restaurants.get(selectedIndex);
            selectedIndex = 0;
            currentStep = WizardStep.SELECT_DISHES;
        }
    }

    private void handleDishSelection(KeyStroke key) {
        List<Dish> menu = selectedRestaurant.getMenu();
        
        if (key.getKeyType() == KeyType.ArrowUp && selectedIndex > 0) {
            selectedIndex--;
        } else if (key.getKeyType() == KeyType.ArrowDown && selectedIndex < menu.size()) {
            selectedIndex++;
        } else if (key.getKeyType() == KeyType.Enter) {
            if (selectedIndex < menu.size()) {
                currentDishForTopping = menu.get(selectedIndex);
                selectedIndex = 0;
                currentStep = WizardStep.SELECT_TOPPINGS;
            } else {
                // "Done" selected
                if (!selectedItems.isEmpty()) {
                    selectedIndex = 0;
                    currentStep = WizardStep.ENTER_NAME;
                }
            }
        }
    }

    private void handleToppingSelection(KeyStroke key) {
        if (key.getKeyType() == KeyType.ArrowUp && selectedIndex > 0) {
            selectedIndex--;
        } else if (key.getKeyType() == KeyType.ArrowDown && selectedIndex < TOPPING_OPTIONS.length - 1) {
            selectedIndex++;
        } else if (key.getKeyType() == KeyType.Enter) {
            Item item = currentDishForTopping;
            
            // Apply selected topping
            item = switch (selectedIndex) {
                case 1 -> new BaconTopping(currentDishForTopping);
                case 2 -> new CheeseTopping(currentDishForTopping);
                case 3 -> new ExtraSauceTopping(currentDishForTopping);
                case 4 -> new JalapenoTopping(currentDishForTopping);
                default -> currentDishForTopping;
            };
            
            selectedItems.add(item);
            selectedIndex = 0;
            currentStep = WizardStep.SELECT_DISHES;
        }
    }

    private void handleNameEntry(KeyStroke key) {
        if (key.getKeyType() == KeyType.Enter && !customerName.isBlank()) {
            selectedIndex = 0;
            currentStep = WizardStep.SELECT_PAYMENT;
        } else if (key.getKeyType() == KeyType.Backspace && !customerName.isEmpty()) {
            customerName = customerName.substring(0, customerName.length() - 1);
        } else if (key.getKeyType() == KeyType.Character) {
            char c = key.getCharacter();
            if (customerName.length() < 30 && (Character.isLetterOrDigit(c) || c == ' ')) {
                customerName += c;
            }
        }
    }

    private void handlePaymentSelection(KeyStroke key) {
        if (key.getKeyType() == KeyType.ArrowUp && selectedIndex > 0) {
            selectedIndex--;
        } else if (key.getKeyType() == KeyType.ArrowDown && selectedIndex < PAYMENT_OPTIONS.length - 1) {
            selectedIndex++;
        } else if (key.getKeyType() == KeyType.Enter) {
            selectedPayment = switch (selectedIndex) {
                case 0 -> new CreditCardPayment("4111111111111111", customerName, "12/28", "123");
                case 1 -> new PayPalPayment(customerName.toLowerCase().replace(" ", "") + "@email.com", "password");
                case 2 -> new CashOnDeliveryPayment(customerName, "+1234567890");
                default -> null;
            };
            currentStep = WizardStep.CONFIRM;
        }
    }

    private Order createOrder() {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        
        Order.Builder builder = new Order.Builder()
                .setOrderId(orderId)
                .setCustomerName(customerName);
        
        for (Item item : selectedItems) {
            builder.addItem(item);
        }
        
        Order order = builder.build();
        
        // Process payment using Command pattern
        PlaceOrderCommand placeCommand = new PlaceOrderCommand(order, selectedPayment);
        placeCommand.execute();
        
        return order;
    }

    private void render() {
        TerminalSize size = screen.getTerminalSize();
        int width = size.getColumns();
        int height = size.getRows();
        
        screen.clear();
        
        // Border
        graphics.setForegroundColor(TextColor.ANSI.GREEN);
        graphics.putString(0, 0, "┌" + "─".repeat(width - 2) + "┐");
        graphics.putString(0, height - 1, "└" + "─".repeat(width - 2) + "┘");
        for (int y = 1; y < height - 1; y++) {
            graphics.putString(0, y, "│");
            graphics.putString(width - 1, y, "│");
        }
        
        // Title
        String title = "  🛒 NEW ORDER - " + getStepTitle();
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(2, 1, title);
        
        graphics.setForegroundColor(TextColor.ANSI.GREEN);
        graphics.putString(2, 2, "─".repeat(width - 4));
        
        // Content based on step
        switch (currentStep) {
            case SELECT_RESTAURANT -> renderRestaurantSelection();
            case SELECT_DISHES -> renderDishSelection();
            case SELECT_TOPPINGS -> renderToppingSelection();
            case ENTER_NAME -> renderNameEntry();
            case SELECT_PAYMENT -> renderPaymentSelection();
        }
        
        // Footer
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(2, height - 2, "[↑↓] Navigate  [ENTER] Select  [ESC] Cancel");
    }

    private String getStepTitle() {
        return switch (currentStep) {
            case SELECT_RESTAURANT -> "Select Restaurant";
            case SELECT_DISHES -> "Select Dishes from " + selectedRestaurant.getName();
            case SELECT_TOPPINGS -> "Add Topping to " + currentDishForTopping.getName();
            case ENTER_NAME -> "Enter Your Name";
            case SELECT_PAYMENT -> "Select Payment Method";
            case CONFIRM -> "Confirm Order";
        };
    }

    private void renderRestaurantSelection() {
        List<Restaurant> restaurants = RestaurantFactory.getAllRestaurants();
        int y = 4;
        
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(4, y++, "Choose a restaurant:");
        y++;
        
        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant r = restaurants.get(i);
            boolean selected = (i == selectedIndex);
            
            graphics.setForegroundColor(selected ? TextColor.ANSI.WHITE_BRIGHT : TextColor.ANSI.WHITE);
            String prefix = selected ? "► " : "  ";
            graphics.putString(4, y++, prefix + r.getName() + " (" + r.getCuisineType() + ")");
        }
    }

    private void renderDishSelection() {
        List<Dish> menu = selectedRestaurant.getMenu();
        int y = 4;
        
        // Show current cart
        if (!selectedItems.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN);
            graphics.putString(4, y++, "🛒 Cart: " + selectedItems.size() + " item(s) - " + 
                    String.format("%.2f€", selectedItems.stream().mapToDouble(Item::getPrice).sum()));
            y++;
        }
        
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(4, y++, "Select dishes to add:");
        y++;
        
        for (int i = 0; i < menu.size(); i++) {
            Dish d = menu.get(i);
            boolean selected = (i == selectedIndex);
            
            graphics.setForegroundColor(selected ? TextColor.ANSI.WHITE_BRIGHT : TextColor.ANSI.WHITE);
            String prefix = selected ? "► " : "  ";
            String categoryIcon = switch (d.getCategory()) {
                case VEG -> "🥬";
                case NON_VEG -> "🍖";
                case VEGAN -> "🌱";
            };
            graphics.putString(4, y++, String.format("%s%s %s - %.2f€", prefix, categoryIcon, d.getName(), d.getPrice()));
        }
        
        // Done option
        boolean doneSelected = (selectedIndex == menu.size());
        graphics.setForegroundColor(doneSelected ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.GREEN);
        String donePrefix = doneSelected ? "► " : "  ";
        graphics.putString(4, y + 1, donePrefix + "✓ Done - Proceed to checkout");
    }

    private void renderToppingSelection() {
        int y = 4;
        
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(4, y++, "Add a topping to: " + currentDishForTopping.getName());
        y++;
        
        for (int i = 0; i < TOPPING_OPTIONS.length; i++) {
            boolean selected = (i == selectedIndex);
            graphics.setForegroundColor(selected ? TextColor.ANSI.WHITE_BRIGHT : TextColor.ANSI.WHITE);
            String prefix = selected ? "► " : "  ";
            graphics.putString(4, y++, prefix + TOPPING_OPTIONS[i]);
        }
    }

    private void renderNameEntry() {
        int y = 4;
        
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(4, y++, "Enter customer name:");
        y++;
        
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(4, y++, "Name: " + customerName + "_");
        y++;
        
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(4, y, "(Press ENTER when done)");
    }

    private void renderPaymentSelection() {
        int y = 4;
        
        // Order summary
        graphics.setForegroundColor(TextColor.ANSI.GREEN);
        graphics.putString(4, y++, "📋 Order Summary:");
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        for (Item item : selectedItems) {
            graphics.putString(6, y++, "• " + item.getName() + " - " + String.format("%.2f€", item.getPrice()));
        }
        double total = selectedItems.stream().mapToDouble(Item::getPrice).sum();
        double deliveryFee = AppConfig.getInstance().getDeliveryFee();
        y++;
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(4, y++, String.format("Subtotal: %.2f€", total));
        graphics.putString(4, y++, String.format("Delivery: %.2f€", deliveryFee));
        graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        graphics.putString(4, y++, String.format("Total: %.2f€", total + deliveryFee));
        y++;
        
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(4, y++, "Select payment method:");
        y++;
        
        String[] paymentIcons = {"💳", "🅿️", "💵"};
        for (int i = 0; i < PAYMENT_OPTIONS.length; i++) {
            boolean selected = (i == selectedIndex);
            graphics.setForegroundColor(selected ? TextColor.ANSI.WHITE_BRIGHT : TextColor.ANSI.WHITE);
            String prefix = selected ? "► " : "  ";
            graphics.putString(4, y++, prefix + paymentIcons[i] + " " + PAYMENT_OPTIONS[i]);
        }
    }
}
