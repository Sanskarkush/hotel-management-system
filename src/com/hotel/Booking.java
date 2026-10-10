package com.hotel;

import java.time.LocalDate;

/**
 * Booking - represents one customer booking.
 */
public class Booking {
    private static int counter = 1000;

    private int bookingId;
    private String customerName;
    private String phone;
    private Room room;
    private LocalDate checkInDate;
    private int nights;
    private String paymentMethod = "";
    private String paymentDetail = "";

    public Booking(String customerName, String phone, Room room,
                   LocalDate checkInDate, int nights) {
        this.bookingId = ++counter;
        this.customerName = customerName;
        this.phone = phone;
        this.room = room;
        this.checkInDate = checkInDate;
        this.nights = nights;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return phone;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public int getNights() {
        return nights;
    }

    public Room getRoom() {
        return room;
    }

    public double getTotalBill() {
        return room.getPricePerNight() * nights;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentDetail() {
        return paymentDetail;
    }

    public void setPaymentDetail(String paymentDetail) {
        this.paymentDetail = paymentDetail;
    }

    @Override
    public String toString() {
        return "Booking #" + bookingId
                + " | " + customerName + " (" + phone + ")"
                + " | Room " + room.getRoomNumber() + " (" + room.getType() + ")"
                + " | Check-in: " + checkInDate
                + " | " + nights + " night(s)"
                + " | Total: Rs." + getTotalBill();
    }
}
