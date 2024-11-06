package com.example.mobileapp;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addtask, container, false);

        etName = view.findViewById(R.id.tv_addtask_addtask_textbox);
        spCategories = view.findViewById(R.id.sp_addtask_categories);
        etDate = view.findViewById(R.id.et_addtask_date_selector);
        etTime = view.findViewById(R.id.et_addtask_time_selector);
        spPriority = view.findViewById(R.id.sp_addtask_priority);
        spFrequency = view.findViewById(R.id.sp_addtask_frequency_selector);
        etDescription = view.findViewById(R.id.et_addtask_description);

        return view;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_addtask, null);



        // Setup các Spinner
        // Categories Spinner
        spCategories = view.findViewById(R.id.sp_addtask_categories);
        etName = view.findViewById(R.id.tv_addtask_addtask_textbox);


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
                CategoriesItem selectedCategory = (CategoriesItem) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "Selected Category: " + selectedCategory.getLabel(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Priority Spinner
        spPriority = view.findViewById(R.id.sp_addtask_priority);
        List<String> priorities = new ArrayList<>();
        priorities.add("High");
        priorities.add("Medium");
        priorities.add("Low");

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, priorities);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(priorityAdapter);

        spPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPriority = (String) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "Selected Priority: " + selectedPriority, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Frequency Spinner
        spFrequency = view.findViewById(R.id.sp_addtask_frequency_selector);
        List<String> frequencies = new ArrayList<>();
        frequencies.add("Daily");
        frequencies.add("Weekly");
        frequencies.add("Monthly");

        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, frequencies);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrequency.setAdapter(frequencyAdapter);

        spFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFrequency = (String) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "Selected Frequency: " + selectedFrequency, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        builder.setView(view)
                .setPositiveButton("Lưu", (dialog, id) -> {
                    // Code for saving task
                })
                .setNegativeButton("Hủy", (dialog, id) -> dialog.cancel());

        return builder.create();
    }
}

