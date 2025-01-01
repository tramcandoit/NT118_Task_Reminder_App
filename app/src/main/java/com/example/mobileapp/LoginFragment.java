package com.example.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.mobileapp.SettingsFragment;

public class LoginFragment extends Fragment {
    private FirebaseAuth auth;
    private EditText edtEmail;
    private EditText edtPassword;
    private AppCompatButton loginButton;
    private TextView tvSignUp;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Khởi tạo Firebase Authentication
        auth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Ánh xạ các view
        TextView tvSetting = view.findViewById(R.id.tv_settings);
        edtEmail = view.findViewById(R.id.et_email_input);
        edtPassword = view.findViewById(R.id.et_pass_input);
        loginButton = view.findViewById(R.id.btn_login);


        // Sự kiện chuyển đến Settings Fragment
        tvSetting.setOnClickListener(view1 -> {
            SettingsFragment settingsFragment = new SettingsFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, settingsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Sự kiện đăng nhập
        loginButton.setOnClickListener(v -> loginUser());

        return view;
    }

    private void loginUser() {
        // Lấy email và password
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Kiểm tra email và password
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        // Đăng nhập bằng Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            // Chuyển đến màn hình chính hoặc fragment khác
                            navigateToMainScreen();
                        } else {
                            // Đăng nhập thất bại
                            Toast.makeText(getContext(), "Đăng nhập thất bại: "
                                    + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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





    @Override
    public void onStart() {
        super.onStart();
        // Kiểm tra xem người dùng đã đăng nhập chưa khi fragment bắt đầu
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Nếu đã đăng nhập, chuyển thẳng đến màn hình chính
            navigateToMainScreen();
        }
    }


}