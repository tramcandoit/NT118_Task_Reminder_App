package com.example.mobileapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class LanguageFragment extends Fragment {
    private RadioGroup rgLanguage;
    private RadioButton rbEng, rbViet;
    private LinearLayout tvSetting;
    private TextView tvSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_language, container, false);

        // Ánh xạ các view
        rgLanguage = view.findViewById(R.id.rg_language);
        rbEng = view.findViewById(R.id.rb_eng);
        rbViet = view.findViewById(R.id.rb_viet);
        tvSetting = view.findViewById(R.id.tv_settings);
        tvSave = view.findViewById(R.id.tv_save);

        // Thiết lập RadioButton mặc định dựa trên ngôn ngữ hiện tại
        String currentLanguage = LanguageManager.getLanguage(requireContext());
        if (currentLanguage.equals("en")) {
            rbEng.setChecked(true);
        } else if (currentLanguage.equals("vi")) {
            rbViet.setChecked(true);
        }

        // Xử lý sự kiện quay lại Settings
        tvSetting.setOnClickListener(view1 -> {
            SettingsFragment settingsFragment = new SettingsFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, settingsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Xử lý sự kiện lưu ngôn ngữ
        tvSave.setOnClickListener(view12 -> saveLanguage());

        // Xử lý sự kiện chọn ngôn ngữ
        rgLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            // Không cần xử lý ở đây vì chỉ lưu khi nhấn nút Save
        });

        return view;
    }

    private void saveLanguage() {
        String selectedLanguage = "en"; // Mặc định

        if (rbViet.isChecked()) {
            selectedLanguage = "vi";
        }

        // Lưu ngôn ngữ
        LanguageManager.setLanguage(requireContext(), selectedLanguage);

        // Hiển thị thông báo
        Toast.makeText(requireContext(), "Language changed", Toast.LENGTH_SHORT).show();

        // Khởi động lại toàn bộ ứng dụng để áp dụng ngôn ngữ
        restartApp();
    }

    private void restartApp() {
        // Khởi động lại MainActivity
        requireActivity().recreate();
    }
}