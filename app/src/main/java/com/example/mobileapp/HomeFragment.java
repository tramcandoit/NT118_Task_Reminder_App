package com.example.mobileapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**

 A simple {@link Fragment} subclass.

 Use the {@link HomeFragment#newInstance} factory method to

 create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnTaskAddedListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView rvCategories;
    CategoriesAdapter categoriesAdapter;
    private List<CategoriesItem> categories;
    private ListView listView;
    private TasksArrayAdapter lvAdapter;
    private List<Task> tasksList;
    private TaskDatabaseHandler db;
    private CategoryDatabaseHandler categoryDb;
    private TextView tvCategoriesMenu;
    private SparseBooleanArray selectedCategories;

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


        rvCategories = view.findViewById(R.id.rv_home_categories);
        listView = view.findViewById(R.id.lv_Todaytask);
        tvCategoriesMenu = view.findViewById(R.id.tv_home_categories_menu);
        db = new TaskDatabaseHandler(requireContext());
        categoryDb = new CategoryDatabaseHandler(requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false); // Hoặc "this" nếu trong Activity
        rvCategories.setLayoutManager(layoutManager);

        int space = 35; // Khoảng cách giữa các item (trong pixel)
        rvCategories.addItemDecoration(new HorizontalSpacingItemDecoration(space));

        categories = new ArrayList<>();
        categories = categoryDb.getAllCategories();

//        categories.add(new CategoriesItem(1111, 111, "Work", R.drawable.icon_user));
//        categories.add(new CategoriesItem(2222, 111, "Health", R.drawable.icon_user));
//        categories.add(new CategoriesItem(3333, 111, "Shopping", R.drawable.icon_user));
//        categories.add(new CategoriesItem(4444, 111, "Cooking", R.drawable.icon_user));
//        categories.add(new CategoriesItem(5555, 111, "Travel", R.drawable.icon_user));
//        categories.add(new CategoriesItem(6666, 111, "Music", R.drawable.icon_user));
//        categories.add(new CategoriesItem(7777, 111, "Misc", R.drawable.icon_user));
//        categories.add(new CategoriesItem(8888, 111, "Study", R.drawable.icon_user));

        selectedCategories = new SparseBooleanArray();
        filterTasksBySelectedCategories();

        categoriesAdapter = new CategoriesAdapter(requireContext(), categories, selectedCategories);
        rvCategories.setAdapter(categoriesAdapter);

        if (categories.size() > 3) {
            rvCategories.setHorizontalScrollBarEnabled(true);
        } else {
            rvCategories.setHorizontalScrollBarEnabled(false);
        }

        rvCategories.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        categoriesAdapter.setOnItemClickListener(position -> {
            int categoryId = categories.get(position).getCategoryId();

            if (selectedCategories.get(categoryId, false)) {
                // Nếu category đã được chọn, bỏ chọn (xóa khỏi selectedCategories)
                selectedCategories.delete(categoryId);
            } else {
                // Nếu category chưa được chọn, thêm vào selectedCategories
                selectedCategories.put(categoryId, true);
            }

            filterTasksBySelectedCategories(); // Call your filtering method
            categoriesAdapter.notifyDataSetChanged();
        });


        // Prepare some test data for list
        tasksList = new ArrayList<>();
        tasksList = db.getAllTasks();

        // In HomeFragment's onCreateView():
        lvAdapter = new TasksArrayAdapter(requireActivity(), tasksList, categoryDb); // Pass categoryDb
        listView.setAdapter(lvAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task clickedTask = tasksList.get(position);


                CategoriesItem selectedCategory = categoryDb.getCategory(clickedTask.getCategoryId());

                // Tìm category theo ID trong database của Category
                String categoryLabel = null; // Khởi tạo categoryLabel
                categoryLabel = selectedCategory.getName();

                // Tạo AlertDialog để hiển thị chi tiết task
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_taskdetails, null);
                builder.setView(dialogView);

                // Tìm các TextView trong dialog layout và thiết lập giá trị
                TextView tvTaskdetailTaskdetailTextbox = dialogView.findViewById(R.id.tv_taskdetail_taskdetail_textbox);
                TextView tvTaskdetailCategories = dialogView.findViewById(R.id.tv_taskdetail_categories);
                TextView tvTaskdetailDateSelector = dialogView.findViewById(R.id.tv_taskdetail_date_selector);
                TextView tvTaskdetailTimeSelector = dialogView.findViewById(R.id.tv_taskdetail_time_selector);
                TextView tvTaskdetailPriority = dialogView.findViewById(R.id.sp_taskdetail_priority);
                TextView tvTaskdetailFrequencySelector = dialogView.findViewById(R.id.tv_taskdetail_frequency_selector);
                TextView tvTaskdetailDescription = dialogView.findViewById(R.id.tv_taskdetail_description);

                // Tạo layout cho AlertDialog (nếu cần hiển thị nhiều thông tin hơn)
                tvTaskdetailTaskdetailTextbox.setText(clickedTask.getName());
                tvTaskdetailCategories.setText(categoryLabel);
                tvTaskdetailDateSelector.setText(clickedTask.getDate());
                tvTaskdetailTimeSelector.setText(clickedTask.getTime());
                tvTaskdetailPriority.setText(clickedTask.getPriority());
                tvTaskdetailFrequencySelector.setText(clickedTask.getRepeat_frequency());
                tvTaskdetailDescription.setText(clickedTask.getDescription());

                builder.setPositiveButton("OK", null);
                builder.setNegativeButton("Delete", (dialog, which) -> {
                    // Xử lý xóa student
                    cancelNotification(clickedTask);   // Hủy thông báo trước khi xóa
                    db.deleteTask(clickedTask); // Xóa khỏi database
                    tasksList.remove(position);
                    lvAdapter.notifyDataSetChanged();
                });
                builder.setNeutralButton("Edit", (dialog, which) -> {
                    // Chỉnh sửa thông tin task

                });

                // Hiển thị AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
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

        tvCategoriesMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoriesMenuFragment categoriesMenuFragment = new CategoriesMenuFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, categoriesMenuFragment); // Thay "R.id.fragment_container" bằng ID của container Fragment trong layout chính của bạn.
                transaction.addToBackStack(null); // Cho phép quay lại fragment Home khi nhấn nút Back.
                transaction.commit();
            }
        });

        return view;
    }

    private void filterTasksBySelectedCategories() {
        List<Task> filteredTasks = new ArrayList<>();
        List<Task> allTasks = db.getAllTasks();
        Log.d("HomeFragment", "All tasks: " + allTasks.size());
        // Check if no categories are selected
        if (selectedCategories.size() == 0) {
            // If no category is selected, show all tasks

            filteredTasks.addAll(db.getAllTasks());
        } else {
            // Otherwise, filter tasks based on selected categories
            for (Task task : db.getAllTasks()) {
                if (selectedCategories.get(task.getCategoryId(), false)) {
                    filteredTasks.add(task);
                }
            }
        }
        Log.d("Suuuuuuuuuuuuuuuuu", selectedCategories.size() + "");

        // Update the ListView with filtered tasks
        lvAdapter = new TasksArrayAdapter(requireActivity(), filteredTasks, categoryDb);
        listView.setAdapter(lvAdapter);
    }

}