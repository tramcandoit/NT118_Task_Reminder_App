package com.example.mobileapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HelpFeedFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    LinearLayout tvSetting;
    private FirebaseAuth auth;
    private EditText edtFeedbackType;
    private EditText edtFeedbackContent;
    private AppCompatButton btnSendFeedback;

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
        View view = inflater.inflate(R.layout.fragment_help_feedback, container, false);

        //Firebase
        auth = FirebaseAuth.getInstance();

        tvSetting = view.findViewById(R.id.tv_settings);
        edtFeedbackType = view.findViewById(R.id.et_feed_type);
        edtFeedbackContent = view.findViewById(R.id.et_feed_content);
        btnSendFeedback = view.findViewById(R.id.btn_send_feed);
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

        btnSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFeedback();
            }
        });
        return view;
    }

    private void sendFeedback() {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Nếu chưa đăng nhập, hiển thị thông báo và chuyển đến màn hình đăng nhập
            Toast.makeText(getContext(), "Please log in to send feedback", Toast.LENGTH_SHORT).show();

            // Chuyển đến Fragment đăng nhập (thay thế bằng tên Fragment đăng nhập của bạn)
            LoginFragment loginFragment = new LoginFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, loginFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            return;
        }

        // Get feedback details
        String feedbackType = edtFeedbackType.getText().toString().trim();
        String feedbackContent = edtFeedbackContent.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(feedbackType)) {
            edtFeedbackType.setError("Feedback type cannot be empty");
            return;
        }

        if (TextUtils.isEmpty(feedbackContent)) {
            edtFeedbackContent.setError("Feedback content cannot be empty");
            return;
        }

        // Create a map to store feedback data
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("user_id", currentUser.getUid());
        feedback.put("user_email", currentUser.getEmail()); // Thêm email người dùng (tùy chọn)
        feedback.put("feedback_type", feedbackType);
        feedback.put("feedback_content", feedbackContent);

        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add feedback to Firestore
        db.collection("feedbacks")
                .add(feedback)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Clear input fields
                        edtFeedbackType.setText("");
                        edtFeedbackContent.setText("");

                        // Show success message
                        Toast.makeText(getContext(), "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Show error message
                        Toast.makeText(getContext(), "Failed to submit feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
