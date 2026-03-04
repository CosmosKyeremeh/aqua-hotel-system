package com.hotel.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DatabaseConfig {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "hoteldb";
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static MongoDatabase getDatabase() {
        if (database == null) {
            try {
                // Create MongoDB client
                mongoClient = MongoClients.create(CONNECTION_STRING);
                database = mongoClient.getDatabase(DATABASE_NAME);
                
                // Test connection by creating collections if they don't exist
                if (!collectionExists("users")) {
                    database.createCollection("users");
                }
                if (!collectionExists("bookings")) {
                    database.createCollection("bookings");
                }
                if (!collectionExists("rooms")) {
                    database.createCollection("rooms");
                }
                
                System.out.println("Successfully connected to MongoDB.");
            } catch (Exception e) {
                System.err.println("Failed to connect to MongoDB: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return database;
    }

    private static boolean collectionExists(String collectionName) {
        return database.listCollectionNames()
                      .into(new java.util.ArrayList<>())
                      .contains(collectionName);
    }

    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        }
    }
} 