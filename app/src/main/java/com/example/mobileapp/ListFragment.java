package com.example.mobileapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public ListFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ListFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ListFragment newInstance(String param1, String param2) {
//        ListFragment fragment = new ListFragment();
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

    private ListView upcoming_task_ListView;
    private List<String> upcoming_task_List;

    private ListView overdue_task_ListView;
    private List<String> overdue_task_List;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_list, container, false);
//
//        // Initialize the ListView for upcoming tasks
//        upcoming_task_ListView = view.findViewById(R.id.upcoming_task_ListView);
//
//        // Create a list of sample events (using String for simplicity)
//        upcoming_task_List = new ArrayList<>();
//        upcoming_task_List.add("Sample upcoming task 1");
//        upcoming_task_List.add("Sample upcoming task 2");
//        upcoming_task_List.add("Sample upcoming task 3");
//        upcoming_task_List.add("Sample upcoming task 4");
//        upcoming_task_List.add("Sample upcoming task 5");
//        upcoming_task_List.add("Sample upcoming task 6");
//        upcoming_task_List.add("Sample upcoming task 7");
//        upcoming_task_List.add("Sample upcoming task 8");
//
//        // Set up ArrayAdapter with the sample event list
//        ArrayAdapter<String> upcoming_task_adapter = new ArrayAdapter<>(
//                getContext(),
//                android.R.layout.simple_list_item_1,
//                upcoming_task_List
//        );
//
//        // Attach the adapter to the ListView
//        upcoming_task_ListView.setAdapter(upcoming_task_adapter);
//
//        // Set long click listener for deleting items
//        upcoming_task_ListView.setOnItemLongClickListener((parent, view1, position, id) -> {
//            // Remove the item from the list
//            upcoming_task_List.remove(position);
//            // Notify the adapter that the data has changed to refresh the ListView
//            upcoming_task_adapter.notifyDataSetChanged();
//            return true;
//        });
//
//
//        // Initialize the ListView for overdue tasks
//        overdue_task_ListView = view.findViewById(R.id.overdue_task_ListView);
//
//        // Create a list of sample events (using String for simplicity)
//        overdue_task_List = new ArrayList<>();
//        overdue_task_List.add("Sample overdue task 1");
//        overdue_task_List.add("Sample overdue task 2");
//        overdue_task_List.add("Sample overdue task 3");
//        overdue_task_List.add("Sample overdue task 4");
//        overdue_task_List.add("Sample overdue task 5");
//
//        // Set up ArrayAdapter with the sample event list
//        ArrayAdapter<String> overdue_task_adapter = new ArrayAdapter<>(
//                getContext(),
//                android.R.layout.simple_list_item_1,
//                overdue_task_List
//        );
//
//        // Attach the adapter to the ListView
//        overdue_task_ListView.setAdapter(overdue_task_adapter);
//
//        // Set long click listener for deleting items
//        overdue_task_ListView.setOnItemLongClickListener((parent, view1, position, id) -> {
//            // Remove the item from the list
//            overdue_task_List.remove(position);
//            // Notify the adapter that the data has changed to refresh the ListView
//            overdue_task_adapter.notifyDataSetChanged();
//            return true;
//        });
//
//        return view;
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // Initialize and set up upcoming tasks
        upcoming_task_ListView = view.findViewById(R.id.upcoming_task_ListView);
        upcoming_task_List = new ArrayList<>();
        upcoming_task_List.add("Sample upcoming task 1");
        upcoming_task_List.add("Sample upcoming task 2");
        upcoming_task_List.add("Sample upcoming task 4");
        upcoming_task_List.add("Sample upcoming task 5");
        upcoming_task_List.add("Sample upcoming task 6");
        upcoming_task_List.add("Sample upcoming task 7");
        upcoming_task_List.add("Sample upcoming task 8");
        upcoming_task_List.add("Sample upcoming task 9");
        upcoming_task_List.add("Sample upcoming task 10");


        // Set custom adapter for upcoming tasks
        TaskAdapter upcoming_task_adapter = new TaskAdapter(getContext(), upcoming_task_List);
        upcoming_task_ListView.setAdapter(upcoming_task_adapter);

        // Initialize and set up overdue tasks
        overdue_task_ListView = view.findViewById(R.id.overdue_task_ListView);
        overdue_task_List = new ArrayList<>();
        overdue_task_List.add("Sample overdue task 1");
        overdue_task_List.add("Sample overdue task 2");
        overdue_task_List.add("Sample overdue task 3");
        overdue_task_List.add("Sample overdue task 4");
        overdue_task_List.add("Sample overdue task 5");

        // Set custom adapter for overdue tasks
        TaskAdapter overdue_task_adapter = new TaskAdapter(getContext(), overdue_task_List);
        overdue_task_ListView.setAdapter(overdue_task_adapter);

        return view;
    }

    // Custom ArrayAdapter with CheckBox
    private static class TaskAdapter extends ArrayAdapter<String> {

        public TaskAdapter(Context context, List<String> tasks) {
            super(context, 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_task_with_checkbox, parent, false);
            }

            CheckBox checkBox = convertView.findViewById(R.id.item_checkbox);
            TextView textView = convertView.findViewById(R.id.item_text);

            // Set task text
            String task = getItem(position);
            textView.setText(task);

            // Handle checkbox state change if needed
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Perform action on checkbox checked state change, if necessary
            });

            return convertView;
        }
    }
}