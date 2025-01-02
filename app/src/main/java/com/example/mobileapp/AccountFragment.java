package com.example.mobileapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private LinearLayout tvSetting;
    private Button btnChangePass;
    private EditText edtEmail;
    private AppCompatButton btnLogOut;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tvSetting = view.findViewById(R.id.tv_settings);
        edtEmail = view.findViewById(R.id.et_email_input);
        btnLogOut = view.findViewById(R.id.btn_logout);

        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();
        // Kiem tra neu da login chua
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null)
        {
            // Neu chua dang nhap, nhay ra man hinh chinh
            Toast.makeText(getContext(), "Bạn chưa đăng nhập !", Toast.LENGTH_SHORT).show();
            navigateToMainScreen();
        }
        else
        {
            // Neu da dang nhap, set email cua user hien tai len edit text Email
            edtEmail.setText(currentUser.getEmail());
        }


        // Click setting de quay ve man hinh fragment settings
        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsFragment settingsFragment = new SettingsFragment();

                // Thực hiện transaction để thay thế fragment hiện tại
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, settingsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        btnChangePass = view.findViewById(R.id.btn_change_pass);
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangepassFragment changePasswordFragment = new ChangepassFragment();

                // Thực hiện transaction để thay thế fragment hiện tại
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, changePasswordFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo AlertDialog
                new AlertDialog.Builder(requireContext())  // Thay 'context' bằng Context hiện tại (this hoặc requireContext())
                        .setTitle("Đăng Xuất")
                        .setMessage("Bạn đã chắc chắn muốn đăng xuất chứ?")
                        .setPositiveButton(LanguageManager.getLocalizedText(requireContext(), "yes"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Xử lý đăng xuấtt
                                FirebaseAuth.getInstance().signOut();

                                // Chuyển về màn hình chính
                                navigateToMainScreen();


                                // Thông báo đăng xuất
                                Toast.makeText(getContext(), LanguageManager.getLocalizedText(requireContext(), "logout_success"), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(LanguageManager.getLocalizedText(requireContext(), "no"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Đóng dialog nếu chọn "Không"
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });


        return view;
    }

    private void navigateToMainScreen() {
        // Chuyển đến MainActivity hoặc fragment chính
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            // Xóa hết các activity trước đó để không thể quay lại màn hình đăng nhập
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
