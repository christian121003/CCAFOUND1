package com.example.ccafound1;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ReportActivity extends AppCompatActivity {
    private EditText Name, Email, Section, Category, Description, Date;
    private ProgressBar progressBarr;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ImageView imageView;
    private Uri imageUri;
    private StorageReference storageRef;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    imageUri = result;
                    imageView.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Report");
        }

        ImageView backButton = findViewById(R.id.back_btn);
        Button submit = findViewById(R.id.report_submit_btn);
        Name = findViewById(R.id.report_input_name);
        Email = findViewById(R.id.report_input_email);
        Section = findViewById(R.id.report_input_section);
        Category = findViewById(R.id.report_input_category);
        Description = findViewById(R.id.report_description);
        Date = findViewById(R.id.report_input_date);
        imageView = findViewById(R.id.report_image_view);

        progressBarr = findViewById(R.id.loading_progress_report);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        backButton.setOnClickListener(v -> finish());
        submit.setOnClickListener(v -> uploadData());
        imageView.setOnClickListener(v -> openGallery());

        loadUserData();
    }

    private void openGallery() {
        imagePickerLauncher.launch("image/*");
    }

    private void loadUserData() {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("name");
                        String userEmail = documentSnapshot.getString("email");
                        String userSection = documentSnapshot.getString("section");

                        Name.setText(userName);
                        Email.setText(userEmail);
                        Section.setText(userSection);

                        Name.setEnabled(false);
                        Email.setEnabled(false);
                        Section.setEnabled(false);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ReportActivity.this, "Error loading user data", Toast.LENGTH_SHORT).show());
    }

    private void uploadData() {
        String name = Name.getText().toString().trim();
        String email = Email.getText().toString().trim();
        String section = Section.getText().toString().trim();
        String category = Category.getText().toString().trim();
        String description = Description.getText().toString().trim();
        String date = Date.getText().toString().trim();

        if (category.isEmpty() || description.isEmpty() || date.isEmpty()) {
            Toast.makeText( this,"All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        String id = UUID.randomUUID().toString();
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", id);
        doc.put("name", name);
        doc.put("email", email);
        doc.put("section", section);
        doc.put("category", category);
        doc.put("description", description);
        doc.put("date", date);

        if (imageUri != null) {
            String fileName = UUID.randomUUID().toString();
            StorageReference fileReference = storageRef.child("lost_images/" + fileName);

            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                doc.put("image_url", imageUrl);
                saveDataToFirestore(doc);
            })).addOnFailureListener(e -> {
                showLoading(false);
                Toast.makeText(ReportActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
            });
        } else {
            saveDataToFirestore(doc);
        }
    }

    private void saveDataToFirestore(Map<String, Object> doc) {
        db.collection("Lost_Item").document(Objects.requireNonNull(doc.get("id")).toString()).set(doc)
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(ReportActivity.this, "Report Uploaded", Toast.LENGTH_SHORT).show();
                        clearFields();
                    } else {
                        Toast.makeText(ReportActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(ReportActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showLoading(boolean show) {
        progressBarr.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void clearFields() {
        Category.setText("");
        Description.setText("");
        Date.setText("");
        imageView.setImageURI(null);
    }
}
