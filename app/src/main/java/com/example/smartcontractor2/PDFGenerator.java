package com.example.smartcontractor2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PDFGenerator {

    private static final String TAG = "PDFGenerator";
    private Context context;

    public PDFGenerator() {
        this.context = context;
    }

    public File generatePDFWithImages(String projectEstimate, List<Uri> selectedImageUris) {
        // Initialize PdfDocument
        PdfDocument pdfDocument = new PdfDocument();
        File pdfFile = new File(context.getExternalFilesDir(null), "ProjectEstimateWithImages.pdf");

        try {
            // Set up paint and text options
            int pageWidth = 595; // A4 page width in points
            int pageHeight = 842; // A4 page height in points
            int currentPageNumber = 1;

            // Add title on the first page
            PdfDocument.PageInfo titlePageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber++).create();
            PdfDocument.Page titlePage = pdfDocument.startPage(titlePageInfo);
            Canvas canvas = titlePage.getCanvas();
            canvas.drawText(projectEstimate, pageWidth / 2, 40, null); // Draw title at the top
            pdfDocument.finishPage(titlePage);

            // Loop through each image URI and add it as a separate page
            for (Uri imageUri : selectedImageUris) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, pageWidth, pageHeight, true);

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber++).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas pageCanvas = page.getCanvas();

                pageCanvas.drawBitmap(scaledBitmap, 0, 0, null); // Draw the image to fill the page
                pdfDocument.finishPage(page);
            }

            // Write the document content to the file
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
            Log.d(TAG, "PDF created successfully: " + pdfFile.getAbsolutePath());

        } catch (IOException e) {
            Log.e(TAG, "Error generating PDF", e);
            return null;
        } finally {
            // Close the document
            pdfDocument.close();
        }

        return pdfFile;
    }
}
