package com.hotel.db;

import com.hotel.User;
import com.hotel.UserRole;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final MongoCollection<Document> users;

    public UserDAO() {
        this.users = DatabaseConfig.getDatabase().getCollection("users");
    }

    public void save(User user) {
        try {
            Document doc = new Document()
                .append("email", user.getEmail())
                .append("fullName", user.getFullName())
                .append("phoneNumber", user.getPhoneNumber())
                .append("password", user.getPassword())  // In production, this should be hashed
                .append("role", user.getRole().toString())
                .append("createdAt", LocalDate.now().toString())
                .append("lastLogin", LocalDate.now().toString())
                .append("isActive", true);

            users.insertOne(doc);
            user.setId(doc.getObjectId("_id").toString());
            System.out.println("User saved successfully with ID: " + user.getId());
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public User findByEmail(String email) {
        try {
            Document doc = users.find(Filters.eq("email", email)).first();
            return doc != null ? documentToUser(doc) : null;
        } catch (Exception e) {
            System.err.println("Error finding user by email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public User findById(String id) {
        try {
            Document doc = users.find(Filters.eq("_id", new ObjectId(id))).first();
            return doc != null ? documentToUser(doc) : null;
        } catch (Exception e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<User> findAll() {
        List<User> userList = new ArrayList<>();
        users.find().forEach(doc -> userList.add(documentToUser(doc)));
        return userList;
    }

    public boolean updateUser(User user) {
        Document update = new Document()
            .append("username", user.getUsername())
            .append("email", user.getEmail())
            .append("hashedPassword", user.getHashedPassword())
            .append("salt", user.getSalt())
            .append("fullName", user.getFullName())
            .append("phoneNumber", user.getPhoneNumber())
            .append("role", user.getRole().toString())
            .append("lastLogin", user.getLastLogin().toString())
            .append("isActive", user.isActive())
            .append("loginAttempts", user.getLoginAttempts());

        UpdateResult result = users.updateOne(
            Filters.eq("_id", new ObjectId(user.getId())),
            Updates.combine(Updates.set("$set", update))
        );
        return result.getModifiedCount() > 0;
    }

    public boolean deleteUser(String id) {
        DeleteResult result = users.deleteOne(Filters.eq("_id", new ObjectId(id)));
        return result.getDeletedCount() > 0;
    }

    public boolean updateLoginAttempts(String email, int attempts) {
        UpdateResult result = users.updateOne(
            Filters.eq("email", email),
            Updates.combine(
                Updates.set("loginAttempts", attempts),
                Updates.set("lastFailedLogin", LocalDate.now().toString())
            )
        );
        return result.getModifiedCount() > 0;
    }

    public boolean resetLoginAttempts(String email) {
        UpdateResult result = users.updateOne(
            Filters.eq("email", email),
            Updates.combine(
                Updates.set("loginAttempts", 0),
                Updates.set("lastFailedLogin", null)
            )
        );
        return result.getModifiedCount() > 0;
    }

    private User documentToUser(Document doc) {
        User user = new User(
            doc.getString("email"),
            doc.getString("fullName"),
            doc.getString("phoneNumber"),
            doc.getString("password")
        );
        user.setId(doc.getObjectId("_id").toString());
        user.setRole(UserRole.valueOf(doc.getString("role")));
        return user;
    }

    public void updateLastLogin(String userId) {
        try {
            users.updateOne(
                Filters.eq("_id", new ObjectId(userId)),
                new Document("$set", new Document("lastLogin", LocalDate.now().toString()))
            );
        } catch (Exception e) {
            System.err.println("Error updating last login: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 