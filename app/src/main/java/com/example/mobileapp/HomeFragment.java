package com.example.mobileapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnTaskAddedListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private GridView gridView;
    private CategoriesAdapter gvAdapter;
    private List<CategoriesItem> gridItems;
    private ListView listView;
    private TasksArrayAdapter lvAdapter;
    private List<Task> tasksList;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onTaskAdded(Task task) {
        tasksList.add(task);
        lvAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        gridView = view.findViewById(R.id.gv_home_categories);
        listView = view.findViewById(R.id.lv_Todaytask);


        gridItems = new ArrayList<>();
        gridItems.add(new CategoriesItem(R.drawable.icon_user, "Home"));
        gridItems.add(new CategoriesItem(R.drawable.icon_user, "Calendar"));
        gridItems.add(new CategoriesItem(R.drawable.icon_user, "Paper"));
        gridItems.add(new CategoriesItem(R.drawable.icon_user, "Daily"));

        gvAdapter = new CategoriesAdapter(requireActivity(), gridItems);
        gridView.setAdapter(gvAdapter);

        // Prepare some test data for list
        tasksList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        // Parse the time strings to Time objects
        tasksList.add(new Task("Task 1", "10:00 AM"));
        tasksList.add(new Task("Task 2", "11:00 AM"));
        tasksList.add(new Task("Task 3", "12:00 PM"));
        tasksList.add(new Task("Task 4", "01:00 PM"));
        tasksList.add(new Task("Task 5", "02:00 PM"));
        tasksList.add(new Task("Task 6", "03:00 PM"));
        tasksList.add(new Task("Task 7", "04:00 PM"));
        tasksList.add(new Task("Task 8", "05:00 PM"));
        tasksList.add(new Task("Task 9", "06:00 PM"));

        lvAdapter = new TasksArrayAdapter(requireActivity(), tasksList);
        listView.setAdapter(lvAdapter);

        return view;
    }
}
