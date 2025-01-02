package com.example.mobileapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class CalendarFragment extends Fragment implements OnEventAddedListener {

    private ListView eventListView;
    private ArrayList<Event> eventList;
    private EventsArrayAdapter adapter;
    private CalendarView calendarView;
    private List<EventDay> events;
    private EventDatabaseHandler event_db;
    private TextView txtNoEvent;
    private List<Event> fullEventList;

    // Thêm sự kiện vào lịch
    private void addEventsToCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        events.clear();

        for (Event event : fullEventList) {
            try {
                String dateStr = event.getDate();
                Calendar eventCalendar = Calendar.getInstance();
                eventCalendar.setTime(sdf.parse(dateStr));

                EventDay eventDay = new EventDay(
                        eventCalendar,
                        R.drawable.ic_check
                );

                events.add(eventDay);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        calendarView.setEvents(events);
    }

    private void loadListEvent() {
        fullEventList.clear();
        fullEventList.addAll(event_db.getAllEvent());

        eventList.clear();
        eventList.addAll(fullEventList);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEventAdded(Event event) {
        event_db.AddEvent(event);
        loadListEvent();
        showTodayEvents();
        addEventsToCalendar();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadListEvent();
        showTodayEvents();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event_db = new EventDatabaseHandler(requireContext());
        eventList = new ArrayList<>();
        events = new ArrayList<>();
        fullEventList = new ArrayList<>();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Ánh xạ các view
        eventListView = view.findViewById(R.id.event_ListView);
        calendarView = view.findViewById(R.id.view_calendar);
        txtNoEvent = view.findViewById(R.id.no_ev_txt);
        txtNoEvent.setVisibility(View.GONE);
        adapter = new EventsArrayAdapter(requireActivity(), eventList);

        eventListView.setAdapter(adapter);
        loadListEvent();
        addEventsToCalendar();
        showTodayEvents();

        // Click vào ngày để xem sự kiện trong ngày
        calendarView.setOnCalendarDayClickListener(new OnCalendarDayClickListener() {
            @Override
            public void onClick(@NonNull CalendarDay calendarDay) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String selectedDateStr = String.format("%02d/%02d/%04d",
                        calendarDay.getCalendar().get(Calendar.DAY_OF_MONTH),
                        calendarDay.getCalendar().get(Calendar.MONTH) + 1,
                        calendarDay.getCalendar().get(Calendar.YEAR)
                );

                List<Event> dayEvents = new ArrayList<>();
                for (Event event : fullEventList) {
                    if (event.getDate().equals(selectedDateStr)) {
                        dayEvents.add(event);
                    }
                }

                eventList.clear();
                eventList.addAll(dayEvents);
                adapter.notifyDataSetChanged();

                if (dayEvents.isEmpty()) {
                    txtNoEvent.setVisibility(View.VISIBLE);
                    txtNoEvent.setText(
                            String.format(
                                    LanguageManager.getLocalizedText(requireContext(), "no_events_on_date"),
                                    selectedDateStr
                            )
                    );
                } else {
                    txtNoEvent.setVisibility(View.GONE);
                }
            }
        });

        // OnClick item trong ListView
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = eventList.get(position);

                // Tạo AlertDialog để hiển thị chi tiết event
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_addevent, null);
                builder.setView(dialogView);

                // Tìm các TextView trong dialog layout và thiết lập giá trị
                EditText etEventName = dialogView.findViewById(R.id.et_addevent_addevent_textbox);
                EditText etEventDate = dialogView.findViewById(R.id.et_addevent_date_selector);
                Spinner spEventFrequency = dialogView.findViewById(R.id.sp_addevent_frequency_selector);
                TextView etEventDescription = dialogView.findViewById(R.id.et_addevent_description);


                List<String> frequencies = new ArrayList<>();
                frequencies.add("Daily");
                frequencies.add("Weekly");
                frequencies.add("Once");
                ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(getContext(), R.layout.addtask_spinner_item_text, frequencies);
                frequencyAdapter.setDropDownViewResource(R.layout.addtask_spinner_item_text);
                spEventFrequency.setAdapter(frequencyAdapter);
                int spinnerFrequencyPosition = frequencyAdapter.getPosition(event.getRepeat_frequency());

                // Date Picker
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();

                String currentDate = sdf.format(calendar.getTime());
                etEventDate.setText(currentDate);

                etEventDate.setOnClickListener(v -> {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                            (viewDate, year, monthOfYear, dayOfMonth) -> {
                                Calendar selectedCalendar = Calendar.getInstance();
                                selectedCalendar.set(year, monthOfYear, dayOfMonth);

                                if (selectedCalendar.before(calendar)) {
                                    Toast.makeText(getContext(), "Vui lòng chọn ngày sau " + etEventDate.getText(), Toast.LENGTH_SHORT).show();
                                } else {
                                    String selectedDate = sdf.format(selectedCalendar.getTime());
                                    etEventDate.setText(selectedDate);
                                }
                            },
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                    );

                    datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, LanguageManager.getLocalizedText(requireContext(), "save"), datePickerDialog);
                    datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, LanguageManager.getLocalizedText(requireContext(), "cancel"), (dialog, which) -> dialog.cancel());
                    datePickerDialog.show();
                });

                // Thiết lập các giá trị vào các trường
                etEventName.setText(event.getName());
                etEventDate.setText(event.getDate());
                spEventFrequency.setSelection(spinnerFrequencyPosition);
                etEventDescription.setText(event.getDescription());

                // Tắt chế độ focusable cho các trường không cần sửa đổi
                etEventName.setFocusable(false);
                etEventName.setFocusableInTouchMode(false);

                etEventDate.setFocusable(false);
                etEventDate.setFocusableInTouchMode(false);

                etEventDescription.setFocusable(false);
                etEventDescription.setFocusableInTouchMode(false);

                spEventFrequency.setOnTouchListener((v, event13) -> true);

                AtomicBoolean editFlag = new AtomicBoolean(false);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    if (editFlag.get() == false) {
                        dialog.dismiss();
                    } else {
                        event.setName(etEventName.getText().toString());
                        event.setDate(etEventDate.getText().toString());
                        event.setRepeat_frequency(spEventFrequency.getSelectedItem().toString());
                        event.setDescription(etEventDescription.getText().toString());

                        event_db.updateEvent(event);

                        eventList.set(position, event);
                        adapter.notifyDataSetChanged();

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    editFlag.set(false);
                    dialog.dismiss();
                });

                builder.setNeutralButton("Edit", (dialog, which) -> {
                    // Khởi tạo nút Edit

                });

                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(dialogInterface -> {
                    Button editButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

                    editButton.setOnClickListener(v -> {
                        // Bật cờ editFlag
                        editFlag.set(true);

                        // Chỉnh sửa thông tin task
                        etEventName.setFocusable(true);
                        etEventName.setFocusableInTouchMode(true);

                        etEventDate.setFocusable(true);
                        etEventDate.setFocusableInTouchMode(true);

                        etEventDescription.setFocusable(true);
                        etEventDescription.setFocusableInTouchMode(true);

                        spEventFrequency.setOnTouchListener(null);
                    });
                });

                dialog.show();

            }
        });

        // OnLongClick item trong ListView
        eventListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Event longClickedEvent = eventList.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Chọn hành động");

                builder.setItems(new CharSequence[]{"Xem nhiệm vụ", "Sửa nhiệm vụ", "Xóa nhiệm vụ"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // Xem nhiệm vụ
                            showEventDetailsDialog(longClickedEvent, position);
                            break;
                        case 1: // Sửa nhiệm vụ
                            showEditDialog(longClickedEvent, position);
                            break;
                        case 2: // Xóa nhiệm vụ
                            event_db.deleteEvent(longClickedEvent); // Xóa khỏi database
                            eventList.remove(position);
                            adapter.notifyDataSetChanged();
                            break;
                    }
                });

                builder.show();

                return true; // Trả về true để không xử lý thêm hành động onClick
            }
        });

        return view;
    }

    private void showEditDialog(Event event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_addevent, null);
        builder.setView(dialogView);

        // Tìm các TextView trong dialog layout và thiết lập giá trị
        EditText etEventName = dialogView.findViewById(R.id.et_addevent_addevent_textbox);
        EditText etEventDate = dialogView.findViewById(R.id.et_addevent_date_selector);
        Spinner spEventFrequency = dialogView.findViewById(R.id.sp_addevent_frequency_selector);
        TextView etEventDescription = dialogView.findViewById(R.id.et_addevent_description);


        List<String> frequencies = new ArrayList<>();
        frequencies.add("Monthly");
        frequencies.add("Yearly");
        frequencies.add("Once");
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(getContext(), R.layout.addtask_spinner_item_text, frequencies);
        frequencyAdapter.setDropDownViewResource(R.layout.addtask_spinner_item_text);
        spEventFrequency.setAdapter(frequencyAdapter);
        int spinnerFrequencyPosition = frequencyAdapter.getPosition(event.getRepeat_frequency());

        // Date Picker
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        String currentDate = sdf.format(calendar.getTime());
        etEventDate.setText(currentDate);

        etEventDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (viewDate, year, monthOfYear, dayOfMonth) -> {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, monthOfYear, dayOfMonth);

                        if (selectedCalendar.before(calendar)) {
                            Toast.makeText(getContext(), "Vui lòng chọn ngày sau " + etEventDate.getText(), Toast.LENGTH_SHORT).show();
                        } else {
                            String selectedDate = sdf.format(selectedCalendar.getTime());
                            etEventDate.setText(selectedDate);
                        }
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, LanguageManager.getLocalizedText(requireContext(), "save"), datePickerDialog);
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, LanguageManager.getLocalizedText(requireContext(), "cancel"), (dialog, which) -> dialog.cancel());
            datePickerDialog.show();
        });

        // Thiết lập các giá trị vào các trường
        etEventName.setText(event.getName());
        etEventDate.setText(event.getDate());
        spEventFrequency.setSelection(spinnerFrequencyPosition);
        etEventDescription.setText(event.getDescription());

        // Tắt chế độ focusable cho các trường không cần sửa đổi
        etEventName.setFocusable(true);
        etEventName.setFocusableInTouchMode(true);

        etEventDate.setFocusable(true);
        etEventDate.setFocusableInTouchMode(true);

        etEventDescription.setFocusable(true);
        etEventDescription.setFocusableInTouchMode(true);



        builder.setPositiveButton("Save", (dialog, which) -> {
                event.setName(etEventName.getText().toString());
                event.setDate(etEventDate.getText().toString());
                event.setRepeat_frequency(spEventFrequency.getSelectedItem().toString());
                event.setDescription(etEventDescription.getText().toString());

                event_db.updateEvent(event);
                eventList.set(position, event);
                adapter.notifyDataSetChanged();

                dialog.dismiss();

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEventDetailsDialog(Event event, int position) {

        // Tạo AlertDialog để hiển thị chi tiết event
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_addevent, null);
        builder.setView(dialogView);

        // Tìm các TextView trong dialog layout và thiết lập giá trị
        EditText etEventName = dialogView.findViewById(R.id.et_addevent_addevent_textbox);
        EditText etEventDate = dialogView.findViewById(R.id.et_addevent_date_selector);
        Spinner spEventFrequency = dialogView.findViewById(R.id.sp_addevent_frequency_selector);
        TextView etEventDescription = dialogView.findViewById(R.id.et_addevent_description);


        List<String> frequencies = new ArrayList<>();
        frequencies.add("Daily");
        frequencies.add("Weekly");
        frequencies.add("Once");
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(getContext(), R.layout.addtask_spinner_item_text, frequencies);
        frequencyAdapter.setDropDownViewResource(R.layout.addtask_spinner_item_text);
        spEventFrequency.setAdapter(frequencyAdapter);
        int spinnerFrequencyPosition = frequencyAdapter.getPosition(event.getRepeat_frequency());

        // Date Picker
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        String currentDate = sdf.format(calendar.getTime());
        etEventDate.setText(currentDate);

        etEventDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (viewDate, year, monthOfYear, dayOfMonth) -> {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, monthOfYear, dayOfMonth);

                        if (selectedCalendar.before(calendar)) {
                            Toast.makeText(getContext(), "Vui lòng chọn ngày sau " + etEventDate.getText(), Toast.LENGTH_SHORT).show();
                        } else {
                            String selectedDate = sdf.format(selectedCalendar.getTime());
                            etEventDate.setText(selectedDate);
                        }
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, LanguageManager.getLocalizedText(requireContext(), "save"), datePickerDialog);
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, LanguageManager.getLocalizedText(requireContext(), "cancel"), (dialog, which) -> dialog.cancel());
            datePickerDialog.show();
        });

        // Thiết lập các giá trị vào các trường
        etEventName.setText(event.getName());
        etEventDate.setText(event.getDate());
        spEventFrequency.setSelection(spinnerFrequencyPosition);
        etEventDescription.setText(event.getDescription());

        // Tắt chế độ focusable cho các trường không cần sửa đổi
        etEventName.setFocusable(false);
        etEventName.setFocusableInTouchMode(false);

        etEventDate.setFocusable(false);
        etEventDate.setFocusableInTouchMode(false);

        etEventDescription.setFocusable(false);
        etEventDescription.setFocusableInTouchMode(false);

        spEventFrequency.setOnTouchListener((v, event13) -> true);

        AtomicBoolean editFlag = new AtomicBoolean(false);

        builder.setPositiveButton("OK", (dialog, which) -> {
            if (editFlag.get() == false) {
                dialog.dismiss();
            } else {
                event.setName(etEventName.getText().toString());
                event.setDate(etEventDate.getText().toString());
                event.setRepeat_frequency(spEventFrequency.getSelectedItem().toString());
                event.setDescription(etEventDescription.getText().toString());

                event_db.updateEvent(event);

                eventList.set(position, event);
                adapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showTodayEvents() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String todayDateStr = sdf.format(Calendar.getInstance().getTime());

        List<Event> todayEvents = new ArrayList<>();
        for (Event event : fullEventList) {
            if (event.getDate().equals(todayDateStr)) {
                todayEvents.add(event);
            }
        }

        eventList.clear();
        eventList.addAll(todayEvents);
        adapter.notifyDataSetChanged();

        if (todayEvents.isEmpty()) {
            txtNoEvent.setVisibility(View.VISIBLE);
            txtNoEvent.setText(String.format(LanguageManager.getLocalizedText(requireContext(), "no_events_today")));
        } else {
            txtNoEvent.setVisibility(View.GONE);
        }

        try {
            Calendar today = Calendar.getInstance();
            calendarView.setDate(today);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
    }
}
