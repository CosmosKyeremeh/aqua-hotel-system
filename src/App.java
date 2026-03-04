import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.hotel.Room;
import com.hotel.User;
import com.hotel.UserRole;
import com.hotel.Booking;
import com.hotel.Payment;
import com.hotel.NotificationManager;
import com.hotel.RoomManager;
import com.hotel.SecurityManager;
import com.hotel.LoginDialog;

public class App {

    private static ArrayList<Room> rooms;
    private static User currentUser;
    private static JFrame mainFrame;
    private static JPanel mainPanel;
    private static JPanel searchPanel;
    private static JPanel resultsPanel;
    private static RoomManager roomManager;
    private static SecurityManager securityManager;
    private static NotificationManager notificationManager;

    public static void main(String[] args) {
        // Initialize managers
        notificationManager = new NotificationManager();
        securityManager = new SecurityManager(notificationManager);
        roomManager = new RoomManager();
        
        initializeRooms();
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void initializeRooms() {
        rooms = new ArrayList<>();
        // Add sample rooms
        rooms.add(new Room("101", "Deluxe Double", 150.00, 2, "King bed, Balcony, Free WiFi", "images/deluxe-double.jpg"));
        rooms.add(new Room("201", "Superior Suite", 250.00, 3, "1 King bed + 1 Sofa bed, Living Room, Mini Kitchen", "images/superior-suite.jpg"));
        rooms.add(new Room("301", "Family Room", 200.00, 4, "2 Queen beds, Balcony, Free Breakfast", "images/family-room.jpg"));
        rooms.add(new Room("401", "Executive Suite", 300.00, 2, "King bed, Jacuzzi, Living Room", "images/executive-suite.jpg"));
        rooms.add(new Room("501", "Standard Twin", 100.00, 2, "2 Single beds, Basic Amenities", "images/standard-twin.jpg"));
    }

    private static void createAndShowGUI() {
        mainFrame = new JFrame("Hotel Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 800);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Create and add components
        createSearchPanel();
        createResultsPanel();
        
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);
        
        mainFrame.add(mainPanel);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static void createSearchPanel() {
        searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Add search components
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 35));
        
        String[] types = {"All Types", "Deluxe Double", "Superior Suite", "Family Room", "Executive Suite", "Standard Twin"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setPreferredSize(new Dimension(150, 35));
        
        SpinnerNumberModel priceModel = new SpinnerNumberModel(500, 0, 1000, 50);
        JSpinner priceSpinner = new JSpinner(priceModel);
        priceSpinner.setPreferredSize(new Dimension(100, 35));
        
        JButton searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.setBackground(new Color(0, 153, 0));
        searchButton.setForeground(Color.WHITE);
        
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Type: "));
        searchPanel.add(typeCombo);
        searchPanel.add(new JLabel("Max Price: $"));
        searchPanel.add(priceSpinner);
        searchPanel.add(searchButton);
        
        searchButton.addActionListener(e -> updateResults());
    }

    private static void createResultsPanel() {
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Add initial results
        updateResults();
    }

    private static void updateResults() {
        resultsPanel.removeAll();
        
        for (Room room : rooms) {
            JPanel roomPanel = createRoomPanel(room);
            roomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, roomPanel.getPreferredSize().height));
            resultsPanel.add(Box.createVerticalStrut(20));
            resultsPanel.add(roomPanel);
        }
        
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private static JPanel createRoomPanel(Room room) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Room image
        JLabel imageLabel = new JLabel(room.getImage());
        imageLabel.setPreferredSize(new Dimension(200, 150));
        
        // Room details
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(room.getType() + " - Room " + room.getNumber());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel descLabel = new JLabel("<html>" + room.getDescription() + "</html>");
        JLabel priceLabel = new JLabel(String.format("$%.2f per night", room.getPricePerNight()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        detailsPanel.add(titleLabel, BorderLayout.NORTH);
        detailsPanel.add(descLabel, BorderLayout.CENTER);
        detailsPanel.add(priceLabel, BorderLayout.SOUTH);
        
        // Book now button
        JButton bookButton = new JButton("Book Now");
        bookButton.setBackground(new Color(0, 153, 0));
        bookButton.setForeground(Color.WHITE);
        bookButton.setPreferredSize(new Dimension(120, 40));
        bookButton.addActionListener(e -> handleBooking(room));
        
        panel.add(imageLabel, BorderLayout.WEST);
        panel.add(detailsPanel, BorderLayout.CENTER);
        panel.add(bookButton, BorderLayout.EAST);
        
        return panel;
    }

    private static void handleBooking(Room room) {
        if (currentUser == null) {
            int choice = JOptionPane.showConfirmDialog(
                mainFrame,
                "You need to be logged in to book a room. Would you like to login now?",
                "Login Required",
                JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                LoginDialog loginDialog = new LoginDialog(mainFrame);
                loginDialog.setVisible(true);
                
                if (loginDialog.isSucceeded()) {
                    currentUser = loginDialog.getLoggedInUser();
                    // Try booking again after successful login
                    handleBooking(room);
                }
            }
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(
            mainFrame,
            String.format("Would you like to book %s for $%.2f per night?\n\nBooking Details:\nGuest: %s\nContact: %s\nEmail: %s",
                room.getType(),
                room.getPricePerNight(),
                currentUser.getFullName(),
                currentUser.getPhoneNumber(),
                currentUser.getEmail()),
            "Confirm Booking",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // TODO: Save booking to database
            JOptionPane.showMessageDialog(
                mainFrame,
                "Booking confirmed! A confirmation email will be sent to " + currentUser.getEmail(),
                "Booking Successful",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
} 