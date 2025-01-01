package com.example.mobileapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment {

    private EditText edtEmail;
    private AppCompatButton btnResetPassword;
    private ProgressDialog progressDialog;
    private LinearLayout tvSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        // Anh xa
        edtEmail = view.findViewById(R.id.edt_email);
        btnResetPassword = view.findViewById(R.id.btn_reset_pass);
        tvSettings = view.findViewById(R.id.tv_settings);

        // Khởi tạo Progress Dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(LanguageManager.getLocalizedText(requireContext(), "email_sent"));
        progressDialog.setCancelable(false);

        // Thực hiện gửi mail reset password khi bấm nút
        btnResetPassword.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            reqResetPassword(email);
        });

        // Back ve man hinh login
        tvSettings.setOnClickListener(v -> backtoLoginFragment());

        return view;
    }

    private void reqResetPassword(String email) {
        // Kiểm tra email rỗng
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(LanguageManager.getLocalizedText(requireContext(), "please_input_email"));
            edtEmail.requestFocus();
            return;
        }

        // Kiểm tra định dạng email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError(LanguageManager.getLocalizedText(requireContext(), "email_format_not_eligible"));
            edtEmail.requestFocus();
            return;
        }

        // Hiển thị Progress Dialog
        progressDialog.show();

        // Gửi email đặt lại mật khẩu
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    // ẩn Progress Dialog
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        // Gửi email thành công
                        Toast.makeText(getContext(),
                                LanguageManager.getLocalizedText(requireContext(), "email_sent"),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Gửi email thất bại
                        Toast.makeText(getContext(),
                                LanguageManager.getLocalizedText(requireContext(), "email_sent_failed"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Đảm bảo dialog bị dismiss khi fragment bị hủy
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void backtoLoginFragment()
    {
        LoginFragment loginFragment = new LoginFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, loginFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}