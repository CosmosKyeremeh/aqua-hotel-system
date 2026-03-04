package com.hotel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class User {
    private String id;
    private String username;
    private String email;
    private String hashedPassword;
    private String salt;
    private String fullName;
    private String phoneNumber;
    private UserRole role;
    private LocalDate createdAt;
    private LocalDate lastLogin;
    private List<Booking> bookingHistory;
    private String address;
    private String idNumber;
    private boolean isActive;
    private int loginAttempts;
    private LocalDateTime lastFailedLogin; // Changed from LocalDate to LocalDateTime

    // Constructor for LoginDialog / RegisterDialog
    public User(String email, String fullName, String phoneNumber, String password) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.hashedPassword = password;
        this.role = UserRole.GUEST;
        this.createdAt = LocalDate.now();
        this.lastLogin = LocalDate.now();
        this.bookingHistory = new ArrayList<>();
        this.isActive = true;
        this.loginAttempts = 0;
    }

    // Simple constructor
    public User(String fullName, String email, String phoneNumber) {
        this.id = UUID.randomUUID().toString();
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = UserRole.GUEST;
        this.createdAt = LocalDate.now();
        this.lastLogin = LocalDate.now();
        this.bookingHistory = new ArrayList<>();
        this.isActive = true;
        this.loginAttempts = 0;
    }

    // Full constructor
    public User(String username, String password, String fullName, String email, String phoneNumber, UserRole role) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.hashedPassword = password;
        this.role = role;
        this.createdAt = LocalDate.now();
        this.lastLogin = LocalDate.now();
        this.bookingHistory = new ArrayList<>();
        this.isActive = true;
        this.loginAttempts = 0;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getHashedPassword() { return hashedPassword; }
    public String getSalt() { return salt; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public UserRole getRole() { return role; }
    public LocalDate getCreatedAt() { return createdAt; }
    public LocalDate getLastLogin() { return lastLogin; }
    public List<Booking> getBookingHistory() { return new ArrayList<>(bookingHistory); }
    public String getAddress() { return address; }
    public String getIdNumber() { return idNumber; }
    public boolean isActive() { return isActive; }
    public int getLoginAttempts() { return loginAttempts; }
    public LocalDateTime getLastFailedLogin() { return lastFailedLogin; } // Returns LocalDateTime

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }
    public void setSalt(String salt) { this.salt = salt; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setRole(UserRole role) { this.role = role; }
    public void setLastLogin(LocalDate lastLogin) { this.lastLogin = lastLogin; }
    public void setLastFailedLogin(LocalDateTime lastFailedLogin) { this.lastFailedLogin = lastFailedLogin; } // Takes LocalDateTime
    public void setAddress(String address) { this.address = address; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public void setActive(boolean active) { isActive = active; }

    // Booking Management
    public void addBooking(Booking booking) {
        bookingHistory.add(booking);
    }

    public List<Booking> getCurrentBookings() {
        LocalDate now = LocalDate.now();
        return bookingHistory.stream()
                .filter(booking -> booking.getCheckOut().isAfter(now))
                .collect(Collectors.toList());
    }

    // Login Attempt Management
    public void incrementLoginAttempts() {
        this.loginAttempts++;
        this.lastFailedLogin = LocalDateTime.now();
    }

    public void resetLoginAttempts() {
        this.loginAttempts = 0;
        this.lastFailedLogin = null;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s", fullName, username, role);
    }

    public boolean canManageUsers() { return role == UserRole.ADMIN; }
    public boolean canManageRooms() { return role == UserRole.ADMIN || role == UserRole.STAFF; }
    public boolean canManageBookings() { return role == UserRole.ADMIN || role == UserRole.STAFF; }
    public boolean canViewRooms() { return isActive; }

    public void updateLastLogin() {
        this.lastLogin = LocalDate.now();
    }

    public String getPassword() {
        return hashedPassword;
    }
}