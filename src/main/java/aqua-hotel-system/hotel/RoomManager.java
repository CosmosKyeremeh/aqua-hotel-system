package com.hotel;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RoomManager {
    private List<Room> rooms;
    private List<Booking> bookings;
    private Map<Room, List<Booking>> bookingsMap;
    private Map<String, RoomStatus> roomStatuses; // roomId -> status
    private Map<String, List<Booking>> roomBookings;
    private NotificationManager notificationManager;

    public enum RoomStatus {
        AVAILABLE,
        OCCUPIED,
        MAINTENANCE,
        CLEANING,
        OUT_OF_SERVICE
    }

    public RoomManager() {
        this.rooms = new ArrayList<>();
        this.bookings = new ArrayList<>();
        bookingsMap = new HashMap<>();
        roomStatuses = new HashMap<>();
        this.roomBookings = new HashMap<>();
        this.notificationManager = App.getNotificationManager();
    }

    public void addRoom(Room room) {
        rooms.add(room);
        bookingsMap.put(room, new ArrayList<>());
        roomStatuses.put(room.getId(), RoomStatus.AVAILABLE);
    }

    public void addBooking(Booking booking) {
        String roomId = booking.getRoom().getId();
        if (!roomBookings.containsKey(roomId)) {
            roomBookings.put(roomId, new ArrayList<>());
        }
        roomBookings.get(roomId).add(booking);
        booking.setStatus(BookingStatus.CONFIRMED);
        notificationManager.sendBookingConfirmation(booking);
    }

    public ArrayList<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        String roomId = room.getId();
        if (!roomBookings.containsKey(roomId)) {
            return true;
        }

        List<Booking> bookings = roomBookings.get(roomId);
        for (Booking booking : bookings) {
            // Skip cancelled bookings
            if (booking.getStatus() == BookingStatus.CANCELLED) {
                continue;
            }

            // Check if there's any overlap with existing bookings
            if (!(checkOut.isBefore(booking.getCheckIn()) || 
                  checkIn.isAfter(booking.getCheckOut()))) {
                return false;
            }
        }
        return true;
    }

    public void allocateRoom(Room room, Booking booking) {
        List<Booking> roomBookings = bookingsMap.get(room);
        if (roomBookings != null && isRoomAvailable(room, booking.getCheckInDate(), booking.getCheckOutDate())) {
            roomBookings.add(booking);
        } else {
            throw new IllegalStateException("Room is not available for the selected dates");
        }
    }

    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, int capacity) {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getCapacity() >= capacity && isRoomAvailable(room, checkIn, checkOut)) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    public List<Booking> getBookingsForRoom(Room room) {
        return roomBookings.getOrDefault(room.getId(), new ArrayList<>());
    }

    public void checkIn(Booking booking) {
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            booking.setStatus(BookingStatus.CHECKED_IN);
            notificationManager.sendCheckInConfirmation(booking);
        }
    }

    public void checkOut(Booking booking) {
        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            booking.setStatus(BookingStatus.CHECKED_OUT);
            notificationManager.sendCheckOutConfirmation(booking);
        }
    }

    public void cancelBooking(Booking booking) {
        if (booking.getStatus() == BookingStatus.CONFIRMED || 
            booking.getStatus() == BookingStatus.PENDING) {
            booking.setStatus(BookingStatus.CANCELLED);
            notificationManager.sendCancellationConfirmation(booking);
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> allBookings = new ArrayList<>();
        for (List<Booking> bookings : roomBookings.values()) {
            allBookings.addAll(bookings);
        }
        return allBookings;
    }

    public List<Booking> getActiveBookings() {
        List<Booking> activeBookings = new ArrayList<>();
        for (List<Booking> bookings : roomBookings.values()) {
            for (Booking booking : bookings) {
                if (booking.getStatus() != BookingStatus.CANCELLED && 
                    booking.getStatus() != BookingStatus.CHECKED_OUT) {
                    activeBookings.add(booking);
                }
            }
        }
        return activeBookings;
    }

    public List<Room> searchRooms(String query, String type, double maxPrice) {
        List<Room> results = new ArrayList<>();
        for (Room room : rooms) {
            if (matchesSearch(room, query, type, maxPrice)) {
                results.add(room);
            }
        }
        return results;
    }

    private boolean matchesSearch(Room room, String query, String type, double maxPrice) {
        boolean matchesQuery = query.isEmpty() || 
            room.getType().toLowerCase().contains(query.toLowerCase()) ||
            room.getDescription().toLowerCase().contains(query.toLowerCase());
            
        boolean matchesType = type.equals("All Types") || room.getType().equals(type);
        boolean matchesPrice = room.getPricePerNight() <= maxPrice;
        
        return matchesQuery && matchesType && matchesPrice;
    }

    public List<Room> getRoomsByStatus(RoomStatus status) {
        return rooms.stream()
                .filter(room -> roomStatuses.get(room.getId()) == status)
                .collect(Collectors.toList());
    }

    public List<Room> getRoomsNeedingCleaning() {
        return getRoomsByStatus(RoomStatus.CLEANING);
    }

    public List<Room> getRoomsInMaintenance() {
        return getRoomsByStatus(RoomStatus.MAINTENANCE);
    }

    public void markRoomForCleaning(String roomId) {
        updateRoomStatus(roomId, RoomStatus.CLEANING);
    }

    public void markRoomAsCleaned(String roomId) {
        updateRoomStatus(roomId, RoomStatus.AVAILABLE);
    }

    public void markRoomForMaintenance(String roomId) {
        updateRoomStatus(roomId, RoomStatus.MAINTENANCE);
    }

    public void markRoomAsServiced(String roomId) {
        updateRoomStatus(roomId, RoomStatus.AVAILABLE);
    }

    public List<Booking> getRoomBookingHistory(String roomId) {
        return new ArrayList<>(bookingsMap.get(rooms.stream().filter(room -> room.getId().equals(roomId)).findFirst().orElse(null)));
    }

    public RoomStatus getRoomStatus(String roomId) {
        return roomStatuses.get(roomId);
    }

    public Optional<Room> getRoomById(String roomId) {
        return rooms.stream()
                .filter(room -> room.getId().equals(roomId))
                .findFirst();
    }

    public void updateRoomStatus(String roomId, RoomStatus status) {
        roomStatuses.put(roomId, status);
    }
} 