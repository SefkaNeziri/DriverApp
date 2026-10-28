
package com.fsp.driverapp;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Tracks the currently displayed application screen.
    private String currentScreen = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register modern Android Back navigation.
        setupBackNavigation();

        // Display Login when the application starts.
        showLogin();
    }

    // Handles hardware Back buttons and Android Back gestures.
    private void setupBackNavigation() {

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        switch (currentScreen) {

                            case "register":
                                showLogin();
                                break;

                            case "parking":
                            case "reservations":
                            case "payments":
                            case "notifications":
                                showDashboard();
                                break;

                            case "dashboard":
                                DemoStore.logout();
                                showLogin();
                                break;

                            case "login":
                            default:
                                // Allow Android to close the activity.
                                setEnabled(false);
                                getOnBackPressedDispatcher()
                                        .onBackPressed();
                                break;
                        }
                    }
                }
        );
    }

    // Displays a short message to the user.
    private void showMessage(String text) {
        Toast.makeText(
                this,
                text,
                Toast.LENGTH_LONG
        ).show();
    }

    // Opens the Login screen.
    private void showLogin() {

        currentScreen = "login";
        setContentView(R.layout.activity_main);

        EditText emailField =
                findViewById(R.id.emailField);

        EditText passwordField =
                findViewById(R.id.passwordField);

        Button loginButton =
                findViewById(R.id.loginButton);

        TextView registerLink =
                findViewById(R.id.registerLink);

        // Validate the Login form when the user taps Login.
        loginButton.setOnClickListener(view -> {

            String email =
                    emailField.getText().toString().trim();

            String password =
                    passwordField.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                showMessage(
                        "Please enter your email and password."
                );
                return;
            }

            if (!Patterns.EMAIL_ADDRESS
                    .matcher(email).matches()) {

                emailField.setError(
                        "Please enter a valid email address."
                );
                return;
            }

            // Demo authentication does not verify passwords.
            DemoStore.driverEmail = email;
            DemoStore.driverName = email.split("@")[0];

            showMessage(
                    "Demo login: backend not connected."
            );

            showDashboard();
        });

        // Navigate to the Registration screen.
        registerLink.setOnClickListener(
                view -> showRegister()
        );
    }

    // Opens the Registration screen.
    private void showRegister() {

        currentScreen = "register";
        setContentView(R.layout.activity_register);

        EditText nameField =
                findViewById(R.id.nameField);

        EditText emailField =
                findViewById(R.id.registerEmailField);

        EditText passwordField =
                findViewById(R.id.registerPasswordField);

        EditText confirmField =
                findViewById(R.id.confirmPasswordField);

        Button createAccountButton =
                findViewById(R.id.createAccountButton);

        TextView backToLoginLink =
                findViewById(R.id.backToLoginLink);

        // Validate the form before demo registration.
        createAccountButton.setOnClickListener(view -> {

            String name =
                    nameField.getText().toString().trim();

            String email =
                    emailField.getText().toString().trim();

            String password =
                    passwordField.getText().toString();

            String confirmation =
                    confirmField.getText().toString();

            if (name.isEmpty()
                    || email.isEmpty()
                    || password.isEmpty()
                    || confirmation.isEmpty()) {

                showMessage("Please fill in all fields.");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS
                    .matcher(email).matches()) {

                emailField.setError(
                        "Please enter a valid email address."
                );
                return;
            }

            if (password.length() < 8) {
                passwordField.setError(
                        "Password must contain at least 8 characters."
                );
                return;
            }

            if (!password.equals(confirmation)) {
                confirmField.setError(
                        "Passwords do not match."
                );
                return;
            }

            // Demo registration does not save a real account.
            DemoStore.driverName = name;
            DemoStore.driverEmail = email;

            showMessage(
                    "Registration validated in demo mode."
            );

            // Open Dashboard directly after registration.
            showDashboard();
        });

        backToLoginLink.setOnClickListener(
                view -> showLogin()
        );
    }

    // Displays the main Driver Dashboard.
    private void showDashboard() {

        currentScreen = "dashboard";
        setContentView(R.layout.activity_dashboard);

        TextView welcomeText =
                findViewById(R.id.welcomeText);

        // Use a string resource for dynamic text.
        welcomeText.setText(
                getString(
                        R.string.welcome_driver,
                        DemoStore.driverName
                )
        );

        findViewById(R.id.findParkingButton)
                .setOnClickListener(
                        view -> showParking()
                );

        findViewById(R.id.reservationsButton)
                .setOnClickListener(
                        view -> showReservations()
                );

        findViewById(R.id.paymentsButton)
                .setOnClickListener(
                        view -> showPayments()
                );

        findViewById(R.id.notificationsButton)
                .setOnClickListener(
                        view -> showNotifications()
                );

        findViewById(R.id.logoutButton)
                .setOnClickListener(view -> {

                    // Clear the current demo session.
                    DemoStore.logout();

                    // Return to the Login screen.
                    showLogin();
                });
    }

    // Displays parking spots and reservation controls.
    private void showParking() {

        currentScreen = "parking";
        setContentView(R.layout.activity_parking);

        Spinner spotSpinner =
                findViewById(R.id.spotSpinner);

        Spinner hoursSpinner =
                findViewById(R.id.hoursSpinner);

        DatePicker datePicker =
                findViewById(R.id.reservationDatePicker);

        TimePicker timePicker =
                findViewById(R.id.reservationTimePicker);

        TextView priceText =
                findViewById(R.id.priceText);

        Button reserveButton =
                findViewById(R.id.reserveButton);

        // Fill the parking selector with demo spots.
        ArrayAdapter<DemoStore.ParkingSpot> spotAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        DemoStore.spots
                );

        spotAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spotSpinner.setAdapter(spotAdapter);

        // Available reservation durations.
        Integer[] durations = {1, 2, 3, 4, 5, 6};

        ArrayAdapter<Integer> hoursAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        durations
                );

        hoursAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        hoursSpinner.setAdapter(hoursAdapter);

        // Display the time using a 24-hour clock.
        timePicker.setIs24HourView(true);

        // Prevent selecting a date before today.
        datePicker.setMinDate(
                System.currentTimeMillis() - 1000
        );

        // Refresh the estimated price when selection changes.
        AdapterView.OnItemSelectedListener priceListener =
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        DemoStore.ParkingSpot spot =
                                (DemoStore.ParkingSpot)
                                        spotSpinner.getSelectedItem();

                        Integer duration =
                                (Integer)
                                        hoursSpinner.getSelectedItem();

                        if (spot != null && duration != null) {

                            String amount = DemoStore.money(
                                    spot.pricePerHour * duration
                            );

                            priceText.setText(
                                    getString(
                                            R.string.estimated_price,
                                            amount
                                    )
                            );
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                        // No action is needed.
                    }
                };

        spotSpinner.setOnItemSelectedListener(priceListener);
        hoursSpinner.setOnItemSelectedListener(priceListener);

        // Create a new demo reservation.
        reserveButton.setOnClickListener(view -> {

            DemoStore.ParkingSpot spot =
                    (DemoStore.ParkingSpot)
                            spotSpinner.getSelectedItem();

            Integer duration =
                    (Integer) hoursSpinner.getSelectedItem();

            if (spot == null || duration == null) {
                showMessage(
                        "Please select a parking spot and duration."
                );
                return;
            }

            // Read the selected reservation date and time.
            LocalDateTime start = LocalDateTime.of(
                    datePicker.getYear(),
                    datePicker.getMonth() + 1,
                    datePicker.getDayOfMonth(),
                    timePicker.getHour(),
                    timePicker.getMinute()
            );

            // Prevent reservations in the past.
            if (!start.isAfter(LocalDateTime.now())) {
                showMessage(
                        "Please choose a future date and time."
                );
                return;
            }

            // Attempt to create a demo reservation.
            DemoStore.Reservation reservation =
                    DemoStore.reserve(
                            spot,
                            start,
                            duration
                    );

            if (reservation == null) {
                showMessage(
                        "This parking spot is unavailable for that time."
                );
                return;
            }

            showMessage(
                    "Demo reservation #"
                            + reservation.id
                            + " created successfully."
            );

            showReservations();
        });

        findViewById(R.id.backFromParkingButton)
                .setOnClickListener(
                        view -> showDashboard()
                );
    }

    // Displays and manages the driver's reservations.
    private void showReservations() {

        currentScreen = "reservations";
        setContentView(R.layout.activity_reservations);

        // Android ListView does not support generic type parameters.
        ListView reservationList =
                findViewById(R.id.reservationsList);

        List<DemoStore.Reservation> reservations =
                DemoStore.myReservations();

        ArrayAdapter<DemoStore.Reservation> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_single_choice,
                        reservations
                );

        reservationList.setAdapter(adapter);

        reservationList.setChoiceMode(
                ListView.CHOICE_MODE_SINGLE
        );

        // Cancel the selected reservation after confirmation.
        findViewById(R.id.cancelReservationButton)
                .setOnClickListener(view -> {

                    int position =
                            reservationList.getCheckedItemPosition();

                    if (position == ListView.INVALID_POSITION
                            || position >= reservations.size()) {

                        showMessage(
                                "Please select a reservation."
                        );
                        return;
                    }

                    DemoStore.Reservation selected =
                            reservations.get(position);

                    if (selected.cancelled) {
                        showMessage(
                                "This reservation is already cancelled."
                        );
                        return;
                    }

                    if (!selected.start.isAfter(
                            LocalDateTime.now())) {

                        showMessage(
                                "Started or past reservations cannot be cancelled."
                        );
                        return;
                    }

                    new AlertDialog.Builder(this)
                            .setTitle("Cancel Reservation")
                            .setMessage(
                                    "Cancel reservation #"
                                            + selected.id + "?"
                            )
                            .setNegativeButton("No", null)
                            .setPositiveButton(
                                    "Yes",
                                    (dialog, which) -> {

                                        // Update the local demo reservation.
                                        selected.cancelled = true;

                                        DemoStore.addNotification(
                                                "Reservation #"
                                                        + selected.id
                                                        + " was cancelled."
                                        );

                                        showMessage(
                                                "Demo reservation cancelled."
                                        );

                                        showReservations();
                                    }
                            )
                            .show();
                });

        findViewById(R.id.backFromReservationsButton)
                .setOnClickListener(
                        view -> showDashboard()
                );
    }

    // Displays payment information and demo payment actions.
    private void showPayments() {

        currentScreen = "payments";
        setContentView(R.layout.activity_payments);

        Spinner paymentSpinner =
                findViewById(R.id.paymentSpinner);

        TextView paymentDetails =
                findViewById(R.id.paymentDetails);

        List<DemoStore.Reservation> activeReservations =
                new ArrayList<>();

        // Only active reservations can be selected for payment.
        for (DemoStore.Reservation reservation
                : DemoStore.myReservations()) {

            if (!reservation.cancelled) {
                activeReservations.add(reservation);
            }
        }

        ArrayAdapter<DemoStore.Reservation> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        activeReservations
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        paymentSpinner.setAdapter(adapter);

        // Refresh payment details when the selection changes.
        paymentSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        DemoStore.Reservation selected =
                                activeReservations.get(position);

                        String status =
                                selected.paid ? "PAID" : "UNPAID";

                        paymentDetails.setText(
                                getString(
                                        R.string.payment_details,
                                        DemoStore.money(
                                                selected.getTotal()
                                        ),
                                        status
                                )
                        );
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                        paymentDetails.setText(
                                R.string.no_reservation_selected
                        );
                    }
                }
        );

        if (activeReservations.isEmpty()) {
            paymentDetails.setText(
                    R.string.no_active_reservations
            );
        }

        // Simulate a payment without charging real money.
        findViewById(R.id.payButton)
                .setOnClickListener(view -> {

                    DemoStore.Reservation selected =
                            (DemoStore.Reservation)
                                    paymentSpinner.getSelectedItem();

                    if (selected == null) {
                        showMessage(
                                "Please create a reservation first."
                        );
                        return;
                    }

                    if (selected.paid) {
                        showMessage(
                                "This reservation is already marked as paid."
                        );
                        return;
                    }

                    // Update the local demo payment status.
                    selected.paid = true;

                    DemoStore.addNotification(
                            "Demo payment recorded for reservation #"
                                    + selected.id + "."
                    );

                    showMessage(
                            "Demo payment completed. No money was charged."
                    );

                    showPayments();
                });

        findViewById(R.id.backFromPaymentsButton)
                .setOnClickListener(
                        view -> showDashboard()
                );
    }

    // Displays notifications belonging to the active driver.
    private void showNotifications() {

        currentScreen = "notifications";
        setContentView(R.layout.activity_notifications);

        // Android ListView is a non-generic class.
        ListView notificationList =
                findViewById(R.id.notificationsList);

        List<String> notifications =
                DemoStore.myNotifications();

        if (notifications.isEmpty()) {
            notifications.add(
                    getString(R.string.no_notifications)
            );
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        notifications
                );

        notificationList.setAdapter(adapter);

        findViewById(R.id.backFromNotificationsButton)
                .setOnClickListener(
                        view -> showDashboard()
                );
    }
}
