package com.example.ccafound1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import android.util.Log;
import android.widget.ProgressBar;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageClass extends AppCompatActivity {

    private static final String API_KEY = "AIzaSyAbxyKbTyb0sOhk_hf8Dr3PmBXsS1jripk";
    private static final float CONFIDENCE_THRESHOLD = 0.8f;

    private ImageView imageView;
    private TextView resultText;
    private ProgressBar progressBar;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_class);

        imageView = findViewById(R.id.imageView);
        resultText = findViewById(R.id.label);
        progressBar = findViewById(R.id.progressBar);
        Button selectImageButton = findViewById(R.id.button);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        handleSelectedImage(result.getData());
                    }
                }
        );

        selectImageButton.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    @SuppressLint("SetTextI18n")
    private void handleSelectedImage(Intent data) {
        Uri imageUri = data.getData();
        if (imageUri != null) {
            try {
                Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(selectedBitmap);
                resultText.setText("Analyzing...");
                classifyImageLocally(selectedBitmap);
            } catch (Exception e) {
                Log.e("ImageError", "Error loading image", e);
                resultText.setText("Failed to load image.");
            }
        }
    }

    private void classifyImageLocally(Bitmap bitmap) {
        progressBar.setVisibility(View.VISIBLE);
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        labeler.process(image)
                .addOnSuccessListener(labels -> {
                    if (labels.isEmpty()) {
                        fallbackToCloud(bitmap);
                        return;
                    }

                    ImageLabel topLabel = labels.get(0);

                    if (topLabel.getConfidence() >= CONFIDENCE_THRESHOLD) {
                        String labelResult = "Label: " + topLabel.getText()
                                + "\nConfidence: " + String.format(Locale.getDefault(), "%.2f", topLabel.getConfidence() * 100) + "%"
                                + "\nSource: Local ML Kit"
                                + "\nDate: " + getCurrentDateTime();
                        resultText.setText(labelResult);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        fallbackToCloud(bitmap);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MLKitError", "Local classification failed", e);
                    fallbackToCloud(bitmap);
                });
    }

    @SuppressLint("SetTextI18n")
    private void fallbackToCloud(Bitmap bitmap) {
        new Thread(() -> {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

                JSONObject requestBody = new JSONObject();
                JSONArray requestsArray = new JSONArray();
                JSONObject image = new JSONObject();
                image.put("content", base64Image);

                JSONObject feature = new JSONObject();
                feature.put("type", "LABEL_DETECTION");
                feature.put("maxResults", 5);

                JSONObject request = new JSONObject();
                request.put("image", image);
                request.put("features", new JSONArray().put(feature));

                requestsArray.put(request);
                requestBody.put("requests", requestsArray);

                URL url = new URL("https://vision.googleapis.com/v1/images:annotate?key=" + API_KEY);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                conn.getOutputStream().write(requestBody.toString().getBytes(StandardCharsets.UTF_8));

                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                JSONObject responseJson = new JSONObject(responseBuilder.toString());
                JSONArray labels = responseJson
                        .getJSONArray("responses")
                        .getJSONObject(0)
                        .getJSONArray("labelAnnotations");

                if (labels.length() > 0) {
                    JSONObject topLabelObj = labels.getJSONObject(0);
                    String topLabelText = topLabelObj.getString("description");
                    float score = (float) topLabelObj.getDouble("score");

                    final String finalResult = "Label: " + topLabelText
                            + "\nConfidence: " + String.format(Locale.getDefault(), "%.2f", score * 100) + "%"
                            + "\nSource: Google Cloud Vision"
                            + "\nDate: " + getCurrentDateTime();

                    runOnUiThread(() -> {
                        resultText.setText(finalResult);
                        progressBar.setVisibility(View.GONE);
                    });
                } else {
                    runOnUiThread(() -> {
                        resultText.setText("No label detected.");
                        progressBar.setVisibility(View.GONE);
                    });
                }

            } catch (Exception e) {
                Log.e("CloudVisionError", "Cloud classification failed", e);
                runOnUiThread(() -> {
                    resultText.setText("Cloud Vision API error!");
                    progressBar.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault()).format(new Date());
    }
}


/*
public class ImageClass extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String API_KEY = "AIzaSyAbxyKbTyb0sOhk_hf8Dr3PmBXsS1jripk";
    private ImageView imageView;
    private TextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_class);

        Button button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        label = findViewById(R.id.label);
    }

    public void ButtonImageRec(View view) {
        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Save the selected image URI
            Uri imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                label.setText("Analyzing image...");


                classifyImageCloud(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                label.setText("Failed to load image!");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void classifyImageCloud(Bitmap bitmap) {
        new Thread(() -> {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

                JSONObject requestBody = new JSONObject();
                JSONArray requestsArray = new JSONArray();
                JSONObject image = new JSONObject();
                image.put("content", base64Image);

                JSONObject feature = new JSONObject();
                feature.put("type", "LABEL_DETECTION");
                feature.put("maxResults", 5);

                JSONObject request = new JSONObject();
                request.put("image", image);
                request.put("features", new JSONArray().put(feature));

                requestsArray.put(request);
                requestBody.put("requests", requestsArray);

                URL url = new URL("https://vision.googleapis.com/v1/images:annotate?key=" + API_KEY);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                conn.getOutputStream().write(requestBody.toString().getBytes(StandardCharsets.UTF_8));

                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                JSONObject responseJson = new JSONObject(responseBuilder.toString());
                JSONArray labels = responseJson
                        .getJSONArray("responses")
                        .getJSONObject(0)
                        .getJSONArray("labelAnnotations");

                StringBuilder resultBuilder = new StringBuilder();
                for (int i = 0; i < labels.length(); i++) {
                    JSONObject labelObj = labels.getJSONObject(i);
                    String labelText = labelObj.getString("description");
                    resultBuilder.append(labelText).append(", ");
                }

                final String finalResult = resultBuilder.toString();

                runOnUiThread(() -> label.setText(finalResult)); // Show labels in TextView

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> label.setText("Error classifying image!"));
            }
        }).start();
    }
}*/
