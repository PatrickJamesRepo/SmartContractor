package com.example.smartcontractor2;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class PitchFinderActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView tvSquare;
    private static final int RUN = 12; // Fixed run for pitch calculation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_finder);

        // Disable night mode if desired
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Initialize TextView
        tvSquare = findViewById(R.id.tv_square);

        // Initialize SensorManager and Accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                tvSquare.setText("Accelerometer not available");
                Toast.makeText(this, "Accelerometer not available on this device.", Toast.LENGTH_LONG).show();
            }
        } else {
            tvSquare.setText("Sensor Manager not available");
            Toast.makeText(this, "Sensor Manager not available.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float sides = event.values[0];
            float upDown = event.values[1];

            // Calculate pitch ratio rise based on the upDown (Y-axis) value
            int rise = Math.abs((int) (upDown * 2)); // Scale and round to integer

            // Calculate the angle in degrees
            double angle = Math.toDegrees(Math.atan2(upDown, sides));

            // Update the TextView with rise/run and angle
            tvSquare.setText(String.format("Pitch: %d/%d\nAngle: %.2f°", rise, RUN, angle));

            // Rotate and translate TextView for responsive movement
            tvSquare.setRotationX(upDown * 3f);
            tvSquare.setRotationY(sides * 3f);
            tvSquare.setRotation(-sides);
            tvSquare.setTranslationX(sides * -10);
            tvSquare.setTranslationY(upDown * 10);

            // Set background color based on "level" status
            int color = (upDown == 0 && sides == 0) ? Color.GREEN : Color.RED;
            tvSquare.setBackgroundColor(color);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed for accuracy changes
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
