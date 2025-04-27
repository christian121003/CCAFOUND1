// RankingActivity.java

package com.example.ccafound1;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RankingAdapter adapter;
    private final List<StudentRanking> rankings = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        recyclerView = findViewById(R.id.recyclerViewRanking);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RankingAdapter(rankings);
        recyclerView.setAdapter(adapter);

        loadTopReporters();
    }

    private void loadTopReporters() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .orderBy("foundCount", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    rankings.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        String imageUrl = doc.getString("profileImageUrl");
                        Long foundCount = doc.getLong("foundCount");

                        StudentRanking student = new StudentRanking(
                                name,
                                imageUrl,
                                foundCount != null ? foundCount : 0
                        );
                        rankings.add(student);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load rankings", Toast.LENGTH_SHORT).show());
    }
}
