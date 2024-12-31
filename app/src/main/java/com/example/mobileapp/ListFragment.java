package com.example.mobileapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListFragment extends Fragment {

    private TaskDatabaseHandler taskDb;
    private PieChart pieChart;
    private BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false); // Inflate layout cho fragment

        pieChart = view.findViewById(R.id.pieChart); //  Lấy PieChart từ layout
        barChart = view.findViewById(R.id.barChart);
        taskDb = new TaskDatabaseHandler(requireContext()); // Khởi tạo database handler

        setupPieChart(); // Thiết lập biểu đồ
        loadPieChartData();  // Load dữ liệu cho biểu đồ

        setupBarChart();
        loadBarChartData(); // Load dữ liệu cho BarChart




        return view;
    }

    // --------------------------------------------------------------------------------------------- //
    private void setupPieChart() {
        pieChart.setUsePercentValues(true); // Hiển thị phần trăm
        pieChart.getDescription().setEnabled(false); // Ẩn mô tả
        pieChart.setExtraOffsets(5, 10, 5, 5); // Khoảng cách giữa biểu đồ và viền
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true); // Có lỗ ở giữa
        pieChart.setHoleColor(Color.WHITE); // Màu sắc của lỗ
        pieChart.setHoleRadius(30f);
        pieChart.setTransparentCircleRadius(41f);
    }

    private void loadPieChartData() {
        List<Task> tasksList = taskDb.getAllTasks();
        List<Task> incompleteTasks = new ArrayList<>();
        List<Task> completedTasks = new ArrayList<>();

        for (Task task : tasksList) {
            String status = task.getStatus();
            if (Objects.equals(task.getStatus(), "completed")) {
                completedTasks.add(task);
            } else {
                incompleteTasks.add(task);
            }
        }
        // Add some sample data for testing
        Task completedTask_1 = new Task();
        Task completedTask_2 = new Task();
        Task completedTask_3 = new Task();
        completedTask_1.setStatus("completed");
        completedTask_2.setStatus("completed");
        completedTask_3.setStatus("completed");
        completedTasks.add(completedTask_1);
        completedTasks.add(completedTask_2);
        completedTasks.add(completedTask_3);

        // Create pie entries for incomplete and completed tasks
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(incompleteTasks.size(), "Incomplete"));
        entries.add(new PieEntry(completedTasks.size(), "Completed"));

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        // Thiết lập cỡ chữ cho incomplete và completed trên biểu đồ (chữ màu trắng)
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(20f);
        dataSet.setValueTextColor(Color.WHITE);

        // Thiết lập cỡ chữ cho phần trăm trên biểu đồ (chữ màu đen)
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);

        // Thiết lập cỡ chữ cho phần chú thích dưới biểu đồ
        Legend legend = pieChart.getLegend();
        legend.setTextSize(20f);
        legend.setFormSize(15f);
        legend.setXEntrySpace(90f);
        legend.setFormToTextSpace(15f);

        pieChart.invalidate();
    }
    // --------------------------------------------------------------------------------------------- //
    private void setupBarChart() {
        barChart.getDescription().setEnabled(false); // Ẩn mô tả
        barChart.setPinchZoom(false); // Không cho phép zoom
        barChart.setDrawBarShadow(false); // Không vẽ bóng cho cột
        barChart.setDrawGridBackground(false); // Không vẽ grid background

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Đặt nhãn ngày ở dưới cùng
        xAxis.setGranularity(1f); // Khoảng cách giữa các nhãn
        xAxis.setDrawGridLines(false);


        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // Giá trị tối thiểu của trục y

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(18f);
        legend.setXEntrySpace(4f);
    }

    private void loadBarChartData() {

        List<Task> tasksList = taskDb.getAllTasks(); // Lấy tất cả task từ database

        HashMap<LocalDate, Integer> tasksByDate = new HashMap<>();
        for (Task task : tasksList) {
            LocalDate dueDate = null;
            String testDate = task.getDate();
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                dueDate = LocalDate.parse(task.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//            }
            tasksByDate.put(dueDate, tasksByDate.getOrDefault(dueDate, 0) + 1);
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int i = 0;
        for (Map.Entry<LocalDate, Integer> entry : tasksByDate.entrySet()) {
            entries.add(new BarEntry(i, entry.getValue()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                labels.add(entry.getKey().format(DateTimeFormatter.ofPattern("dd/MM")));
            }
            i++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Tasks");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Tùy chỉnh màu sắc
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(20f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.size());


        barChart.invalidate(); // Refresh biểu đồ
    }
    // --------------------------------------------------------------------------------------------- //
}