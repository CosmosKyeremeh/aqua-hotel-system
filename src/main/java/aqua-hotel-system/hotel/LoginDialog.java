package com.hotel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import com.hotel.db.UserDAO;

public class LoginDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private boolean succeeded;
    private User loggedInUser;
    private static final Color PRIMARY_DARK = new Color(33, 37, 41);
    private static final Color PRIMARY_LIGHT = new Color(248, 249, 250);
    private static final Color ACCENT_COLOR = new Color(205, 170, 125);
    private static final Font TITLE_FONT = new Font("Times New Roman", Font.BOLD, 28);
    private static final Font LABEL_FONT = new Font("Georgia", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Times New Roman", Font.BOLD, 14);
    private SecurityManager securityManager;
    private UserDAO userDAO;

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        this.securityManager = new SecurityManager(new NotificationManager());
        this.userDAO = new UserDAO();
        
        // Create main panel with padding
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.insets = new Insets(5, 5, 5, 5);

        // Email field
        JLabel emailLabel = new JLabel("Email: ");
        emailLabel.setFont(LABEL_FONT);
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        formPanel.add(emailLabel, cs);

        emailField = new JTextField(20);
        emailField.setFont(LABEL_FONT);
        styleTextField(emailField);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        formPanel.add(emailField, cs);

        // Password field
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(LABEL_FONT);
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        formPanel.add(passwordLabel, cs);

        passwordField = new JPasswordField(20);
        passwordField.setFont(LABEL_FONT);
        styleTextField(passwordField);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        formPanel.add(passwordField, cs);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        loginButton = createStyledButton("Login");
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            
            try {
                User user = userDAO.findByEmail(email);
                if (user != null && user.getPassword().equals(password)) {  // In production, use proper password hashing
                    loggedInUser = user;
                    userDAO.updateLastLogin(user.getId());
                    succeeded = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this,
                        "Invalid email or password",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                    emailField.setText("");
                    passwordField.setText("");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(LoginDialog.this,
                    "Error during login: " + ex.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelButton = createStyledButton("Cancel");
        cancelButton.addActionListener(e -> {
            succeeded = false;
            dispose();
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        // Add components to main panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add panel to dialog
        getContentPane().add(panel);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);

        // Handle window closing
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                succeeded = false;
                dispose();
            }
        });

        // Set default button
        getRootPane().setDefaultButton(loginButton);
    }

    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(200, 35));
        textField.setBorder(new CompoundBorder(
            new LineBorder(new Color(206, 212, 218)),
            new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(139, 69, 19)); // Dark brown
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(205, 170, 125)); // Light brown
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(139, 69, 19)); // Dark brown
            }
        });

        return button;
    }

    private boolean authenticate(String email, String password) {
        // Find user in database
        User user = userDAO.findByEmail(email);
        if (user == null) {
            return false;
        }

        // Authenticate user
        User authenticatedUser = securityManager.authenticateUser(email, password);
        if (authenticatedUser != null) {
            // Reset login attempts on successful login
            userDAO.resetLoginAttempts(email);
            authenticatedUser.updateLastLogin();
            userDAO.updateUser(authenticatedUser);
            loggedInUser = authenticatedUser;
            return true;
        } else {
            // Increment login attempts (handled by SecurityManager)
            user.incrementLoginAttempts();
            userDAO.updateLoginAttempts(email, user.getLoginAttempts());
            return false;
        }
    }

    private User createDummyUser() {
        // Create a dummy user with the correct constructor parameters
        return new User(
            emailField.getText(),                    // email
            "John Doe",                             // fullName
            "+1234567890",                          // phoneNumber
            new String(passwordField.getPassword())  // password
        );
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
} 