package com.hotel;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import java.util.Date;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import javax.swing.SpinnerNumberModel;
import com.hotel.db.BookingDAO;
import com.hotel.db.UserDAO;
import com.hotel.db.RoomDAO;
import com.hotel.utils.ReceiptGenerator;

public class App {
    private static ArrayList<Room> rooms;
    private static User currentUser;
    private static JFrame mainFrame;
    private static JPanel mainPanel;
    private static JPanel contentPanel;
    private static JPanel searchPanel;
    private static JPanel resultsPanel;
    private static RoomManager roomManager;
    private static NotificationManager notificationManager;
    private static final Color PRIMARY_DARK = new Color(33, 37, 41);
    private static final Color ACCENT_COLOR = new Color(205, 170, 125); // Warm gold
    private static final Color BUTTON_COLOR = new Color(139, 69, 19); // Saddle brown
    private static final Font TITLE_FONT = new Font("Times New Roman", Font.BOLD, 36);
    private static final Font SUBTITLE_FONT = new Font("Times New Roman", Font.PLAIN, 24);
    private static final Font BODY_FONT = new Font("Georgia", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Times New Roman", Font.BOLD, 14);
    private static final String[] navItems = {"Home", "Rooms", "Facilities", "Services", "Gallery"};
    
    public static void main(String[] args) {
        try {
            // Set the look and feel before creating any GUI components
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Initialize managers with error handling
            try {
                notificationManager = new NotificationManager();
                new SecurityManager(notificationManager);
                roomManager = new RoomManager();
                initializeRooms();
            } catch (Exception e) {
                System.err.println("Error initializing managers: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Create and show GUI on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    createAndShowGUI();
                } catch (Exception e) {
                    System.err.println("Error creating GUI: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                        null,
                        "Error starting application: " + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            
        } catch (Exception e) {
            System.err.println("Fatal error during startup: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Fatal error during startup: " + e.getMessage(),
                "Fatal Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private static void initializeRooms() {
        try {
            // Ensure images directory exists first
            File imagesDir = new File("images");
            if (!imagesDir.exists()) {
                System.out.println("Creating images directory...");
                imagesDir.mkdir();
            }

            // Initialize collections
            rooms = new ArrayList<>();
            roomManager = new RoomManager();
            
            // Create default rooms
            Room[] defaultRooms = {
                new Room("101", "Deluxe Double", 150.00, 2, 
                    "Luxurious room with king bed, balcony, and free WiFi. Perfect for couples.", 
                    "images/deluxe-double.jpg"),
                new Room("201", "Superior Suite", 250.00, 3, 
                    "Spacious suite with king bed, sofa bed, living room, and mini kitchen.", 
                    "images/superior-suite.jpg"),
                new Room("301", "Family Room", 200.00, 4, 
                    "Comfortable room with two queen beds, balcony, and complimentary breakfast.", 
                    "images/family-room.jpg"),
                new Room("401", "Executive Suite", 300.00, 2, 
                    "Premium suite featuring king bed, jacuzzi, and separate living room area.", 
                    "images/executive-suite.jpg"),
                new Room("501", "Standard Twin", 100.00, 2, 
                    "Cozy room with two single beds and essential amenities for a comfortable stay.", 
                    "images/standard-twin.jpg")
            };
            
            // Add rooms to both collections
            for (Room room : defaultRooms) {
                rooms.add(room);
                roomManager.addRoom(room);
                System.out.println("Added room: " + room.getType() + ", ID: " + room.getId());
            }
            
            System.out.println("Total rooms initialized: " + rooms.size());
            
        } catch (Exception e) {
            System.err.println("Error in initializeRooms: " + e.getMessage());
            e.printStackTrace();
            rooms = new ArrayList<>();
        }
    }

    private static void createAndShowGUI() {
        // Set application title
        mainFrame = new JFrame("Aqua Hotel System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 800);
        
        // Set application icon
        try {
            File iconFile = new File("images/hotel_icon.png");
            if (iconFile.exists()) {
                BufferedImage iconImage = ImageIO.read(iconFile);
                mainFrame.setIconImage(iconImage);
            } else {
                System.err.println("Icon file not found: images/hotel_icon.png");
                // Create a default icon if the file doesn't exist
                BufferedImage defaultIcon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = defaultIcon.createGraphics();
                g2d.setColor(ACCENT_COLOR);
                g2d.fillRect(0, 0, 32, 32);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                g2d.drawString("A", 10, 22);
                g2d.dispose();
                mainFrame.setIconImage(defaultIcon);
            }
        } catch (Exception e) {
            System.err.println("Error setting application icon: " + e.getMessage());
        }

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Create main panel with background
        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    BufferedImage bgImage = ImageIO.read(new File("images/1.jpg"));
                    Image scaledImg = bgImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
                    g.drawImage(scaledImg, 0, 0, this);
                    
                    // Add a semi-transparent overlay
                    g.setColor(new Color(0, 0, 0, 100));
                    g.fillRect(0, 0, getWidth(), getHeight());
                } catch (Exception e) {
                    // If background image fails to load, use a gradient instead
                    GradientPaint gradient = new GradientPaint(
                        0, 0, PRIMARY_DARK,
                        0, getHeight(), new Color(PRIMARY_DARK.getRed(), PRIMARY_DARK.getGreen(), PRIMARY_DARK.getBlue(), 150)
                    );
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setOpaque(false);
        
        // Create header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create scrollable content panel
        JPanel mainContentWrapper = new JPanel(new BorderLayout());
        mainContentWrapper.setOpaque(false);
        
        contentPanel = new JPanel(new BorderLayout(30, 30)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // More transparent white background for better visibility
                g.setColor(new Color(255, 255, 255, 150));  // Changed from 200 to 150
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        
        // Create home page content
        showHomePage();
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainContentWrapper.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(mainContentWrapper, BorderLayout.CENTER);
        
        mainFrame.add(mainPanel);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    private static void showHomePage() {
        contentPanel.removeAll();
        
        // Create hero section
        JPanel heroPanel = createHeroPanel();
        
        // Create main content area
        JPanel mainContent = new JPanel(new BorderLayout(0, 30));
        mainContent.setOpaque(false);
        
        // Create amenities panel
        JPanel amenitiesPanel = createAmenitiesPanel();
        mainContent.add(amenitiesPanel, BorderLayout.CENTER);
        
        // Add components to content panel
        contentPanel.add(heroPanel, BorderLayout.NORTH);
        contentPanel.add(mainContent, BorderLayout.CENTER);
        contentPanel.add(createFooterPanel(), BorderLayout.SOUTH);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private static void showRoomsPage() {
        System.out.println("Showing rooms page");
        contentPanel.removeAll();
        
        // Setup the rooms display
        setupRoomsDisplay();
        
        // Get all rooms and display them
        System.out.println("Total rooms in list: " + rooms.size());
        for (Room room : rooms) {
            System.out.println("Room: " + room.getType() + ", ID: " + room.getId());
        }
        
        displayRooms(rooms);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private static void showFacilitiesPage() {
        contentPanel.removeAll();
        
        JPanel facilitiesPanel = new JPanel(new BorderLayout(0, 30));
        facilitiesPanel.setOpaque(false);
        
        
        JLabel titleLabel = new JLabel("Our Facilities");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        // Facilities grid
        JPanel facilitiesGrid = new JPanel(new GridLayout(0, 2, 20, 20));
        facilitiesGrid.setOpaque(false);
        
        String[][] facilities = {
            {"Conference Center", "State-of-the-art conference rooms equipped with modern technology for all your business needs."},
            {"Fitness Center", "24/7 access to our fully equipped gym with personal trainers available."},
            {"Swimming Pool", "Indoor and outdoor pools with temperature control and dedicated kids' area."},
            {"Restaurant", "Fine dining restaurant serving international cuisine with panoramic city views."},
            {"Business Center", "Professional business center with high-speed internet and printing services."},
            {"Parking", "Secure underground parking with valet service available."}
        };
        
        for (String[] facility : facilities) {
            facilitiesGrid.add(createFacilityCard(facility[0], facility[1]));
        }
        
        facilitiesPanel.add(titleLabel, BorderLayout.NORTH);
        facilitiesPanel.add(facilitiesGrid, BorderLayout.CENTER);
        facilitiesPanel.add(createFooterPanel(), BorderLayout.SOUTH);
        
        contentPanel.add(facilitiesPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private static void showServicesPage() {
        contentPanel.removeAll();
        
        JPanel servicesPanel = new JPanel(new BorderLayout(0, 30));
        servicesPanel.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel("Our Services");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        // Services grid
        JPanel servicesGrid = new JPanel(new GridLayout(0, 3, 20, 20));
        servicesGrid.setOpaque(false);
        
        String[][] services = {
            {"Room Service", "24/7 in-room dining service", "Enjoy gourmet meals in the comfort of your room"},
            {"Concierge", "Personal concierge service", "Let us handle all your requests and arrangements"},
            {"Housekeeping", "Daily housekeeping service", "Maintaining the highest standards of cleanliness"},
            {"Laundry", "Same-day laundry service", "Professional cleaning and pressing services"},
            {"Airport Transfer", "Luxury airport transfers", "Comfortable transportation to and from the airport"},
            {"Childcare", "Professional childcare service", "Qualified caregivers for your peace of mind"}
        };
        
        for (String[] service : services) {
            servicesGrid.add(createServiceCard(service[0], service[1], service[2]));
        }
        
        servicesPanel.add(titleLabel, BorderLayout.NORTH);
        servicesPanel.add(servicesGrid, BorderLayout.CENTER);
        servicesPanel.add(createFooterPanel(), BorderLayout.SOUTH);
        
        contentPanel.add(servicesPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private static void showGalleryPage() {
        contentPanel.removeAll();
        
        JPanel galleryPanel = new JPanel(new BorderLayout(0, 30));
        galleryPanel.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel("Photo Gallery");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        // Gallery grid
        JPanel galleryGrid = new JPanel(new GridLayout(0, 3, 10, 10));
        galleryGrid.setOpaque(false);
        
        String[] images = {
            "1.jpg", "2.jpg", "3.jpg",
            "4.jpg", "5.jpg", "6.jpg",
            "7.jpg"
        };
        
        for (String image : images) {
            galleryGrid.add(createGalleryImage(image));
        }
        
        galleryPanel.add(titleLabel, BorderLayout.NORTH);
        galleryPanel.add(galleryGrid, BorderLayout.CENTER);
        galleryPanel.add(createFooterPanel(), BorderLayout.SOUTH);
        
        contentPanel.add(galleryPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private static JPanel createFacilityCard(String title, String description) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(222, 226, 230)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(BODY_FONT);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private static JPanel createServiceCard(String title, String subtitle, String description) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(222, 226, 230)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        subtitleLabel.setForeground(ACCENT_COLOR);
        
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(BODY_FONT);
        
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private static JPanel createGalleryImage(String imagePath) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    BufferedImage img = ImageIO.read(new File("images/" + imagePath));
                    Image scaledImg = img.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
                    g.drawImage(scaledImg, 0, 0, this);
                } catch (Exception e) {
                    g.setColor(PRIMARY_DARK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        panel.setPreferredSize(new Dimension(300, 200));
        panel.setBorder(new LineBorder(Color.WHITE, 2));
        
        // Add hover effect
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBorder(new LineBorder(ACCENT_COLOR, 2));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBorder(new LineBorder(Color.WHITE, 2));
            }
            
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showImageDialog(imagePath);
            }
        });
        
        return panel;
    }
    
    private static void showImageDialog(String imagePath) {
        JDialog dialog = new JDialog(mainFrame, "", true);
        dialog.setUndecorated(true);
        
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    BufferedImage img = ImageIO.read(new File("images/" + imagePath));
                    Image scaledImg = img.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
                    g.drawImage(scaledImg, 0, 0, this);
                } catch (Exception e) {
                    g.setColor(PRIMARY_DARK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dialog.dispose();
            }
        });
        
        dialog.add(panel);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }
    
    private static JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_DARK);
        headerPanel.setPreferredSize(new Dimension(-1, 80));
        headerPanel.setBorder(new EmptyBorder(10, 50, 10, 50));
        
        // Logo panel with icon
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setOpaque(false);
        
        try {
            BufferedImage logoImg = ImageIO.read(new File("images/logo.png"));
            Image scaledLogo = logoImg.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            JLabel logoIcon = new JLabel(new ImageIcon(scaledLogo));
            logoPanel.add(logoIcon);
        } catch (Exception e) {
            // Create a stylized text logo if image is not available
            JLabel textLogo = new JLabel("AS");
            textLogo.setFont(new Font("Times New Roman", Font.BOLD, 32));
            textLogo.setForeground(ACCENT_COLOR);
            textLogo.setBorder(new CompoundBorder(
                new LineBorder(ACCENT_COLOR, 2),
                new EmptyBorder(5, 10, 5, 10)
            ));
            logoPanel.add(textLogo);
        }
        
        JLabel logoLabel = new JLabel("AQUA HOTEL SYSTEM");
        logoLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
        logoLabel.setForeground(ACCENT_COLOR);
        logoPanel.add(logoLabel);
        
        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        navPanel.setOpaque(false);
        for (String item : navItems) {
            JButton navButton = createNavButton(item);
            navPanel.add(navButton);
        }
        
        // Auth buttons
        JPanel authPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        authPanel.setOpaque(false);
        
        JButton loginButton = createStyledButton("LOGIN");
        JButton registerButton = createStyledButton("REGISTER");
        loginButton.setBackground(ACCENT_COLOR);
        registerButton.setBackground(BUTTON_COLOR);
        
        loginButton.addActionListener(e -> {
            LoginDialog loginDialog = new LoginDialog(mainFrame);
            loginDialog.setVisible(true);
            
            if (loginDialog.isSucceeded()) {
                currentUser = loginDialog.getLoggedInUser();
                updateAuthButtons(loginButton, registerButton);
            }
        });
        
        registerButton.addActionListener(e -> {
            RegisterDialog registerDialog = new RegisterDialog(mainFrame);
            registerDialog.setVisible(true);
            
            if (registerDialog.isSucceeded()) {
                currentUser = registerDialog.getRegisteredUser();
                updateAuthButtons(loginButton, registerButton);
            }
        });
        
        authPanel.add(loginButton);
        authPanel.add(registerButton);
        
        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.CENTER);
        headerPanel.add(authPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private static JPanel createHeroPanel() {
        JPanel heroPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    BufferedImage img = ImageIO.read(new File("images/1.jpg"));
                    Image scaledImg = img.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
                    g.drawImage(scaledImg, 0, 0, this);
                    
                    // Add a darker overlay for better text visibility
                    g.setColor(new Color(0, 0, 0, 180));
                    g.fillRect(0, 0, getWidth(), getHeight());
                } catch (Exception e) {
                    // If image fails to load, use a gradient background
                    GradientPaint gradient = new GradientPaint(
                        0, 0, PRIMARY_DARK,
                        0, getHeight(), new Color(PRIMARY_DARK.getRed(), PRIMARY_DARK.getGreen(), PRIMARY_DARK.getBlue(), 200)
                    );
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        heroPanel.setPreferredSize(new Dimension(-1, 300));
        
        // Add overlay text
        JPanel overlayPanel = new JPanel(new GridBagLayout());
        overlayPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome to Aqua Safari");
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 48));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Experience Luxury in Paradise");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(ACCENT_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        overlayPanel.add(welcomeLabel, gbc);
        
        gbc.gridy = 1;
        overlayPanel.add(subtitleLabel, gbc);
        
        heroPanel.add(overlayPanel, BorderLayout.CENTER);
        
        return heroPanel;
    }

    private static JPanel createAmenitiesPanel() {
        // Main container with fixed size
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setOpaque(false);
        containerPanel.setBorder(new EmptyBorder(30, 0, 30, 0));
        
        // Scrolling panel that will contain the amenities
        JPanel scrollingPanel = new JPanel(new GridLayout(1, 8, 20, 0));
        scrollingPanel.setOpaque(false);
        
        // Updated amenities with new image paths
        String[][] amenities = {
            {"Spa & Wellness", "spa&welness.JPEG", "Rejuvenate your body and mind"},
            {"Fine Dining", "finedining.JPEG", "Exquisite culinary experiences"},
            {"Conference Rooms", "conference rooms.JPEG", "Modern meeting facilities"},
            {"Pool & Fitness", "pool&fitness.JPEG", "Stay active and refreshed"},
            // Duplicate cards for continuous scroll
            {"Spa & Wellness", "spa&welness.JPEG", "Rejuvenate your body and mind"},
            {"Fine Dining", "finedining.JPEG", "Exquisite culinary experiences"},
            {"Conference Rooms", "conference rooms.JPEG", "Modern meeting facilities"},
            {"Pool & Fitness", "pool&fitness.JPEG", "Stay active and refreshed"}
        };
        
        for (String[] amenity : amenities) {
            scrollingPanel.add(createAmenityCard(amenity[0], amenity[1], amenity[2]));
        }
        
        // Create a viewport to show only part of the scrolling panel
        JPanel viewportPanel = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(mainFrame.getWidth() - 100, super.getPreferredSize().height);
            }
        };
        viewportPanel.setOpaque(false);
        viewportPanel.add(scrollingPanel, BorderLayout.WEST);
        
        containerPanel.add(viewportPanel, BorderLayout.CENTER);
        
        // Animation timer
        Timer scrollTimer = new Timer(50, new ActionListener() {
            private int position = 0;
            private final int step = 2;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                position -= step;
                
                // Reset position when all items have scrolled
                if (-position >= (amenities.length / 2) * (300 + 20)) { // card width + gap
                    position = 0;
                }
                
                scrollingPanel.setBorder(new EmptyBorder(0, position, 0, 0));
                scrollingPanel.revalidate();
                scrollingPanel.repaint();
            }
        });
        
        scrollTimer.start();
        
        return containerPanel;
    }

    private static JPanel createAmenityCard(String title, String imagePath, String description) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(300, 200));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(222, 226, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        try {
            BufferedImage img = ImageIO.read(new File("images/" + imagePath));
            Image scaledImg = img.getScaledInstance(150, 100, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            card.add(imageLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
            e.printStackTrace();
            // If image not found, use text only
        }
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>", SwingConstants.CENTER);
        descLabel.setFont(BODY_FONT);
        
        card.add(titleLabel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);
        
        return card;
    }

    private static JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(PRIMARY_DARK);
        footer.setBorder(new EmptyBorder(30, 0, 30, 0));
        
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        contentPanel.setOpaque(false);
        
        // Contact info
        JPanel contactPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        contactPanel.setOpaque(false);
        contactPanel.add(createFooterLabel("Contact Us", true));
        contactPanel.add(createFooterLabel("Phone: +233 123 456 789", false));
        contactPanel.add(createFooterLabel("Email: info@urbanohotel.com", false));
        contactPanel.add(createFooterLabel("Address: 123 Independence Ave, Accra", false));
        
        // Quick links
        JPanel linksPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        linksPanel.setOpaque(false);
        linksPanel.add(createFooterLabel("Quick Links", true));
        linksPanel.add(createFooterLabel("About Us", false));
        linksPanel.add(createFooterLabel("Services", false));
        linksPanel.add(createFooterLabel("Gallery", false));
        
        // Social media
        JPanel socialPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        socialPanel.setOpaque(false);
        socialPanel.add(createFooterLabel("Follow Us", true));
        socialPanel.add(createFooterLabel("Facebook", false));
        socialPanel.add(createFooterLabel("Instagram", false));
        socialPanel.add(createFooterLabel("Twitter", false));
        
        contentPanel.add(contactPanel);
        contentPanel.add(linksPanel);
        contentPanel.add(socialPanel);
        
        footer.add(contentPanel, BorderLayout.CENTER);
        
        return footer;
    }

    private static JLabel createFooterLabel(String text, boolean isHeader) {
        JLabel label = new JLabel(text);
        label.setForeground(isHeader ? ACCENT_COLOR : Color.WHITE);
        label.setFont(new Font("Arial", isHeader ? Font.BOLD : Font.PLAIN, isHeader ? 16 : 14));
        return label;
    }

    private static JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add bottom border that appears on hover
        button.setBorder(new EmptyBorder(5, 15, 5, 15));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(ACCENT_COLOR);
                button.setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 2, 0, ACCENT_COLOR),
                    new EmptyBorder(5, 15, 3, 15)
                ));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
                button.setBorder(new EmptyBorder(5, 15, 5, 15));
            }
            
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(ACCENT_COLOR.getRed(), 
                                             ACCENT_COLOR.getGreen(), 
                                             ACCENT_COLOR.getBlue(), 
                                             200));
            }
            
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (button.contains(evt.getPoint())) {
                    button.setForeground(ACCENT_COLOR);
                }
            }
        });
        
        button.addActionListener(e -> {
            switch (text) {
                case "Home":
                    showHomePage();
                    break;
                case "Facilities":
                    showFacilitiesPage();
                    break;
                case "Services":
                    showServicesPage();
                    break;
                case "Gallery":
                    showGalleryPage();
                    break;
                case "Rooms":
                    showRoomsPage();
                    break;
                case "Contact":
                    // TODO: Implement contact page
                    break;
            }
        });
        
        return button;
    }
    
    private static JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Create search components
        JTextField searchField = createStyledTextField("", 150);
        searchField.setToolTipText("Search by room type or description");
        
        String[] types = {"All Types", "Deluxe Double", "Superior Suite", "Family Room", "Executive Suite", "Standard Twin"};
        JComboBox<String> typeFilter = createStyledComboBox(types);
        
        SpinnerNumberModel priceModel = new SpinnerNumberModel(500, 0, 1000, 50);
        JSpinner priceFilter = new JSpinner(priceModel);
        
        SpinnerNumberModel adultsModel = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner adultsSpinner = new JSpinner(adultsModel);
        
        SpinnerNumberModel childrenModel = new SpinnerNumberModel(0, 0, 10, 1);
        JSpinner childrenSpinner = new JSpinner(childrenModel);
        
        JButton searchButton = createStyledButton("SEARCH");
        searchButton.setBackground(ACCENT_COLOR);
        searchButton.setForeground(Color.WHITE);
        
        // Add action listener for search
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase().trim();
            String selectedType = (String) typeFilter.getSelectedItem();
            int maxPrice = (Integer) priceFilter.getValue();
            int adults = (Integer) adultsSpinner.getValue();
            int children = (Integer) childrenSpinner.getValue();
            
            ArrayList<Room> filteredRooms = new ArrayList<>();
            
            for (Room room : rooms) {
                boolean matchesSearch = searchText.isEmpty() || 
                                     room.getType().toLowerCase().contains(searchText) ||
                                     room.getDescription().toLowerCase().contains(searchText);
                
                boolean matchesType = selectedType.equals("All Types") || 
                                    room.getType().equals(selectedType);
                
                boolean matchesPrice = maxPrice == 0 || 
                                     room.getPricePerNight() <= maxPrice;
                
                boolean matchesCapacity = (adults + children) <= room.getCapacity();
                
                if (matchesSearch && matchesType && matchesPrice && matchesCapacity) {
                    filteredRooms.add(room);
                }
            }
            
            displayRooms(filteredRooms);
        });
        
        // Create labeled components
        JPanel searchFieldPanel = createLabeledComponent("Search:", searchField);
        JPanel typePanel = createLabeledComponent("Type:", typeFilter);
        JPanel pricePanel = createLabeledComponent("Max Price ($):", priceFilter);
        JPanel adultsPanel = createLabeledComponent("Adults:", adultsSpinner);
        JPanel childrenPanel = createLabeledComponent("Children:", childrenSpinner);
        
        // Add components to panel
        panel.add(searchFieldPanel);
        panel.add(typePanel);
        panel.add(pricePanel);
        panel.add(adultsPanel);
        panel.add(childrenPanel);
        panel.add(searchButton);
        
        return panel;
    }
    
    private static JPanel createLabeledComponent(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(BODY_FONT);
        
        panel.add(label, BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static class HoverableImagePanel extends JPanel {
        private boolean isHovered = false;
        private ImageIcon image;
        
        public HoverableImagePanel(ImageIcon image) {
            this.image = image;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
                if (isHovered) {
                    g.setColor(new Color(0, 0, 0, 100));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        }
        
        public void setHovered(boolean hover) {
            this.isHovered = hover;
            repaint();
        }
    }

    private static void displayRooms(ArrayList<Room> roomsToDisplay) {
        System.out.println("Starting displayRooms method");
        System.out.println("Number of rooms to display: " + (roomsToDisplay != null ? roomsToDisplay.size() : "null"));
        
        // Clear the existing components
        resultsPanel.removeAll();
        
        if (roomsToDisplay == null || roomsToDisplay.isEmpty()) {
            System.out.println("No rooms to display, showing message");
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setBackground(new Color(0, 0, 0, 0)); // Transparent background
            
            JLabel noResultsLabel = new JLabel("No rooms found matching your criteria");
            noResultsLabel.setFont(new Font("Arial", Font.BOLD, 16));
            noResultsLabel.setForeground(Color.WHITE);
            noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            messagePanel.add(noResultsLabel, BorderLayout.CENTER);
            
            resultsPanel.add(messagePanel);
        } else {
            System.out.println("Setting up grid layout for rooms");
            resultsPanel.setLayout(new GridLayout(0, 3, 10, 10));
            
            for (Room room : roomsToDisplay) {
                System.out.println("Creating panel for room: " + room.getType());
                try {
                    JComponent roomPanel = createRoomPanel(room);
                    resultsPanel.add(roomPanel);
                    System.out.println("Added room panel: " + room.getType());
                } catch (Exception e) {
                    System.err.println("Error creating room panel for " + room.getType() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        System.out.println("Revalidating and repainting results panel");
        resultsPanel.revalidate();
        resultsPanel.repaint();
        mainFrame.repaint();
    }

    private static JComponent createRoomPanel(Room room) {
        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1));

        // Image panel
        JLabel imageLabel = new JLabel(room.getImage());
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setPreferredSize(new Dimension(300, 200));
        imageLabel.setMaximumSize(new Dimension(300, 200));

        // Room type
        JLabel typeLabel = new JLabel(room.getType());
        typeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        typeLabel.setForeground(PRIMARY_DARK);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description
        JLabel descLabel = new JLabel("<html><div style='text-align: center; width: 250px;'>" 
            + room.getDescription() + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(Color.DARK_GRAY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Price
        JLabel priceLabel = new JLabel(String.format("$%.2f per night", room.getPricePerNight()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(ACCENT_COLOR);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Book button
        JButton bookButton = new JButton("Book Now");
        bookButton.setFont(new Font("Arial", Font.BOLD, 14));
        bookButton.setForeground(Color.WHITE);
        bookButton.setBackground(BUTTON_COLOR);
        bookButton.setBorderPainted(false);
        bookButton.setFocusPainted(false);
        bookButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookButton.setMaximumSize(new Dimension(150, 40));
        
        // Add hover effect to button
        bookButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bookButton.setBackground(ACCENT_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bookButton.setBackground(BUTTON_COLOR);
            }
        });

        bookButton.addActionListener(e -> handleBooking(room));

        // Add components with spacing
        panel.add(Box.createVerticalStrut(10));
        panel.add(imageLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(typeLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(priceLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(bookButton);
        panel.add(Box.createVerticalStrut(15));

        return panel;
    }

    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(ACCENT_COLOR);
        button.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        button.setContentAreaFilled(false); // Make button transparent
        
        // Create elegant border
        button.setBorder(new CompoundBorder(
            new LineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(10, 25, 10, 25)
        ));
        
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
                button.setBackground(ACCENT_COLOR);
                button.setContentAreaFilled(true);
                button.setBorder(new CompoundBorder(
                    new LineBorder(ACCENT_COLOR, 2),
                    new EmptyBorder(10, 25, 10, 25)
                ));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(ACCENT_COLOR);
                button.setBackground(new Color(0, 0, 0, 0));
                button.setContentAreaFilled(false);
                button.setBorder(new CompoundBorder(
                    new LineBorder(ACCENT_COLOR, 1),
                    new EmptyBorder(10, 25, 10, 25)
                ));
            }
            
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(ACCENT_COLOR.getRed(), 
                                            ACCENT_COLOR.getGreen(), 
                                            ACCENT_COLOR.getBlue(), 
                                            200));
            }
            
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (button.contains(evt.getPoint())) {
                    button.setBackground(ACCENT_COLOR);
                }
            }
        });
        
        return button;
    }

    private static JTextField createStyledTextField(String placeholder, int width) {
        JTextField textField = new JTextField(placeholder);
        textField.setPreferredSize(new Dimension(width, 35));
        textField.setBorder(new CompoundBorder(
            new LineBorder(new Color(206, 212, 218)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    private static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setPreferredSize(new Dimension(150, 35));
        comboBox.setFont(BODY_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(PRIMARY_DARK);
        
        // Custom renderer for items
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(5, 10, 5, 10));
                setFont(BODY_FONT);
                
                if (isSelected) {
                    setBackground(ACCENT_COLOR);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(PRIMARY_DARK);
                }
                return this;
            }
        });
        
        // Simple border instead of custom UI
        comboBox.setBorder(new CompoundBorder(
            new LineBorder(new Color(206, 212, 218)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        return comboBox;
    }

    private static JSpinner createStyledSpinner(int value, int min, int max, int step) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        spinner.setPreferredSize(new Dimension(100, 35));
        spinner.setFont(BODY_FONT);
        
        // Style the editor
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
            spinnerEditor.getTextField().setFont(BODY_FONT);
            spinnerEditor.getTextField().setForeground(PRIMARY_DARK);
            spinnerEditor.getTextField().setBorder(null);
        }
        
        // Style the buttons
        for (Component comp : spinner.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setBackground(ACCENT_COLOR);
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
            }
        }
        
        spinner.setBorder(new CompoundBorder(
            new LineBorder(new Color(206, 212, 218)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        return spinner;
    }

    private static JSpinner createStyledDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        spinner.setPreferredSize(new Dimension(150, 35));
        spinner.setFont(BODY_FONT);
        
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinner, "MM/dd/yyyy");
        spinner.setEditor(dateEditor);
        
        // Style the editor
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
            spinnerEditor.getTextField().setFont(BODY_FONT);
            spinnerEditor.getTextField().setForeground(PRIMARY_DARK);
            spinnerEditor.getTextField().setBorder(null);
        }
        
        // Style the buttons
        for (Component comp : spinner.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setBackground(ACCENT_COLOR);
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
            }
        }
        
        spinner.setBorder(new CompoundBorder(
            new LineBorder(new Color(206, 212, 218)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        return spinner;
    }

    private static void updateAuthButtons(JButton loginButton, JButton registerButton) {
        if (currentUser == null) {
            // User is logged out - show normal login/register buttons
            loginButton.setText("LOGIN");
            registerButton.setText("REGISTER");
            loginButton.setEnabled(true);
            registerButton.setEnabled(true);
            
            // Reset register button action listener
            for (ActionListener al : registerButton.getActionListeners()) {
                registerButton.removeActionListener(al);
            }
            registerButton.addActionListener(e -> {
                RegisterDialog registerDialog = new RegisterDialog(mainFrame);
                registerDialog.setVisible(true);
                
                if (registerDialog.isSucceeded()) {
                    currentUser = registerDialog.getRegisteredUser();
                    updateAuthButtons(loginButton, registerButton);
                }
            });
        } else {
            // User is logged in - show welcome message and logout button
            loginButton.setText("Welcome, " + currentUser.getFullName());
            loginButton.setEnabled(false);
            registerButton.setText("LOGOUT");
            
            // Remove any existing action listeners from the register/logout button
            for (ActionListener al : registerButton.getActionListeners()) {
                registerButton.removeActionListener(al);
            }
            
            // Add new logout action listener
            registerButton.addActionListener(e -> {
                currentUser = null;
                JOptionPane.showMessageDialog(mainFrame, 
                    "Successfully logged out!", 
                    "Logout Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
                updateAuthButtons(loginButton, registerButton);
            });
        }
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
                    handleBooking(room);
                }
            }
            return;
        }
        
        DateRangeDialog dateDialog = new DateRangeDialog(mainFrame);
        dateDialog.setVisible(true);
        
        if (dateDialog.isConfirmed()) {
            LocalDate checkIn = dateDialog.getCheckInDate();
            LocalDate checkOut = dateDialog.getCheckOutDate();
            
            long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            double totalPrice = room.getPricePerNight() * nights;
            
            int result = JOptionPane.showConfirmDialog(
                mainFrame,
                String.format("Booking Details:\nRoom: %s\nCheck-in: %s\nCheck-out: %s\nNights: %d\nTotal Price: $%.2f\n\n" +
                    "Guest: %s\nContact: %s\nEmail: %s\n\nWould you like to confirm this booking?",
                    room.getType(), checkIn, checkOut, nights, totalPrice,
                    currentUser.getFullName(), currentUser.getPhoneNumber(), currentUser.getEmail()),
                "Confirm Booking",
                JOptionPane.YES_NO_OPTION
            );
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    // Initialize DAOs
                    UserDAO userDAO = new UserDAO();
                    RoomDAO roomDAO = new RoomDAO();
                    BookingDAO bookingDAO = new BookingDAO(userDAO, roomDAO);
                    
                    // Create booking
                    Booking booking = new Booking(
                        currentUser,
                        room,
                        checkIn,
                        checkOut,
                        totalPrice
                    );
                    
                    // Update room availability
                    room.setAvailable(false);
                    roomDAO.updateRoom(room);
                    
                    // Save booking
                    bookingDAO.save(booking);
                    
                    // Update room manager and send notification
                    roomManager.addBooking(booking);
                    notificationManager.sendBookingConfirmation(booking);
                    
                    // Try to generate receipt, but don't let failure block the booking
                    try {
                        String receiptPath = ReceiptGenerator.generateReceipt(booking);
                        if (receiptPath != null) {
                            int downloadChoice = JOptionPane.showConfirmDialog(
                                mainFrame,
                                "Booking confirmed! Would you like to download the receipt?",
                                "Booking Successful",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            
                            if (downloadChoice == JOptionPane.YES_OPTION) {
                                try {
                                    File pdfFile = new File(receiptPath);
                                    if (Desktop.isDesktopSupported()) {
                                        Desktop.getDesktop().open(pdfFile);
                                    } else {
                                        JOptionPane.showMessageDialog(
                                            mainFrame,
                                            "Receipt saved to: " + pdfFile.getAbsolutePath(),
                                            "Receipt Generated",
                                            JOptionPane.INFORMATION_MESSAGE
                                        );
                                    }
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(
                                        mainFrame,
                                        "Could not open the receipt automatically.\nFile saved to: " + receiptPath,
                                        "Receipt Generated",
                                        JOptionPane.INFORMATION_MESSAGE
                                    );
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Log the error but don't show it to the user
                        System.err.println("Error generating receipt: " + e.getMessage());
                        e.printStackTrace();
                        
                        // Show a simpler message to the user
                        JOptionPane.showMessageDialog(
                            mainFrame,
                            "Your booking was successful, but there was an issue generating the receipt.\n" +
                            "A confirmation email will be sent to " + currentUser.getEmail(),
                            "Booking Successful",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }
                    
                    // Show success message
                    JOptionPane.showMessageDialog(
                        mainFrame,
                        "Booking successful! A confirmation email will be sent to " + currentUser.getEmail(),
                        "Booking Successful",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                } catch (Exception e) {
                    System.err.println("Error processing booking: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                        mainFrame,
                        "There was an error processing your booking. Please try again.",
                        "Booking Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }

    private static void setupRoomsDisplay() {
        // Create search and results panels
        searchPanel = createSearchPanel();
        resultsPanel = new JPanel();
        resultsPanel.setOpaque(false);
        
        // Create title section
        JPanel titleSection = new JPanel(new BorderLayout());
        titleSection.setOpaque(false);
        JLabel titleLabel = new JLabel("Our Luxurious Rooms");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        titleSection.add(titleLabel, BorderLayout.CENTER);
        
        // Add components to content panel
        contentPanel.add(titleSection, BorderLayout.NORTH);
        contentPanel.add(searchPanel, BorderLayout.CENTER);
        contentPanel.add(resultsPanel, BorderLayout.SOUTH);
    }

    private static JPanel createFeaturesPanel() {
        JPanel featuresPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        featuresPanel.setOpaque(false);
        featuresPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create feature panels with updated image paths
        featuresPanel.add(createFeaturePanel("Conference Rooms", "images/conference rooms.JPEG"));
        featuresPanel.add(createFeaturePanel("Fine Dining", "images/finedining.JPEG"));
        featuresPanel.add(createFeaturePanel("Pool & Fitness", "images/pool&fitness.JPEG"));
        featuresPanel.add(createFeaturePanel("Spa & Wellness", "images/spa&welness.JPEG"));

        return featuresPanel;
    }

    private static JPanel createFeaturePanel(String title, String imagePath) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        try {
            // Load and scale image
            BufferedImage img = ImageIO.read(new File(imagePath));
            Image scaledImg = img.getScaledInstance(300, 200, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
            imageLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            panel.add(imageLabel, BorderLayout.CENTER);

            // Add title
            JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(new Font("Georgia", Font.BOLD, 16));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
            panel.add(titleLabel, BorderLayout.SOUTH);

        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
            e.printStackTrace();
            
            // Create placeholder if image fails to load
            JPanel placeholder = new JPanel();
            placeholder.setPreferredSize(new Dimension(300, 200));
            placeholder.setBackground(new Color(200, 200, 200));
            JLabel errorLabel = new JLabel("Image Not Found", SwingConstants.CENTER);
            errorLabel.setForeground(Color.WHITE);
            placeholder.add(errorLabel);
            panel.add(placeholder, BorderLayout.CENTER);
        }

        return panel;
    }
} 