package com.hotel.gui;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import javax.imageio.ImageIO;

/**
 * DemoShots - opens the GUI under a virtual display and captures
 * screenshots of each screen. Test-only, not part of the app.
 *
 * Run:  DISPLAY=:99 java -cp out:lib/flatlaf.jar com.hotel.gui.DemoShots
 * (from the project root, so fonts/ resolves)
 */
public class DemoShots {

    private static Robot robot;
    private static int shotNo = 0;

    public static void main(String[] args) throws Exception {
        try {
            FlatLightLaf.setup();
        } catch (Exception ignored) {
        }
        robot = new Robot();
        robot.setAutoDelay(100);

        final HotelGUI[] holder = new HotelGUI[1];
        SwingUtilities.invokeAndWait(() -> {
            holder[0] = new HotelGUI();
            holder[0].setVisible(true);
        });
        HotelGUI frame = holder[0];
        Thread.sleep(1200);
        shot("1-dashboard");

        // Booking dialog
        SwingUtilities.invokeLater(frame::openBookingDialog);
        Thread.sleep(1000);
        shot("2-booking-dialog");
        SwingUtilities.invokeAndWait(frame::closeBookingDialog);
        Thread.sleep(400);

        // Two bookings: Suite + Presidential
        SwingUtilities.invokeAndWait(() -> {
            frame.getManager().bookRoom("Aarav Singh", "9123456780",
                    301, LocalDate.of(2026, 10, 20), 2);
            frame.getManager().bookRoom("Priya Verma", "9812345678",
                    401, LocalDate.of(2026, 10, 22), 1);
            frame.refreshAll();
        });
        Thread.sleep(800);
        shot("3-after-booking");

        // Bookings tab
        SwingUtilities.invokeAndWait(frame::showBookingsTab);
        Thread.sleep(600);
        shot("4-bookings-tab");

        // Bill dialog with UPI selected
        final BillDialog[] billHolder = new BillDialog[1];
        SwingUtilities.invokeAndWait(() -> {
            billHolder[0] = frame.showBillDialog(
                    frame.getManager().findBooking(1001));
            billHolder[0].setPaymentMethod("UPI");
        });
        Thread.sleep(1000);
        shot("5-bill-payment");
        SwingUtilities.invokeAndWait(() ->
                billHolder[0].setVisible(false));
        Thread.sleep(300);

        // Check out #1001 via UPI -> revenue card updates
        SwingUtilities.invokeAndWait(() -> {
            frame.getManager().checkOut(1001, "UPI", "aarav@okhdfc");
            frame.showRoomsTab();
            frame.refreshAll();
        });
        Thread.sleep(800);
        shot("6-revenue");

        System.out.println("SHOTS DONE");
        System.exit(0);
    }

    private static void shot(String name) throws Exception {
        shotNo++;
        Rectangle screen = new Rectangle(
                Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage img = robot.createScreenCapture(screen);
        File out = new File("/tmp/shots/" + shotNo + "-" + name + ".png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "png", out);
        System.out.println("saved " + out.getPath());
    }
}
