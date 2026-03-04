package com.hotel.utils;

import com.hotel.Booking;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class ReceiptGenerator {
    private static final Font TITLE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
    
    public static String generateReceipt(Booking booking) {
        String fileName = "booking_receipt_" + booking.getId() + ".pdf";
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            
            // Add hotel logo
            // Image logo = Image.getInstance("path/to/hotel/logo.png");
            // logo.scaleToFit(100, 100);
            // document.add(logo);
            
            // Add title
            Paragraph title = new Paragraph("Booking Receipt", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);
            
            // Add receipt number and date
            document.add(new Paragraph("Receipt No: " + booking.getId(), NORMAL_FONT));
            document.add(new Paragraph("Date: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), NORMAL_FONT));
            document.add(new Paragraph("\n"));
            
            // Add guest information
            document.add(new Paragraph("Guest Information", HEADER_FONT));
            document.add(new Paragraph("Name: " + booking.getUser().getFullName(), NORMAL_FONT));
            document.add(new Paragraph("Email: " + booking.getUser().getEmail(), NORMAL_FONT));
            document.add(new Paragraph("Phone: " + booking.getUser().getPhoneNumber(), NORMAL_FONT));
            document.add(new Paragraph("\n"));
            
            // Add booking details
            document.add(new Paragraph("Booking Details", HEADER_FONT));
            document.add(new Paragraph("Room Type: " + booking.getRoom().getType(), NORMAL_FONT));
            document.add(new Paragraph("Room Number: " + booking.getRoom().getNumber(), NORMAL_FONT));
            document.add(new Paragraph("Check-in Date: " + booking.getCheckIn(), NORMAL_FONT));
            document.add(new Paragraph("Check-out Date: " + booking.getCheckOut(), NORMAL_FONT));
            document.add(new Paragraph("Number of Nights: " + 
                booking.getCheckIn().until(booking.getCheckOut()).getDays(), NORMAL_FONT));
            document.add(new Paragraph("\n"));
            
            // Add payment details
            document.add(new Paragraph("Payment Details", HEADER_FONT));
            document.add(new Paragraph("Room Rate per Night: $" + 
                String.format("%.2f", booking.getRoom().getPricePerNight()), NORMAL_FONT));
            document.add(new Paragraph("Total Amount: $" + 
                String.format("%.2f", booking.getTotalPrice()), NORMAL_FONT));
            document.add(new Paragraph("\n"));
            
            // Add terms and conditions
            document.add(new Paragraph("Terms and Conditions", HEADER_FONT));
            document.add(new Paragraph("1. Check-in time is 2:00 PM and check-out time is 12:00 PM", NORMAL_FONT));
            document.add(new Paragraph("2. Early check-in and late check-out are subject to availability", NORMAL_FONT));
            document.add(new Paragraph("3. No refunds for early check-out", NORMAL_FONT));
            document.add(new Paragraph("\n"));
            
            // Add footer
            document.add(new Paragraph("Thank you for choosing our hotel!", NORMAL_FONT));
            document.add(new Paragraph("For any queries, please contact us at: info@hotel.com", NORMAL_FONT));
            
            document.close();
            return fileName;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 