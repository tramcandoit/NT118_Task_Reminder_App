package com.example.mobileapp;

import android.media.metrics.Event;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment #newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CalendarFragment extends Fragment {
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public CalendarFragment() {
//        // Required empty public constructor
//    }
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
    private List<String> eventList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Initialize the ListView
        eventListView = view.findViewById(R.id.event_ListView);

        // Create a list of sample events (using String for simplicity)
        eventList = new ArrayList<>();
        eventList.add("16/09/2023 - 9:45 PM - Finish Report");
        eventList.add("16/09/2023 - 10:00 AM - Water the plants");
        eventList.add("27/09/2023 - 8:00 PM - Dinner with friends");

        // Set up ArrayAdapter with the sample event list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                eventList
        );

        // Attach the adapter to the ListView
        eventListView.setAdapter(adapter);

        // Set long click listener for deleting items
        eventListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            // Remove the item from the list
            eventList.remove(position);
            // Notify the adapter that the data has changed to refresh the ListView
            adapter.notifyDataSetChanged();
            return true;
        });

        return view;
    }
}