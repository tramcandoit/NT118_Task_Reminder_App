package com.example.mobileapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignupFragment extends Fragment {
    private FirebaseAuth auth;
    private TextView tvLogIn;
    private LinearLayout tvSettings;
    private AppCompatButton btnSignUp;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtrePassword;
    private boolean isPasswordVisible = false;
    private ImageView imgHidePass1;
    private ImageView imgHidePass2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Khởi tạo Firebase Authentication
        auth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        // Anh xa
        tvLogIn = view.findViewById(R.id.tv_login);
        tvSettings = view.findViewById(R.id.tv_settings);
        edtEmail = view.findViewById(R.id.et_email_input);
        edtPassword = view.findViewById(R.id.et_pass_input);
        edtrePassword = view.findViewById(R.id.et_pass_reinput);
        btnSignUp = view.findViewById(R.id.btn_signup);
        imgHidePass1 = view.findViewById(R.id.ic_hide);
        imgHidePass2 = view.findViewById(R.id.ic_hide_re);


        // Quay ve fragment log in
        tvLogIn.setOnClickListener(v -> navigateToLogInFragment());
        tvSettings.setOnClickListener(v -> navigateToLogInFragment());
        btnSignUp.setOnClickListener(v ->
        {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String repassword = edtrePassword.getText().toString().trim();
            createAccount(email, password, repassword);
        });
        imgHidePass1.setOnClickListener(v -> setHideorVisiblePass());
        imgHidePass2.setOnClickListener(v -> setHideorVisiblePass());

        return  view;
    }

    private void navigateToLogInFragment()
    {
        LoginFragment loginFragment = new LoginFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, loginFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setHideorVisiblePass() {
        if (isPasswordVisible) {
            // Ẩn mật khẩu
            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imgHidePass1.setImageResource(R.drawable.ic_hide); // Icon mắt bị gạch
            imgHidePass2.setImageResource(R.drawable.ic_hide);
            isPasswordVisible = false;
        } else {
            // Hiển thị mật khẩu
            edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imgHidePass1.setImageResource(R.drawable.ic_show); // Icon mắt không bị gạch
            imgHidePass2.setImageResource(R.drawable.ic_show);
            isPasswordVisible = true;
        }

        // Đưa con trỏ về cuối EditText
        edtPassword.setSelection(edtPassword.length());
    }

    private void createAccount(String email, String password, String repassword) {
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

        // Kiểm tra password rỗng
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError(LanguageManager.getLocalizedText(requireContext(), "password_empty"));
            edtPassword.requestFocus();
            return;
        }


        // Kiểm tra password nhập lại
        if (TextUtils.isEmpty(repassword)) {
            edtrePassword.setError(LanguageManager.getLocalizedText(requireContext(), "repassword_empty"));
            edtrePassword.requestFocus();
            return;
        }

        // Kiểm tra password có khớp nhau không
        if (!password.equals(repassword)) {
            edtrePassword.setError(LanguageManager.getLocalizedText(requireContext(), "password_not_match"));
            edtrePassword.requestFocus();
            return;
        }

        // Hiển thị Progress Dialog
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(LanguageManager.getLocalizedText(requireContext(), "creating_account"));
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Tạo tài khoản bằng Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // Luôn đóng Progress Dialog
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        // Tạo tài khoản thành công
                        FirebaseUser user = auth.getCurrentUser();



                        // Chuyển đến màn hình tiếp theo
                        Toast.makeText(getContext(),
                                LanguageManager.getLocalizedText(requireContext(), "account_created"),
                                Toast.LENGTH_SHORT).show();

                        navigateToLogInFragment();
                    } else {
                        // Xử lý lỗi khi tạo tài khoản
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            edtPassword.setError(LanguageManager.getLocalizedText(requireContext(), "password_weak"));
                            edtPassword.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            edtEmail.setError(LanguageManager.getLocalizedText(requireContext(), "email_format_not_eligible"));
                            edtEmail.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            edtEmail.setError(LanguageManager.getLocalizedText(requireContext(), "email_already_exists"));
                            edtEmail.requestFocus();
                        } catch (Exception e) {
                            Toast.makeText(getContext(),
                                    LanguageManager.getLocalizedText(requireContext(), "signup_failed") + ": " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
