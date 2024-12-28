package com.example.mobileapp;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
                // Chuyen format cho ngay
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String selectedDateStr = String.format("%02d/%02d/%04d",
                        calendarDay.getCalendar().get(Calendar.DAY_OF_MONTH),
                        calendarDay.getCalendar().get(Calendar.MONTH) + 1,
                        calendarDay.getCalendar().get(Calendar.YEAR)
                );

                // Loc event de hien thi event o ngay da chon
                List<Event> dayEvents = new ArrayList<>();
                for (Event event : fullEventList) {
                    if (event.getDate().equals(selectedDateStr)) {
                        dayEvents.add(event);
                    }
                }

                // Cap nhat list event hien thi
                eventList.clear();
                eventList.addAll(dayEvents);
                adapter.notifyDataSetChanged();

                // Hien thi hoac an text khong co su kien
                if (dayEvents.isEmpty()) {
                    txtNoEvent.setVisibility(View.VISIBLE);
                    txtNoEvent.setText(String.format("No events on %s", selectedDateStr));
                } else {
                    txtNoEvent.setVisibility(View.GONE);
                }
            }
        });




        // Click de xem chi tiet event
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event clickedEvent = eventList.get(position);

                // AlertDialog de hien properties event
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(clickedEvent.getName());

                // Hien thi thong tin event
                String taskDetails = "Date: " + clickedEvent.getDate() + "\n" +
                        "Frequency: " + clickedEvent.getRepeat_frequency() + "\n" +
                        "Description: " + clickedEvent.getDescription();
                builder.setMessage(taskDetails);
                // Them nut OK
                builder.setPositiveButton("OK", null);
                // Thêm nut Delete va event xoa su kien
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Tao dialog de xac nhan xoa
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Confirm delete")
                                .setMessage("Are you sure you want to delete this event?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface confirmDialog, int which) {
                                        // Chọn event đang được chọn để remove
                                        Event eventToRemove = eventList.get(position);

                                        // Xóa event tương ứng trong database
                                        event_db.RemoveEvent(eventToRemove.getEventId());

                                        // Cập nhật fullEventList
                                        fullEventList.remove(eventToRemove);

                                        // Xóa khỏi danh sách hiển thị
                                        eventList.remove(position);

                                        // Cập nhật adapter
                                        adapter.notifyDataSetChanged();

                                        // Cập nhật lại highlight trên Calendar
                                        addEventsToCalendar();

                                        // Hiển thị thông báo đã xóa thành công
                                        Toast.makeText(requireContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                });

                builder.show();
            }
        });

        return view;
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
            txtNoEvent.setText("No events today");
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