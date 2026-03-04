package com.hotel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ReportingManager {
    private final RoomManager roomManager;
    private final List<Booking> bookings;
    private final List<Payment> payments;

    public ReportingManager(RoomManager roomManager) {
        this.roomManager = roomManager;
        this.bookings = new ArrayList<>();
        this.payments = new ArrayList<>();
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
    }

    // Occupancy Reports
    public double calculateOccupancyRate(LocalDate startDate, LocalDate endDate) {
        long totalRoomDays = roomManager.getAllRooms().size() * ChronoUnit.DAYS.between(startDate, endDate);
        long occupiedRoomDays = bookings.stream()
                .filter(booking -> isBookingInPeriod(booking, startDate, endDate))
                .mapToLong(booking -> calculateOverlappingDays(booking, startDate, endDate))
                .sum();
        
        return totalRoomDays > 0 ? (double) occupiedRoomDays / totalRoomDays * 100 : 0;
    }

    private boolean isBookingInPeriod(Booking booking, LocalDate startDate, LocalDate endDate) {
        return !booking.getCheckInDate().isAfter(endDate) && !booking.getCheckOutDate().isBefore(startDate);
    }

    private long calculateOverlappingDays(Booking booking, LocalDate startDate, LocalDate endDate) {
        LocalDate overlapStart = booking.getCheckInDate().isBefore(startDate) ? startDate : booking.getCheckInDate();
        LocalDate overlapEnd = booking.getCheckOutDate().isAfter(endDate) ? endDate : booking.getCheckOutDate();
        return ChronoUnit.DAYS.between(overlapStart, overlapEnd);
    }

    // Revenue Reports
    public Map<String, Double> calculateRevenueByRoomType(LocalDate startDate, LocalDate endDate) {
        return bookings.stream()
                .filter(booking -> isBookingInPeriod(booking, startDate, endDate))
                .collect(Collectors.groupingBy(
                        booking -> booking.getRoom().getType(),
                        Collectors.summingDouble(Booking::getTotalAmount)
                ));
    }

    public double calculateTotalRevenue(LocalDate startDate, LocalDate endDate) {
        return payments.stream()
                .filter(payment -> isPaymentInPeriod(payment, startDate, endDate))
                .mapToDouble(Payment::getPaidAmount)
                .sum();
    }

    private boolean isPaymentInPeriod(Payment payment, LocalDate startDate, LocalDate endDate) {
        LocalDate paymentDate = payment.getCreatedAt();
        return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
    }

    // Room Performance Reports
    public List<RoomPerformance> getRoomPerformanceMetrics(LocalDate startDate, LocalDate endDate) {
        return roomManager.getAllRooms().stream()
                .map(room -> calculateRoomPerformance(room, startDate, endDate))
                .collect(Collectors.toList());
    }

    private RoomPerformance calculateRoomPerformance(Room room, LocalDate startDate, LocalDate endDate) {
        List<Booking> roomBookings = bookings.stream()
                .filter(booking -> booking.getRoom().getId().equals(room.getId()))
                .filter(booking -> isBookingInPeriod(booking, startDate, endDate))
                .collect(Collectors.toList());

        double occupancyRate = calculateRoomOccupancyRate(room, roomBookings, startDate, endDate);
        double revenue = roomBookings.stream().mapToDouble(Booking::getTotalAmount).sum();
        long totalBookings = roomBookings.size();

        return new RoomPerformance(room, occupancyRate, revenue, totalBookings);
    }

    private double calculateRoomOccupancyRate(Room room, List<Booking> roomBookings, 
                                            LocalDate startDate, LocalDate endDate) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        long occupiedDays = roomBookings.stream()
                .mapToLong(booking -> calculateOverlappingDays(booking, startDate, endDate))
                .sum();
        
        return totalDays > 0 ? (double) occupiedDays / totalDays * 100 : 0;
    }

    // Booking Analytics
    public Map<String, Long> getBookingsByRoomType(LocalDate startDate, LocalDate endDate) {
        return bookings.stream()
                .filter(booking -> isBookingInPeriod(booking, startDate, endDate))
                .collect(Collectors.groupingBy(
                        booking -> booking.getRoom().getType(),
                        Collectors.counting()
                ));
    }

    public double getAverageStayDuration(LocalDate startDate, LocalDate endDate) {
        return bookings.stream()
                .filter(booking -> isBookingInPeriod(booking, startDate, endDate))
                .mapToLong(booking -> ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate()))
                .average()
                .orElse(0);
    }

    // Inner class for room performance metrics
    public static class RoomPerformance {
        private final Room room;
        private final double occupancyRate;
        private final double revenue;
        private final long totalBookings;

        public RoomPerformance(Room room, double occupancyRate, double revenue, long totalBookings) {
            this.room = room;
            this.occupancyRate = occupancyRate;
            this.revenue = revenue;
            this.totalBookings = totalBookings;
        }

        public Room getRoom() { return room; }
        public double getOccupancyRate() { return occupancyRate; }
        public double getRevenue() { return revenue; }
        public long getTotalBookings() { return totalBookings; }

        @Override
        public String toString() {
            return String.format("Room %s - Occupancy: %.2f%%, Revenue: $%.2f, Bookings: %d",
                    room.getNumber(), occupancyRate, revenue, totalBookings);
        }
    }
} 