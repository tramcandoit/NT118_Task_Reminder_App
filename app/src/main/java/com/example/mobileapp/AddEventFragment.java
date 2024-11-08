package com.example.mobileapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEventFragment extends DialogFragment {
    private EditText etEventName;
    private EditText etDate;
    private Spinner spFrequency;
    private EditText etDescription;
    OnEventAddedListener listener;
    private EventDatabaseHandler event_db;

    public static AddEventFragment newInstance(OnEventAddedListener listener) {
        AddEventFragment fragment = new AddEventFragment();
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


        // Khởi tạo biến Event
        final Event event = new Event();
        // Tạo AlertDialog để hiển thị chi tiết thông tin
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // Tạo layout cho AlertDialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_addevent, null);

        // Ánh xạ
        etEventName = dialogView.findViewById(R.id.tv_addevent_addevent_textbox);
        etDate = dialogView.findViewById(R.id.et_addevent_date_selector);

        etDescription = dialogView.findViewById(R.id.et_addevent_description);

        // Frequency Spinner
        spFrequency = dialogView.findViewById(R.id.sp_addevent_frequency_selector);
        List<String> frequencies = new ArrayList<>();
        frequencies.add("Weekly");
        frequencies.add("Monthly");
        frequencies.add("Once");
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(getContext(), R.layout.addevent_spinner_item_text, frequencies);
        frequencyAdapter.setDropDownViewResource(R.layout.addevent_spinner_item_text);
        spFrequency.setAdapter(frequencyAdapter);
        spFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Lay thoi gian hien tai
        final Calendar calendar = Calendar.getInstance();

        // Định dạng ngày dd/mm/yyyy
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Chuyển ngày hiện tại thành chuỗi và gán cho etDate
        String currentDate = sdf.format(calendar.getTime());
        // Gán vào edit text chọn ngày
        etDate.setText(currentDate);

        etDate.setOnClickListener(v -> {

            // Mở DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {

                        calendar.set(year, monthOfYear, dayOfMonth);
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                        // Đặt ngày đã chọn vào etDate
                        etDate.setText(selectedDate);
                    },
                    // Đặt ngày ban đầu cho DatePickerDialog (ngày hiện tại)
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            );

            // Đặt nút Lưu trong DatePickerDialog
            datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Save", datePickerDialog);

            // Đặt nút Hủy trong DatePickerDialog
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> dialog.cancel());

            // Hiển thị DatePickerDialog
            datePickerDialog.show();
        });




        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, id) -> {
                    event.setName(etEventName.getText().toString());
                    event.setDate(etDate.getText().toString());
                    event.setRepeat_frequency(spFrequency.getSelectedItem().toString());
                    event.setDescription(etDescription.getText().toString());

                    // Thêm vào danh sách tasks ở HomeFragment
                    if (listener != null) {
                        listener.onEventAdded(event);

                    }

                    //Sau khi lưu, đóng dialog
                    dismiss();

                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    AddEventFragment.this.getDialog().cancel();
                });

        return builder.create();
    }
}