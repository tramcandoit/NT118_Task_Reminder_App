package com.example.mobileapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

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

        // ... (Ánh xạ View)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_addtask, null);


        // ... (ánh xạ view và các xử lý khác)

        builder.setView(view)
                .setPositiveButton("Lưu", (dialog, id) -> {
                    // Lấy dữ liệu từ các trường EditText, Spinner
                    // ...
                })
                .setNegativeButton("Hủy", (dialog, id) -> {
                    AddTaskFragment.this.getDialog().cancel();
                });

        return builder.create();
    }
}