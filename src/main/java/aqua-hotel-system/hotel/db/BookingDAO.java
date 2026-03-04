package com.hotel.db;

import com.hotel.Booking;
import com.hotel.BookingStatus;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private final MongoCollection<Document> bookings;
    private final UserDAO userDAO;
    private final RoomDAO roomDAO;

    public BookingDAO(UserDAO userDAO, RoomDAO roomDAO) {
        this.bookings = DatabaseConfig.getDatabase().getCollection("bookings");
        this.userDAO = userDAO;
        this.roomDAO = roomDAO;
    }

    public void save(Booking booking) {
        try {
            Document doc = new Document()
                .append("userId", booking.getUserId())
                .append("roomId", booking.getRoomId())
                .append("checkIn", booking.getCheckIn().toString())
                .append("checkOut", booking.getCheckOut().toString())
                .append("totalPrice", booking.getTotalPrice())
                .append("status", booking.getStatus().toString())
                .append("createdAt", LocalDate.now().toString())
                .append("updatedAt", LocalDate.now().toString())
                .append("specialRequests", booking.getSpecialRequests())
                .append("numberOfGuests", booking.getNumberOfGuests())
                .append("guestName", booking.getUser().getFullName())
                .append("guestEmail", booking.getUser().getEmail())
                .append("guestPhone", booking.getUser().getPhoneNumber())
                .append("roomType", booking.getRoom().getType())
                .append("roomNumber", booking.getRoom().getNumber());

            bookings.insertOne(doc);
            booking.setId(doc.getObjectId("_id").toString());
            System.out.println("Booking saved successfully with ID: " + booking.getId());
        } catch (Exception e) {
            System.err.println("Error saving booking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update(Booking booking) {
        Document query = new Document("_id", new ObjectId(booking.getId()));
        Document update = new Document("$set", new Document()
            .append("userId", booking.getUserId())
            .append("roomId", booking.getRoomId())
            .append("checkIn", booking.getCheckIn().toString())
            .append("checkOut", booking.getCheckOut().toString())
            .append("totalPrice", booking.getTotalPrice())
            .append("status", booking.getStatus().toString())
            .append("updatedAt", LocalDateTime.now().toString())
            .append("specialRequests", booking.getSpecialRequests())
            .append("numberOfGuests", booking.getNumberOfGuests()));

        bookings.updateOne(query, update);
    }

    public Booking findById(String id) {
        Document doc = bookings.find(new Document("_id", new ObjectId(id))).first();
        return doc != null ? documentToBooking(doc) : null;
    }

    public List<Booking> findByUserId(String userId) {
        List<Booking> userBookings = new ArrayList<>();
        for (Document doc : bookings.find(new Document("userId", userId))) {
            userBookings.add(documentToBooking(doc));
        }
        return userBookings;
    }

    public List<Booking> findByRoomId(String roomId) {
        List<Booking> roomBookings = new ArrayList<>();
        bookings.find(Filters.eq("roomId", roomId))
                .forEach(doc -> roomBookings.add(documentToBooking(doc)));
        return roomBookings;
    }

    public List<Booking> findActiveBookings() {
        List<Booking> activeBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        bookings.find(Filters.and(
                Filters.lt("checkIn", now.toString()),
                Filters.gt("checkOut", now.toString()),
                Filters.eq("status", "CONFIRMED")))
                .forEach(doc -> activeBookings.add(documentToBooking(doc)));
        return activeBookings;
    }

    private Booking documentToBooking(Document doc) {
        try {
            LocalDate checkIn = LocalDate.parse(doc.getString("checkIn"));
            LocalDate checkOut = LocalDate.parse(doc.getString("checkOut"));
            double totalPrice = doc.getDouble("totalPrice");
            int numberOfGuests = doc.getInteger("numberOfGuests", 1);

            Booking booking = new Booking(
                doc.getString("userId"),
                doc.getString("roomId"),
                checkIn,
                checkOut,
                totalPrice,
                numberOfGuests
            );

            booking.setId(doc.getObjectId("_id").toString());

            // Fixed: parse String → BookingStatus enum properly
            String statusStr = doc.getString("status");
            if (statusStr != null) {
                try {
                    booking.setStatus(BookingStatus.valueOf(statusStr));
                } catch (IllegalArgumentException e) {
                    booking.setStatus(BookingStatus.PENDING); // fallback
                }
            }

            booking.setSpecialRequests(doc.getString("specialRequests"));

            if (userDAO != null) {
                booking.setUser(userDAO.findById(doc.getString("userId")));
            }
            if (roomDAO != null) {
                booking.setRoom(roomDAO.findById(doc.getString("roomId")));
            }

            return booking;
        } catch (Exception e) {
            System.err.println("Error converting document to booking: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void delete(String id) {
        bookings.deleteOne(new Document("_id", new ObjectId(id)));
    }

    public List<Booking> findAll() {
        List<Booking> allBookings = new ArrayList<>();
        try {
            for (Document doc : bookings.find()) {
                allBookings.add(documentToBooking(doc));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving bookings: " + e.getMessage());
            e.printStackTrace();
        }
        return allBookings;
    }
}