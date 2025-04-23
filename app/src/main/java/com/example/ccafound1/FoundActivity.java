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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FoundActivity extends AppCompatActivity {
    private EditText Name, Email, Contact, Category, Description, Date;
    private ProgressBar progressBarf;
    private FirebaseFirestore db;
    private ImageView imageView;
    private Uri imageUri;
    private StorageReference storageRef;
    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            imageUri = result;
            imageView.setImageURI(imageUri);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Report");
        }
        Button submit = findViewById(R.id.found_submit_btn);
        ImageView backButton = findViewById(R.id.back_btn);
        Name = findViewById(R.id.found_input_name);
        Email = findViewById(R.id.found_input_email);
        Contact = findViewById(R.id.found_input_contact);
        Category = findViewById(R.id.found_input_category);
        Description = findViewById(R.id.found_description);
        Date = findViewById(R.id.found_input_date);
        imageView = findViewById(R.id.found_image_view);
        progressBarf = findViewById(R.id.loading_progress_found);
        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        backButton.setOnClickListener(v -> finish());
        submit.setOnClickListener(v -> uploadData());
        imageView.setOnClickListener(v -> openGallery());
    }
    private void openGallery() {
        imagePickerLauncher.launch("image/*"); // Launch the image picker for any image type
    }
    private void uploadData() {
        String name = Name.getText().toString().trim();
        String email = Email.getText().toString().trim();
        String contact = Contact.getText().toString().trim();
        String category = Category.getText().toString().trim();
        String description = Description.getText().toString().trim();
        String date = Date.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || category.isEmpty() || description.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        String id = UUID.randomUUID().toString();
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", id);
        doc.put("name", name);
        doc.put("email", email);
        doc.put("contact", contact);
        doc.put("category", category);
        doc.put("description", description);
        doc.put("date", date);

        if (imageUri != null) {
            String fileName = UUID.randomUUID().toString();
            StorageReference fileReference = storageRef.child("found_images/" + fileName);

            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                doc.put("image_url", imageUrl);
                saveDataToFirestore(doc);
            })).addOnFailureListener(e -> {
                showLoading(false);
                Toast.makeText(FoundActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
            });
        } else {
            saveDataToFirestore(doc);
        }
    }
    private void saveDataToFirestore(Map<String, Object> doc) {
        db.collection("Found_Item").document(Objects.requireNonNull(doc.get("id")).toString()).set(doc).addOnCompleteListener(task -> {
            showLoading(false);
            if (task.isSuccessful()) {
                Toast.makeText(FoundActivity.this, "Report Uploaded", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(FoundActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            showLoading(false);
            Toast.makeText(FoundActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    private void showLoading(boolean show) {
        progressBarf.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    private void clearFields() {
        Category.setText("");
        Description.setText("");
        Date.setText("");
        imageView.setImageURI(null);
    }
}
