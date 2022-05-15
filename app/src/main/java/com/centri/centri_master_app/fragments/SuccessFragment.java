/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Success Fragment shows success message.
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-03-07
 */

package com.centri.centri_master_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.centri.centri_master_app.R;
import com.centri.centri_master_app.activities.MainActivity;
import com.centri.centri_master_app.databinding.FragmentSuccessBinding;
import com.centri.centri_master_app.utils.Enums;

import org.jetbrains.annotations.NotNull;

public class SuccessFragment extends Fragment implements View.OnClickListener {

    public static final String MSG = "msg";
    public static final String BUTTON_TITLE = "btn_title";
    public static final String APP_BAR_TITLE = "app_bar_title";
    public static final String NAV_TYPE = "nav_type";

    private String msg;
    private String btnTitle;
    Enums.SuccessNavigationType navType;
    FragmentSuccessBinding binding;

    public SuccessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            msg = getArguments().getString(MSG);
            btnTitle = getArguments().getString(BUTTON_TITLE);
            navType = (Enums.SuccessNavigationType) getArguments().getSerializable(NAV_TYPE);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_success, container, false);
        binding.setOnClick(this);
        if (msg != null) {
            binding.tvMsg.setText(msg);
        }
        if (btnTitle != null) {
            binding.tvNavigate.setText(btnTitle);
        }
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        NavController navController = Navigation.findNavController(view);
        int id = view.getId();
        if (id == R.id.tv_navigate) {
            if (navType != null && navType == Enums.SuccessNavigationType.DASHBOARD) {
                startActivity(new Intent(getContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        }

    }
}