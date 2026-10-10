package com.hotel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * HotelManager - handles all hotel operations:
 * rooms, bookings, check-in, check-out and billing.
 */
public class HotelManager {
    private List<Room> rooms;
    private List<Booking> bookings;
    private double totalRevenue = 0;

    public HotelManager() {
        rooms = new ArrayList<>();
        bookings = new ArrayList<>();
        addSampleRooms();
    }

    // Some rooms ready to use, so the program starts with data.
    private void addSampleRooms() {
        rooms.add(new Room(101, "Single", 1200));
        rooms.add(new Room(102, "Single", 1200));
        rooms.add(new Room(201, "Double", 2200));
        rooms.add(new Room(202, "Double", 2200));
        rooms.add(new Room(203, "Deluxe", 3200));
        rooms.add(new Room(204, "Deluxe", 3200));
        rooms.add(new Room(301, "Suite", 4500));
        rooms.add(new Room(302, "Suite", 4500));
        rooms.add(new Room(401, "Presidential", 8000));
    }

    public void viewAllRooms() {
        System.out.println("\n--- All Rooms ---");
        for (Room r : rooms) {
            System.out.println(r);
        }
    }

    public void viewAvailableRooms() {
        System.out.println("\n--- Available Rooms ---");
        boolean found = false;
        for (Room r : rooms) {
            if (!r.isOccupied()) {
                System.out.println(r);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No rooms available right now.");
        }
    }

    public void addRoom(int roomNumber, String type, double price) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) {
                System.out.println("Room " + roomNumber + " already exists.");
                return;
            }
        }
        rooms.add(new Room(roomNumber, type, price));
        System.out.println("Room " + roomNumber + " added successfully.");
    }

    private Room findRoom(int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) {
                return r;
            }
        }
        return null;
    }

    public void bookRoom(String name, String phone, int roomNumber,
                         LocalDate checkIn, int nights) {
        Room room = findRoom(roomNumber);
        if (room == null) {
            System.out.println("Room " + roomNumber + " does not exist.");
            return;
        }
        if (room.isOccupied()) {
            System.out.println("Room " + roomNumber + " is already occupied.");
            return;
        }
        if (nights <= 0) {
            System.out.println("Number of nights must be at least 1.");
            return;
        }
        Booking booking = new Booking(name, phone, room, checkIn, nights);
        bookings.add(booking);
        room.setOccupied(true);
        System.out.println("\nBooking successful!");
        System.out.println(booking);
    }

    public void viewAllBookings() {
        System.out.println("\n--- All Bookings ---");
        if (bookings.isEmpty()) {
            System.out.println("No bookings yet.");
            return;
        }
        for (Booking b : bookings) {
            System.out.println(b);
        }
    }

    public void checkOut(int bookingId) {
        checkOut(bookingId, "Cash", "");
    }

    /** Check-out with payment details. Records revenue. */
    public void checkOut(int bookingId, String paymentMethod,
                         String paymentDetail) {
        Booking target = findBooking(bookingId);
        if (target == null) {
            System.out.println("No booking found with ID " + bookingId);
            return;
        }
        target.setPaymentMethod(paymentMethod);
        target.setPaymentDetail(paymentDetail);
        totalRevenue += target.getTotalBill();
        System.out.println("\n--- Final Bill ---");
        System.out.println(target);
        System.out.println("Amount to pay: Rs." + target.getTotalBill());
        System.out.println("Paid via " + paymentMethod);
        System.out.println("Thank you, " + target.getCustomerName()
                + "! Visit again.");
        target.getRoom().setOccupied(false);
        bookings.remove(target);
    }

    /** Find a booking by its ID, or null if not found. */
    public Booking findBooking(int bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId() == bookingId) {
                return b;
            }
        }
        return null;
    }

    /** All rooms (for the GUI table). */
    public List<Room> getRooms() {
        return rooms;
    }

    /** All active bookings. */
    public List<Booking> getBookings() {
        return bookings;
    }

    /** Total revenue collected from check-outs. */
    public double getTotalRevenue() {
        return totalRevenue;
    }
}
