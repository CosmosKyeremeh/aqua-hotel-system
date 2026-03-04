package com.hotel.db;

import com.hotel.Room;
import com.mongodb.client.*;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private final MongoCollection<Document> roomsCollection;

    public RoomDAO() {
        MongoDatabase database = DatabaseConfig.getDatabase();
        this.roomsCollection = database.getCollection("rooms");
    }

    public void save(Room room) {
        Document roomDoc = new Document()
            .append("_id", room.getId())
            .append("number", room.getNumber())
            .append("type", room.getType())
            .append("pricePerNight", room.getPricePerNight())
            .append("capacity", room.getCapacity())
            .append("description", room.getDescription())
            .append("imagePath", room.getImageUrl())
            .append("hasWifi", room.hasWifi())
            .append("hasTV", room.hasTV())
            .append("hasMinibar", room.hasMinibar())
            .append("hasBalcony", room.hasBalcony())
            .append("floor", room.getFloor())
            .append("view", room.getView())
            .append("status", room.getStatus())
            .append("amenities", room.getAmenities())
            .append("isAvailable", room.isAvailable());

        roomsCollection.insertOne(roomDoc);
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        FindIterable<Document> documents = roomsCollection.find();

        for (Document doc : documents) {
            Room room = documentToRoom(doc);
            rooms.add(room);
        }

        return rooms;
    }

    public Room findById(String id) {
        Document doc = roomsCollection.find(new Document("_id", id)).first();
        return doc != null ? documentToRoom(doc) : null;
    }

    public void updateRoom(Room room) {
        Document update = new Document("$set", new Document()
            .append("type", room.getType())
            .append("pricePerNight", room.getPricePerNight())
            .append("capacity", room.getCapacity())
            .append("description", room.getDescription())
            .append("status", room.getStatus())
            .append("isAvailable", room.isAvailable()));

        roomsCollection.updateOne(new Document("_id", room.getId()), update);
    }

    private Room documentToRoom(Document doc) {
        Room room = new Room(
            doc.getString("number"),
            doc.getString("type"),
            doc.getDouble("pricePerNight"),
            doc.getInteger("capacity"),
            doc.getString("description"),
            doc.getString("imagePath")
        );
        
        room.setId(doc.getString("_id"));
        room.setWifi(doc.getBoolean("hasWifi", true));
        room.setTV(doc.getBoolean("hasTV", true));
        room.setMinibar(doc.getBoolean("hasMinibar", true));
        room.setBalcony(doc.getBoolean("hasBalcony", false));
        room.setFloor(doc.getInteger("floor"));
        room.setView(doc.getString("view"));
        room.setStatus(doc.getString("status"));
        room.setAvailable(doc.getBoolean("isAvailable", true));
        
        @SuppressWarnings("unchecked")
        List<String> amenities = (List<String>) doc.get("amenities");
        if (amenities != null) {
            room.setAmenities(amenities);
        }
        
        return room;
    }
} 