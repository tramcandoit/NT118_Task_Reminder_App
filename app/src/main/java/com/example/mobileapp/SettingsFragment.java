package com.example.mobileapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    // Khai báo các view
    private TextView tvAccount;
    private TextView tvNotiSound;
    private TextView tvProductivity;
    private TextView tvTheme;
    private TextView tvLanguage;
    private TextView tvHelpFeed;
    private TextView tvAbout;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // lấy ra các view
        tvAccount = view.findViewById(R.id.tv_account);
        tvNotiSound= view.findViewById(R.id.tv_noti_sound);
        tvProductivity = view.findViewById(R.id.tv_productivity);
        tvTheme = view.findViewById(R.id.tv_theme);
        tvLanguage = view.findViewById(R.id.tv_language);
        tvHelpFeed = view.findViewById(R.id.tv_help_feed);
        tvAbout = view.findViewById(R.id.tv_about);
        auth = FirebaseAuth.getInstance();

        // Xử lý sự kiện click
        tvAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = auth.getCurrentUser();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Áp dụng hiệu ứng slide sang phải
                transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

                if (currentUser == null) {
                    LoginFragment loginFragment = new LoginFragment();
                    transaction.replace(R.id.fragment_container, loginFragment);
                } else {
                    AccountFragment accountFragment = new AccountFragment();
                    transaction.replace(R.id.fragment_container, accountFragment);
                }

                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        tvNotiSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotiSoundFragment notiSoundFragment = new NotiSoundFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Áp dụng hiệu ứng slide sang phải
                transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

                transaction.replace(R.id.fragment_container, notiSoundFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        tvProductivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductivityFragment productivityFragment = new ProductivityFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Áp dụng hiệu ứng slide sang phải
                transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

                transaction.replace(R.id.fragment_container, productivityFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        tvTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThemeFragment themeFragment = new ThemeFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Áp dụng hiệu ứng slide sang phải
                transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

                transaction.replace(R.id.fragment_container, themeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        tvLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LanguageFragment languageFragment = new LanguageFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Áp dụng hiệu ứng slide sang phải
                transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

                transaction.replace(R.id.fragment_container, languageFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        tvHelpFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpFeedFragment helpFeedFragment = new HelpFeedFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Áp dụng hiệu ứng slide sang phải
                transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

                transaction.replace(R.id.fragment_container, helpFeedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutFragment aboutFragment = new AboutFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Áp dụng hiệu ứng slide sang phải
                transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

                transaction.replace(R.id.fragment_container, aboutFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });



        return view;
    }
}