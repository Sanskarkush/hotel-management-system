package com.hotel.gui;

import com.hotel.Booking;
import com.hotel.HotelManager;

import javax.swing.*;
import java.awt.*;

/**
 * BillDialog - shows the final bill at check-out,
 * with payment method options (Cash / UPI / Card / Net Banking).
 */
public class BillDialog extends JDialog {

    private static final String[] METHODS =
            {"Cash", "UPI", "Credit Card", "Debit Card", "Net Banking"};

    private JComboBox<String> methodBox;
    private JLabel detailLabel;
    private JTextField detailField;
    private JLabel cashNote;

    public BillDialog(HotelGUI parent, HotelManager manager, Booking booking) {
        super(parent, "Final Bill", false);
        setSize(440, 520);
        setLocationRelativeTo(parent);
        buildUI(parent, manager, booking);
    }

    private void buildUI(HotelGUI parent, HotelManager manager,
                         Booking booking) {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel();
        header.setBackground(HotelGUI.NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel title = new JLabel("Final Bill");
        title.setFont(HotelGUI.uiFont(2, 20f));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Bill lines
        JPanel bill = new JPanel();
        bill.setLayout(new BoxLayout(bill, BoxLayout.Y_AXIS));
        bill.setBackground(Color.WHITE);
        bill.setBorder(BorderFactory.createEmptyBorder(18, 30, 8, 30));

        bill.add(line("Booking ID:", "#" + booking.getBookingId()));
        bill.add(line("Customer:", booking.getCustomerName()));
        bill.add(line("Room:", booking.getRoom().getRoomNumber()
                + " (" + booking.getRoom().getType() + ")"));
        bill.add(line("Price/night:",
                "Rs." + booking.getRoom().getPricePerNight()));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        bill.add(Box.createVerticalStrut(8));
        bill.add(sep);
        bill.add(Box.createVerticalStrut(8));

        JLabel total = new JLabel("Total to pay: Rs."
                + booking.getTotalBill());
        total.setFont(HotelGUI.uiFont(2, 18f));
        total.setForeground(HotelGUI.ORANGE);
        total.setAlignmentX(Component.LEFT_ALIGNMENT);
        bill.add(total);
        bill.add(Box.createVerticalStrut(14));

        // ---- Payment section ----
        JLabel payTitle = new JLabel("PAYMENT");
        payTitle.setFont(HotelGUI.uiFont(1, 12f));
        payTitle.setForeground(Color.GRAY);
        payTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        bill.add(payTitle);
        bill.add(Box.createVerticalStrut(6));

        JPanel methodRow = new JPanel(new BorderLayout(8, 0));
        methodRow.setBackground(Color.WHITE);
        methodRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        methodRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        JLabel methodLabel = new JLabel("Method:");
        methodLabel.setFont(HotelGUI.uiFont(0, 14f));
        methodBox = new JComboBox<>(METHODS);
        methodBox.setFont(HotelGUI.uiFont(0, 14f));
        methodRow.add(methodLabel, BorderLayout.WEST);
        methodRow.add(methodBox, BorderLayout.CENTER);
        bill.add(methodRow);
        bill.add(Box.createVerticalStrut(8));

        JPanel detailRow = new JPanel(new BorderLayout(8, 0));
        detailRow.setBackground(Color.WHITE);
        detailRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        detailLabel = new JLabel("UPI ID:");
        detailLabel.setFont(HotelGUI.uiFont(0, 14f));
        detailField = new JTextField();
        detailField.setFont(HotelGUI.uiFont(0, 14f));
        detailRow.add(detailLabel, BorderLayout.WEST);
        detailRow.add(detailField, BorderLayout.CENTER);
        bill.add(detailRow);

        cashNote = new JLabel("Please collect cash at the reception.");
        cashNote.setFont(HotelGUI.uiFont(0, 12f));
        cashNote.setForeground(Color.GRAY);
        cashNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        cashNote.setVisible(false);
        bill.add(cashNote);

        methodBox.addActionListener(e -> updateDetailRow());
        updateDetailRow();

        bill.add(Box.createVerticalStrut(10));
        JLabel thanks = new JLabel("Thank you! Visit again.");
        thanks.setFont(HotelGUI.uiFont(0, 13f));
        thanks.setForeground(Color.GRAY);
        thanks.setAlignmentX(Component.LEFT_ALIGNMENT);
        bill.add(thanks);

        add(bill, BorderLayout.CENTER);

        // Pay & check-out button
        JButton done = new JButton("Pay & Check-Out");
        done.setBackground(HotelGUI.GREEN);
        done.setForeground(Color.WHITE);
        done.setFont(HotelGUI.uiFont(1, 15f));
        done.setFocusPainted(false);
        done.setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));
        done.setCursor(new Cursor(Cursor.HAND_CURSOR));
        done.addActionListener(e -> {
            String method = (String) methodBox.getSelectedItem();
            String detail = detailField.getText().trim();
            if (!"Cash".equals(method) && detail.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter " + detailLabel.getText()
                                .replace(":", "") + ".",
                        "Payment Detail Needed",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            manager.checkOut(booking.getBookingId(), method, detail);
            parent.refreshAll();
            setVisible(false);
            JOptionPane.showMessageDialog(parent,
                    "Payment of Rs." + booking.getTotalBill()
                            + " received via " + method + ".\n"
                            + "Thank you, " + booking.getCustomerName()
                            + "! Visit again.",
                    "Payment Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));
        bottom.add(done);
        add(bottom, BorderLayout.SOUTH);
    }

    /** Show/hide the detail field based on the chosen method. */
    private void updateDetailRow() {
        String method = (String) methodBox.getSelectedItem();
        if ("Cash".equals(method)) {
            detailLabel.setVisible(false);
            detailField.setVisible(false);
            cashNote.setVisible(true);
        } else {
            cashNote.setVisible(false);
            detailLabel.setVisible(true);
            detailField.setVisible(true);
            switch (method) {
                case "UPI":
                    detailLabel.setText("UPI ID:");
                    detailField.setText("");
                    break;
                case "Credit Card":
                case "Debit Card":
                    detailLabel.setText("Card Number:");
                    detailField.setText("");
                    break;
                default:
                    detailLabel.setText("Bank Name:");
                    detailField.setText("");
                    break;
            }
        }
        revalidate();
        repaint();
    }

    /** Test hook: pre-select a payment method. */
    public void setPaymentMethod(String method) {
        methodBox.setSelectedItem(method);
    }

    private JPanel line(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel l = new JLabel(label);
        l.setFont(HotelGUI.uiFont(0, 14f));
        l.setForeground(Color.GRAY);
        JLabel v = new JLabel(value);
        v.setFont(HotelGUI.uiFont(2, 14f));
        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.EAST);
        return p;
    }
}
