package com.example.mobileapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTaskFragment extends DialogFragment {
    EditText etName;
    Spinner spCategories;
    EditText etDate;
    EditText etTime;
    Spinner spPriority;
    Spinner spFrequency;
    EditText etDescription;


    TaskDatabaseHandler db;
    CategoryDatabaseHandler categoryDb;
    List<CategoriesItem> categories;
    List<Task> tasks;
    ArrayList<Task> taskArrayList;
    TasksArrayAdapter taskAdapter;

    OnTaskAddedListener listener;

    public static AddTaskFragment newInstance(OnTaskAddedListener listener) {
        AddTaskFragment fragment = new AddTaskFragment();
        fragment.listener = listener;
        return fragment;
    }

    private void scheduleNotification(Task task) {
        Context context = requireContext();
        Intent intent = new Intent(context, TaskNotificationReceiver.class);
        intent.putExtra("taskName", task.getName());
        intent.putExtra("taskDescription", task.getDescription());
        intent.putExtra("taskId", task.getTaskId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                task.getTaskId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()); // Kết hợp date và time
            // Kết hợp ngày và giờ từ EditText
            String dateTimeString = task.getDate() + " " + task.getTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(dateTimeString)); // Parse cả ngày và giờ

            switch (task.getRepeat_frequency()) {
                case "Once":
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent);
                    break;
                case "Daily":
                    scheduleRepeatingNotification(
                            alarmManager,
                            pendingIntent,
                            calendar,
                            task.getRepeat_frequency()
                    );
                    break;
                case "Weekly":
                    scheduleRepeatingNotification(
                            alarmManager,
                            pendingIntent,
                            calendar,
                            task.getRepeat_frequency()
                    );
                    break;
            }
        } catch (ParseException e) {
            // Xử lý ParseException
            Toast.makeText(getContext(), "Invalid date/time format", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void scheduleRepeatingNotification(AlarmManager alarmManager, PendingIntent pendingIntent, Calendar calendar, String frequency) {
        long interval;
        if ("Daily".equals(frequency)) {
            interval = AlarmManager.INTERVAL_DAY;
        } else if ("Weekly".equals(frequency)) {
            interval = AlarmManager.INTERVAL_DAY * 7;
        } else {
            return; // Không hỗ trợ tần suất khác
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );

        calendar.add(Calendar.MILLISECOND, (int) interval); // Cập nhật thời gian cho lần lặp tiếp theo
        AlarmManager finalAlarmManager = alarmManager;
        new android.os.Handler().postDelayed(() -> {
            scheduleRepeatingNotification(finalAlarmManager, pendingIntent, calendar, frequency);
        }, interval);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        db = new TaskDatabaseHandler(requireContext());
        categoryDb = new CategoryDatabaseHandler(requireContext());

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

        // ----------------------------------------------------------------------------------------- //
        // Categories spinner
        categories = new ArrayList<>();
        categories = categoryDb.getAllCategories();

        SpinnerAdapter categoryAdapter = new SpinnerAdapter(getContext(), categories);
        spCategories.setAdapter(categoryAdapter);
        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < categories.size()) {
                    CategoriesItem selectedCategory = categories.get(position); // Lấy trực tiếp từ danh sách categories
                    Toast.makeText(getContext(), "Selected Category: " + selectedCategory.getName(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ----------------------------------------------------------------------------------------- //
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

        // ----------------------------------------------------------------------------------------- //
        // Frequency Spinner
        spFrequency = dialogView.findViewById(R.id.sp_addtask_frequency_selector);
        List<String> frequencies = new ArrayList<>();
        frequencies.add("Daily");
        frequencies.add("Weekly");
        frequencies.add("Once");
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

        // ----------------------------------------------------------------------------------------- //
        // Date Picker
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        String currentDate = sdf.format(calendar.getTime());
        etDate.setText(currentDate);

        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, monthOfYear, dayOfMonth);

                        if (selectedCalendar.before(calendar)) {
                            Toast.makeText(getContext(), "Vui lòng chọn ngày sau " + etDate.getText(), Toast.LENGTH_SHORT).show();
                        } else {
                            String selectedDate = sdf.format(selectedCalendar.getTime());
                            etDate.setText(selectedDate);
                        }
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, LanguageManager.getLocalizedText(requireContext(), "save"), datePickerDialog);
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, LanguageManager.getLocalizedText(requireContext(), "cancel"), (dialog, which) -> dialog.cancel());
            datePickerDialog.show();
        });

        // ----------------------------------------------------------------------------------------- //
        // Time Picker
        SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat sdf_date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Thêm sdf_date
        SimpleDateFormat sdf_datetime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()); // Thêm sdf_datetime

        String currentTime = sdf_time.format(calendar.getTime());
        etTime.setText(currentTime);

        etTime.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view, selectedHour, selectedMinute) -> {
                        try {
                            // Lấy ngày từ etDate
                            Date selectedDate = sdf_date.parse(etDate.getText().toString());
                            Calendar selectedCalendar = Calendar.getInstance();
                            selectedCalendar.setTime(selectedDate);

                            // Đặt giờ đã chọn
                            selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                            selectedCalendar.set(Calendar.MINUTE, selectedMinute);

                            // So sánh với thời gian hiện tại
                            if (selectedCalendar.before(Calendar.getInstance())) {
                                Toast.makeText(getContext(), "Vui lòng chọn giờ sau " + etTime.getText(), Toast.LENGTH_SHORT).show();
                            } else {
                                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                                etTime.setText(selectedTime);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            // Xử lý lỗi parse date nếu cần
                            Toast.makeText(getContext(), "Lỗi định dạng ngày", Toast.LENGTH_SHORT).show();
                        }
                    }, hour, minute, true);

            timePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, LanguageManager.getLocalizedText(requireContext(), "save"), timePickerDialog);
            timePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, LanguageManager.getLocalizedText(requireContext(), "cancel"), (dialog, which) -> dialog.cancel());
            timePickerDialog.show();
        });


        // ----------------------------------------------------------------------------------------- //
        // Ghi nhận các tham số và thêm vào Task
        builder.setView(dialogView)
                .setPositiveButton(LanguageManager.getLocalizedText(requireContext(), "save"), (dialog, id) -> {
                    // Lấy dữ liệu từ các trường EditText, Spinner
                    task.setName(etName.getText().toString());
                    task.setCategoryId(categories.get(spCategories.getSelectedItemPosition()).getCategoryId()); // Lấy category từ danh sách categories với vị trí tương úng trong spinner spCategories, sau đó dùng getCategoryId()
                    task.setDate(etDate.getText().toString());
                    task.setTime(etTime.getText().toString());
                    task.setPriority(spPriority.getSelectedItem().toString());
                    task.setRepeat_frequency(spFrequency.getSelectedItem().toString());
                    task.setDescription(etDescription.getText().toString());

                    // Thêm vào Database
                    db.addTask(task);

                    // Thêm vào danh sách tasks ở HomeFragment
                    if (listener != null) {
                        listener.onTaskAdded(task);
                        scheduleNotification(task);
                    }

                    // Sau khi lưu, đóng dialog
                    dismiss();
                    // Hiện thông báo thêm task thành công
                    showSuccessMessage();
                })
                .setNegativeButton(LanguageManager.getLocalizedText(requireContext(), "cancel"), (dialog, id) -> {
                    AddTaskFragment.this.getDialog().cancel();
                });

        return builder.create();
    }
    private void showSuccessMessage() {
        Toast.makeText(requireContext(), LanguageManager.getLocalizedText(requireContext(), "task_added"), Toast.LENGTH_SHORT).show();
    }

}