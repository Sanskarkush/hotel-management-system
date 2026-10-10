package com.hotel.gui;

import com.hotel.HotelManager;
import com.hotel.Room;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * BookingDialog - form for booking a room (check-in).
 */
public class BookingDialog extends JDialog {

    private HotelManager manager;
    private HotelGUI parent;
    private JTextField nameField;
    private JTextField phoneField;
    private JComboBox<String> roomBox;
    private JTextField dateField;
    private JSpinner nightsSpinner;

    public BookingDialog(HotelGUI parent, HotelManager manager) {
        super(parent, "Book a Room", false); // non-modal so tests can drive it
        this.parent = parent;
        this.manager = manager;
        setSize(420, 380);
        setLocationRelativeTo(parent);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JLabel heading = new JLabel("  New Booking");
        heading.setFont(HotelGUI.uiFont(2, 18f));
        heading.setForeground(HotelGUI.NAVY);
        heading.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(7, 7, 7, 7);
        gc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(18);
        phoneField = new JTextField(18);
        roomBox = new JComboBox<>();
        dateField = new JTextField(LocalDate.now().toString(), 18);
        nightsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 60, 1));

        addRow(form, gc, 0, "Customer name:", nameField);
        addRow(form, gc, 1, "Phone:", phoneField);
        addRow(form, gc, 2, "Room:", roomBox);
        addRow(form, gc, 3, "Check-in (yyyy-mm-dd):", dateField);
        addRow(form, gc, 4, "Nights:", nightsSpinner);

        add(form, BorderLayout.CENTER);

        JButton confirm = new JButton("Confirm Booking");
        confirm.setBackground(HotelGUI.GREEN);
        confirm.setForeground(Color.WHITE);
        confirm.setFont(HotelGUI.uiFont(2, 14f));
        confirm.setFocusPainted(false);
        confirm.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        confirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirm.addActionListener(e -> doBooking());

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));
        bottom.add(confirm);
        add(bottom, BorderLayout.SOUTH);
    }

    private void addRow(JPanel form, GridBagConstraints gc,
                        int row, String label, JComponent field) {
        gc.gridx = 0;
        gc.gridy = row;
        gc.weightx = 0.35;
        JLabel l = new JLabel(label);
        l.setFont(HotelGUI.uiFont(0, 13f));
        form.add(l, gc);
        gc.gridx = 1;
        gc.weightx = 0.65;
        form.add(field, gc);
    }

    /** Reload the room dropdown with currently available rooms. */
    public void prepare() {
        roomBox.removeAllItems();
        for (Room r : manager.getRooms()) {
            if (!r.isOccupied()) {
                roomBox.addItem(r.getRoomNumber() + " - " + r.getType()
                        + " (Rs." + String.format("%.0f", r.getPricePerNight()) + ")");
            }
        }
        if (roomBox.getItemCount() == 0) {
            roomBox.addItem("No rooms available");
        }
    }

    private void doBooking() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        if (name.isEmpty() || phone.isEmpty()) {
            showError("Please enter customer name and phone.");
            return;
        }
        String selected = (String) roomBox.getSelectedItem();
        if (selected == null || selected.startsWith("No rooms")) {
            showError("No available rooms to book.");
            return;
        }
        int roomNumber = Integer.parseInt(selected.split(" ")[0]);
        LocalDate date;
        try {
            date = LocalDate.parse(dateField.getText().trim());
        } catch (DateTimeParseException ex) {
            showError("Date must be in yyyy-mm-dd format.");
            return;
        }
        int nights = (Integer) nightsSpinner.getValue();

        int before = manager.getBookings().size();
        manager.bookRoom(name, phone, roomNumber, date, nights);
        if (manager.getBookings().size() > before) {
            parent.refreshTable();
            JOptionPane.showMessageDialog(this,
                    "Room " + roomNumber + " booked for " + name + "!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            setVisible(false);
        } else {
            showError("Booking failed. The room may have just been taken.");
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg,
                "Cannot Book", JOptionPane.ERROR_MESSAGE);
    }
}
