package com.krishna.navbar.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krishna.navbar.R;
import com.krishna.navbar.adapters.SelectTrackAdapter;
import com.krishna.navbar.models.Track;

import java.util.List;

public class SelectTracksDialog extends Dialog {

    private final List<Track> tracks;
    private final SelectTrackAdapter adapter;
    private final OnTracksSelectedListener listener;
    private final String dialogTitle;

    public interface OnTracksSelectedListener {
        void onTracksSelected(List<Track> selectedTracks);
    }

    public SelectTracksDialog(@NonNull Context context, List<Track> tracks, String dialogTitle, OnTracksSelectedListener listener) {
        super(context);
        this.tracks = tracks;
        this.listener = listener;
        this.dialogTitle = dialogTitle;
        this.adapter = new SelectTrackAdapter(context, tracks);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_tracks);

        TextView tvTitle = findViewById(R.id.tvDialogTitle);
        RecyclerView recyclerView = findViewById(R.id.rvSelectTracks);
        Button btnSelectAll = findViewById(R.id.btnSelectAll);
        Button btnClearAll = findViewById(R.id.btnClearAll);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnConfirm = findViewById(R.id.btnConfirm);

        tvTitle.setText(dialogTitle);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnSelectAll.setOnClickListener(v -> adapter.selectAllTracks());
        btnClearAll.setOnClickListener(v -> adapter.clearSelection());

        btnCancel.setOnClickListener(v -> dismiss());
        btnConfirm.setOnClickListener(v -> {
            List<Track> selectedTracks = adapter.getSelectedTracks();
            if (selectedTracks.size() > 0) {
                listener.onTracksSelected(selectedTracks);
                dismiss();
            }
        });
    }
} 