package com.hotel.gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.hotel.Booking;
import com.hotel.HotelManager;
import com.hotel.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * HotelGUI - main window of the Hotel Management System.
 * Modern web-dashboard style: dark sidebar, stat cards, tables.
 * Uses FlatLaf look-and-feel (lib/flatlaf.jar).
 */
public class HotelGUI extends JFrame {

    // Colour theme
    public static final Color NAVY = new Color(22, 50, 79);
    public static final Color GREEN = new Color(46, 125, 50);
    public static final Color ORANGE = new Color(230, 81, 0);
    public static final Color BLUE = new Color(21, 101, 192);
    public static final Color TEAL = new Color(0, 121, 107);
    public static final Color SIDEBAR = new Color(17, 24, 39);
    public static final Color SIDEBAR_HOVER = new Color(31, 41, 55);
    public static final Color PAGE_BG = new Color(243, 244, 246);
    public static final Color GOLD = new Color(176, 124, 20);

    // Poppins fonts (loaded from fonts/ folder, SansSerif fallback)
    private static Font poppinsRegular;
    private static Font poppinsSemiBold;
    private static Font poppinsBold;

    static {
        try {
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            poppinsRegular = Font.createFont(Font.TRUETYPE_FONT,
                    new File("fonts/Poppins-Regular.ttf"));
            poppinsSemiBold = Font.createFont(Font.TRUETYPE_FONT,
                    new File("fonts/Poppins-SemiBold.ttf"));
            poppinsBold = Font.createFont(Font.TRUETYPE_FONT,
                    new File("fonts/Poppins-Bold.ttf"));
            ge.registerFont(poppinsRegular);
            ge.registerFont(poppinsSemiBold);
            ge.registerFont(poppinsBold);
        } catch (Exception e) {
            poppinsRegular = null;
            poppinsSemiBold = null;
            poppinsBold = null;
        }
    }

    /**
     * App font helper. weight: 0 = regular, 1 = semi-bold, 2 = bold.
     */
    public static Font uiFont(int weight, float size) {
        Font base;
        int style = Font.PLAIN;
        if (weight == 2) {
            base = poppinsBold;
            style = Font.BOLD;
        } else if (weight == 1) {
            base = poppinsSemiBold;
            style = Font.BOLD;
        } else {
            base = poppinsRegular;
        }
        if (base == null) {
            return new Font("SansSerif", style, (int) size);
        }
        return base.deriveFont(style, size);
    }

    private HotelManager manager;
    private DefaultTableModel roomTableModel;
    private DefaultTableModel bookingTableModel;
    private JTabbedPane tabs;

    private JLabel statTotal;
    private JLabel statAvailable;
    private JLabel statOccupied;
    private JLabel statBookings;
    private JLabel statRevenue;

    private BookingDialog bookingDialog;

    public HotelGUI() {
        manager = new HotelManager();
        setTitle("Hotel Management System");
        setSize(1080, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    public HotelManager getManager() {
        return manager;
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainPage(), BorderLayout.CENTER);
        refreshAll();
    }

    // ---------------- Sidebar ----------------

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(SIDEBAR);
        side.setPreferredSize(new Dimension(210, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(24, 0, 16, 0));

        JLabel brand = new JLabel("HOTEL");
        brand.setFont(uiFont(2, 24f));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel brandSub = new JLabel("Management System");
        brandSub.setFont(uiFont(0, 12f));
        brandSub.setForeground(new Color(156, 163, 175));
        brandSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(brand);
        side.add(Box.createVerticalStrut(4));
        side.add(brandSub);
        side.add(Box.createVerticalStrut(28));

        side.add(navButton("Dashboard", e -> showRoomsTab()));
        side.add(Box.createVerticalStrut(6));
        side.add(navButton("Book a Room", e -> openBookingDialog()));
        side.add(Box.createVerticalStrut(6));
        side.add(navButton("Check-Out & Bill", e -> doCheckOut()));
        side.add(Box.createVerticalStrut(6));
        side.add(navButton("Add a Room", e -> doAddRoom()));
        side.add(Box.createVerticalStrut(6));
        side.add(navButton("View Bookings", e -> showBookingsTab()));

        side.add(Box.createVerticalGlue());
        JLabel foot = new JLabel("Diploma Project • 2026");
        foot.setFont(uiFont(0, 11f));
        foot.setForeground(new Color(107, 114, 128));
        foot.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(foot);

        return side;
    }

    private JButton navButton(String text,
                              java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.setFont(uiFont(1, 14f));
        b.setForeground(new Color(229, 231, 235));
        b.setBackground(SIDEBAR);
        b.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.addActionListener(action);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(SIDEBAR);
            }
        });
        return b;
    }

    // ---------------- Main page ----------------

    private JPanel buildMainPage() {
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setBackground(PAGE_BG);
        page.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PAGE_BG);
        JLabel title = new JLabel("Dashboard");
        title.setFont(uiFont(2, 26f));
        title.setForeground(new Color(17, 24, 39));
        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"));
        JLabel date = new JLabel(today);
        date.setFont(uiFont(0, 13f));
        date.setForeground(new Color(107, 114, 128));
        header.add(title, BorderLayout.NORTH);
        header.add(date, BorderLayout.SOUTH);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        page.add(header, BorderLayout.NORTH);

        // Body: stats + tabs
        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(PAGE_BG);
        body.add(buildStatsPanel(), BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setFont(uiFont(1, 14f));
        tabs.addTab("Rooms", buildRoomPanel());
        tabs.addTab("Bookings", buildBookingPanel());
        tabs.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        body.add(tabs, BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildStatsPanel() {
        JPanel stats = new JPanel(new GridLayout(1, 5, 12, 0));
        stats.setBackground(PAGE_BG);
        stats.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        statTotal = new JLabel();
        statAvailable = new JLabel();
        statOccupied = new JLabel();
        statBookings = new JLabel();
        statRevenue = new JLabel();

        stats.add(statCard("Total Rooms", statTotal, BLUE));
        stats.add(statCard("Available", statAvailable, GREEN));
        stats.add(statCard("Occupied", statOccupied, ORANGE));
        stats.add(statCard("Active Bookings", statBookings, TEAL));
        stats.add(statCard("Revenue", statRevenue, GOLD));
        return stats;
    }

    /** White card with a coloured accent bar on the left. */
    private JPanel statCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 14");
        card.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 16));

        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(6, 0));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBackground(Color.WHITE);
        text.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        valueLabel.setFont(uiFont(2, 30f));
        valueLabel.setForeground(accent);
        JLabel name = new JLabel(label.toUpperCase());
        name.setFont(uiFont(1, 11f));
        name.setForeground(new Color(107, 114, 128));
        text.add(valueLabel);
        text.add(Box.createVerticalStrut(2));
        text.add(name);

        card.add(bar, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JScrollPane buildRoomPanel() {
        String[] columns = {"Room No", "Type", "Price / Night (Rs.)", "Status"};
        roomTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable roomTable = new JTable(roomTableModel);
        styleTable(roomTable);
        roomTable.getColumnModel().getColumn(3)
                .setCellRenderer(new StatusRenderer());
        JScrollPane sp = new JScrollPane(roomTable);
        sp.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        return sp;
    }

    private JScrollPane buildBookingPanel() {
        String[] columns = {"Booking ID", "Customer", "Phone", "Room No",
                "Check-in", "Nights", "Total (Rs.)"};
        bookingTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable bookingTable = new JTable(bookingTableModel);
        styleTable(bookingTable);
        JScrollPane sp = new JScrollPane(bookingTable);
        sp.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        return sp;
    }

    private void styleTable(JTable table) {
        table.setFont(uiFont(0, 14f));
        table.setRowHeight(30);
        table.getTableHeader().setFont(uiFont(1, 13f));
        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centre);
        }
    }

    // ---------------- Actions ----------------

    /** Reload stats, room table and booking table. */
    public void refreshAll() {
        int total = manager.getRooms().size();
        int occupied = 0;
        for (Room r : manager.getRooms()) {
            if (r.isOccupied()) {
                occupied++;
            }
        }
        statTotal.setText(String.valueOf(total));
        statAvailable.setText(String.valueOf(total - occupied));
        statOccupied.setText(String.valueOf(occupied));
        statBookings.setText(String.valueOf(manager.getBookings().size()));
        statRevenue.setText("Rs." + String.format("%.0f",
                manager.getTotalRevenue()));
        // Shrink the revenue number if it gets too long for the card
        statRevenue.setFont(statRevenue.getText().length() > 5
                ? uiFont(2, 20f) : uiFont(2, 30f));

        roomTableModel.setRowCount(0);
        for (Room r : manager.getRooms()) {
            roomTableModel.addRow(new Object[]{
                    r.getRoomNumber(),
                    r.getType(),
                    String.format("%.0f", r.getPricePerNight()),
                    r.isOccupied() ? "OCCUPIED" : "AVAILABLE"
            });
        }

        bookingTableModel.setRowCount(0);
        for (Booking b : manager.getBookings()) {
            bookingTableModel.addRow(new Object[]{
                    "#" + b.getBookingId(),
                    b.getCustomerName(),
                    b.getCustomerPhone(),
                    b.getRoom().getRoomNumber(),
                    b.getCheckInDate(),
                    b.getNights(),
                    String.format("%.0f", b.getTotalBill())
            });
        }
    }

    public void refreshTable() {
        refreshAll();
    }

    public void showRoomsTab() {
        tabs.setSelectedIndex(0);
    }

    public void showBookingsTab() {
        tabs.setSelectedIndex(1);
    }

    public void openBookingDialog() {
        if (bookingDialog == null) {
            bookingDialog = new BookingDialog(this, manager);
        }
        bookingDialog.prepare();
        bookingDialog.setVisible(true);
    }

    public void closeBookingDialog() {
        if (bookingDialog != null) {
            bookingDialog.setVisible(false);
        }
    }

    private void doCheckOut() {
        String input = JOptionPane.showInputDialog(this,
                "Enter Booking ID:", "Check-Out", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        try {
            int id = Integer.parseInt(input.trim());
            Booking b = manager.findBooking(id);
            if (b == null) {
                JOptionPane.showMessageDialog(this,
                        "No booking found with ID " + id,
                        "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showBillDialog(b);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid booking ID number.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    public BillDialog showBillDialog(Booking b) {
        BillDialog dialog = new BillDialog(this, manager, b);
        dialog.setVisible(true);
        return dialog;
    }

    private void doAddRoom() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        JTextField numField = new JTextField();
        String[] types = {"Single", "Double", "Suite"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        JTextField priceField = new JTextField();
        panel.add(new JLabel("Room number:"));
        panel.add(numField);
        panel.add(new JLabel("Type:"));
        panel.add(typeBox);
        panel.add(new JLabel("Price per night (Rs.):"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add New Room", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int num = Integer.parseInt(numField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                manager.addRoom(num, (String) typeBox.getSelectedItem(), price);
                refreshAll();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Room number and price must be numbers.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Colours the Status column cells. */
    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(getFont().deriveFont(Font.BOLD));
            if ("AVAILABLE".equals(value)) {
                c.setBackground(new Color(220, 245, 220));
                c.setForeground(new Color(27, 94, 32));
            } else {
                c.setBackground(new Color(255, 220, 220));
                c.setForeground(new Color(183, 28, 28));
            }
            if (isSelected) {
                c.setBackground(c.getBackground().darker());
            }
            return c;
        }
    }

    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception ignored) {
            // Fall back to the default look if FlatLaf is missing
        }
        SwingUtilities.invokeLater(() -> new HotelGUI().setVisible(true));
    }
}
