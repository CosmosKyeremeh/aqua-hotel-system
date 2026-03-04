package com.hotel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Calendar;

public class DateRangeDialog extends JDialog {
    private JSpinner checkInDateSpinner;
    private JSpinner checkOutDateSpinner;
    private boolean confirmed;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public DateRangeDialog(JFrame parent) {
        super(parent, "Select Dates", true);
        
        // Create panel with GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Initialize date spinners
        Calendar calendar = Calendar.getInstance();
        Date initDate = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date tomorrow = calendar.getTime();

        SpinnerDateModel checkInModel = new SpinnerDateModel(initDate, null, null, Calendar.DAY_OF_MONTH);
        checkInDateSpinner = new JSpinner(checkInModel);
        checkInDateSpinner.setEditor(new JSpinner.DateEditor(checkInDateSpinner, "yyyy-MM-dd"));

        SpinnerDateModel checkOutModel = new SpinnerDateModel(tomorrow, null, null, Calendar.DAY_OF_MONTH);
        checkOutDateSpinner = new JSpinner(checkOutModel);
        checkOutDateSpinner.setEditor(new JSpinner.DateEditor(checkOutDateSpinner, "yyyy-MM-dd"));
        
        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Check-in Date:"), gbc);
        
        gbc.gridx = 1;
        add(checkInDateSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Check-out Date:"), gbc);
        
        gbc.gridx = 1;
        add(checkOutDateSpinner, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        
        okButton.addActionListener(e -> {
            Date checkInValue = (Date) checkInDateSpinner.getValue();
            Date checkOutValue = (Date) checkOutDateSpinner.getValue();
            
            if (checkOutValue.before(checkInValue)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Check-out date must be after check-in date",
                    "Invalid Dates",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Convert to LocalDate
            Calendar cal = Calendar.getInstance();
            
            cal.setTime(checkInValue);
            checkInDate = LocalDate.of(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            );
            
            cal.setTime(checkOutValue);
            checkOutDate = LocalDate.of(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            );
            
            confirmed = true;
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public LocalDate getCheckInDate() {
        return checkInDate;
    }
    
    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
} 