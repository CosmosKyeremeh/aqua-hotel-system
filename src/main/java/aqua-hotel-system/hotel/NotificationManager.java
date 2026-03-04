package com.hotel;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class NotificationManager {
    private Map<String, List<Notification>> userNotifications; // userId -> notifications
    private List<NotificationSubscriber> subscribers;

    public enum NotificationType {
        BOOKING_CONFIRMATION,
        CHECK_IN_REMINDER,
        CHECK_OUT_REMINDER,
        PAYMENT_RECEIVED,
        PAYMENT_DUE,
        ROOM_MAINTENANCE,
        ROOM_CLEANING,
        SYSTEM_ALERT,
        STAFF_ASSIGNMENT
    }

    public enum NotificationPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }

    public NotificationManager() {
        this.userNotifications = new HashMap<>();
        this.subscribers = new ArrayList<>();
    }

    public void subscribe(NotificationSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(NotificationSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void sendNotification(String userId, String title, String message,
                               NotificationType type, NotificationPriority priority) {
        Notification notification = new Notification(title, message, type, priority);
        userNotifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(notification);
        for (NotificationSubscriber subscriber : subscribers) {
            subscriber.onNotification(notification);
        }
    }

    public void sendBookingConfirmation(Booking booking) {
        System.out.println("Sending booking confirmation to " + booking.getUser().getEmail());
        System.out.println("Room: " + booking.getRoom().getType());
        System.out.println("Check-in: " + booking.getCheckIn());
        System.out.println("Check-out: " + booking.getCheckOut());
        System.out.println("Total Price: $" + booking.getTotalPrice());
    }

    // --- Missing methods added below ---

    public void sendCheckInConfirmation(Booking booking) {
        String title = "Check-in Confirmed";
        String message = String.format("Check-in confirmed for Room %s. Welcome!",
                booking.getRoom().getNumber());
        sendNotification(booking.getUser().getId(), title, message,
                NotificationType.CHECK_IN_REMINDER, NotificationPriority.MEDIUM);
        System.out.println("Check-in confirmation sent to " + booking.getUser().getEmail());
    }

    public void sendCheckOutConfirmation(Booking booking) {
        String title = "Check-out Confirmed";
        String message = String.format("Check-out from Room %s confirmed. Thank you for staying with us!",
                booking.getRoom().getNumber());
        sendNotification(booking.getUser().getId(), title, message,
                NotificationType.CHECK_OUT_REMINDER, NotificationPriority.MEDIUM);
        System.out.println("Check-out confirmation sent to " + booking.getUser().getEmail());
    }

    public void sendCancellationConfirmation(Booking booking) {
        String title = "Booking Cancelled";
        String message = String.format("Your booking for Room %s has been cancelled.",
                booking.getRoom().getNumber());
        sendNotification(booking.getUser().getId(), title, message,
                NotificationType.BOOKING_CONFIRMATION, NotificationPriority.HIGH);
        System.out.println("Cancellation confirmation sent to " + booking.getUser().getEmail());
    }

    // --- End of added methods ---

    public void sendWelcomeEmail(String userId) {
        System.out.println("Sending welcome email to user " + userId);
    }

    public void sendLoginFailureAlert(String userId) {
        System.out.println("Sending login failure alert for user " + userId);
    }

    public void sendCheckInReminder(String userId, Booking booking) {
        String title = "Check-in Reminder";
        String message = String.format("Your check-in for Room %s is tomorrow. Check-in time starts from 2 PM.",
                booking.getRoom().getNumber());
        sendNotification(userId, title, message);
    }

    public void sendCheckOutReminder(String userId, Booking booking) {
        String title = "Check-out Reminder";
        String message = String.format("Your check-out from Room %s is tomorrow. Please check out by 11 AM.",
                booking.getRoom().getNumber());
        sendNotification(userId, title, message);
    }

    public void sendPaymentConfirmation(String userId, Payment payment) {
        String title = "Payment Received";
        String message = String.format("Payment of $%.2f has been received. Receipt number: %s",
                payment.getPaidAmount(), payment.getReceiptNumber());
        sendNotification(userId, title, message, NotificationType.PAYMENT_RECEIVED, NotificationPriority.MEDIUM);
    }

    public void sendPaymentReminder(String userId, Payment payment) {
        String title = "Payment Due";
        String message = String.format("Payment of $%.2f is due for your booking.",
                payment.getRemainingAmount());
        sendNotification(userId, title, message, NotificationType.PAYMENT_DUE, NotificationPriority.HIGH);
    }

    public void sendMaintenanceAlert(String userId, Room room) {
        String title = "Room Maintenance Required";
        String message = String.format("Room %s requires maintenance attention.", room.getNumber());
        sendNotification(userId, title, message, NotificationType.ROOM_MAINTENANCE, NotificationPriority.MEDIUM);
    }

    public void sendCleaningAlert(String userId, Room room) {
        String title = "Room Cleaning Required";
        String message = String.format("Room %s needs to be cleaned.", room.getNumber());
        sendNotification(userId, title, message, NotificationType.ROOM_CLEANING, NotificationPriority.MEDIUM);
    }

    public List<Notification> getUserNotifications(String userId) {
        return userNotifications.getOrDefault(userId, new ArrayList<>());
    }

    public List<Notification> getUserUnreadNotifications(String userId) {
        return getUserNotifications(userId).stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
    }

    public void markNotificationAsRead(String userId, String notificationId) {
        getUserNotifications(userId).stream()
                .filter(n -> n.getId().equals(notificationId))
                .findFirst()
                .ifPresent(Notification::markAsRead);
    }

    public void clearUserNotifications(String userId) {
        userNotifications.remove(userId);
    }

    public void sendNotification(String message, String recipient) {
        System.out.println("Notification to " + recipient + ": " + message);
    }

    public void sendNotification(String userId, String title, String message) {
        System.out.println("Notification to " + userId + ": " + title + " - " + message);
    }

    // Inner class
    public static class Notification {
        private final String id;
        private final String title;
        private final String message;
        private final NotificationType type;
        private final NotificationPriority priority;
        private final LocalDateTime timestamp;
        private boolean read;

        public Notification(String title, String message, NotificationType type, NotificationPriority priority) {
            this.id = UUID.randomUUID().toString();
            this.title = title;
            this.message = message;
            this.type = type;
            this.priority = priority;
            this.timestamp = LocalDateTime.now();
            this.read = false;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public NotificationType getType() { return type; }
        public NotificationPriority getPriority() { return priority; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public boolean isRead() { return read; }
        public void markAsRead() { this.read = true; }
    }

    public interface NotificationSubscriber {
        void onNotification(Notification notification);
    }
}