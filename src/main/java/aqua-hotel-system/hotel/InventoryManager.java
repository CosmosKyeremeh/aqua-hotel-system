package com.hotel;

import com.hotel.NotificationManager;
import com.hotel.Room;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryManager {
    private Map<String, InventoryItem> inventory;
    private List<MaintenanceTask> maintenanceTasks;
    private List<CleaningTask> cleaningTasks;
    private NotificationManager notificationManager;

    public enum InventoryCategory {
        LINENS,
        TOILETRIES,
        CLEANING_SUPPLIES,
        AMENITIES,
        MAINTENANCE_SUPPLIES,
        FURNITURE,
        ELECTRONICS
    }

    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }

    public enum TaskStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    public InventoryManager(NotificationManager notificationManager) {
        this.inventory = new HashMap<>();
        this.maintenanceTasks = new ArrayList<>();
        this.cleaningTasks = new ArrayList<>();
        this.notificationManager = notificationManager;
    }

    // Inventory Management
    public void addInventoryItem(String name, InventoryCategory category, int quantity,
                               double unitCost, int reorderPoint) {
        InventoryItem item = new InventoryItem(name, category, quantity, unitCost, reorderPoint);
        inventory.put(name, item);
    }

    public void updateInventoryQuantity(String itemName, int quantityChange) {
        InventoryItem item = inventory.get(itemName);
        if (item != null) {
            item.updateQuantity(quantityChange);
            if (item.getQuantity() <= item.getReorderPoint()) {
                notifyLowInventory(item);
            }
        }
    }

    private void notifyLowInventory(InventoryItem item) {
        String message = String.format("Low inventory alert: %s (Quantity: %d, Reorder Point: %d)",
                item.getName(), item.getQuantity(), item.getReorderPoint());
        notificationManager.sendNotification("inventory_staff", "Low Inventory Alert",
                message, NotificationManager.NotificationType.SYSTEM_ALERT,
                NotificationManager.NotificationPriority.HIGH);
    }

    public List<InventoryItem> getLowStockItems() {
        return inventory.values().stream()
                .filter(item -> item.getQuantity() <= item.getReorderPoint())
                .collect(Collectors.toList());
    }

    // Maintenance Tasks
    public MaintenanceTask createMaintenanceTask(Room room, String description,
                                               TaskPriority priority, String assignedTo) {
        MaintenanceTask task = new MaintenanceTask(room, description, priority, assignedTo);
        maintenanceTasks.add(task);
        notifyMaintenanceStaff(task);
        return task;
    }

    private void notifyMaintenanceStaff(MaintenanceTask task) {
        String message = String.format("New maintenance task for Room %s: %s",
                task.getRoom().getNumber(), task.getDescription());
        notificationManager.sendNotification(task.getAssignedTo(), "New Maintenance Task",
                message, NotificationManager.NotificationType.ROOM_MAINTENANCE,
                NotificationManager.NotificationPriority.valueOf(task.getPriority().name()));
    }

    // Cleaning Tasks
    public CleaningTask createCleaningTask(Room room, TaskPriority priority, String assignedTo) {
        CleaningTask task = new CleaningTask(room, priority, assignedTo);
        cleaningTasks.add(task);
        notifyCleaningStaff(task);
        return task;
    }

    private void notifyCleaningStaff(CleaningTask task) {
        String message = String.format("New cleaning task for Room %s",
                task.getRoom().getNumber());
        notificationManager.sendNotification(task.getAssignedTo(), "New Cleaning Task",
                message, NotificationManager.NotificationType.ROOM_CLEANING,
                NotificationManager.NotificationPriority.valueOf(task.getPriority().name()));
    }

    // Task Management
    public List<MaintenanceTask> getPendingMaintenanceTasks() {
        return maintenanceTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.PENDING)
                .collect(Collectors.toList());
    }

    public List<CleaningTask> getPendingCleaningTasks() {
        return cleaningTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.PENDING)
                .collect(Collectors.toList());
    }

    // Inner Classes
    public static class InventoryItem {
        private final String name;
        private final InventoryCategory category;
        private int quantity;
        private final double unitCost;
        private final int reorderPoint;
        private final List<InventoryTransaction> transactions;

        public InventoryItem(String name, InventoryCategory category, int quantity,
                           double unitCost, int reorderPoint) {
            this.name = name;
            this.category = category;
            this.quantity = quantity;
            this.unitCost = unitCost;
            this.reorderPoint = reorderPoint;
            this.transactions = new ArrayList<>();
        }

        public void updateQuantity(int change) {
            this.quantity += change;
            transactions.add(new InventoryTransaction(change));
        }

        public String getName() { return name; }
        public InventoryCategory getCategory() { return category; }
        public int getQuantity() { return quantity; }
        public double getUnitCost() { return unitCost; }
        public int getReorderPoint() { return reorderPoint; }
        public List<InventoryTransaction> getTransactions() {
            return new ArrayList<>(transactions);
        }
    }

    public static class InventoryTransaction {
        private final String id;
        private final int quantityChange;
        private final LocalDateTime timestamp;

        public InventoryTransaction(int quantityChange) {
            this.id = UUID.randomUUID().toString();
            this.quantityChange = quantityChange;
            this.timestamp = LocalDateTime.now();
        }

        public String getId() { return id; }
        public int getQuantityChange() { return quantityChange; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }

    public static class MaintenanceTask {
        private final String id;
        private final Room room;
        private final String description;
        private TaskStatus status;
        private final TaskPriority priority;
        private final String assignedTo;
        private final LocalDateTime createdAt;
        private LocalDateTime completedAt;
        private String notes;

        public MaintenanceTask(Room room, String description, TaskPriority priority, String assignedTo) {
            this.id = UUID.randomUUID().toString();
            this.room = room;
            this.description = description;
            this.status = TaskStatus.PENDING;
            this.priority = priority;
            this.assignedTo = assignedTo;
            this.createdAt = LocalDateTime.now();
        }

        public void complete(String notes) {
            this.status = TaskStatus.COMPLETED;
            this.completedAt = LocalDateTime.now();
            this.notes = notes;
        }

        public String getId() { return id; }
        public Room getRoom() { return room; }
        public String getDescription() { return description; }
        public TaskStatus getStatus() { return status; }
        public TaskPriority getPriority() { return priority; }
        public String getAssignedTo() { return assignedTo; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getCompletedAt() { return completedAt; }
        public String getNotes() { return notes; }
    }

    public static class CleaningTask {
        private final String id;
        private final Room room;
        private TaskStatus status;
        private final TaskPriority priority;
        private final String assignedTo;
        private final LocalDateTime createdAt;
        private LocalDateTime completedAt;
        private String notes;

        public CleaningTask(Room room, TaskPriority priority, String assignedTo) {
            this.id = UUID.randomUUID().toString();
            this.room = room;
            this.status = TaskStatus.PENDING;
            this.priority = priority;
            this.assignedTo = assignedTo;
            this.createdAt = LocalDateTime.now();
        }

        public void complete(String notes) {
            this.status = TaskStatus.COMPLETED;
            this.completedAt = LocalDateTime.now();
            this.notes = notes;
        }

        public String getId() { return id; }
        public Room getRoom() { return room; }
        public TaskStatus getStatus() { return status; }
        public TaskPriority getPriority() { return priority; }
        public String getAssignedTo() { return assignedTo; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getCompletedAt() { return completedAt; }
        public String getNotes() { return notes; }
    }
}