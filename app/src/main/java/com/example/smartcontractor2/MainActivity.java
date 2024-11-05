package com.example.smartcontractor2;

import androidx.appcompat.app.AppCompatDelegate;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private PDFGenerator pdfGenerator;
    private PreferencesManager preferencesManager;

    // UI elements
    private Button concreteButton, framingButton;
    private Button cameraButton, pitchFinderButton, generatePdfButton;
    private Button torpedoLevelButton, constructionCalculatorButton, arMeasurementButton;
    private TextView resultTextView;
    private ImageView imageView; // To display the selected or captured image
    private String categoryName;
    private List<Uri> selectedImageUris = new ArrayList<>(); // To store selected image URIs for PDF

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize PDF generator and preferences manager
        pdfGenerator = new PDFGenerator();
        preferencesManager = new PreferencesManager(this);

        // Load categories and initialize UI elements
        loadCategories();
        initializeViews();

        // Initialize theme switch
        Switch themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setChecked(preferencesManager.isDarkMode());
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            preferencesManager.setDarkMode(isChecked);
        });

        setupButtonListeners();
    }

    private void loadCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Concrete", "length * width * depth * 2.5"));
        categories.add(new Category("Framing", "length * width * 3.0"));
        preferencesManager.saveCategories(categories);
    }

    private void initializeViews() {
        concreteButton = findViewById(R.id.concreteButton);
        framingButton = findViewById(R.id.framingButton);
        cameraButton = findViewById(R.id.cameraButton);
        pitchFinderButton = findViewById(R.id.pitchFinderButton);
        generatePdfButton = findViewById(R.id.generatePdfButton);
        torpedoLevelButton = findViewById(R.id.torpedoLevelButton);
        constructionCalculatorButton = findViewById(R.id.constructionCalculatorButton);
        arMeasurementButton = findViewById(R.id.arMeasurementButton);
        resultTextView = findViewById(R.id.resultTextView);
        imageView = findViewById(R.id.imageView);
    }

    private void setupButtonListeners() {
        concreteButton.setOnClickListener(view -> openCalculator("Concrete"));
        framingButton.setOnClickListener(view -> openCalculator("Framing"));
        cameraButton.setOnClickListener(view -> showImageSourceOptions());
        pitchFinderButton.setOnClickListener(view -> openPitchFinder());
        generatePdfButton.setOnClickListener(view -> requestManageStoragePermission());
        torpedoLevelButton.setOnClickListener(view -> openTorpedoLevel());
        constructionCalculatorButton.setOnClickListener(view -> openConstructionCalculator());
        arMeasurementButton.setOnClickListener(view -> showImageSourceOptions());
    }

    private void showImageSourceOptions() {
        String[] options = {"Take a Picture", "Select from Album"};
        new AlertDialog.Builder(this)
                .setTitle("Choose Image Source")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        dispatchTakePictureIntent();
                    } else {
                        dispatchPickPictureIntent();
                    }
                })
                .show();
    }

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void dispatchPickPictureIntent() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(pickIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(imageBitmap);
                imageView.setVisibility(View.VISIBLE);
                saveImageToAlbum(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                if (data.getClipData() != null) { // Multiple images selected
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        selectedImageUris.add(imageUri);
                    }
                } else if (data.getData() != null) { // Single image selected
                    selectedImageUris.add(data.getData());
                }
                Toast.makeText(this, selectedImageUris.size() + " images selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageToAlbum(Bitmap bitmap) {
        OutputStream fos;
        String imageName = "SmartContractor_" + System.currentTimeMillis() + ".jpg";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SmartContractor");
                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (imageUri != null) {
                    fos = getContentResolver().openOutputStream(imageUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    if (fos != null) fos.close();
                    Toast.makeText(this, "Image saved to SmartContractor album", Toast.LENGTH_SHORT).show();
                    selectedImageUris.add(imageUri); // Add to selected images for PDF
                }
            } else {
                File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "SmartContractor");
                if (!directory.exists()) directory.mkdirs();
                File imageFile = new File(directory, imageName);
                fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                Uri imageUri = Uri.fromFile(imageFile);
                selectedImageUris.add(imageUri); // Add to selected images for PDF
                Toast.makeText(this, "Image saved to SmartContractor album", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to save image", e);
        }
    }

    private void openCalculator(String categoryName) {
        Intent intent = new Intent(this, CalculatorActivity.class);
        intent.putExtra("CATEGORY", categoryName);
        startActivity(intent);
    }

    private void openPitchFinder() {
        startActivity(new Intent(this, PitchFinderActivity.class));
    }

    private void openTorpedoLevel() {
        startActivity(new Intent(this, TorpedoLevel.class));
    }

    private void openConstructionCalculator() {
        startActivity(new Intent(this, ConstructionCalculator.class));
    }

    private void requestManageStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, STORAGE_PERMISSION_CODE);
            } else {
                generatePDF();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    private void generatePDF() {
        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "No images selected for PDF", Toast.LENGTH_SHORT).show();
            return;
        }

        File pdfFile = pdfGenerator.generatePDFWithImages("Project Estimate", selectedImageUris);

        if (pdfFile != null && pdfFile.exists()) {
            resultTextView.setText("PDF generated at: " + pdfFile.getAbsolutePath());
            Log.d(TAG, "PDF generated: " + pdfFile.getAbsolutePath());
            Toast.makeText(this, "PDF generated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            resultTextView.setText("Failed to generate PDF.");
            Toast.makeText(this, "Failed to generate PDF.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generatePDF();
            } else {
                Toast.makeText(this, "Storage permission is required to save PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
