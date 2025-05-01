package com.krishna.navbar.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krishna.navbar.R;
import com.krishna.navbar.models.TimeSlot;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {
    
    private List<TimeSlot> timeSlots;
    private int selectedPosition = -1;
    private OnTimeSlotSelectedListener listener;

    public interface OnTimeSlotSelectedListener {
        void onTimeSlotSelected(TimeSlot timeSlot);
    }

    public TimeSlotAdapter(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public void setOnTimeSlotSelectedListener(OnTimeSlotSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlots.get(position);
        
        holder.tvTimeSlot.setText(timeSlot.getTime());
        
        // Update selected state
        boolean isSelected = position == selectedPosition;
        timeSlot.setSelected(isSelected);
        
        // Update appearance based on selection and availability
        if (!timeSlot.isAvailable()) {
            // Unavailable slot styling
            holder.tvTimeSlot.setBackgroundResource(R.drawable.bg_time_slot_unselected);
            holder.tvTimeSlot.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_secondary));
            holder.tvTimeSlot.setAlpha(0.5f);
            holder.itemView.setEnabled(false);
        } else {
            // Available slot styling
            holder.tvTimeSlot.setBackgroundResource(isSelected ? 
                    R.drawable.bg_time_slot_selected : 
                    R.drawable.bg_time_slot_unselected);
            holder.tvTimeSlot.setTextColor(holder.itemView.getContext().getResources().getColor(
                    isSelected ? R.color.white : R.color.black));
            holder.tvTimeSlot.setAlpha(1.0f);
            holder.itemView.setEnabled(true);
            
            // Set click listener for available slots
            holder.itemView.setOnClickListener(v -> {
                selectTimeSlot(holder.getAdapterPosition());
            });
        }
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }
    
    public void selectTimeSlot(int position) {
        if (position >= 0 && position < timeSlots.size()) {
            TimeSlot timeSlot = timeSlots.get(position);
            
            if (timeSlot.isAvailable()) {
                int previousSelected = selectedPosition;
                selectedPosition = position;
                
                // Notify item changed for previous and new selection
                if (previousSelected != -1) {
                    notifyItemChanged(previousSelected);
                }
                notifyItemChanged(selectedPosition);
                
                // Call listener
                if (listener != null) {
                    listener.onTimeSlotSelected(timeSlot);
                }
            }
        }
    }

    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeSlot;

        TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeSlot = itemView.findViewById(R.id.tv_time_slot);
        }
    }
} 