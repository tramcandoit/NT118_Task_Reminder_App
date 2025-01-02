package com.example.mobileapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
    private AppCompatButton btnDeleteAcc;


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
        btnDeleteAcc = view.findViewById(R.id.btn_delete_acc);

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
                transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
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
        btnDeleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo AlertDialog
                new AlertDialog.Builder(requireContext())
                        .setTitle(LanguageManager.getLocalizedText(requireContext(), "del_account"))
                        .setMessage(LanguageManager.getLocalizedText(requireContext(), "del_account_confirm"))
                        .setPositiveButton(LanguageManager.getLocalizedText(requireContext(), "yes"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Hiển thị Progress Dialog
                                ProgressDialog progressDialog = new ProgressDialog(requireContext());
                                progressDialog.setMessage("Đang xóa tài khoản...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    // Yêu cầu xác thực lại
                                    reauthenticateAndDelete(user, progressDialog);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Hiện chưa có tài khoản", Toast.LENGTH_SHORT).show();
                                }
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

    private void reauthenticateAndDelete(FirebaseUser user, ProgressDialog progressDialog) {
        // Tạo dialog nhập mật khẩu
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reauthenticate, null);
        builder.setView(dialogView);

        EditText edtPassword = dialogView.findViewById(R.id.edt_password);

        builder.setTitle("Xác thực lại")
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = edtPassword.getText().toString().trim();

                        // Tạo thông tin đăng nhập
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(user.getEmail(), password);

                        // Xác thực lại
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Xác thực thành công, tiến hành xóa tài khoản
                                            deleteUserAccount(user, progressDialog);
                                        } else {
                                            // Xác thực thất bại
                                            progressDialog.dismiss();
                                            Toast.makeText(requireContext(),
                                                    "Xác thực thất bại. Vui lòng thử lại.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void deleteUserAccount(FirebaseUser user, ProgressDialog progressDialog) {
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            // Xóa tài khoản thành công
                            // Đăng xuất
                            FirebaseAuth.getInstance().signOut();

                            Toast.makeText(getContext(),
                                    "Tài khoản đã được xóa thành công",
                                    Toast.LENGTH_SHORT).show();

                            // Chuyển về màn hình đăng nhập
                            navigateToMainScreen();
                        } else {
                            // Xóa tài khoản thất bại
                            Toast.makeText(getContext(),
                                    "Không thể xóa tài khoản. Vui lòng thử lại.",
                                    Toast.LENGTH_SHORT).show();
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
}
