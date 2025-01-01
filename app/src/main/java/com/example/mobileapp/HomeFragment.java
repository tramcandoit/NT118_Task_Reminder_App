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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnTaskAddedListener{

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
    public void scheduleNotification(Task task) {
        Context context = requireContext();
        Intent intent = new Intent(context, TaskNotificationReceiver.class);
        intent.putExtra("taskName", task.getName());
        intent.putExtra("taskDescription", task.getDescription());
        intent.putExtra("taskId", (int) task.getTaskId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                task.getTaskId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()); // Kết hợp date và time
            // Kết hợp ngày và giờ từ EditText
            String dateTimeString = task.getDate() + " " + task.getTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(dateTimeString)); // Parse cả ngày và giờ

            switch (task.getRepeat_frequency()) {
                case "Once":
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent);
                    break;
                case "Daily":
                    scheduleRepeatingNotification(
                            alarmManager,
                            pendingIntent,
                            calendar,
                            task.getRepeat_frequency()
                    );
                    break;
                case "Weekly":
                    scheduleRepeatingNotification(
                            alarmManager,
                            pendingIntent,
                            calendar,
                            task.getRepeat_frequency()
                    );
                    break;
            }
        } catch (ParseException e) {
            // Xử lý ParseException
            Toast.makeText(getContext(), "Invalid date/time format", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void scheduleRepeatingNotification(AlarmManager alarmManager, PendingIntent pendingIntent, Calendar calendar, String frequency) {
        long interval;
        if ("Daily".equals(frequency)) {
            interval = AlarmManager.INTERVAL_DAY;
        } else if ("Weekly".equals(frequency)) {
            interval = AlarmManager.INTERVAL_DAY * 7;
        } else {
            return; // Không hỗ trợ tần suất khác
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );

        calendar.add(Calendar.MILLISECOND, (int) interval); // Cập nhật thời gian cho lần lặp tiếp theo
        AlarmManager finalAlarmManager = alarmManager;
        new android.os.Handler().postDelayed(() -> {
            scheduleRepeatingNotification(finalAlarmManager, pendingIntent, calendar, frequency);
        }, interval);
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




        categoriesAdapter = new CategoriesAdapter(requireContext(), categories); // Hoặc "this" nếu trong Activity
        rvCategories.setAdapter(categoriesAdapter);

        if (categories.size() > 4) {
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
                View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_addtask, null);
                builder.setView(dialogView);

                // Tìm các TextView trong dialog layout và thiết lập giá trị
                EditText etTaskdetailName = dialogView.findViewById(R.id.tv_addtask_addtask_textbox);
                Spinner spTaskdetailCategories = dialogView.findViewById(R.id.sp_addtask_categories);
                EditText etTaskdetailDateSelector = dialogView.findViewById(R.id.et_addtask_date_selector);
                EditText etTaskdetailTimeSelector = dialogView.findViewById(R.id.et_addtask_time_selector);
                Spinner spTaskdetailPriority = dialogView.findViewById(R.id.sp_addtask_priority);
                Spinner spTaskdetailFrequencySelector = dialogView.findViewById(R.id.sp_addtask_frequency_selector);
                EditText etTaskdetailDescription = dialogView.findViewById(R.id.et_addtask_description);

                int spinnerCategoryPosition = categoriesAdapter.getItemPositionByName(categoryLabel);
                SpinnerAdapter categoriesSpAdapter = new SpinnerAdapter(getContext(), categories);
                spTaskdetailCategories.setAdapter(categoriesSpAdapter);

                List<String> priorities = new ArrayList<>();
                priorities.add("High");
                priorities.add("Medium");
                priorities.add("Low");
                ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getContext(), R.layout.addtask_spinner_item_text, priorities);
                priorityAdapter.setDropDownViewResource(R.layout.addtask_spinner_item_text);
                spTaskdetailPriority.setAdapter(priorityAdapter);
                int spinnerPriorityPosition = priorityAdapter.getPosition(clickedTask.getPriority());

                List<String> frequencies = new ArrayList<>();
                frequencies.add("Daily");
                frequencies.add("Weekly");
                frequencies.add("Once");
                ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(getContext(), R.layout.addtask_spinner_item_text, frequencies);
                frequencyAdapter.setDropDownViewResource(R.layout.addtask_spinner_item_text);
                spTaskdetailFrequencySelector.setAdapter(frequencyAdapter);
                int spinnerFrequencyPosition = frequencyAdapter.getPosition(clickedTask.getRepeat_frequency());


                // Tạo layout cho AlertDialog (nếu cần hiển thị nhiều thông tin hơn)
                etTaskdetailName.setText(clickedTask.getName());
                spTaskdetailCategories.setSelection(spinnerCategoryPosition);
                etTaskdetailDateSelector.setText(clickedTask.getDate());
                etTaskdetailTimeSelector.setText(clickedTask.getTime());
                spTaskdetailPriority.setSelection(spinnerPriorityPosition);
                spTaskdetailFrequencySelector.setSelection(spinnerFrequencyPosition);
                etTaskdetailDescription.setText(clickedTask.getDescription());

                // Khóa các trường không cho phép chỉnh sửa
                etTaskdetailName.setFocusable(false);
                etTaskdetailName.setFocusableInTouchMode(false);

                etTaskdetailDateSelector.setFocusable(false);
                etTaskdetailDateSelector.setFocusableInTouchMode(false);

                etTaskdetailTimeSelector.setFocusable(false);
                etTaskdetailTimeSelector.setFocusableInTouchMode(false);

                etTaskdetailDescription.setFocusable(false);
                etTaskdetailDescription.setFocusableInTouchMode(false);

                spTaskdetailCategories.setOnTouchListener((v, event) -> true);
                spTaskdetailPriority.setOnTouchListener((v, event) -> true);
                spTaskdetailFrequencySelector.setOnTouchListener((v, event) -> true);

                // Thiết lập các sự kiện cho các nút
                builder.setPositiveButton("OK", (dialog, which) -> {
                    // Lấy dữ liệu từ các trường EditText, Spinner
                    clickedTask.setName(etTaskdetailName.getText().toString());
                    clickedTask.setCategoryId(categories.get(spTaskdetailCategories.getSelectedItemPosition()).getCategoryId()); // Lấy category từ danh sách categories với vị trí tương úng trong spinner spCategories, sau đó dùng getCategoryId()
                    clickedTask.setDate(etTaskdetailDateSelector.getText().toString());
                    clickedTask.setTime(etTaskdetailTimeSelector.getText().toString());
                    clickedTask.setPriority(spTaskdetailPriority.getSelectedItem().toString());
                    clickedTask.setRepeat_frequency(spTaskdetailFrequencySelector.getSelectedItem().toString());
                    clickedTask.setDescription(etTaskdetailDescription.getText().toString());

                    // Cập nhật Task trong Database
                    db.updateTask(clickedTask);

                    // Thêm vào danh sách tasks ở HomeFragment
                    tasksList.set(position, clickedTask);
                    cancelNotification(clickedTask);
                    scheduleNotification(clickedTask);
                    lvAdapter.notifyDataSetChanged();

                    // Sau khi lưu, đóng dialog
                    dialog.dismiss();
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.setNeutralButton("Edit", (dialog, which) -> {
                    // Khởi tạo nút Edit
                });

                // Hiển thị AlertDialog
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(dialogInterface -> {
                    Button editButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

                    editButton.setOnClickListener(v -> {
                        // Chỉnh sửa thông tin task
                        etTaskdetailName.setFocusable(true);
                        etTaskdetailName.setFocusableInTouchMode(true);

                        etTaskdetailDateSelector.setFocusable(true);
                        etTaskdetailDateSelector.setFocusableInTouchMode(true);

                        etTaskdetailTimeSelector.setFocusable(true);
                        etTaskdetailTimeSelector.setFocusableInTouchMode(true);

                        etTaskdetailDescription.setFocusable(true);
                        etTaskdetailDescription.setFocusableInTouchMode(true);

                        spTaskdetailCategories.setOnTouchListener(null);
                        spTaskdetailPriority.setOnTouchListener(null);
                        spTaskdetailFrequencySelector.setOnTouchListener(null);

                    });
                });
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


}
