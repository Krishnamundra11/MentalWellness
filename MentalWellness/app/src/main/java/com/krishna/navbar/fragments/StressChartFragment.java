package com.krishna.navbar.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.krishna.navbar.R;
import com.krishna.navbar.models.ScoreDataPoint;
import com.krishna.navbar.utils.FirestoreHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StressChartFragment extends Fragment {

    // UI Components
    private TextView dateRangeText;
    private TextView stressLevelValue;
    private LinearLayout emojiContainer;
    private LineChart lineChart;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirestoreHelper firestoreHelper;

    // Data
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private List<ScoreDataPoint> dataPoints = new ArrayList<>();
    private final String[] dayLabels = {"M", "T", "W", "T", "F", "S", "S"};
    private final Map<String, Integer> dayIndexMap = new HashMap<String, Integer>() {{
        put("Monday", 0);
        put("Tuesday", 1);
        put("Wednesday", 2);
        put("Thursday", 3);
        put("Friday", 4);
        put("Saturday", 5);
        put("Sunday", 6);
    }};
    
    private final String[] emojiForStress = {
        "😀", // Very low stress
        "🙂", // Low stress
        "😐", // Moderate stress
        "😟", // High stress
        "😫"  // Very high stress
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stress_chart, container, false);
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        firestoreHelper = new FirestoreHelper();
        
        if (currentUser == null) {
            Toast.makeText(getContext(), "Please log in to view your statistics", Toast.LENGTH_SHORT).show();
            return view;
        }
        
        // Initialize UI components
        dateRangeText = view.findViewById(R.id.stressDateRange);
        stressLevelValue = view.findViewById(R.id.stressLevelValue);
        emojiContainer = view.findViewById(R.id.emojiContainer);
        
        // Create and add LineChart
        lineChart = new LineChart(getContext());
        ViewGroup chartContainer = view.findViewById(R.id.lineChartContainer);
        chartContainer.addView(lineChart);
        
        // Set up date range (last 7 days by default)
        setupDateRange();
        
        // Set up chart
        setupChart();
        
        // Update date range text
        updateDateText();
        
        // Load data
        loadData();
        
        return view;
    }
    
    private void setupDateRange() {
        // Set end date to today
        endDate = Calendar.getInstance();
        
        // Set start date to 7 days ago
        startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -6); // 7 days including today
    }
    
    private void updateDateText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        String dateRange = dateFormat.format(startDate.getTime()) + " - " + dateFormat.format(endDate.getTime());
        dateRangeText.setText(dateRange);
    }
    
    private void setupChart() {
        // Basic chart setup
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        
        // Remove description
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
        
        // X-axis setup
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dayLabels));
        
        // Left Y-axis setup
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        
        // Custom ValueFormatter for Y axis labels
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "Low";
                if (value == 25) return "";
                if (value == 50) return "Moderate";
                if (value == 75) return "";
                if (value == 100) return "High";
                return "";
            }
        });
        
        // Right Y-axis setup (disabled)
        lineChart.getAxisRight().setEnabled(false);
        
        // Legend setup
        lineChart.getLegend().setEnabled(false);
    }
    
    private void loadData() {
        dataPoints.clear();
        
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        
        String startDateStr = dbDateFormat.format(startDate.getTime());
        String endDateStr = dbDateFormat.format(endDate.getTime());
        
        System.out.println("DEBUG: StressChartFragment loading data from " + startDateStr + " to " + endDateStr);
        
        firestoreHelper.getResponsesByDateRange("stress", startDateStr, endDateStr)
            .addOnSuccessListener(queryDocumentSnapshots -> {
                System.out.println("DEBUG: StressChartFragment retrieved " + queryDocumentSnapshots.size() + " documents");
                
                // Create a map of date to score for easy lookup
                Map<String, Integer> dateScoreMap = new HashMap<>();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    String date = document.getString("date");
                    Long scoreObj = document.getLong("score");
                    
                    if (date != null && scoreObj != null) {
                        dateScoreMap.put(date, scoreObj.intValue());
                        System.out.println("DEBUG: StressChartFragment found score " + scoreObj + " for date " + date);
                    } else {
                        System.out.println("DEBUG: StressChartFragment document missing date or score: " + document.getId());
                    }
                }
                
                // Create a copy of the start date for iteration
                Calendar currentDate = (Calendar) startDate.clone();
                
                // Populate data points for each day in the range
                int totalScore = 0;
                int daysWithData = 0;
                
                while (!currentDate.after(endDate)) {
                    String dateStr = dbDateFormat.format(currentDate.getTime());
                    String dayOfWeek = dayFormat.format(currentDate.getTime());
                    
                    // Get score for this date if available, or 0 if not
                    int score = dateScoreMap.getOrDefault(dateStr, 0);
                    
                    // Only count days with data for average calculation
                    if (score > 0) {
                        totalScore += score;
                        daysWithData++;
                    }
                    
                    dataPoints.add(new ScoreDataPoint(dateStr, score, dayOfWeek));
                    
                    // Move to next day
                    currentDate.add(Calendar.DAY_OF_MONTH, 1);
                }
                
                System.out.println("DEBUG: StressChartFragment found " + daysWithData + " days with data, total score: " + totalScore);
                
                // Update stress level text
                if (daysWithData > 0) {
                    int avgScore = totalScore / daysWithData;
                    updateStressLevel(avgScore);
                } else {
                    // No data available
                    stressLevelValue.setText("No data available");
                    System.out.println("DEBUG: StressChartFragment no data available");
                }
                
                // Update chart
                updateChart();
            })
            .addOnFailureListener(e -> {
                System.out.println("DEBUG: StressChartFragment error loading data: " + e.getMessage());
                Toast.makeText(getContext(), "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
    
    private void updateStressLevel(int avgScore) {
        String levelText;
        int color;
        
        if (avgScore < 30) {
            levelText = avgScore + "% (Low)";
            color = Color.GREEN;
        } else if (avgScore < 70) {
            levelText = avgScore + "% (Moderate)";
            color = Color.parseColor("#FFA500"); // Orange
        } else {
            levelText = avgScore + "% (High)";
            color = Color.RED;
        }
        
        stressLevelValue.setText(levelText);
        stressLevelValue.setTextColor(color);
    }
    
    private void updateChart() {
        List<Entry> entries = new ArrayList<>();
        
        // Create line entries for each day
        for (int i = 0; i < dataPoints.size(); i++) {
            ScoreDataPoint dataPoint = dataPoints.get(i);
            int dayIndex = dayIndexMap.getOrDefault(dataPoint.getDayOfWeek(), i);
            entries.add(new Entry(dayIndex, dataPoint.getScore()));
        }
        
        LineDataSet dataSet = new LineDataSet(entries, "Stress Score");
        
        // Format line chart
        dataSet.setColor(Color.parseColor("#FF6B6B"));
        dataSet.setCircleColor(Color.parseColor("#FF6B6B"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(false);
        dataSet.setHighlightEnabled(true);
        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setDrawVerticalHighlightIndicator(false);
        
        // Add the dataset to the chart
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        
        // Refresh chart
        lineChart.invalidate();
        
        // Update emoji indicators
        updateEmojiIndicators();
    }
    
    private void updateEmojiIndicators() {
        // Clear existing emojis
        emojiContainer.removeAllViews();
        
        // Add emoji for each data point
        for (int i = 0; i < dataPoints.size(); i++) {
            ScoreDataPoint dataPoint = dataPoints.get(i);
            
            TextView emojiView = new TextView(getContext());
            emojiView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            emojiView.setGravity(android.view.Gravity.CENTER);
            emojiView.setTextSize(24);
            
            // Only show emoji if score > 0
            if (dataPoint.getScore() > 0) {
                // Map score to emoji index
                int emojiIndex = Math.min((dataPoint.getScore() / 20), emojiForStress.length - 1);
                emojiView.setText(emojiForStress[emojiIndex]);
                emojiView.setVisibility(View.VISIBLE);
                
                // Position emoji vertically based on score value
                int topMargin = (int) ((100 - dataPoint.getScore()) / 100.0 * emojiContainer.getHeight()) - 60;
                if (topMargin < 0) topMargin = 0;
                
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) emojiView.getLayoutParams();
                params.topMargin = topMargin;
                emojiView.setLayoutParams(params);
            } else {
                emojiView.setVisibility(View.INVISIBLE);
            }
            
            emojiContainer.addView(emojiView);
        }
    }
} 