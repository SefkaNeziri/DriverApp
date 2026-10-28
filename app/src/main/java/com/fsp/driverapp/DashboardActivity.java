
package com.fsp.driverapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the dashboard layout.
        setContentView(R.layout.activity_dashboard);

        // Find the views defined in the XML layout.
        TextView welcomeText = findViewById(R.id.welcomeText);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Read the driver's name passed from Registration.
        String driverName = getIntent().getStringExtra("driverName");

        // Display a personalized welcome message.
        if (driverName != null && !driverName.isEmpty()) {
            welcomeText.setText("Welcome, " + driverName + "!");
        }

        // Log out and return to the Login screen.
        logoutButton.setOnClickListener(view -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    MainActivity.class
            );

            // Remove previous screens from the navigation history.
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }
}
