package com.example.ccafound1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private TextView txtEmail, txtName;
    private ImageView mainProf;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    protected void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        setContentView(R.layout.activity_main);

        setSupportActionBar(toolbar);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        ImageButton reportBtn = findViewById(R.id.report_btn);
        ImageButton returnBtn = findViewById(R.id.return_btn);
        ImageButton statusBtn = findViewById(R.id.status_btn);

        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navbar);
        toolbar = findViewById(R.id.header_toolbar);
        txtEmail = findViewById(R.id.main_cca_acc);
        txtName = findViewById(R.id.main_name);
        mainProf = findViewById(R.id.main_profile_img);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        loadUserData();

        statusBtn.setOnClickListener(v -> {
            Intent statusIntent = new Intent(MainActivity.this, StatusActivity.class);
            startActivity(statusIntent);
        });

        reportBtn.setOnClickListener(v -> {
            Intent reportIntent = new Intent(MainActivity.this, ReportActivity.class);
            startActivity(reportIntent);
        });

        returnBtn.setOnClickListener(v -> {
            Intent returnIntent = new Intent(MainActivity.this, FoundActivity.class);
            startActivity(returnIntent);
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void loadUserData() {
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        String name = documentSnapshot.getString("name");
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                        txtEmail.setText(email);
                        txtName.setText(name);
                        if (profileImageUrl != null) {
                            Glide.with(this)
                                    .load(profileImageUrl)
                                    .circleCrop()
                                    .into(mainProf);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Failed to load user data: ", Toast.LENGTH_SHORT).show());
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        if (itemId == R.id.nav_home) {
            Intent HomeIntent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(HomeIntent);
        } else if (itemId == R.id.nav_profile) {
            Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        } else if (itemId == R.id.nav_activity) {
            Intent activityIntent = new Intent(MainActivity.this, ActivitiesActivity.class);
            startActivity(activityIntent);
        } else if (itemId == R.id.nav_status) {
            Intent statusIntent = new Intent(MainActivity.this, StatusActivity.class);
            startActivity(statusIntent);
        } else if (itemId == R.id.nav_report) {
            Intent reportIntent = new Intent(MainActivity.this, ReportActivity.class);
            startActivity(reportIntent);
        } else if (itemId == R.id.nav_return) {
            Intent returnIntent = new Intent(MainActivity.this, FoundActivity.class);
            startActivity(returnIntent);
        }
        return true;
    }

}