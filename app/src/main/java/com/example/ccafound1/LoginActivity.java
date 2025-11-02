package com.example.ccafound1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpText, forgotPasswordText;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        EdgeToEdge.enable(this);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        initFirebase();
        initViews();
        setupListeners();
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.txt_email);
        passwordEditText = findViewById(R.id.txt_pass);
        loginButton = findViewById(R.id.btn_login);
        signUpText = findViewById(R.id.sign_up);
        forgotPasswordText = findViewById(R.id.forget);
        loadingDialog = new LoadingDialog(this);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> loginUser());
        signUpText.setOnClickListener(v -> navigateTo(RegisterPage.class));
        forgotPasswordText.setOnClickListener(v -> navigateTo(ForgotPasswordActivity.class));
    }

    private void loginUser() {
        String email = getText(emailEditText);
        String password = getText(passwordEditText);

        if (!validateEmail(email) || !validatePassword(password)) return;

        setLoading(true);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            saveFcmToken(user.getUid());
                            checkUserProfile(user);
                        }
                    } else {
                        handleAuthError(task.getException());
                        setLoading(false);
                    }
                });
    }

    private void checkUserProfile(FirebaseUser user) {
        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        navigateTo(MainActivity.class);
                    } else {
                        createUserProfile(user);
                    }
                })
                .addOnFailureListener(e -> {
                    showToast("Failed to verify user profile");
                    loginButton.setEnabled(true);
                    setLoading(false);
                });
    }

    private void createUserProfile(FirebaseUser user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.getDisplayName() != null ? user.getDisplayName() : "");
        userData.put("email", user.getEmail());
        userData.put("profileImageUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
        userData.put("accountType", "student");

        db.collection("users").document(user.getUid()).set(userData)
                .addOnSuccessListener(aVoid -> navigateTo(ProfileActivity.class))
                .addOnFailureListener(e -> {
                    auth.signOut();
                    showToast("Failed to create profile");
                    loginButton.setEnabled(true);
                    setLoading(false);
                });
    }

    private void saveFcmToken(String userId) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String fcmToken = task.getResult();
                Map<String, Object> tokenMap = new HashMap<>();
                tokenMap.put("fcmToken", fcmToken);
                db.collection("users").document(userId).update(tokenMap);
            }
        });
    }

    private String getText(TextInputEditText input) {
        return input.getText() != null ? input.getText().toString().trim() : "";
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email format");
            return false;
        }
        if (!email.endsWith("@cca.edu.ph")) {
            emailEditText.setError("Please use your school email");
            return false;
        }
        emailEditText.setError(null);
        return true;
    }

    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Minimum 6 characters required");
            return false;
        }
        passwordEditText.setError(null);
        return true;
    }

    private void handleAuthError(Exception e) {
        loginButton.setEnabled(true);
        String message = "Login failed";

        if (e != null && e.getMessage() != null) {
            String err = e.getMessage().toLowerCase();
            if (err.contains("no user record")) message = "Account not found. Please register.";
            else if (err.contains("password is invalid")) message = "Incorrect password";
            else if (err.contains("network error")) message = "No internet connection";
        }
        showToast(message);
    }

    private void setLoading(boolean loading) {
        if (loading) {
            loadingDialog.loginLoadingDialog();
            loginButton.setEnabled(false);
        } else {
            loadingDialog.dismissDialog();
            loginButton.setEnabled(true);
        }
    }

    private void navigateTo(Class<?> cls) {
        loadingDialog.dismissDialog();
        startActivity(new Intent(this, cls));
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismissDialog();
    }
}
