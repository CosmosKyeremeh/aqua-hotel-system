package com.hotel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import com.hotel.db.UserDAO;

public class SecurityManager {
    private final NotificationManager notificationManager;
    private final UserDAO userDAO;
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public SecurityManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
        this.userDAO = new UserDAO();
    }

    public boolean registerUser(String email, String fullName, String phoneNumber, String password) {
        if (userDAO.findByEmail(email) != null) {
            return false;
        }

        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        User user = new User(fullName, email, phoneNumber);
        user.setHashedPassword(hashedPassword);
        user.setSalt(salt);

        userDAO.save(user); // Fixed: was createUser(), now save()

        notificationManager.sendWelcomeEmail(user.getId());
        return true;
    }

    public User authenticateUser(String email, String password) {
        User user = userDAO.findByEmail(email);
        if (user == null) {
            return null;
        }

        if (isUserLockedOut(user)) {
            notificationManager.sendLoginFailureAlert(user.getId());
            return null;
        }

        if (!verifyPassword(password, user.getHashedPassword(), user.getSalt())) {
            handleFailedLogin(user);
            return null;
        }

        user.resetLoginAttempts();
        user.setLastLogin(LocalDate.now()); // Fixed: was LocalDateTime.now(), User.setLastLogin takes LocalDate
        userDAO.updateUser(user);
        return user;
    }

    private boolean isUserLockedOut(User user) {
        if (user.getLastFailedLogin() == null) {
            return false;
        }

        if (user.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
            // Fixed: getLastFailedLogin() now returns LocalDateTime, so plusMinutes() works
            LocalDateTime lockoutEnd = user.getLastFailedLogin().plusMinutes(LOCKOUT_DURATION_MINUTES);
            if (LocalDateTime.now().isBefore(lockoutEnd)) {
                return true;
            }
            user.resetLoginAttempts();
            userDAO.resetLoginAttempts(user.getEmail());
        }
        return false;
    }

    private void handleFailedLogin(User user) {
        user.incrementLoginAttempts();
        user.setLastFailedLogin(LocalDateTime.now()); // Fixed: setLastFailedLogin now takes LocalDateTime
        userDAO.updateLoginAttempts(user.getEmail(), user.getLoginAttempts());
        notificationManager.sendLoginFailureAlert(user.getId());
    }

    private String generateSalt() {
        byte[] salt = new byte[16];
        SECURE_RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private boolean verifyPassword(String password, String hashedPassword, String salt) {
        if (hashedPassword == null || salt == null) return false;
        String computedHash = hashPassword(password, salt);
        return computedHash.equals(hashedPassword);
    }

    public boolean validateLogin(String username, String password) {
        return true;
    }

    public void logLoginAttempt(String username, boolean success) {
        if (!success) {
            notificationManager.sendNotification(
                "Failed login attempt for user: " + username,
                "admin@hotel.com"
            );
        }
    }
}