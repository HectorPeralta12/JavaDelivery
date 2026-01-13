package com.javadelivery.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.javadelivery.AppConfig;
import com.javadelivery.CancelOrderCommand;
import com.javadelivery.Item;
import com.javadelivery.Order;
import com.javadelivery.OrderStatus;
import com.javadelivery.simulation.OrderManager;
import com.javadelivery.simulation.TimelineEvent;
import com.javadelivery.tui.render.ProgressBarRenderer;
import com.javadelivery.tui.render.StatusBadgeRenderer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main TUI Application using Lanterna.
 * Displays a modern dashboard for order management.
 */
public class TuiApp {
    private Terminal terminal;
    private Screen screen;
    private TextGraphics graphics;
    private OrderManager orderManager;
    private ScheduledExecutorService refreshScheduler;

    private int selectedOrderIndex = 0;
    private volatile boolean running = true;
    private volatile LocalDateTime lastUpdate = LocalDateTime.now();

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public TuiApp(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public void start() throws IOException {
        // Initialize terminal
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        terminal = factory.createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null); // Hide cursor

        graphics = screen.newTextGraphics();

        // Set up auto-refresh
        refreshScheduler = Executors.newSingleThreadScheduledExecutor();
        refreshScheduler.scheduleAtFixedRate(() -> {
            lastUpdate = LocalDateTime.now();
            try {
                render();
            } catch (IOException e) {
                // Ignore render errors during refresh
            }
        }, 0, 2, TimeUnit.SECONDS);

        // Listen for order changes
        orderManager.addChangeListener(() -> {
            lastUpdate = LocalDateTime.now();
            try {
                render();
            } catch (IOException e) {
                // Ignore
            }
        });

        // Main input loop
        while (running) {
            KeyStroke keyStroke = screen.pollInput();
            if (keyStroke != null) {
                handleInput(keyStroke);
            }
            Thread.yield();
        }

        stop();
    }

    private void handleInput(KeyStroke keyStroke) throws IOException {
        if (keyStroke.getKeyType() == KeyType.Character) {
            char c = Character.toLowerCase(keyStroke.getCharacter());
            switch (c) {
                case 'q' -> running = false;
                case 'r' -> {
                    lastUpdate = LocalDateTime.now();
                    render();
                }
                case 'n' -> {
                    // Open new order wizard
                    openNewOrderWizard();
                }
                case 'c' -> {
                    // Cancel selected order
                    cancelSelectedOrder();
                }
            }
        } else {
            switch (keyStroke.getKeyType()) {
                case ArrowUp -> {
                    if (selectedOrderIndex > 0) {
                        selectedOrderIndex--;
                        render();
                    }
                }
                case ArrowDown -> {
                    if (selectedOrderIndex < orderManager.getOrders().size() - 1) {
                        selectedOrderIndex++;
                        render();
                    }
                }
                case Escape -> running = false;
                default -> {}
            }
        }
    }

    private void render() throws IOException {
        screen.clear();
        TerminalSize size = screen.getTerminalSize();
        int width = size.getColumns();
        int height = size.getRows();

        // Draw border
        drawBorder(width, height);

        // Draw header
        drawHeader(width);

        // Draw order list (left side)
        drawOrderList(width, height);

        // Draw order details (right side)
        drawOrderDetails(width, height);

        // Draw activity log
        drawActivityLog(width, height);

        // Draw status bar
        drawStatusBar(width, height);

        screen.refresh();
    }

    private void drawBorder(int width, int height) {
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        
        // Top border
        graphics.putString(0, 0, "┌" + "─".repeat(width - 2) + "┐");
        
        // Bottom border
        graphics.putString(0, height - 1, "└" + "─".repeat(width - 2) + "┘");
        
        // Side borders
        for (int y = 1; y < height - 1; y++) {
            graphics.putString(0, y, "│");
            graphics.putString(width - 1, y, "│");
        }
    }

    private void drawHeader(int width) {
        AppConfig config = AppConfig.getInstance();
        String title = "  🍕 " + config.getAppName().toUpperCase() + " v" + config.getVersion();
        String time = "Last update: " + lastUpdate.format(TIME_FORMAT) + "  ";

        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(1, 1, title);
        
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(width - time.length() - 1, 1, time);

        // Header separator
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(0, 2, "├" + "─".repeat(width - 2) + "┤");
    }

    private void drawOrderList(int width, int height) {
        int listWidth = (width * 55) / 100; // 55% of width
        int startY = 3;
        int endY = height - 8;

        // Column headers
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        String headers = String.format("  %-3s│ %-15s│ %-10s│%-7s", "#", "Customer", "Status", "Progress");
        graphics.putString(1, startY, headers.substring(0, Math.min(headers.length(), listWidth - 2)));

        // Separator
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(1, startY + 1, "─".repeat(listWidth - 2));

        // Orders
        List<Order> orders = orderManager.getOrders();
        int displayY = startY + 2;
        
        for (int i = 0; i < orders.size() && displayY < endY; i++) {
            Order order = orders.get(i);
            boolean selected = (i == selectedOrderIndex);

            // Selection indicator
            String prefix = selected ? "►" : " ";
            
            // Status info
            String icon = StatusBadgeRenderer.getIcon(order.getStatus());
            String statusName = StatusBadgeRenderer.getShortName(order.getStatus());
            int progress = StatusBadgeRenderer.getProgressPercent(order.getStatus());
            String progressBar = ProgressBarRenderer.render(progress);

            // Highlight selected row
            if (selected) {
                graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
            } else {
                graphics.setForegroundColor(TextColor.ANSI.WHITE);
            }

            String customerName = order.getCustomerName();
            if (customerName.length() > 14) {
                customerName = customerName.substring(0, 12) + "..";
            }

            String row = String.format("%s%-3s│ %-15s│%s%-9s│%s",
                    prefix,
                    order.getOrderId().replace("ORD-", ""),
                    customerName,
                    icon,
                    statusName,
                    progressBar);

            graphics.putString(1, displayY, row.substring(0, Math.min(row.length(), listWidth - 2)));
            displayY++;
        }

        // Vertical separator between list and details
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        for (int y = 2; y < height - 6; y++) {
            graphics.putString(listWidth, y, "│");
        }
        graphics.putString(listWidth, 2, "┬");
    }

    private void drawOrderDetails(int width, int height) {
        int listWidth = (width * 55) / 100;
        int detailX = listWidth + 2;
        int detailWidth = width - listWidth - 3;
        int startY = 3;

        List<Order> orders = orderManager.getOrders();
        if (orders.isEmpty() || selectedOrderIndex >= orders.size()) {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(detailX, startY, "No order selected");
            return;
        }

        Order order = orders.get(selectedOrderIndex);

        // Title
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(detailX, startY, "ORDER DETAILS");

        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(detailX, startY + 1, "─".repeat(Math.min(detailWidth, 30)));

        // Order info
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(detailX, startY + 2, "ID: " + order.getOrderId());
        graphics.putString(detailX, startY + 3, "Customer: " + order.getCustomerName());
        
        // Items
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(detailX, startY + 4, "Items:");
        
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        int itemY = startY + 5;
        for (Item item : order.getItems()) {
            String itemStr = "  - " + truncate(item.getName(), 20) + String.format(" %.2f€", item.getPrice());
            graphics.putString(detailX, itemY++, truncate(itemStr, detailWidth));
            if (itemY > startY + 9) break;
        }

        // Total
        graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        graphics.putString(detailX, itemY + 1, String.format("Total: %.2f€", order.getBaseTotal()));

        // Timeline section
        int timelineY = itemY + 3;
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(detailX, timelineY, "─".repeat(Math.min(detailWidth, 30)));
        
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(detailX, timelineY + 1, "TIMELINE");

        List<TimelineEvent> timeline = orderManager.getOrderTimeline(order.getOrderId());
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        int eventY = timelineY + 2;
        int maxEvents = Math.min(timeline.size(), 5);
        for (int i = timeline.size() - maxEvents; i < timeline.size() && eventY < height - 8; i++) {
            TimelineEvent event = timeline.get(i);
            String eventStr = event.getFormattedTime() + " " + event.getIcon() + " " + event.getDescription();
            graphics.putString(detailX, eventY++, truncate(eventStr, detailWidth));
        }
    }

    private void drawActivityLog(int width, int height) {
        int logY = height - 6;
        
        // Separator
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(0, logY, "├" + "─".repeat(width - 2) + "┤");
        
        // Title
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(2, logY + 1, "ACTIVITY LOG");

        // Log entries
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        List<TimelineEvent> log = orderManager.getActivityLog();
        int maxEntries = Math.min(log.size(), 2);
        for (int i = 0; i < maxEntries; i++) {
            TimelineEvent event = log.get(i);
            String logStr = "[" + event.getFormattedTime() + "] " + event.getDescription();
            graphics.putString(2, logY + 2 + i, truncate(logStr, width - 4));
        }
    }

    private void drawStatusBar(int width, int height) {
        int statusY = height - 2;
        
        // Separator
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(0, statusY - 1, "├" + "─".repeat(width - 2) + "┤");
        
        // Status bar content
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        String statusBar = "  [↑↓] Navigate  [N] New Order  [C] Cancel  [R] Refresh  [Q] Quit  ";
        graphics.putString(1, statusY, statusBar);
    }

    private void openNewOrderWizard() throws IOException {
        // Pause auto-refresh during wizard
        refreshScheduler.shutdown();
        
        NewOrderWizard wizard = new NewOrderWizard(screen, graphics);
        Order newOrder = wizard.run();
        
        if (newOrder != null) {
            orderManager.addOrder(newOrder);
        }
        
        // Resume auto-refresh
        refreshScheduler = Executors.newSingleThreadScheduledExecutor();
        refreshScheduler.scheduleAtFixedRate(() -> {
            lastUpdate = LocalDateTime.now();
            try {
                render();
            } catch (IOException e) {
                // Ignore
            }
        }, 0, 2, TimeUnit.SECONDS);
        
        render();
    }

    private void cancelSelectedOrder() throws IOException {
        List<Order> orders = orderManager.getOrders();
        if (orders.isEmpty() || selectedOrderIndex >= orders.size()) {
            return;
        }
        
        Order order = orders.get(selectedOrderIndex);
        
        // Can only cancel orders that are not delivered or already cancelled
        if (order.getStatus() == OrderStatus.DELIVERED || 
            order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }
        
        CancelOrderCommand cancelCommand = new CancelOrderCommand(order, "Cancelled by user");
        cancelCommand.execute();
        
        lastUpdate = LocalDateTime.now();
        render();
    }

    private String truncate(String str, int maxLen) {
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 2) + "..";
    }

    public void stop() {
        running = false;
        if (refreshScheduler != null) {
            refreshScheduler.shutdown();
        }
        orderManager.shutdown();
        try {
            if (screen != null) {
                screen.stopScreen();
            }
            if (terminal != null) {
                terminal.close();
            }
        } catch (IOException e) {
            // Ignore close errors
        }
    }
}
