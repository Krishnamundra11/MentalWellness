package com.krishna.navbar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.krishna.navbar.R;
import com.krishna.navbar.models.Therapist;

import java.util.List;

public class TherapistAdapter extends RecyclerView.Adapter<TherapistAdapter.TherapistViewHolder> {
    
    private List<Therapist> therapists;
    private Context context;
    private OnTherapistClickListener listener;
    
    public interface OnTherapistClickListener {
        void onProfileClick(Therapist therapist, int position);
        void onFavoriteToggle(Therapist therapist, int position);
    }
    
    public TherapistAdapter(Context context, List<Therapist> therapists, OnTherapistClickListener listener) {
        this.context = context;
        this.therapists = therapists;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TherapistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_therapist, parent, false);
        return new TherapistViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TherapistViewHolder holder, int position) {
        Therapist therapist = therapists.get(position);
        
        holder.imgProfile.setImageResource(therapist.getProfileImageResourceId());
        holder.tvName.setText(therapist.getName());
        holder.tvSpecialization.setText(therapist.getSpecialization());
        holder.tvRating.setText(String.valueOf(therapist.getRating()));
        holder.tvReviews.setText("(" + therapist.getReviewCount() + " reviews)");
        holder.chipExperience.setText(therapist.getExperience());
        holder.chipLanguages.setText(therapist.getLanguages());
        holder.tvNextAvailable.setText("Next available: " + therapist.getNextAvailable());
        
        // Set favorite icon
        holder.btnFavorite.setImageResource(therapist.isFavorite() ? 
                R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        
        // Set click listeners
        holder.btnViewProfile.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProfileClick(therapist, position);
            }
        });
        
        holder.btnFavorite.setOnClickListener(v -> {
            therapist.toggleFavorite();
            holder.btnFavorite.setImageResource(therapist.isFavorite() ? 
                    R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
            
            if (listener != null) {
                listener.onFavoriteToggle(therapist, position);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return therapists.size();
    }
    
    public void updateList(List<Therapist> filteredList) {
        this.therapists = filteredList;
        notifyDataSetChanged();
    }
    
    static class TherapistViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile;
        TextView tvName, tvSpecialization, tvRating, tvReviews, tvNextAvailable;
        Chip chipExperience, chipLanguages;
        Button btnViewProfile;
        ImageButton btnFavorite;
        
        public TherapistViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.img_profile);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSpecialization = itemView.findViewById(R.id.tv_specialization);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvReviews = itemView.findViewById(R.id.tv_reviews);
            chipExperience = itemView.findViewById(R.id.chip_experience);
            chipLanguages = itemView.findViewById(R.id.chip_languages);
            tvNextAvailable = itemView.findViewById(R.id.tv_next_available);
            btnViewProfile = itemView.findViewById(R.id.btn_view_profile);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
        }
    }
}
