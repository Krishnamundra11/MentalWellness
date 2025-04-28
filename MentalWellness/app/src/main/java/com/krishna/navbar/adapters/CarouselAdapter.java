package com.krishna.navbar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.krishna.navbar.R;
import com.krishna.navbar.models.CarouselItem;

import java.util.List;

/**
 * Adapter for the wellbeing category carousel on the landing page
 */
public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    private final List<CarouselItem> items;
    private final Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public CarouselAdapter(Context context, List<CarouselItem> items) {
        this.context = context;
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carousel, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        CarouselItem item = items.get(position);
        
        holder.titleTextView.setText(item.getTitle());
        holder.subtitleTextView.setText(item.getSubtitle());
        holder.iconImageView.setImageResource(item.getImageResourceId());
        
        // Set card background color
        ((CardView) holder.itemView).setCardBackgroundColor(item.getBackgroundColor());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CarouselViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView subtitleTextView;
        ImageView iconImageView;

        CarouselViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_carousel_title);
            subtitleTextView = itemView.findViewById(R.id.tv_carousel_subtitle);
            iconImageView = itemView.findViewById(R.id.iv_carousel_icon);
        }
    }
} 