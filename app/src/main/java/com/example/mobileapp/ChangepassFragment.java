package com.example.mobileapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangepassFragment extends Fragment {
    private EditText edtCurrentPass, edtNewPass, edtReNewPass;
    private TextView tvChangePass;
    private TextView tvCancel;
    private ImageView imgHideCurrentPass, imgHideNewPass, imgHideReNewPass;

    private boolean isCurrentPassVisible = false;
    private boolean isNewPassVisible = false;
    private boolean isReNewPassVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_changepass, container, false);

        // Ánh xạ view
        initializeViews(view);

        // Thiết lập sự kiện
        setupEventListeners();

        return view;
    }

    private void initializeViews(View view) {
        edtCurrentPass = view.findViewById(R.id.et_pass_current);
        edtNewPass = view.findViewById(R.id.et_pass_new);
        edtReNewPass = view.findViewById(R.id.et_pass_confirm);
        tvChangePass = view.findViewById(R.id.tv_save);
        tvCancel = view.findViewById(R.id.tv_cancel);

        imgHideCurrentPass = view.findViewById(R.id.ic_hide_current);
        imgHideNewPass = view.findViewById(R.id.ic_hide_new);
        imgHideReNewPass = view.findViewById(R.id.ic_hide_confirm);
    }

    private void setupEventListeners() {
        // Sự kiện thay đổi mật khẩu
        tvChangePass.setOnClickListener(v -> validateAndChangePassword());

        // Sự kiện quay lại
        tvCancel.setOnClickListener(v -> navigateBack());

        // Sự kiện ẩn/hiện mật khẩu
        setupPasswordVisibilityToggles();
    }

    private void setupPasswordVisibilityToggles() {
        // Ẩn/hiện mật khẩu hiện tại
        imgHideCurrentPass.setOnClickListener(v -> togglePasswordVisibility(
                edtCurrentPass,
                imgHideCurrentPass,
                isCurrentPassVisible = !isCurrentPassVisible
        ));

        // Ẩn/hiện mật khẩu mới
        imgHideNewPass.setOnClickListener(v -> togglePasswordVisibility(
                edtNewPass,
                imgHideNewPass,
                isNewPassVisible = !isNewPassVisible
        ));

        // Ẩn/hiện mật khẩu nhập lại
        imgHideReNewPass.setOnClickListener(v -> togglePasswordVisibility(
                edtReNewPass,
                imgHideReNewPass,
                isReNewPassVisible = !isReNewPassVisible
        ));
    }

    private void togglePasswordVisibility(EditText editText, ImageView imageView, boolean isVisible) {
        if (isVisible) {
            // Hiển thị mật khẩu
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imageView.setImageResource(R.drawable.ic_show);
        } else {
            // Ẩn mật khẩu
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imageView.setImageResource(R.drawable.ic_hide);
        }

        // Đưa con trỏ về cuối EditText
        editText.setSelection(editText.length());
    }

    private void validateAndChangePassword() {
        // Lấy các giá trị từ EditText
        String currentPass = edtCurrentPass.getText().toString().trim();
        String newPass = edtNewPass.getText().toString().trim();
        String reNewPass = edtReNewPass.getText().toString().trim();

        // Kiểm tra mật khẩu hiện tại
        if (TextUtils.isEmpty(currentPass)) {
            edtCurrentPass.setError("Vui lòng nhập mật khẩu hiện tại");
            edtCurrentPass.requestFocus();
            return;
        }

        // Kiểm tra mật khẩu mới
        if (TextUtils.isEmpty(newPass)) {
            edtNewPass.setError("Vui lòng nhập mật khẩu mới");
            edtNewPass.requestFocus();
            return;
        }

        // Kiểm tra độ dài mật khẩu
        if (newPass.length() < 6) {
            edtNewPass.setError("Mật khẩu phải có ít nhất 6 ký tự");
            edtNewPass.requestFocus();
            return;
        }

        // Kiểm tra nhập lại mật khẩu
        if (TextUtils.isEmpty(reNewPass)) {
            edtReNewPass.setError("Vui lòng nhập lại mật khẩu mới");
            edtReNewPass.requestFocus();
            return;
        }

        // Kiểm tra mật khẩu mới và nhập lại khớp nhau
        if (!newPass.equals(reNewPass)) {
            edtReNewPass.setError("Mật khẩu không khớp");
            edtReNewPass.requestFocus();
            return;
        }

        // Kiểm tra mật khẩu mới và mật khẩu hiện tại không được giống nhau
        if (currentPass.equals(newPass)) {
            edtNewPass.setError("Mật khẩu mới phải khác mật khẩu cũ");
            edtNewPass.requestFocus();
            return;
        }

        // Thực hiện thay đổi mật khẩu
        changePassword(currentPass, newPass);
    }

    private void changePassword(String currentPass, String newPass) {
        // Hiển thị Progress Dialog
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Đang thay đổi mật khẩu...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Lấy người dùng hiện tại
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getEmail() != null) {
            // Tạo thông tin đăng nhập để xác thực
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), currentPass);

            // Xác thực lại người dùng
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Xác thực thành công, tiến hành đổi mật khẩu
                                user.updatePassword(newPass)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> updateTask) {
                                                progressDialog.dismiss();

                                                if (updateTask.isSuccessful()) {
                                                    // Đổi mật khẩu thành công
                                                    Toast.makeText(requireContext(),
                                                            "Thay đổi mật khẩu thành công",
                                                            Toast.LENGTH_SHORT).show();

                                                    // Quay về màn hình trước
                                                    navigateBack();
                                                } else {
                                                    // Đổi mật khẩu thất bại
                                                    Toast.makeText(requireContext(),
                                                            "Thay đổi mật khẩu thất bại",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // Xác thực thất bại
                                progressDialog.dismiss();
                                Toast.makeText(requireContext(),
                                        "Mật khẩu hiện tại không đúng",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Không có người dùng đăng nhập
            progressDialog.dismiss();
            Toast.makeText(requireContext(),
                    "Vui lòng đăng nhập lại",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateBack() {
        AccountFragment accountFragment = new AccountFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        transaction.replace(R.id.fragment_container, accountFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}