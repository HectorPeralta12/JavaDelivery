package com.javadelivery;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String orderId;
    private final String customerName;
    private final List<Item> items;
    private final double baseTotal;

    private Order(Builder builder) {
        this.orderId = builder.orderId;
        this.customerName = builder.customerName;
        this.items = builder.items;
        this.baseTotal = calculateBaseTotal();
    }

    private double calculateBaseTotal() {
        return items.stream()
                .mapToDouble(Item::getPrice)
                .sum();
    }

    public double getBaseTotal() {
        return baseTotal;
    }

    public double applyDiscount(DiscountStrategy strategy) {
        return strategy.applyDiscount(baseTotal);
    }

    public static class Builder {
        private String orderId;
        private String customerName;
        private List<Item> items = new ArrayList<>();

        public Builder setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder setCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder addItem(Item item) {
            this.items.add(item);
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}