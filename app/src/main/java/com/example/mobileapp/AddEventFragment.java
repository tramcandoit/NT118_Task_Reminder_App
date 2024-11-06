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

public class AddEventFragment extends DialogFragment {
    EditText etEventName;
    EditText etDate;
    Spinner spPriority;
    Spinner spFrequency;
    EditText etDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addevent, container, false);

        // ... (Ánh xạ View)
        etEventName = view.findViewById(R.id.tv_addevent_addevent_textbox);
        etDate = view.findViewById(R.id.et_addevent_date_selector);
        spPriority = view.findViewById(R.id.sp_addevent_priority);
        spFrequency = view.findViewById(R.id.sp_addevent_frequency_selector);
        etDescription = view.findViewById(R.id.et_addevent_description);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_addevent, null);

        // ... (ánh xạ view và các xử lý khác)
        builder.setView(view)
                .setPositiveButton("Save", (dialog, id) -> {
                    // Lấy dữ liệu từ các trường EditText, Spinner
                    // ...
                    String eventName = etEventName.getText().toString();
                    String date = etDate.getText().toString();
                    String priority = spPriority.getSelectedItem().toString();
                    String frequency = spFrequency.getSelectedItem().toString();
                    String description = etDescription.getText().toString();

                    
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    AddEventFragment.this.getDialog().cancel();
                });

        return builder.create();
    }
}
