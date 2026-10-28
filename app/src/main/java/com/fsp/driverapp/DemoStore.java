
package com.fsp.driverapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DemoStore {

    // The currently active demo driver.
    public static String driverName = "";
    public static String driverEmail = "";

    // Demo parking spots with hourly prices in euros.
    public static final List<ParkingSpot> spots = List.of(
            new ParkingSpot("A1", "City Center", 2.50),
            new ParkingSpot("A2", "City Center", 2.50),
            new ParkingSpot("B1", "Shopping Mall", 1.80),
            new ParkingSpot("B2", "Shopping Mall", 1.80),
            new ParkingSpot("C1", "University", 1.20),
            new ParkingSpot("C2", "University", 1.20)
    );

    // These lists are stored only in application memory.
    public static final List<Reservation> reservations =
            new ArrayList<>();

    public static final List<Notification> notifications =
            new ArrayList<>();

    private static int nextReservationId = 1;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    // Prevent creating instances of this utility class.
    private DemoStore() {
    }

    // A parking spot belongs to a parking location.
    public static class ParkingSpot {

        public final String id;
        public final String location;
        public final double pricePerHour;

        public ParkingSpot(
                String id,
                String location,
                double pricePerHour
        ) {
            this.id = id;
            this.location = location;
            this.pricePerHour = pricePerHour;
        }

        @Override
        public String toString() {
            return location + " - " + id + " ("
                    + money(pricePerHour) + "/hour)";
        }
    }

    // A reservation contains the selected spot and time.
    public static class Reservation {

        public final int id;
        public final String email;
        public final ParkingSpot spot;
        public final LocalDateTime start;
        public final int hours;

        public boolean cancelled = false;
        public boolean paid = false;

        public Reservation(
                int id,
                String email,
                ParkingSpot spot,
                LocalDateTime start,
                int hours
        ) {
            this.id = id;
            this.email = email;
            this.spot = spot;
            this.start = start;
            this.hours = hours;
        }

        public LocalDateTime getEnd() {
            return start.plusHours(hours);
        }

        public double getTotal() {
            return spot.pricePerHour * hours;
        }

        @Override
        public String toString() {
            String status = cancelled
                    ? "CANCELLED" : paid ? "PAID" : "UNPAID";

            return "#" + id + " | " + spot.location
                    + " - " + spot.id + "\n"
                    + start.format(DATE_FORMAT)
                    + " | " + hours + " hours\n"
                    + money(getTotal()) + " | " + status;
        }
    }

    // Each notification belongs to a specific demo driver.
    public static class Notification {

        public final String email;
        public final String message;

        public Notification(String email, String message) {
            this.email = email;
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    // Format monetary values consistently.
    public static String money(double amount) {
        return String.format(
                Locale.US, "€%.2f", amount
        );
    }

    // Return reservations belonging to the active driver.
    public static List<Reservation> myReservations() {
        List<Reservation> result = new ArrayList<>();

        for (Reservation reservation : reservations) {
            if (reservation.email.equalsIgnoreCase(driverEmail)) {
                result.add(reservation);
            }
        }

        return result;
    }

    // Check whether two reservations overlap.
    public static boolean isAvailable(
            ParkingSpot spot,
            LocalDateTime start,
            int hours
    ) {
        LocalDateTime end = start.plusHours(hours);

        for (Reservation reservation : reservations) {

            if (reservation.cancelled
                    || !reservation.spot.id.equals(spot.id)) {
                continue;
            }

            boolean overlaps =
                    start.isBefore(reservation.getEnd())
                            && end.isAfter(reservation.start);

            if (overlaps) {
                return false;
            }
        }

        return true;
    }

    // Create a reservation only when the selected time is free.
    public static synchronized Reservation reserve(
            ParkingSpot spot,
            LocalDateTime start,
            int hours
    ) {
        if (!isAvailable(spot, start, hours)) {
            return null;
        }

        Reservation reservation = new Reservation(
                nextReservationId++,
                driverEmail,
                spot,
                start,
                hours
        );

        reservations.add(reservation);

        addNotification(
                "Reservation #" + reservation.id
                        + " created for spot " + spot.id + "."
        );

        return reservation;
    }

    // Save a notification for the active driver.
    public static void addNotification(String message) {
        notifications.add(
                new Notification(driverEmail, message)
        );
    }

    // Return only the active driver's notifications.
    public static List<String> myNotifications() {
        List<String> result = new ArrayList<>();

        for (Notification notification : notifications) {
            if (notification.email.equalsIgnoreCase(driverEmail)) {
                result.add(notification.message);
            }
        }

        return result;
    }

    // Clear the current demo session after logout.
    public static void logout() {
        driverName = "";
        driverEmail = "";
    }
}
