
package com.fsp.driverapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    // Input fields from the registration layout.
    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the registration screen.
        setContentView(R.layout.activity_register);

        // Connect Java variables to XML views.
        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.registerEmailField);
        passwordField = findViewById(R.id.registerPasswordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);

        Button createAccountButton =
                findViewById(R.id.createAccountButton);

        TextView backToLoginLink =
                findViewById(R.id.backToLoginLink);

        // Validate the form when the button is clicked.
        createAccountButton.setOnClickListener(
                view -> handleRegistration()
        );

        // Return to Login without creating an account.
        backToLoginLink.setOnClickListener(view -> finish());
    }

    private void handleRegistration() {

        // Read user input.
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString();
        String confirmation =
                confirmPasswordField.getText().toString();

        // Check required fields.
        if (name.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmation.isEmpty()) {

            Toast.makeText(this,
                    "Please fill in all fields.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Invalid email address.");
            return;
        }

        // Validate password length.
        if (password.length() < 8) {
            passwordField.setError(
                    "Password must contain at least 8 characters."
            );
            return;
        }

        // Check password confirmation.
        if (!password.equals(confirmation)) {
            confirmPasswordField.setError(
                    "Passwords do not match."
            );
            return;
        }

        // Demo registration: no account is saved to a database.
        Toast.makeText(this,
                "Registration successful in demo mode!",
                Toast.LENGTH_SHORT).show();

        // Open Dashboard immediately after demo validation.
        Intent intent = new Intent(
                this,
                DashboardActivity.class
        );

        intent.putExtra("driverName", name);
        intent.putExtra("driverEmail", email);

        // Clear previous Login and Registration screens.
        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
