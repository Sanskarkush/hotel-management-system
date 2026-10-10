package com.hotel;

/**
 * Room - represents a single hotel room.
 */
public class Room {
    private int roomNumber;
    private String type;        // Single, Double, Suite
    private double pricePerNight;
    private boolean occupied;

    public Room(int roomNumber, String type, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.occupied = false;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getType() {
        return type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    @Override
    public String toString() {
        String status = occupied ? "Occupied" : "Available";
        return "Room " + roomNumber + " | " + type
                + " | Rs." + pricePerNight + "/night | " + status;
    }
}
