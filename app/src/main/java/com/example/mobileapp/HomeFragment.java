package com.example.mobileapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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

    private GridView gvCategories;
    private CategoriesAdapter gvAdapter;
    private List<CategoriesItem> categories;
    private ListView listView;
    private TasksArrayAdapter lvAdapter;
    private List<Task> tasksList;
    private TaskDatabaseHandler db;

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
    private void cancelNotification(Task task) {
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
        alarmManager.cancel(pendingIntent);
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

        gvCategories = view.findViewById(R.id.gv_home_categories);
        listView = view.findViewById(R.id.lv_Todaytask);
        db = new TaskDatabaseHandler(requireContext());


        categories = new ArrayList<>();
        categories.add(new CategoriesItem(R.drawable.icon_user, "Work"));
        categories.add(new CategoriesItem(R.drawable.icon_user, "Health"));
        categories.add(new CategoriesItem(R.drawable.icon_user, "Shopping"));
        categories.add(new CategoriesItem(R.drawable.icon_user, "Cooking"));

        gvAdapter = new CategoriesAdapter(requireActivity(), categories);
        gvCategories.setAdapter(gvAdapter);

        // Prepare some test data for list
        tasksList = new ArrayList<>();
        tasksList = db.getAllTasks();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        lvAdapter = new TasksArrayAdapter(requireActivity(), tasksList);
        listView.setAdapter(lvAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task clickedTask = tasksList.get(position);

                // Lấy ra tên category từ ID
                int categoryId = clickedTask.getCategoryId(); // Lấy categoryId (int - vị trí của category)
                String categoryLabel = ((CategoriesItem) gvCategories.getItemAtPosition(categoryId)).getLabel(); //Lấy ra label từ category đã lưu

                // Tạo AlertDialog để hiển thị chi tiết task
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(clickedTask.getName());

                // Tạo layout cho AlertDialog (nếu cần hiển thị nhiều thông tin hơn)
                String taskDetails = "Time: " + clickedTask.getTime() + "\n" +
                        "Category: " + categoryLabel + "\n" + //  Lấy tên category từ ID
                        "Priority: " + clickedTask.getPriority() + "\n" +
                        "Frequency: " + clickedTask.getRepeat_frequency() + "\n" +
                        "Description: " + clickedTask.getDescription();
                builder.setMessage(taskDetails);
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = tasksList.get(position);

                cancelNotification(task);   // Hủy thông báo trước khi xóa
                db.deleteTask(task); // Xóa khỏi database
                tasksList.remove(position);
                lvAdapter.notifyDataSetChanged();

                return false;
            }
        });

        return view;
    }
}
