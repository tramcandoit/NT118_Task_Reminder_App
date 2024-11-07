package com.example.mobileapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.widget.ArrayAdapter;
import com.example.mobileapp.Database.TaskDatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class AddTaskFragment extends DialogFragment {
    EditText etName;
    Spinner spCategories;
    EditText etDate;
    EditText etTime;
    Spinner spPriority;
    Spinner spFrequency;
    EditText etDescription;

    TaskDatabaseHandler db;
    List<Task> tasks;
    ArrayList<Task> taskArrayList;
    TasksArrayAdapter taskAdapter;

    OnTaskAddedListener listener;

    public static AddTaskFragment newInstance(OnTaskAddedListener listener) {
        AddTaskFragment fragment = new AddTaskFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Khởi tạo biến Task
        final Task task = new Task();

        // Tạo AlertDialog để hiển thị chi tiết thông tin
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Tạo layout cho AlertDialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_addtask, null);

        // ... (ánh xạ dialogView và các xử lý khác)
        etName = dialogView.findViewById(R.id.tv_addtask_addtask_textbox);
        spCategories = dialogView.findViewById(R.id.sp_addtask_categories);
        etDate = dialogView.findViewById(R.id.et_addtask_date_selector);
        etTime = dialogView.findViewById(R.id.et_addtask_time_selector);
        spPriority = dialogView.findViewById(R.id.sp_addtask_priority);
        spFrequency = dialogView.findViewById(R.id.sp_addtask_frequency_selector);
        etDescription = dialogView.findViewById(R.id.et_addtask_description);


        // Categories spinner
        List<CategoriesItem> categories = new ArrayList<>();
        categories.add(new CategoriesItem(R.drawable.icon_user, "Work"));
        categories.add(new CategoriesItem(R.drawable.icon_user, "Work1"));
        categories.add(new CategoriesItem(R.drawable.icon_user, "Work2"));
        categories.add(new CategoriesItem(R.drawable.icon_user, "Work3"));
        SpinnerAdapter categoryAdapter = new SpinnerAdapter(getContext(), categories);
        spCategories.setAdapter(categoryAdapter);
        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                CategoriesItem selectedCategory = (CategoriesItem) parent.getItemAtPosition(position);
//                Toast.makeText(getContext(), "Selected Category: " + selectedCategory.getLabel(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Priority Spinner
        spPriority = dialogView.findViewById(R.id.sp_addtask_priority);
        List<String> priorities = new ArrayList<>();
        priorities.add("High");
        priorities.add("Medium");
        priorities.add("Low");
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getContext(), R.layout.addtask_spinner_item_text, priorities);
        priorityAdapter.setDropDownViewResource(R.layout.addtask_spinner_item_text);
        spPriority.setAdapter(priorityAdapter);
        spPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedPriority = (String) parent.getItemAtPosition(position);
//                Toast.makeText(getContext(), "Selected Priority: " + selectedPriority, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Frequency Spinner
        spFrequency = dialogView.findViewById(R.id.sp_addtask_frequency_selector);
        List<String> frequencies = new ArrayList<>();
        frequencies.add("Daily");
        frequencies.add("Weekly");
        frequencies.add("Monthly");
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(getContext(), R.layout.addtask_spinner_item_text, frequencies);
        frequencyAdapter.setDropDownViewResource(R.layout.addtask_spinner_item_text);
        spFrequency.setAdapter(frequencyAdapter);
        spFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedFrequency = (String) parent.getItemAtPosition(position);
//                Toast.makeText(getContext(), "Selected Frequency: " + selectedFrequency, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, id) -> {
                    // Lấy dữ liệu từ các trường EditText, Spinner
                    // Lấy dữ liệu từ các trường EditText, Spinner
                    task.setName(etName.getText().toString());
                    //task.setCategoryId(spCategories.getSelectedItemPosition());
                    task.setDate(etDate.getText().toString());
                    task.setTime(etTime.getText().toString());
                    //task.setPriority(spPriority.getSelectedItem().toString());
                    //task.setRepeat_frequency(spFrequency.getSelectedItem().toString());
                    task.setDescription(etDescription.getText().toString());

                    // Thêm vào Database
                    // db.addTask(task);

                    // Thêm vào danh sách tasks ở HomeFragment
                    if (listener != null) {
                        listener.onTaskAdded(task);
                    }

                    // Sau khi lưu, đóng dialog
                    dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    AddTaskFragment.this.getDialog().cancel();
                });

        return builder.create();
    }
}