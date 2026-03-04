package com.hotel;

import java.time.LocalDate;

public class Booking {
    private String id;
    private User user;
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalPrice;
    private BookingStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String specialRequests;
    private int numberOfGuests;

    // Main constructor used by the application
    public Booking(User user, Room room, LocalDate checkIn, LocalDate checkOut, double totalPrice) {
        this.id = generateBookingId();
        this.user = user;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalPrice = totalPrice;
        this.status = BookingStatus.PENDING;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.numberOfGuests = room.getCapacity();
    }

    // Constructor for database operations
    public Booking(String userId, String roomId, LocalDate checkIn, LocalDate checkOut, 
                  double totalPrice, int numberOfGuests) {
        this.id = generateBookingId();
        // These will be set later by DAO
        this.user = null;  
        this.room = null;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalPrice = totalPrice;
        this.status = BookingStatus.PENDING;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.numberOfGuests = numberOfGuests;
    }

    private String generateBookingId() {
        // Simple booking ID generation - you might want to make this more sophisticated
        return "BK" + System.currentTimeMillis();
    }

    // Getters
    public String getId() { return id; }
    public User getUser() { return user; }
    public Room getRoom() { return room; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getTotalPrice() { return totalPrice; }
    public BookingStatus getStatus() { return status; }
    public LocalDate getCreatedAt() { return createdAt; }
    public LocalDate getUpdatedAt() { return updatedAt; }
    public String getSpecialRequests() { return specialRequests; }
    public int getNumberOfGuests() { return numberOfGuests; }
    
    // Database specific getters
    public String getUserId() { return user != null ? user.getId() : null; }
    public String getRoomId() { return room != null ? room.getId() : null; }
    
    // Alias methods for reporting
    public LocalDate getCheckInDate() { return checkIn; }
    public LocalDate getCheckOutDate() { return checkOut; }
    public double getTotalAmount() { return totalPrice; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setRoom(Room room) { this.room = room; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(BookingStatus status) { 
        this.status = status; 
        this.updatedAt = LocalDate.now();
    }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }

    // Business logic
    public boolean isActive() {
        return checkOut.isAfter(LocalDate.now());
    }

    public boolean canCancel() {
        return checkIn.isAfter(LocalDate.now()) && 
               !status.equals(BookingStatus.CANCELLED) && 
               !status.equals(BookingStatus.COMPLETED);
    }

    public void cancel() {
        if (canCancel()) {
            status = BookingStatus.CANCELLED;
            updatedAt = LocalDate.now();
        }
    }

    public void complete() {
        if (checkOut.isBefore(LocalDate.now()) && status.equals(BookingStatus.CONFIRMED)) {
            status = BookingStatus.COMPLETED;
            updatedAt = LocalDate.now();
        }
    }

    @Override
    public String toString() {
        return String.format("Booking{id=%s, room=%s, checkIn=%s, checkOut=%s, status=%s}",
                id, room, checkIn, checkOut, status);
    }
} 