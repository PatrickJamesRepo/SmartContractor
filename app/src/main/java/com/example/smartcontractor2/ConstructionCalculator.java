package com.example.smartcontractor2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ConstructionCalculator extends AppCompatActivity {

    private TextView resultTextView;
    private StringBuilder input = new StringBuilder();
    private double lastResult = 0;
    private String lastOperator = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_construction_calculator);

        resultTextView = findViewById(R.id.resultTextView);

        // Set up button listeners
        setupButtons();
    }

    private void setupButtons() {
        int[] numberButtonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3,
                R.id.button4, R.id.button5, R.id.button6, R.id.button7,
                R.id.button8, R.id.button9
        };

        // Number buttons
        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(view -> {
                Button button = (Button) view;
                input.append(button.getText().toString());
                resultTextView.setText(input.toString());
            });
        }

        // Operator buttons
        findViewById(R.id.buttonAdd).setOnClickListener(view -> applyOperator("+"));
        findViewById(R.id.buttonSubtract).setOnClickListener(view -> applyOperator("-"));
        findViewById(R.id.buttonMultiply).setOnClickListener(view -> applyOperator("*"));
        findViewById(R.id.buttonDivide).setOnClickListener(view -> applyOperator("/"));

        // Equal button
        findViewById(R.id.buttonEquals).setOnClickListener(view -> calculateResult());

        // Clear button
        findViewById(R.id.buttonClear).setOnClickListener(view -> {
            input.setLength(0);
            lastOperator = "";
            lastResult = 0;
            resultTextView.setText("0");
        });
    }

    private void applyOperator(String operator) {
        if (input.length() > 0) {
            calculateResult();
            lastOperator = operator;
            input.setLength(0);
        }
    }

    private void calculateResult() {
        if (input.length() == 0) return;

        double currentValue = Double.parseDouble(input.toString());

        switch (lastOperator) {
            case "+":
                lastResult += currentValue;
                break;
            case "-":
                lastResult -= currentValue;
                break;
            case "*":
                lastResult *= currentValue;
                break;
            case "/":
                if (currentValue != 0) lastResult /= currentValue;
                else resultTextView.setText("Error");
                return;
            default:
                lastResult = currentValue;
                break;
        }

        resultTextView.setText(String.valueOf(lastResult));
        input.setLength(0);
    }
}
