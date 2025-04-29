package com.krishna.navbar.ui.meditation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.krishna.navbar.R;

import java.util.ArrayList;
import java.util.List;

public class RelatedMeditationAdapter extends RecyclerView.Adapter<RelatedMeditationAdapter.ViewHolder> {
    private List<MeditationSession> sessions = new ArrayList<>();
    private OnSessionClickListener listener;

    public interface OnSessionClickListener {
        void onSessionClick(MeditationSession session);
    }

    public RelatedMeditationAdapter(OnSessionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_related_meditation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MeditationSession session = sessions.get(position);
        holder.meditationTitle.setText(session.getTitle());
        holder.meditationDuration.setText(session.getDurationMinutes() + " minutes");

        if (session.getImageUrl() != null && !session.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(session.getImageUrl())
                    .into(holder.meditationImage);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSessionClick(session);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public void setSessions(List<MeditationSession> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView meditationImage;
        TextView meditationTitle;
        TextView meditationDuration;

        ViewHolder(View itemView) {
            super(itemView);
            meditationImage = itemView.findViewById(R.id.meditation_image);
            meditationTitle = itemView.findViewById(R.id.meditation_title);
            meditationDuration = itemView.findViewById(R.id.meditation_duration);
        }
    }
} 