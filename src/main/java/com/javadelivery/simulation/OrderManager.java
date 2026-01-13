package com.javadelivery.simulation;

import com.javadelivery.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Manages active orders and simulates state transitions.
 * Implements OrderObserver to track order status changes.
 */
public class OrderManager implements OrderObserver {
    private final List<Order> orders;
    private final List<TimelineEvent> activityLog;
    private final Map<String, List<TimelineEvent>> orderTimelines;
    private final List<Runnable> changeListeners;
    private final ScheduledExecutorService scheduler;
    private volatile boolean running;

    private static final int MAX_LOG_ENTRIES = 50;

    public OrderManager() {
        this.orders = new CopyOnWriteArrayList<>();
        this.activityLog = new CopyOnWriteArrayList<>();
        this.orderTimelines = new ConcurrentHashMap<>();
        this.changeListeners = new CopyOnWriteArrayList<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.running = false;
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.addObserver(this);
        orderTimelines.put(order.getOrderId(), new CopyOnWriteArrayList<>());
        
        // Add initial timeline event
        addTimelineEvent(order.getOrderId(), "Order placed", "⏳");
        addToActivityLog("New order #" + order.getOrderId() + " received");
        notifyChangeListeners();
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    public List<TimelineEvent> getActivityLog() {
        return Collections.unmodifiableList(activityLog);
    }

    public List<TimelineEvent> getOrderTimeline(String orderId) {
        return orderTimelines.getOrDefault(orderId, Collections.emptyList());
    }

    public void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }

    private void notifyChangeListeners() {
        for (Runnable listener : changeListeners) {
            listener.run();
        }
    }

    private void addTimelineEvent(String orderId, String description, String icon) {
        List<TimelineEvent> timeline = orderTimelines.get(orderId);
        if (timeline != null) {
            timeline.add(new TimelineEvent(description, icon));
        }
    }

    private void addToActivityLog(String message) {
        activityLog.add(0, new TimelineEvent(message, "📋"));
        // Trim log if too long
        while (activityLog.size() > MAX_LOG_ENTRIES) {
            activityLog.remove(activityLog.size() - 1);
        }
    }

    @Override
    public void onOrderStatusChanged(String orderId, OrderStatus oldStatus, OrderStatus newStatus) {
        String icon = getStatusIcon(newStatus);
        addTimelineEvent(orderId, newStatus.getDisplayName(), icon);
        addToActivityLog("Order #" + orderId + ": " + oldStatus.getDisplayName() + " → " + newStatus.getDisplayName());
        notifyChangeListeners();
    }

    @Override
    public String getObserverName() {
        return "OrderManager-TUI";
    }

    private String getStatusIcon(OrderStatus status) {
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

    /**
     * Starts the order simulation - randomly advances order states
     */
    public void startSimulation() {
        if (running) return;
        running = true;
        
        scheduler.scheduleAtFixedRate(() -> {
            if (!running || orders.isEmpty()) return;
            
            // Randomly pick an order to advance
            List<Order> advanceable = orders.stream()
                    .filter(o -> canAdvance(o.getStatus()))
                    .toList();
            
            if (!advanceable.isEmpty()) {
                Order order = advanceable.get(ThreadLocalRandom.current().nextInt(advanceable.size()));
                OrderStatus nextStatus = getNextStatus(order.getStatus());
                if (nextStatus != null) {
                    order.updateStatus(nextStatus);
                }
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

    public void stopSimulation() {
        running = false;
    }

    public void shutdown() {
        stopSimulation();
        scheduler.shutdown();
    }

    private boolean canAdvance(OrderStatus status) {
        return status != OrderStatus.DELIVERED && status != OrderStatus.CANCELLED;
    }

    private OrderStatus getNextStatus(OrderStatus current) {
        return switch (current) {
            case PLACED -> OrderStatus.CONFIRMED;
            case CONFIRMED -> OrderStatus.PREPARING;
            case PREPARING -> OrderStatus.READY;
            case READY -> OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> OrderStatus.DELIVERED;
            default -> null;
        };
    }

    /**
     * Creates sample orders for demonstration
     */
    public void createSampleOrders() {
        List<Restaurant> restaurants = RestaurantFactory.getAllRestaurants();
        if (restaurants.size() < 3) return;

        Restaurant pizzaPalace = restaurants.get(0);
        Restaurant burgerHouse = restaurants.get(1);
        Restaurant sushiMaster = restaurants.get(2);

        // Order 1 - Pizza
        if (!pizzaPalace.getMenu().isEmpty()) {
            Item pizza = new BaconTopping(pizzaPalace.getMenu().get(0));
            Order order1 = new Order.Builder()
                    .setOrderId("ORD-001")
                    .setCustomerName("John Doe")
                    .addItem(pizza)
                    .build();
            addOrder(order1);
        }

        // Order 2 - Burger
        if (!burgerHouse.getMenu().isEmpty()) {
            Item burger = new CheeseTopping(burgerHouse.getMenu().get(0));
            Order order2 = new Order.Builder()
                    .setOrderId("ORD-002")
                    .setCustomerName("Jane Smith")
                    .addItem(burger)
                    .build();
            addOrder(order2);
            order2.updateStatus(OrderStatus.CONFIRMED);
            order2.updateStatus(OrderStatus.PREPARING);
        }

        // Order 3 - Sushi (almost delivered)
        if (sushiMaster.getMenu().size() >= 2) {
            Order order3 = new Order.Builder()
                    .setOrderId("ORD-003")
                    .setCustomerName("Bob Wilson")
                    .addItem(sushiMaster.getMenu().get(0))
                    .addItem(sushiMaster.getMenu().get(1))
                    .build();
            addOrder(order3);
            order3.updateStatus(OrderStatus.CONFIRMED);
            order3.updateStatus(OrderStatus.PREPARING);
            order3.updateStatus(OrderStatus.READY);
            order3.updateStatus(OrderStatus.OUT_FOR_DELIVERY);
        }

        // Order 4 - New pizza order
        if (pizzaPalace.getMenu().size() >= 2) {
            Order order4 = new Order.Builder()
                    .setOrderId("ORD-004")
                    .setCustomerName("Alice Brown")
                    .addItem(pizzaPalace.getMenu().get(1))
                    .build();
            addOrder(order4);
        }
    }
}
