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

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.krishna.navbar.R;
import com.krishna.navbar.models.Therapist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;

public class TherapistAdapter extends RecyclerView.Adapter<TherapistAdapter.TherapistViewHolder> {
    
    private List<Therapist> therapists;
    private Context context;
    private OnTherapistClickListener listener;
    private Map<String, Boolean> favoriteStatus = new HashMap<>();
    
    public interface OnTherapistClickListener {
        void onProfileClick(Therapist therapist, int position);
        void onFavoriteToggle(Therapist therapist, int position);
    }
    
    public TherapistAdapter(Context context, List<Therapist> therapists, OnTherapistClickListener listener) {
        this.context = context;
        this.therapists = therapists != null ? therapists : new ArrayList<>();
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
        
        // Load image from URL if available, otherwise use placeholder
        if (therapist.getProfileImageUrl() != null && !therapist.getProfileImageUrl().isEmpty()) {
            Glide.with(context).load(therapist.getProfileImageUrl()).into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(R.drawable.placeholder_therapist);
        }

        // Set text fields
        holder.tvName.setText(therapist.getName());
        
        // Handle specialization (now a List<String>)
        if (therapist.getSpecialization() != null && !therapist.getSpecialization().isEmpty()) {
            holder.tvSpecialization.setText(therapist.getSpecialization().get(0));
        } else {
            holder.tvSpecialization.setText("");
        }
        
        holder.tvRating.setText(String.valueOf(therapist.getRating()));
        holder.tvReviews.setText("(" + therapist.getReviewCount() + " reviews)");
        holder.chipExperience.setText(therapist.getExperience());
        
        // Handle languages (now a List<String>)
        if (therapist.getLanguages() != null && !therapist.getLanguages().isEmpty()) {
            holder.chipLanguages.setText(String.join(", ", therapist.getLanguages()));
        } else {
            holder.chipLanguages.setText("");
        }
        
        // Find next available slot from availableSlots
        String nextAvailable = getNextAvailableSlot(therapist);
        holder.tvNextAvailable.setText("Next available: " + nextAvailable);
        
        // Get favorite status from our map
        boolean isFavorite = favoriteStatus.containsKey(therapist.getId()) ? 
                             favoriteStatus.get(therapist.getId()) : false;
        
        // Set favorite icon
        holder.btnFavorite.setImageResource(isFavorite ? 
                R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        
        // Set click listeners
        holder.btnViewProfile.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProfileClick(therapist, position);
            }
        });
        
        holder.btnFavorite.setOnClickListener(v -> {
            // Toggle favorite
            boolean newStatus = !isFavorite;
            favoriteStatus.put(therapist.getId(), newStatus);
            
            // Update icon
            holder.btnFavorite.setImageResource(newStatus ? 
                    R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
            
            if (listener != null) {
                listener.onFavoriteToggle(therapist, position);
            }
        });
    }
    
    // Get next available slot from availableSlots
    private String getNextAvailableSlot(Therapist therapist) {
        if (therapist.getAvailableSlots() == null || therapist.getAvailableSlots().isEmpty()) {
            return "No slots available";
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(new Date());
        
        // Check today's slots first
        List<String> todaySlots = therapist.getAvailableSlots().get(today);
        if (todaySlots != null && !todaySlots.isEmpty()) {
            return "Today, " + todaySlots.get(0);
        }
        
        // Check tomorrow or next available day
        for (Map.Entry<String, List<String>> entry : therapist.getAvailableSlots().entrySet()) {
            if (entry.getKey().compareTo(today) > 0 && entry.getValue() != null && !entry.getValue().isEmpty()) {
                return "Available on " + formatDate(entry.getKey()) + ", " + entry.getValue().get(0);
            }
        }
        
        return "No slots available";
    }
    
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }
    
    @Override
    public int getItemCount() {
        return therapists != null ? therapists.size() : 0;
    }
    
    public void updateList(List<Therapist> filteredList) {
        this.therapists = filteredList != null ? filteredList : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public boolean isFavorite(Therapist therapist) {
        return favoriteStatus.containsKey(therapist.getId()) ? 
               favoriteStatus.get(therapist.getId()) : false;
    }
    
    public void toggleFavorite(Therapist therapist) {
        boolean currentStatus = isFavorite(therapist);
        favoriteStatus.put(therapist.getId(), !currentStatus);
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
