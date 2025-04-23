package com.example.ccafound1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private GoogleSignInClient gsc;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null) {
                            firebaseAuthWithGoogle(account.getIdToken());
                        }
                    } catch (ApiException e) {
                        Toast.makeText(this, "Google sign in failed", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        SignInButton googleSignInButton = findViewById(R.id.sign_in_button);
        googleSignInButton.setColorScheme(SignInButton.COLOR_DARK);
        googleSignInButton.setSize(SignInButton.SIZE_WIDE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.app_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        gsc.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = gsc.getSignInIntent();
            signInLauncher.launch(signInIntent);
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String email = firebaseUser != null ? firebaseUser.getEmail() : null;

                    if (email != null && email.endsWith("@cca.edu.ph")) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String uid = firebaseUser.getUid();

                        db.collection("users").document(uid).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Map<String, Object> newUser = new HashMap<>();
                                        newUser.put("name", firebaseUser.getDisplayName());
                                        newUser.put("email", firebaseUser.getEmail());
                                        newUser.put("profileImageUrl", firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : "");
                                        newUser.put("contact", "");

                                        db.collection("users").document(uid).set(newUser)
                                                .addOnSuccessListener(unused -> {
                                                    Toast.makeText(LoginActivity.this, "Account registered!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    auth.signOut();
                                                    Toast.makeText(LoginActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Failed to check user record", Toast.LENGTH_SHORT).show());
                    } else {
                        auth.signOut();
                        gsc.signOut().addOnCompleteListener(task -> Toast.makeText(LoginActivity.this, "Use your school email only (@cca.edu.ph)", Toast.LENGTH_LONG).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show());
    }
}
