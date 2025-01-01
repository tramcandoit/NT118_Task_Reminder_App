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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment #newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CalendarFragment extends Fragment implements OnEventAddedListener {
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
    public CalendarFragment() {
        // Required empty public constructor
    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment CalendarFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static CalendarFragment newInstance(String param1, String param2) {
//        CalendarFragment fragment = new CalendarFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    private ListView eventListView;
    private ArrayList<Event> eventList;
    private EventsArrayAdapter adapter;
    private CalendarView calendarView;
    private List<EventDay> events;
    private EventDatabaseHandler event_db;
    private TextView txtNoEvent;
    private List<Event> fullEventList;


    // Them event vao lich
    private void addEventsToCalendar() {
        // Tao date format ngay thang nam
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        // Xoa list event de update list event moi
        events.clear();

        for (Event event : fullEventList) { // Thay đổi từ eventList sang fullEventList
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
        fullEventList.clear(); // Clear danh sách gốc
        fullEventList.addAll(event_db.getAllEvent()); // Nạp toàn bộ sự kiện vào danh sách gốc

        eventList.clear(); // Clear danh sách hiển thị
        eventList.addAll(fullEventList); // Nạp toàn bộ sự kiện vào danh sách hiển thị

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEventAdded(Event event) {
        event_db.AddEvent(event);
        loadListEvent(); // Nạp lại toàn bộ sự kiện
        showTodayEvents(); // Hiển thị sự kiện của ngày hiện tại
        addEventsToCalendar(); // Cập nhật highlight cho calendar
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

        // Tao layout calendar
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Anh xa
        eventListView = view.findViewById(R.id.event_ListView);
        calendarView = view.findViewById(R.id.view_calendar);
        txtNoEvent = view.findViewById(R.id.no_ev_txt);
        txtNoEvent.setVisibility(View.GONE);
        adapter = new EventsArrayAdapter(
                requireActivity(),
                eventList
        );


        // Set adapter cho danh sach event
        eventListView.setAdapter(adapter);

        // load event
        loadListEvent();
        // load highlight tuong ung event dang co
        addEventsToCalendar();
        // hien thi event trong ngay (khi khoi dong app)
        showTodayEvents();

        // Click vào ngày để xem event trong ngay do
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

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event clickedEvent = eventList.get(position);
                showEventDetailsDialog(clickedEvent);
            }
        });


        return view;
    }

    private void showEventDetailsDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_eventdetails, null);

        // Ánh xạ các view trong dialog
        TextView tvEventName = dialogView.findViewById(R.id.tv_eventdetail_eventdetail_textbox);
        TextView tvEventDate = dialogView.findViewById(R.id.tv_eventdetail_date_selector);
        TextView tvEventFrequency = dialogView.findViewById(R.id.tv_eventdetail_frequency_selector);
        TextView tvEventDescription = dialogView.findViewById(R.id.tv_eventdetail_description);


        // Đặt giá trị cho các view
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

                                // Cập nhật eventList và fullEventList
                                eventList.remove(event);
                                fullEventList.remove(event);

                                // Cập nhật lại giao diện
                                adapter.notifyDataSetChanged();
                                addEventsToCalendar();

                                Toast.makeText(
                                        requireContext(),
                                        LanguageManager.getLocalizedText(requireContext(), "event_deleted"),
                                        Toast.LENGTH_SHORT
                                ).show();

                                dialog.dismiss(); // Đóng dialog details sau khi xóa
                            })
                            .setNegativeButton(LanguageManager.getLocalizedText(requireContext(), "no"), null)
                            .show();
                })
                .setNeutralButton(LanguageManager.getLocalizedText(requireContext(), "edit"), (dialog, which) -> {

                });


        AlertDialog dialog = builder.create();
        dialog.show();;
    }


    private void showTodayEvents() {
        // Định dạng ngày hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String todayDateStr = sdf.format(Calendar.getInstance().getTime());

        // Lọc các sự kiện của ngày hiện tại
        List<Event> todayEvents = new ArrayList<>();
        for (Event event : fullEventList) {
            if (event.getDate().equals(todayDateStr)) {
                todayEvents.add(event);
            }
        }

        // Cập nhật danh sách sự kiện hiển thị
        eventList.clear();
        eventList.addAll(todayEvents);
        adapter.notifyDataSetChanged();

        // Hiển thị/ẩn text thông báo không có sự kiện
        if (todayEvents.isEmpty()) {
            txtNoEvent.setVisibility(View.VISIBLE);
            txtNoEvent.setText(String.format(
                    LanguageManager.getLocalizedText(requireContext(), "no_events_today")
            ));
        } else {
            txtNoEvent.setVisibility(View.GONE);
        }

        // Set calendar to today's date
        try {
            Calendar today = Calendar.getInstance();
            calendarView.setDate(today);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
    }
}