# JavaDelivery 🍕

A food delivery management system built with Java, demonstrating multiple **design patterns** through an interactive Terminal User Interface (TUI).

## Features

- **TUI Dashboard** - Modern terminal interface using Lanterna library
- **Real-time Updates** - Orders progress automatically with visual feedback
- **Interactive Order Creation** - Full wizard to create custom orders
- **Design Patterns** - Factory, Decorator, Strategy, Observer, Singleton, Command

## Quick Start

```bash
# Compile
mvn clean compile

# Run
mvn exec:java
```

## TUI Controls

| Key | Action |
|-----|--------|
| `↑↓` | Navigate between orders |
| `N` | **New Order** - Interactive wizard |
| `C` | **Cancel** selected order |
| `R` | Refresh screen |
| `Q` | Quit |

## New Order Wizard (Press N)

1. **Select Restaurant** - Choose from available restaurants
2. **Select Dishes** - Pick items from the menu
3. **Add Toppings** - Customize with extras (Decorator pattern)
4. **Enter Name** - Customer name input
5. **Payment Method** - Credit Card, PayPal, or Cash (Strategy pattern)
6. **Confirm** - Order is placed using Command pattern

## Order Lifecycle

Orders automatically progress through states (every ~3 seconds):

```
⏳ Placed    █░░░░░  →  ✅ Confirmed ██░░░░  →  🟡 Cooking ███░░░
     ↓
📦 Ready    ████░░  →  🔵 On Way   █████░  →  ✔ Delivered ██████
```

## Design Patterns Used

| Pattern | Implementation |
|---------|----------------|
| **Factory** | `DishFactory`, `RestaurantFactory` |
| **Decorator** | Toppings: `BaconTopping`, `CheeseTopping`, etc. |
| **Strategy** | Payments: `CreditCardPayment`, `PayPalPayment`, `CashOnDeliveryPayment` |
| **Observer** | `OrderObserver`, `CustomerNotifier` |
| **Singleton** | `AppConfig` (thread-safe) |
| **Command** | `PlaceOrderCommand`, `CancelOrderCommand` |

## Project Structure

```
src/main/java/com/javadelivery/
├── Main.java              # Entry point
├── tui/                   # Terminal UI
│   ├── TuiApp.java        # Main dashboard
│   └── NewOrderWizard.java # Order creation wizard
├── simulation/            # Order simulation
│   └── OrderManager.java  # Manages orders & auto-progression
└── (domain models, patterns...)
```

## Requirements

- Java 17+
- Maven 3.6+