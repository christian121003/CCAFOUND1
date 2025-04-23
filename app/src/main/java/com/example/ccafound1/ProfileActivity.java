package com.example.ccafound1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> imagePicker;
    private EditText profName, profEmail, profContact;
    private ImageView profImage;
    private Uri imageUri;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        profName = findViewById(R.id.profile_input_name);
        profEmail = findViewById(R.id.profile_input_email);
        profContact = findViewById(R.id.profile_input_contact);
        profImage = findViewById(R.id.image_upload);

        ImageView profBack = findViewById(R.id.back_btn);
        ImageButton profSave = findViewById(R.id.profile_save_btn);
        Button profLogout = findViewById(R.id.btn_logout); // UPDATED ID

        profImage.setOnClickListener(v -> openImagePicker());
        profSave.setOnClickListener(v -> updateProfile());
        profLogout.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Logout button clicked", Toast.LENGTH_SHORT).show(); // Debug message
            signOut();
        });

        loadProfile();

        imagePicker = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        profImage.setImageURI(imageUri);
                    }
                });

        profBack.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, MainActivity.class)));
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePicker.launch(intent);
    }

    private void loadProfile() {
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        profName.setText(documentSnapshot.getString("name"));
                        profContact.setText(documentSnapshot.getString("contact"));
                        profEmail.setText(documentSnapshot.getString("email"));

                        String imageUrl = documentSnapshot.getString("profileImageUrl");
                        if (imageUrl != null) {
                            Glide.with(this)
                                    .load(imageUrl)
                                    .into(profImage);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show());
    }

    private void updateProfile() {
        String name = profName.getText().toString().trim();
        String contact = profContact.getText().toString().trim();
        String email = profEmail.getText().toString().trim();

        if (name.isEmpty()) {
            profName.setError("Name cannot be empty");
            return;
        }

        if (contact.isEmpty() || !isValidPhilippinePhone(contact)) {
            profContact.setError("Invalid Philippine Contact Number");
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            profEmail.setError("Invalid Email");
            return;
        }

        Map<String, Object> updatedProfile = new HashMap<>();
        updatedProfile.put("name", name);
        updatedProfile.put("contact", contact);
        updatedProfile.put("email", email);

        if (imageUri != null) {
            StorageReference storageRef = storage.getReference().child("profileImages/" + currentUser.getUid() + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                updatedProfile.put("profileImageUrl", uri.toString());
                                saveUpdatedProfileToFirestore(updatedProfile);
                            }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfileActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                        saveUpdatedProfileToFirestore(updatedProfile);
                    });
        } else {
            saveUpdatedProfileToFirestore(updatedProfile);
        }
    }

    private void saveUpdatedProfileToFirestore(Map<String, Object> updatedProfile) {
        db.collection("users").document(currentUser.getUid())
                .update(updatedProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    loadProfile();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Profile update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private boolean isValidPhilippinePhone(String phone) {
        String phoneRegex = "^(\\+63|0)9\\d{7,8}$";
        return phone.matches(phoneRegex);
    }

    private void signOut() {
        Log.d("ProfileActivity", "signOut() called");
        Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show();

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
