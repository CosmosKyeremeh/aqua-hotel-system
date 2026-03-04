package com.hotel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import com.hotel.db.UserDAO;

public class RegisterDialog extends JDialog {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton cancelButton;
    private boolean succeeded;
    private User registeredUser;
    private UserDAO userDAO;
    private static final Color PRIMARY_DARK = new Color(33, 37, 41);
    private static final Color PRIMARY_LIGHT = new Color(248, 249, 250);
    private static final Color ACCENT_COLOR = new Color(205, 170, 125);
    private static final Font TITLE_FONT = new Font("Times New Roman", Font.BOLD, 28);
    private static final Font LABEL_FONT = new Font("Georgia", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Times New Roman", Font.BOLD, 14);

    public RegisterDialog(Frame parent) {
        super(parent, "Register", true);
        this.userDAO = new UserDAO();
        
        // Create main panel with padding
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.insets = new Insets(5, 5, 5, 5);

        // Name field
        JLabel nameLabel = new JLabel("Full Name: ");
        nameLabel.setFont(LABEL_FONT);
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        formPanel.add(nameLabel, cs);

        nameField = new JTextField(20);
        nameField.setFont(LABEL_FONT);
        styleTextField(nameField);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        formPanel.add(nameField, cs);

        // Email field
        JLabel emailLabel = new JLabel("Email: ");
        emailLabel.setFont(LABEL_FONT);
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        formPanel.add(emailLabel, cs);

        emailField = new JTextField(20);
        emailField.setFont(LABEL_FONT);
        styleTextField(emailField);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        formPanel.add(emailField, cs);

        // Phone field
        JLabel phoneLabel = new JLabel("Phone: ");
        phoneLabel.setFont(LABEL_FONT);
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        formPanel.add(phoneLabel, cs);

        phoneField = new JTextField(20);
        phoneField.setFont(LABEL_FONT);
        styleTextField(phoneField);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        formPanel.add(phoneField, cs);

        // Password field
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(LABEL_FONT);
        cs.gridx = 0;
        cs.gridy = 3;
        cs.gridwidth = 1;
        formPanel.add(passwordLabel, cs);

        passwordField = new JPasswordField(20);
        passwordField.setFont(LABEL_FONT);
        styleTextField(passwordField);
        cs.gridx = 1;
        cs.gridy = 3;
        cs.gridwidth = 2;
        formPanel.add(passwordField, cs);

        // Confirm Password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password: ");
        confirmPasswordLabel.setFont(LABEL_FONT);
        cs.gridx = 0;
        cs.gridy = 4;
        cs.gridwidth = 1;
        formPanel.add(confirmPasswordLabel, cs);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(LABEL_FONT);
        styleTextField(confirmPasswordField);
        cs.gridx = 1;
        cs.gridy = 4;
        cs.gridwidth = 2;
        formPanel.add(confirmPasswordField, cs);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        registerButton = createStyledButton("Register");
        registerButton.addActionListener(e -> {
            if (validateFields()) {
                try {
                    // Check if user already exists
                    User existingUser = userDAO.findByEmail(emailField.getText());
                    if (existingUser != null) {
                        JOptionPane.showMessageDialog(RegisterDialog.this,
                            "A user with this email already exists!",
                            "Registration Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Create and save new user
                    registeredUser = new User(
                        emailField.getText(),
                        nameField.getText(),
                        phoneField.getText(),
                        new String(passwordField.getPassword())
                    );
                    
                    userDAO.save(registeredUser);
                    succeeded = true;
                    dispose();
                    
                    JOptionPane.showMessageDialog(parent,
                        "Registration successful! You can now log in.",
                        "Registration Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RegisterDialog.this,
                        "Error during registration: " + ex.getMessage(),
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        cancelButton = createStyledButton("Cancel");
        cancelButton.addActionListener(e -> {
            succeeded = false;
            dispose();
        });

        buttonPanel.add(registerButton);
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
        getRootPane().setDefaultButton(registerButton);
    }

    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(200, 35));
        textField.setBorder(new CompoundBorder(
            new LineBorder(new Color(206, 212, 218)),
            new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleButton(JButton button, boolean isPrimary) {
        button.setFont(BUTTON_FONT);
        if (isPrimary) {
            button.setBackground(ACCENT_COLOR);
            button.setForeground(Color.WHITE);
            button.setContentAreaFilled(true);
        } else {
            button.setForeground(ACCENT_COLOR);
            button.setBackground(Color.WHITE);
            button.setContentAreaFilled(false);
        }
        button.setBorder(new CompoundBorder(
            new LineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(8, 20, 8, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!isPrimary) {
                    button.setBackground(ACCENT_COLOR);
                    button.setForeground(Color.WHITE);
                    button.setContentAreaFilled(true);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (!isPrimary) {
                    button.setForeground(ACCENT_COLOR);
                    button.setBackground(Color.WHITE);
                    button.setContentAreaFilled(false);
                }
            }
        });
    }

    private boolean validateFields() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validate fields
        if (name.trim().isEmpty() || email.trim().isEmpty() || phone.trim().isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate password match
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public User getRegisteredUser() {
        return registeredUser;
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
} 