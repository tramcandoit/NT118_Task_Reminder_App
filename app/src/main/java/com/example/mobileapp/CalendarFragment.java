package com.example.mobileapp;


import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

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

    private void addEventsToCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        events.clear();

        for (Event event : eventList) {
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

    private void loadListEvent()
    {
        eventList.clear();
        eventList.addAll(event_db.getAllEvent());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEventAdded(Event event) {
        event_db.AddEvent(event);
        loadListEvent();
        addEventsToCalendar();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the event list when the fragment resumes
        loadListEvent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        event_db = new EventDatabaseHandler(requireContext());
        eventList = new ArrayList<>();
        events = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Initialize the ListView
        eventListView = view.findViewById(R.id.event_ListView);
        calendarView = view.findViewById(R.id.view_calendar);

        adapter = new EventsArrayAdapter(
                requireActivity(),
                eventList
        );

        // Attach the adapter to the ListView
        eventListView.setAdapter(adapter);

        // Load danh sách Event
        loadListEvent();
        // Thêm highlight vào lịch
        addEventsToCalendar();




        // Long click để xóa event
        eventListView.setOnItemLongClickListener((parent, view1, position, id) -> {

            // Chon event dang duoc chon de remove
            Event eventToRemove = eventList.get(position);
            // Xoa event tuong ung trong database
            event_db.RemoveEvent(eventToRemove.getEventId()); // Tạm thời chưa có delete
            // Xoa khoi danh sach hien thi
            eventList.remove(position);
            // Cap nhat adapter
            adapter.notifyDataSetChanged();
            // Cap nhat lai highlight tren Calendar
            addEventsToCalendar();
            return true;
        });

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event clickedEvent = eventList.get(position);

                // Tạo AlertDialog để hiển thị chi tiết task
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(clickedEvent.getName());

                // Tạo layout cho AlertDialog (nếu cần hiển thị nhiều thông tin hơn)
                String taskDetails = "Date: " + clickedEvent.getDate() + "\n" +
                        "Frequency: " + clickedEvent.getRepeat_frequency() + "\n" +
                        "Description: " + clickedEvent.getDescription();
                builder.setMessage(taskDetails);
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        });

        return view;
    }
}