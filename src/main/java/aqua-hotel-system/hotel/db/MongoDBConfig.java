package com.hotel.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClientSettings;
import com.mongodb.ConnectionString;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

public class MongoDBConfig {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "hoteldb";
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static void initialize() {
        try {
            // Create codec registry for POJO (Plain Old Java Object) support
            CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
            );

            // Configure MongoDB client settings
            MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                .codecRegistry(pojoCodecRegistry)
                .build();

            // Create MongoDB client
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(DATABASE_NAME);
            
            // Create indexes
            createIndexes();
            
            System.out.println("MongoDB connection established successfully");
        } catch (Exception e) {
            System.err.println("Error connecting to MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createIndexes() {
        // Users collection indexes
        database.getCollection("users").createIndex(
            Indexes.ascending("email"),
            new IndexOptions().unique(true)
        );

        // Rooms collection indexes
        database.getCollection("rooms").createIndex(
            Indexes.ascending("number"),
            new IndexOptions().unique(true)
        );
        database.getCollection("rooms").createIndex(Indexes.ascending("type"));
        database.getCollection("rooms").createIndex(Indexes.ascending("isAvailable"));

        // Bookings collection indexes
        database.getCollection("bookings").createIndex(Indexes.ascending("userId"));
        database.getCollection("bookings").createIndex(Indexes.ascending("roomId"));
        database.getCollection("bookings").createIndex(Indexes.ascending("status"));
        database.getCollection("bookings").createIndex(
            Indexes.compoundIndex(
                Indexes.ascending("checkIn"),
                Indexes.ascending("checkOut")
            )
        );
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            initialize();
        }
        return database;
    }

    public static void close() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                System.out.println("MongoDB connection closed successfully");
            } catch (Exception e) {
                System.err.println("Error closing MongoDB connection: " + e.getMessage());
            }
        }
    }
} 