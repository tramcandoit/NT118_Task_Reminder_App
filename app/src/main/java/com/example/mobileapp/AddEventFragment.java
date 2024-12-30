package com.example.mobileapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEventFragment extends DialogFragment {
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final List<String> VALID_FREQUENCIES = Arrays.asList("Weekly", "Monthly", "Once");
    private static final String NOTIFICATION_CHANNEL_ID = "events_channel";

    private EditText etEventName;
    private EditText etDate;
    private Spinner spFrequency;
    private EditText etDescription;
    private OnEventAddedListener listener;
    private EventDatabaseHandler event_db;
    private Calendar calendar;

    public static AddEventFragment newInstance(OnEventAddedListener listener) {
        AddEventFragment fragment = new AddEventFragment();
        fragment.listener = listener;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_addevent, null);

        initializeViews(dialogView);
        setupFrequencySpinner();
        setupDatePicker();

        AlertDialog dialog = builder.setView(dialogView)
                .setPositiveButton(LanguageManager.getLocalizedText(requireContext(), "save"), null)
                .setNegativeButton(LanguageManager.getLocalizedText(requireContext(), "cancel"), (dialogInterface, i) -> dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> setupSaveButton(dialog));

        return dialog;
    }

    private void initializeViews(View view) {
        etEventName = view.findViewById(R.id.tv_addevent_addevent_textbox);
        etDate = view.findViewById(R.id.et_addevent_date_selector);
        etDescription = view.findViewById(R.id.et_addevent_description);
        spFrequency = view.findViewById(R.id.sp_addevent_frequency_selector);
        event_db = new EventDatabaseHandler(requireContext());

    }

    private void setupFrequencySpinner() {
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.addevent_spinner_item_text,
                VALID_FREQUENCIES
        );
        frequencyAdapter.setDropDownViewResource(R.layout.addevent_spinner_item_text);
        spFrequency.setAdapter(frequencyAdapter);
    }
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private void setupDatePicker() {
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        etDate.setText(sdf.format(calendar.getTime()));

        etDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth);

                    // So sánh selectedCalendar với ngày hiện tại, bỏ qua giờ phút giây
                    Calendar today = Calendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);


                    if (!selectedCalendar.after(today)) { // Kiểm tra selectedCalendar có sau today không (không bao gồm today)
                        Toast.makeText(getContext(), "Vui lòng chọn ngày sau ngày hôm nay", Toast.LENGTH_SHORT).show();
                    } else {
                        String selectedDate = sdf.format(selectedCalendar.getTime());
                        etDate.setText(selectedDate);
                        calendar.setTime(selectedCalendar.getTime());
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, LanguageManager.getLocalizedText(requireContext(), "save"), datePickerDialog);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, LanguageManager.getLocalizedText(requireContext(), "cancel"), (dialog, which) -> dialog.cancel());
        datePickerDialog.show();
    }

    private void scheduleNotification(Event event) {
        Context context = requireContext();
        Intent intent = new Intent(context, EventNotificationReceiver.class);
        intent.putExtra("eventName", event.getName());
        intent.putExtra("eventDescription", event.getDescription());
        intent.putExtra("eventId", event.getEventId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                event.getEventId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(event.getDate()));

            // Set notification time to 8:00 AM
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            switch (event.getRepeat_frequency()) {
                case "Once":
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                    break;
                case "Weekly":
                    alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY * 7,
                            pendingIntent
                    );
                    break;
                case "Monthly":
                    alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY * 30,
                            pendingIntent
                    );
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setupSaveButton(AlertDialog dialog) {
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            if (validateAndSaveEvent()) {
                dismiss();
            }
        });
    }

    private boolean validateAndSaveEvent() {
        String eventName = etEventName.getText().toString().trim();
        if (!validateEventName(eventName)) {
            return false;
        }

        String selectedDateStr = etDate.getText().toString();
        if (!validateDate(selectedDateStr)) {
            return false;
        }

        String description = etDescription.getText().toString().trim();
        if (!validateDescription(description)) {
            return false;
        }

        Event event = createEventFromInput();
        if (listener != null) {
            listener.onEventAdded(event);
            scheduleNotification(event);
            showSuccessMessage();
            return true;
        }

        return false;
    }

    private boolean validateEventName(String name) {
        if (name.isEmpty()) {
            showError("Event name cannot be empty");
            return false;
        }
        if (name.length() > MAX_NAME_LENGTH) {
            showError("Event name is too long (max " + MAX_NAME_LENGTH + " characters)");
            return false;
        }
        return true;
    }

    private boolean validateDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTime(sdf.parse(dateStr));

            Calendar currentDay = Calendar.getInstance();
            selectedDate.set(Calendar.HOUR_OF_DAY, 0);
            selectedDate.set(Calendar.MINUTE, 0);
            selectedDate.set(Calendar.SECOND, 0);
            selectedDate.set(Calendar.MILLISECOND, 0);

            currentDay.set(Calendar.HOUR_OF_DAY, 0);
            currentDay.set(Calendar.MINUTE, 0);
            currentDay.set(Calendar.SECOND, 0);
            currentDay.set(Calendar.MILLISECOND, 0);

            if (selectedDate.before(currentDay)) {
                showError("Cannot add an event in the past");
                return false;
            }
            return true;
        } catch (Exception e) {
            showError("Invalid date format");
            return false;
        }
    }

    private boolean validateDescription(String description) {
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            showError("Description is too long (max " + MAX_DESCRIPTION_LENGTH + " characters)");
            return false;
        }
        return true;
    }

    private Event createEventFromInput() {
        Event event = new Event();
        event.setName(etEventName.getText().toString().trim());
        event.setDate(etDate.getText().toString());
        event.setRepeat_frequency(spFrequency.getSelectedItem().toString());
        String description = etDescription.getText().toString().trim();
        event.setDescription(description.isEmpty() ? "No description" : description);
        return event;
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccessMessage() {
        Toast.makeText(requireContext(), LanguageManager.getLocalizedText(requireContext(), "event_added"), Toast.LENGTH_SHORT).show();
    }
}