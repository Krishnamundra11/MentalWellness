package com.krishna.navbar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krishna.navbar.R;
import com.krishna.navbar.models.DateItem;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private List<DateItem> dateItems;
    private int selectedPosition = -1;
    private OnDateSelectedListener listener;

    public interface OnDateSelectedListener {
        void onDateSelected(DateItem dateItem);
    }

    public DateAdapter(List<DateItem> dateItems) {
        this.dateItems = dateItems;
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateItem dateItem = dateItems.get(position);
        
        holder.tvDayName.setText(dateItem.getDayName());
        holder.tvDayNumber.setText(dateItem.getDayNumber());
        
        // Update selected state
        boolean isSelected = position == selectedPosition;
        dateItem.setSelected(isSelected);
        
        // Update background based on selection state
        holder.itemView.setBackgroundResource(isSelected ? 
                R.drawable.bg_date_selected : 
                R.drawable.bg_date_unselected);
                
        // Update text color based on selection state
        int textColor = holder.itemView.getContext().getResources().getColor(
                isSelected ? R.color.white : R.color.black);
        holder.tvDayName.setTextColor(textColor);
        holder.tvDayNumber.setTextColor(textColor);
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            selectDate(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return dateItems.size();
    }
    
    public void selectDate(int position) {
        if (position >= 0 && position < dateItems.size()) {
            int previousSelected = selectedPosition;
            selectedPosition = position;
            
            // Notify item changed for previous and new selection
            if (previousSelected != -1) {
                notifyItemChanged(previousSelected);
            }
            notifyItemChanged(selectedPosition);
            
            // Call listener
            if (listener != null) {
                listener.onDateSelected(dateItems.get(position));
            }
        }
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName;
        TextView tvDayNumber;

        DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tv_day_name);
            tvDayNumber = itemView.findViewById(R.id.tv_day_number);
        }
    }
} 