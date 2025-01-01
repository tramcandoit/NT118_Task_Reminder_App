package com.example.mobileapp;

import android.graphics.Color;
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
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

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

    private void loadBarChartData(int numberOfDays) {

        LocalDate currentDate = LocalDate.now();
        LocalDate sevenDaysAgo = currentDate.minusDays(numberOfDays -1) ;

        List<Task> tasksList = taskDb.getAllTasks();

        // Lọc task theo khoảng thời gian
        List<Task> filteredTasks = tasksList.stream()
                .filter(task -> {
                    try {
                        LocalDate dueDate = LocalDate.parse(task.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return dueDate.isAfter(sevenDaysAgo.minusDays(1)) && dueDate.isBefore(currentDate.plusDays(1));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        // Tạo map chứa dữ liệu cho numberOfDays ngày
        Map<LocalDate, Integer> tasksForDays = new TreeMap<>();

        for (int i = 0; i < numberOfDays; i++) {
            LocalDate date = currentDate.minusDays(i);
            tasksForDays.put(date, 0); // Khởi tạo số lượng task là 0 cho mỗi ngày
        }


        // Đếm số task cho mỗi ngày trong filteredTasks
        for (Task task : filteredTasks) {
            try {
                LocalDate dueDate = LocalDate.parse(task.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                tasksForDays.put(dueDate, tasksForDays.getOrDefault(dueDate, 0) + 1);
            }
            catch (Exception e) {
                // Do nothing
            }
        }


        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int i = 0;
        // Duyệt tasksForDays theo thứ tự (do đã được sắp xếp bởi TreeMap)
        for (Map.Entry<LocalDate, Integer> entry : tasksForDays.entrySet()) {
            entries.add(new BarEntry(i, entry.getValue()));
            labels.add(entry.getKey().format(DateTimeFormatter.ofPattern("dd/MM")));
            i++;
        }
        BarDataSet dataSet = new BarDataSet(entries, "Tasks");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Tùy chỉnh màu sắc
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(16f);
        dataSet.setValueFormatter(new DefaultValueFormatter(0));
        dataSet.setBarBorderWidth(0.9f); // Đặt độ dày cho đường viền cột

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f); // Đặt chiều rộng cột
        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.size());


        barChart.invalidate(); // Refresh biểu đồ
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocalDate firstUseDate = taskDb.getFirstTaskCreatedDate();
        LocalDate currentDate = LocalDate.now();

        setupPieChart();
        loadPieChartData();

        setupBarChart();
        if (firstUseDate == null) {
            // Người dùng mới cài app
            barChart.clear(); // Xóa dữ liệu hiện tại của biểu đồ (nếu có)
            barChart.setNoDataText("Chưa có task nào trong khoảng thời gian này."); // Hiển thị thông báo trên biểu đồ
            barChart.invalidate(); // Refresh biểu đồ

        }
        else {
            long daysSinceFirstUse = ChronoUnit.DAYS.between(firstUseDate, currentDate) + 1;
            // Biểu đồ thống kê số lượng task trong 7 ngày
            loadBarChartData(7);

        }

    }
    // --------------------------------------------------------------------------------------------- //
}