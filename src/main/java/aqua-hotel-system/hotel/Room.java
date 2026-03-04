package com.hotel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Room {
    private String id;
    private String number;
    private String type;
    private double pricePerNight;
    private int capacity;
    private String description;
    private String imageUrl;
    private ImageIcon image;
    private boolean hasWifi;
    private boolean hasTV;
    private boolean hasMinibar;
    private boolean hasBalcony;
    private int floor;
    private String view;
    private String status;
    private List<String> amenities;
    private boolean isAvailable;

    public Room(String number, String type, double pricePerNight, int capacity, String description, String imageUrl) {
        this.id = "ROOM_" + number;
        this.number = number;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isAvailable = true;
        this.hasWifi = true;
        this.hasTV = true;
        this.hasMinibar = true;
        this.hasBalcony = false;
        this.floor = Integer.parseInt(number.substring(0, 1));
        this.view = "City";
        this.status = "AVAILABLE";
        this.amenities = new ArrayList<>();
        initializeAmenities();
        loadImage();
    }

    private void initializeAmenities() {
        if (hasWifi) amenities.add("WiFi");
        if (hasTV) amenities.add("TV");
        if (hasMinibar) amenities.add("Minibar");
        if (hasBalcony) amenities.add("Balcony");
    }

    private void loadImage() {
        try {
            File imageFile = new File(imageUrl);
            if (imageFile.exists()) {
                BufferedImage img = ImageIO.read(imageFile);
                Image scaledImg = img.getScaledInstance(300, 200, Image.SCALE_SMOOTH);
                this.image = new ImageIcon(scaledImg);
            } else {
                createDefaultImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading image for room " + number + ": " + e.getMessage());
            createDefaultImage();
        }
    }

    private void createDefaultImage() {
        BufferedImage defaultImg = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = defaultImg.createGraphics();
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillRect(0, 0, 300, 200);
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Image Not Available", 75, 100);
        g2d.dispose();
        this.image = new ImageIcon(defaultImg);
    }

    // Getters
    public String getId() { return id; }
    public String getNumber() { return number; }
    public String getType() { return type; }
    public double getPricePerNight() { return pricePerNight; }
    public int getCapacity() { return capacity; }
    public String getDescription() { return description; }
    public ImageIcon getImage() { return image; }
    public boolean hasWifi() { return hasWifi; }
    public boolean hasTV() { return hasTV; }
    public boolean hasMinibar() { return hasMinibar; }
    public boolean hasBalcony() { return hasBalcony; }
    public int getFloor() { return floor; }
    public String getView() { return view; }
    public String getStatus() { return status; }
    public List<String> getAmenities() { return new ArrayList<>(amenities); }
    public String getImageUrl() { return imageUrl; }
    public boolean isAvailable() { return isAvailable; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setNumber(String number) { this.number = number; }
    public void setType(String type) { this.type = type; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setDescription(String description) { this.description = description; }
    public void setWifi(boolean hasWifi) { 
        this.hasWifi = hasWifi;
        updateAmenities();
    }
    public void setTV(boolean hasTV) { 
        this.hasTV = hasTV;
        updateAmenities();
    }
    public void setMinibar(boolean hasMinibar) { 
        this.hasMinibar = hasMinibar;
        updateAmenities();
    }
    public void setBalcony(boolean hasBalcony) { 
        this.hasBalcony = hasBalcony;
        updateAmenities();
    }
    public void setFloor(int floor) { this.floor = floor; }
    public void setView(String view) { this.view = view; }
    public void setStatus(String status) { this.status = status; }
    public void setAmenities(List<String> amenities) { this.amenities = new ArrayList<>(amenities); }
    public void setAvailable(boolean available) { 
        this.isAvailable = available;
        this.status = available ? "AVAILABLE" : "OCCUPIED";
    }

    private void updateAmenities() {
        amenities.clear();
        initializeAmenities();
    }

    @Override
    public String toString() {
        return String.format("Room %s - %s (Floor %d)", number, type, floor);
    }
} 