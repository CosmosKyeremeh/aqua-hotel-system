package com.hotel;

import java.time.LocalDate;

public class Payment {
    private String id;
    private Booking booking;
    private double paidAmount;
    private String paymentMethod;
    private String status;
    private LocalDate createdAt;
    private String receiptNumber;
    private String transactionId;

    public Payment(Booking booking, double paidAmount, String paymentMethod) {
        this.id = generatePaymentId();
        this.booking = booking;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
        this.status = "COMPLETED";
        this.createdAt = LocalDate.now();
        this.receiptNumber = generateReceiptNumber();
        this.transactionId = generateTransactionId();
    }

    private String generatePaymentId() {
        return "PAY" + System.currentTimeMillis();
    }

    private String generateReceiptNumber() {
        return "RCP" + System.currentTimeMillis();
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    // Getters
    public String getId() { return id; }
    public Booking getBooking() { return booking; }
    public double getPaidAmount() { return paidAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    public LocalDate getCreatedAt() { return createdAt; }
    public String getReceiptNumber() { return receiptNumber; }
    public String getTransactionId() { return transactionId; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public double getRemainingAmount() {
        return booking != null ? booking.getTotalPrice() - paidAmount : 0;
    }

    @Override
    public String toString() {
        return String.format("Payment{id=%s, amount=%.2f, method=%s, status=%s}",
                id, paidAmount, paymentMethod, status);
    }
} 