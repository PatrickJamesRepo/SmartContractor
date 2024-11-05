package com.example.smartcontractor2;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TorpedoLevel extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView levelTextView;
    private View bubbleView;

    private static final double LEVEL_THRESHOLD = 2.0; // Threshold for level detection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Lock to landscape
        setContentView(R.layout.activity_torpedo_level);

        levelTextView = findViewById(R.id.levelTextView);
        bubbleView = findViewById(R.id.bubbleView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer == null) {
                Toast.makeText(this, "Accelerometer not available", Toast.LENGTH_SHORT).show();
                finish(); // Close activity if no accelerometer is available
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];

            // Calculate roll angle to detect horizontal level in landscape mode
            double rollAngle = Math.toDegrees(Math.atan2(y, x));

            // Display "Level" if device is horizontally level within the threshold
            if (Math.abs(rollAngle) < LEVEL_THRESHOLD) {
                levelTextView.setText("Level");
                bubbleView.setTranslationX(0); // Center bubble when level
            } else {
                levelTextView.setText(""); // Clear text when not level
                bubbleView.setTranslationX((float) (rollAngle * 10)); // Move bubble based on roll angle
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
