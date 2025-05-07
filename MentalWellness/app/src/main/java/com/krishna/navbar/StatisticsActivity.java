package com.krishna.navbar;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.krishna.navbar.models.ScoreDataPoint;
import com.krishna.navbar.utils.FirestoreHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    // UI Components
    private TabLayout tabLayout;
    private TextView metricTitle, metricValue, metricTotalText;
    private TextView dateRangeText;
    private TextView levelTitle, levelValue;
    private TextView activityTitle;
    private ImageButton backButton;
    private BarChart barChart;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirestoreHelper firestoreHelper;

    // Data
    private String currentCategory = "stress"; // Default category
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        firestoreHelper = new FirestoreHelper();

        if (currentUser == null) {
            Toast.makeText(this, "Please log in to view your statistics", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Check for data in Firestore
        checkFirestoreData();

        // Check if category was passed from intent
        if (getIntent().hasExtra("category")) {
            currentCategory = getIntent().getStringExtra("category");
        }

        // Set up date range (last 7 days by default)
        setupDateRange();

        // Initialize UI
        initializeViews();
        
        // Set up listeners
        setupListeners();
        
        // Load data for current category
        updateUI();
        loadData();
    }
    
    /**
     * Check what data exists in Firestore for this user
     */
    private void checkFirestoreData() {
        // Check all user data
        db.collection("users")
            .document(currentUser.getUid())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    System.out.println("DEBUG: User document exists: " + documentSnapshot.getId());
                } else {
                    System.out.println("DEBUG: User document does not exist");
                }
            });
            
        // Check questionnaires collection
        db.collection("users")
            .document(currentUser.getUid())
            .collection("questionnaires")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                System.out.println("DEBUG: Found " + querySnapshot.size() + " questionnaire documents");
                
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    System.out.println("DEBUG: Questionnaire document: " + doc.getId() + 
                        ", category: " + doc.getString("category") + 
                        ", date: " + doc.getString("date") + 
                        ", score: " + doc.getLong("score"));
                }
            })
            .addOnFailureListener(e -> {
                System.out.println("DEBUG: Error checking questionnaires: " + e.getMessage());
            });
    }

    private void initializeViews() {
        // Header
        backButton = findViewById(R.id.backButton);
        activityTitle = findViewById(R.id.activityTitle);
        
        // Tab layout
        tabLayout = findViewById(R.id.tabLayout);
        
        // Metric section
        metricTitle = findViewById(R.id.metricTitle);
        metricValue = findViewById(R.id.metricValue);
        metricTotalText = findViewById(R.id.metricTotalText);
        dateRangeText = findViewById(R.id.dateRangeText);
        
        // Chart
        barChart = new BarChart(this);
        // Cast to FrameLayout since that's what's used in the XML
        FrameLayout chartContainer = (FrameLayout) findViewById(R.id.chartContainer);
        chartContainer.addView(barChart);
        setupChart();
        
        // Statistics
        levelTitle = findViewById(R.id.levelTitle);
        levelValue = findViewById(R.id.levelValue);
        
        // Set title and initial tab selection based on category
        activityTitle.setText("Activity");
        
        // Set the appropriate tab as selected
        switch (currentCategory) {
            case "stress":
                tabLayout.getTabAt(0).select();
                break;
            case "sleep":
                tabLayout.getTabAt(1).select();
                break;
            case "academic":
                tabLayout.getTabAt(2).select();
                break;
        }
    }
    
    private void setupListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());
        
        // Tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentCategory = "stress";
                        break;
                    case 1:
                        currentCategory = "sleep";
                        break;
                    case 2:
                        currentCategory = "academic";
                        break;
                }
                updateUI();
                loadData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        
        // Date range selection
        dateRangeText.setOnClickListener(v -> showDateRangePicker());
    }
    
    private void setupDateRange() {
        // Set end date to today
        endDate = Calendar.getInstance();
        
        // Set start date to 7 days ago
        startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -6); // 7 days including today
        
        // Make sure we're using proper time (start of day for start date, end of day for end date)
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);
        
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        endDate.set(Calendar.MILLISECOND, 999);
    }
    
    private void setupChart() {
        // Basic chart setup
        barChart.setOnChartValueSelectedListener(this);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(7);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        
        // Remove description
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        
        // X-axis setup
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dayLabels));
        
        // Y-axis setup
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);
        
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
    }
    
    private void updateUI() {
        // Update UI based on the current category
        switch (currentCategory) {
            case "stress":
                metricTitle.setText("Stress Score");
                metricTotalText.setText("Avg Score");
                levelTitle.setText("Stress Level");
                break;
                
            case "sleep":
                metricTitle.setText("Sleep Duration");
                metricTotalText.setText("Total Time");
                levelTitle.setText("Sleep Level");
                break;
                
            case "academic":
                metricTitle.setText("Academic Score");
                metricTotalText.setText("Avg Score");
                levelTitle.setText("Academic Level");
                break;
        }
        
        // Update date range text
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        String dateRange = dateFormat.format(startDate.getTime()) + " - " + dateFormat.format(endDate.getTime());
        dateRangeText.setText(dateRange);
    }
    
    private void loadData() {
        // Show loading state
        metricValue.setText("Loading...");
        levelValue.setText("Loading...");
        
        // Clear existing data
        dataPoints.clear();
        if (barChart.getData() != null) {
            barChart.clear();
        }
        
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        
        // Create a copy of the start date for iteration
        Calendar currentDate = (Calendar) startDate.clone();
        
        // Log the date range we're requesting
        String startDateStr = dbDateFormat.format(startDate.getTime());
        String endDateStr = dbDateFormat.format(endDate.getTime());
        System.out.println("DEBUG: Loading data for " + currentCategory + 
                           " from " + startDateStr + " to " + endDateStr);
        
        // Use FirestoreHelper instead of direct Firestore calls
        firestoreHelper.getResponsesByDateRange(currentCategory, startDateStr, endDateStr)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    try {
                        // Log how many documents were found
                        System.out.println("DEBUG: Retrieved " + queryDocumentSnapshots.size() + " documents");
                        
                        // Create a map of date to score for easy lookup
                        Map<String, Integer> dateScoreMap = new HashMap<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String date = document.getString("date");
                            Long scoreObj = document.getLong("score");
                            
                            if (date != null && scoreObj != null) {
                                // Only include dates within our desired range
                                if (date.compareTo(startDateStr) >= 0 && date.compareTo(endDateStr) <= 0) {
                                    dateScoreMap.put(date, scoreObj.intValue());
                                    System.out.println("DEBUG: Found score " + scoreObj + " for date " + date);
                                }
                            } else {
                                System.out.println("DEBUG: Document missing date or score: " + document.getId());
                            }
                        }
                        
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
                        
                        System.out.println("DEBUG: Found " + daysWithData + " days with data, total score: " + totalScore);
                        
                        // Update metric value based on category
                        if (daysWithData > 0) {
                            int avgScore = totalScore / daysWithData;
                            
                            if (currentCategory.equals("sleep")) {
                                // For sleep, we show total hours
                                metricValue.setText(totalScore / 100 * 8 + " hours"); // Approximating 8 hours per 100%
                            } else {
                                // For academic and stress, we show average score
                                metricValue.setText(String.valueOf(avgScore));
                            }
                            
                            // Update level text
                            updateLevelText(avgScore);
                        } else {
                            // No data available
                            metricValue.setText("No data");
                            levelValue.setText("No data available");
                        }
                        
                        // Update chart
                        updateChart();
                        
                    } catch (Exception e) {
                        System.out.println("DEBUG: Error processing data: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(this, "Error processing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        
                        // Reset UI to error state
                        metricValue.setText("Error");
                        levelValue.setText("Error loading data");
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("DEBUG: Error loading data: " + e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    
                    // Reset UI to error state
                    metricValue.setText("Error");
                    levelValue.setText("Error loading data");
                });
    }
    
    private void updateLevelText(int avgScore) {
        String levelText;
        int color;
        
        switch (currentCategory) {
            case "stress":
                if (avgScore < 30) {
                    levelText = avgScore + "% Low";
                    color = Color.GREEN;
                } else if (avgScore < 70) {
                    levelText = avgScore + "% Moderate";
                    color = Color.parseColor("#FFA500"); // Orange
                } else {
                    levelText = avgScore + "% High";
                    color = Color.RED;
                }
                break;
                
            case "sleep":
                if (avgScore < 30) {
                    levelText = avgScore + "% Poor";
                    color = Color.RED;
                } else if (avgScore < 70) {
                    levelText = avgScore + "% Normal";
                    color = Color.parseColor("#FFA500"); // Orange
                } else {
                    levelText = avgScore + "% Excellent";
                    color = Color.GREEN;
                }
                break;
                
            case "academic":
                if (avgScore < 30) {
                    levelText = avgScore + "% Needs Improvement";
                    color = Color.RED;
                } else if (avgScore < 70) {
                    levelText = avgScore + "% Good";
                    color = Color.parseColor("#FFA500"); // Orange
                } else {
                    levelText = avgScore + "% Excellent";
                    color = Color.GREEN;
                }
                break;
                
            default:
                levelText = avgScore + "%";
                color = Color.GRAY;
        }
        
        levelValue.setText(levelText);
        levelValue.setTextColor(color);
    }
    
    private void updateChart() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();
        
        // Create bar entries for each day in chronological order
        for (int i = 0; i < dataPoints.size(); i++) {
            ScoreDataPoint dataPoint = dataPoints.get(i);
            entries.add(new BarEntry(i, dataPoint.getScore()));
            
            // Format the date for X-axis label (e.g., "5/2")
            try {
                SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = dbFormat.parse(dataPoint.getDate());
                SimpleDateFormat displayFormat = new SimpleDateFormat("M/d", Locale.getDefault());
                xAxisLabels.add(displayFormat.format(date));
            } catch (ParseException e) {
                xAxisLabels.add(dataPoint.getDate()); // Fallback to raw date
            }
        }
        
        // Update X-axis with proper date labels
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(45f);
        
        // Show all dates in the selected period
        xAxis.setLabelCount(xAxisLabels.size(), false);
        
        // Set up Y-axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        
        // Disable right Y-axis
        barChart.getAxisRight().setEnabled(false);
        
        // Create and style the dataset
        BarDataSet dataSet;
        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            dataSet = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            dataSet.setValues(entries);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            dataSet = new BarDataSet(entries, "Scores");
            dataSet.setDrawValues(true);
            dataSet.setValueTextSize(10f);
            dataSet.setValueTextColor(Color.BLACK);
            
            // Set bar colors based on category
            int[] colors = new int[entries.size()];
            for (int i = 0; i < entries.size(); i++) {
                int score = (int) entries.get(i).getY();
                switch (currentCategory) {
                    case "stress":
                        colors[i] = score > 70 ? Color.RED : score > 40 ? Color.YELLOW : Color.GREEN;
                        break;
                    case "sleep":
                        colors[i] = score > 70 ? Color.GREEN : score > 40 ? Color.YELLOW : Color.RED;
                        break;
                    case "academic":
                        colors[i] = score > 70 ? Color.GREEN : score > 40 ? Color.YELLOW : Color.RED;
                        break;
                }
            }
            dataSet.setColors(colors);
            
            BarData data = new BarData(dataSet);
            data.setBarWidth(0.7f);
            barChart.setData(data);
        }
        
        // Configure chart appearance
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setScaleEnabled(true);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        
        // Remove description
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        
        // Add animation
        barChart.animateY(1000);
        
        // Refresh chart
        barChart.invalidate();
    }
    
    private void showDateRangePicker() {
        // Store backup of current dates in case user cancels
        final Calendar backupStartDate = (Calendar) startDate.clone();
        final Calendar backupEndDate = (Calendar) endDate.clone();
        
        // Create a dialog for date range selection
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_date_range, null);
        final AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Select Date Range")
            .setView(dialogView)
            .setPositiveButton("Apply", null) // We'll set this later to prevent auto-dismiss
            .setNegativeButton("Cancel", (dialogInterface, i) -> {
                // Restore backup dates if cancelled
                startDate = backupStartDate;
                endDate = backupEndDate;
            })
            .create();
        
        // Get current date for max bounds
        final Calendar now = Calendar.getInstance();
        
        // Quick selection buttons
        Button btnLast7Days = dialogView.findViewById(R.id.btn_last_7_days);
        Button btnLast14Days = dialogView.findViewById(R.id.btn_last_14_days);
        Button btnLast30Days = dialogView.findViewById(R.id.btn_last_30_days);
        Button btnThisMonth = dialogView.findViewById(R.id.btn_this_month);
        
        TextView tvStartDate = dialogView.findViewById(R.id.tv_start_date);
        TextView tvEndDate = dialogView.findViewById(R.id.tv_end_date);
        
        // Set initial values
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        tvStartDate.setText(dateFormat.format(startDate.getTime()));
        tvEndDate.setText(dateFormat.format(endDate.getTime()));
        
        // Start date picker
        tvStartDate.setOnClickListener(v -> {
            DatePickerDialog startPicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar newStartDate = Calendar.getInstance();
                    newStartDate.set(year, month, dayOfMonth, 0, 0, 0);
                    
                    // Validate that start date is not after end date
                    if (newStartDate.after(endDate)) {
                        Toast.makeText(this, "Start date cannot be after end date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Validate that start date is not in the future
                    if (newStartDate.after(now)) {
                        Toast.makeText(this, "Start date cannot be in the future", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    startDate = newStartDate;
                    tvStartDate.setText(dateFormat.format(startDate.getTime()));
                },
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH)
            );
            
            // Set max date to end date or today, whichever is earlier
            startPicker.getDatePicker().setMaxDate(Math.min(endDate.getTimeInMillis(), now.getTimeInMillis()));
            startPicker.show();
        });
        
        // End date picker
        tvEndDate.setOnClickListener(v -> {
            DatePickerDialog endPicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar newEndDate = Calendar.getInstance();
                    newEndDate.set(year, month, dayOfMonth, 23, 59, 59);
                    
                    // Validate that end date is not before start date
                    if (newEndDate.before(startDate)) {
                        Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Validate that end date is not in the future
                    if (newEndDate.after(now)) {
                        Toast.makeText(this, "End date cannot be in the future", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    endDate = newEndDate;
                    tvEndDate.setText(dateFormat.format(endDate.getTime()));
                },
                endDate.get(Calendar.YEAR),
                endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DAY_OF_MONTH)
            );
            
            // Set min date to start date
            endPicker.getDatePicker().setMinDate(startDate.getTimeInMillis());
            // Set max date to today
            endPicker.getDatePicker().setMaxDate(now.getTimeInMillis());
            endPicker.show();
        });
        
        // Quick selection buttons
        btnLast7Days.setOnClickListener(v -> {
            updateDateRange(7, tvStartDate, tvEndDate, dateFormat);
        });
        
        btnLast14Days.setOnClickListener(v -> {
            updateDateRange(14, tvStartDate, tvEndDate, dateFormat);
        });
        
        btnLast30Days.setOnClickListener(v -> {
            updateDateRange(30, tvStartDate, tvEndDate, dateFormat);
        });
        
        btnThisMonth.setOnClickListener(v -> {
            // Set end date to today
            endDate = Calendar.getInstance();
            endDate.set(Calendar.HOUR_OF_DAY, 23);
            endDate.set(Calendar.MINUTE, 59);
            endDate.set(Calendar.SECOND, 59);
            
            // Set start date to first day of current month
            startDate = Calendar.getInstance();
            startDate.set(Calendar.DAY_OF_MONTH, 1);
            startDate.set(Calendar.HOUR_OF_DAY, 0);
            startDate.set(Calendar.MINUTE, 0);
            startDate.set(Calendar.SECOND, 0);
            
            tvStartDate.setText(dateFormat.format(startDate.getTime()));
            tvEndDate.setText(dateFormat.format(endDate.getTime()));
        });
        
        dialog.show();
        
        // Override the positive button to prevent dialog from auto-dismissing
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            // Validate date range before applying
            if (startDate.after(endDate)) {
                Toast.makeText(this, "Invalid date range", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Apply the date range selection and close dialog
            updateUI();
            loadData();
            dialog.dismiss();
        });
    }
    
    private void updateDateRange(int days, TextView tvStartDate, TextView tvEndDate, SimpleDateFormat dateFormat) {
        // Set end date to today
        endDate = Calendar.getInstance();
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        
        // Set start date to N days ago
        startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -(days - 1));
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        
        tvStartDate.setText(dateFormat.format(startDate.getTime()));
        tvEndDate.setText(dateFormat.format(endDate.getTime()));
    }

    @Override
    public void onValueSelected(com.github.mikephil.charting.data.Entry e, Highlight h) {
        // Handle chart value selection - show detailed information
        if (e instanceof BarEntry) {
            int index = (int) e.getX();
            
            // Find the corresponding data point
            if (index >= 0 && index < dataPoints.size()) {
                ScoreDataPoint dataPoint = dataPoints.get(index);
                int score = (int) e.getY();
                
                // Format the date for display
                String formattedDate = "";
                try {
                    SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = dbFormat.parse(dataPoint.getDate());
                    SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
                    formattedDate = displayFormat.format(date);
                } catch (ParseException ex) {
                    formattedDate = dataPoint.getDate();
                }
                
                // Create and show detailed dialog
                View detailView = getLayoutInflater().inflate(R.layout.dialog_score_detail, null);
                TextView tvDate = detailView.findViewById(R.id.tv_detail_date);
                TextView tvCategory = detailView.findViewById(R.id.tv_detail_category);
                TextView tvScore = detailView.findViewById(R.id.tv_detail_score);
                ProgressBar progressBar = detailView.findViewById(R.id.progress_detail);
                
                // Set values
                tvDate.setText(formattedDate);
                tvCategory.setText(currentCategory.substring(0, 1).toUpperCase() + currentCategory.substring(1));
                tvScore.setText(score + "%");
                progressBar.setProgress(score);
                
                // Set appropriate color based on score and category
                int color;
                switch (currentCategory) {
                    case "stress":
                        color = score > 70 ? Color.RED : score > 40 ? Color.YELLOW : Color.GREEN;
                        break;
                    case "sleep":
                        color = score > 70 ? Color.GREEN : score > 40 ? Color.YELLOW : Color.RED;
                        break;
                    case "academic":
                        color = score > 70 ? Color.GREEN : score > 40 ? Color.YELLOW : Color.RED;
                        break;
                    default:
                        color = Color.GRAY;
                }
                
                // Apply color to progress bar
                progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                tvScore.setTextColor(color);
                
                // Show dialog
                new AlertDialog.Builder(this)
                    .setTitle("Score Details")
                    .setView(detailView)
                    .setPositiveButton("OK", null)
                    .show();
            }
        }
    }

    @Override
    public void onNothingSelected() {
        // Handle nothing selected case
    }
} 