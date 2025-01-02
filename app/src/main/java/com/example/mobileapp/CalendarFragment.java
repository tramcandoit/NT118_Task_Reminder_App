package com.example.mobileapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
                Event clickedEvent = eventList.get(position);
                showEventDetailsDialog(clickedEvent);
            }
        });

        // OnLongClick item trong ListView
        eventListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Event longClickedEvent = eventList.get(position);
                // Xử lý khi item được long-click
                Toast.makeText(requireContext(), "Long-clicked: " + longClickedEvent.getName(), Toast.LENGTH_SHORT).show();
                return true; // Trả về true để không xử lý thêm hành động onClick
            }
        });

        return view;
    }

    private void showEventDetailsDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_eventdetails, null);

        TextView tvEventName = dialogView.findViewById(R.id.tv_eventdetail_eventdetail_textbox);
        TextView tvEventDate = dialogView.findViewById(R.id.tv_eventdetail_date_selector);
        TextView tvEventFrequency = dialogView.findViewById(R.id.tv_eventdetail_frequency_selector);
        TextView tvEventDescription = dialogView.findViewById(R.id.tv_eventdetail_description);

        tvEventName.setText(event.getName());
        tvEventDate.setText(event.getDate());
        tvEventFrequency.setText(event.getRepeat_frequency());
        tvEventDescription.setText(event.getDescription());

        builder.setView(dialogView)
                .setPositiveButton(LanguageManager.getLocalizedText(requireContext(), "ok"), null)
                .setNegativeButton(LanguageManager.getLocalizedText(requireContext(), "delete"), (dialog, which) -> {
                    new AlertDialog.Builder(requireContext())
                            .setTitle(LanguageManager.getLocalizedText(requireContext(), "confirm_delete"))
                            .setMessage(LanguageManager.getLocalizedText(requireContext(), "confirm_delete_message"))
                            .setPositiveButton(LanguageManager.getLocalizedText(requireContext(), "yes"), (confirmDialog, whichButton) -> {
                                event_db.RemoveEvent(event.getEventId());

                                eventList.remove(event);
                                fullEventList.remove(event);

                                adapter.notifyDataSetChanged();
                                addEventsToCalendar();

                                Toast.makeText(requireContext(), LanguageManager.getLocalizedText(requireContext(), "event_deleted"), Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            })
                            .setNegativeButton(LanguageManager.getLocalizedText(requireContext(), "no"), null)
                            .show();
                })
                .setNeutralButton(LanguageManager.getLocalizedText(requireContext(), "edit"), (dialog, which) -> {
                    // Chỉnh sửa event tại đây
                });

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
