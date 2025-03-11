package com.krishna.navbar.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.models.Therapist;
import com.example.app.activities.TherapistDetailActivity;

import java.util.List;

public class TherapistAdapter extends RecyclerView.Adapter<TherapistAdapter.ViewHolder> {
    private Context context;
    private List<Therapist> therapistList;

    public TherapistAdapter(Context context, List<Therapist> therapistList) {
        this.context = context;
        this.therapistList = therapistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_therapist_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Therapist therapist = therapistList.get(position);

        holder.txtName.setText(therapist.getName());
        holder.txtExperience.setText(therapist.getExperience());
        holder.txtExpertise.setText(therapist.getExpertise());

        // Load image using Glide
        Glide.with(context).load(therapist.getProfileImage()).into(holder.imgProfile);

        // Open detailed profile when clicking the card
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TherapistDetailActivity.class);
            intent.putExtra("therapist", therapist);
            context.startActivity(intent);
        });

        holder.btnBook.setOnClickListener(v -> {
            // Open booking screen (to be implemented)
        });
    }

    @Override
    public int getItemCount() {
        return therapistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile;
        TextView txtName, txtExperience, txtExpertise;
        Button btnBook;

        public ViewHolder(View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtName = itemView.findViewById(R.id.txtName);
            txtExperience = itemView.findViewById(R.id.txtExperience);
            txtExpertise = itemView.findViewById(R.id.txtExpertise);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}

