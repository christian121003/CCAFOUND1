package com.example.ccafound1;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {
    private final List<StudentRanking> rankings;

    public RankingAdapter(List<StudentRanking> rankings) {
        this.rankings = rankings;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        StudentRanking student = rankings.get(position);

        // Set the rank (1-based index)
        holder.rankTextView.setText(String.valueOf(position + 1));
        holder.nameTextView.setText(student.getName());
        holder.reportCountTextView.setText("Reported: " + student.getFoundCount());

        // Load profile image using Glide
        Glide.with(holder.itemView.getContext())
                .load(student.getProfileImageUrl())
                .into(holder.profileImageView);
    }

    @Override
    public int getItemCount() {
        return rankings.size();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView, reportCountTextView, rankTextView;

        public RankingViewHolder(View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.tvRank);
            nameTextView = itemView.findViewById(R.id.tvStudentName);
            reportCountTextView = itemView.findViewById(R.id.tvReportCount);
            profileImageView = itemView.findViewById(R.id.imgProfile);
        }
    }
}
