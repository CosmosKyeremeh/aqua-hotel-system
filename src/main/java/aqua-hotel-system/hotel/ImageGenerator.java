package com.hotel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageGenerator {
    public static void main(String[] args) {
        String[] roomTypes = {
            "deluxe-double",
            "superior-suite",
            "family-room",
            "executive-suite",
            "standard-twin"
        };

        for (String roomType : roomTypes) {
            generatePlaceholderImage(roomType);
        }
    }

    private static void generatePlaceholderImage(String roomType) {
        int width = 800;
        int height = 600;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Set background
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, width, height);

        // Draw room type text
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        FontMetrics fm = g2d.getFontMetrics();
        String text = roomType.replace("-", " ").toUpperCase();
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, (width - textWidth) / 2, height / 2);

        // Draw border
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(10));
        g2d.drawRect(5, 5, width - 10, height - 10);

        g2d.dispose();

        // Save image
        try {
            File outputDir = new File("images");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File outputFile = new File(outputDir, roomType + ".jpg");
            ImageIO.write(image, "jpg", outputFile);
            System.out.println("Generated: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error generating image for " + roomType + ": " + e.getMessage());
        }
    }
} 