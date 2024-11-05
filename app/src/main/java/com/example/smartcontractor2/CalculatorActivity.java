package com.example.smartcontractor2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CalculatorActivity extends AppCompatActivity {

    private static final String TAG = "CalculatorActivity";

    private TextView categoryTextView;
    private EditText lengthEditText, widthEditText, depthEditText, areaEditText, countEditText;
    private Button calculateButton;
    private TextView resultTextView;

    private String categoryName;
    private Category category;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        preferencesManager = new PreferencesManager(this);

        categoryTextView = findViewById(R.id.categoryTextView);
        lengthEditText = findViewById(R.id.lengthEditText);
        widthEditText = findViewById(R.id.widthEditText);
        depthEditText = findViewById(R.id.depthEditText);
        areaEditText = findViewById(R.id.areaEditText);
        countEditText = findViewById(R.id.countEditText);
        calculateButton = findViewById(R.id.calculateButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Get the category from the Intent
        categoryName = getIntent().getStringExtra("CATEGORY");
        categoryTextView.setText(categoryName + " Calculator");
        Log.d(TAG, "Category received: " + categoryName);

        // Retrieve the category object
        category = preferencesManager.getCategoryByName(categoryName);

        // Show or hide input fields based on required inputs
        configureInputFields();

        calculateButton.setOnClickListener(v -> {
            Log.d(TAG, "Calculate button clicked");
            performCalculation();
        });


    }

    private void configureInputFields() {
        // Hide all input fields initially
        lengthEditText.setVisibility(View.GONE);
        widthEditText.setVisibility(View.GONE);
        depthEditText.setVisibility(View.GONE);
        areaEditText.setVisibility(View.GONE);
        countEditText.setVisibility(View.GONE);

        // Based on the formula, determine which inputs are needed
        String formula = category.getFormula();

        if (formula.contains("length")) {
            lengthEditText.setVisibility(View.VISIBLE);
        }
        if (formula.contains("width")) {
            widthEditText.setVisibility(View.VISIBLE);
        }
        if (formula.contains("depth")) {
            depthEditText.setVisibility(View.VISIBLE);
        }
        if (formula.contains("area")) {
            areaEditText.setVisibility(View.VISIBLE);
        }
        if (formula.contains("count")) {
            countEditText.setVisibility(View.VISIBLE);
        }
    }

    private void performCalculation() {
        try {
            String categoryName = category.getName(); // Assuming 'getName()' returns the category name
            double result = 0.0;

            // Collect variables and their values
            double length = 0.0, width = 0.0, depth = 0.0, area = 0.0, count = 0.0;

            if (categoryName.equalsIgnoreCase("Concrete")) {
                String lengthStr = lengthEditText.getText().toString();
                String widthStr = widthEditText.getText().toString();
                String depthStr = depthEditText.getText().toString();

                if (lengthStr.isEmpty()) {
                    lengthEditText.setError("Required");
                    return;
                }
                if (widthStr.isEmpty()) {
                    widthEditText.setError("Required");
                    return;
                }
                if (depthStr.isEmpty()) {
                    depthEditText.setError("Required");
                    return;
                }

                length = Double.parseDouble(lengthStr);
                width = Double.parseDouble(widthStr);
                depth = Double.parseDouble(depthStr);

                // Example formula: length * width * depth * 2.5
                result = length * width * depth * 2.5;
            }
            // Add similar blocks for other categories like Framing, Sheathing, etc.
            else if (categoryName.equalsIgnoreCase("Framing")) {
                String lengthStr = lengthEditText.getText().toString();
                String widthStr = widthEditText.getText().toString();

                if (lengthStr.isEmpty()) {
                    lengthEditText.setError("Required");
                    return;
                }
                if (widthStr.isEmpty()) {
                    widthEditText.setError("Required");
                    return;
                }

                length = Double.parseDouble(lengthStr);
                width = Double.parseDouble(widthStr);

                // Example formula: length * width * 3.0
                result = length * width * 3.0;
            }
            // Continue adding blocks for other categories...

            // Display the result
            resultTextView.setText(String.format("Estimated Cost: $%.2f", result));
            Log.d(TAG, "Calculation result: " + result);

        } catch (NumberFormatException e) {
            resultTextView.setText("Invalid number format.");
            Log.e(TAG, "Number format error: " + e.getMessage());
        } catch (Exception e) {
            resultTextView.setText("Error in calculation.");
            Log.e(TAG, "Calculation error: " + e.getMessage());
        }
    }


    }

