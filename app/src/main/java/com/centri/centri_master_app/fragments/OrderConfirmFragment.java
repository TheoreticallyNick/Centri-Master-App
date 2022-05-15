package com.centri.centri_master_app.fragments;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.centri.centri_master_app.CentriApp;
import com.centri.centri_master_app.R;
import com.centri.centri_master_app.data.models.Order;
import com.centri.centri_master_app.databinding.FragmentOrderConfirmBinding;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.utils.Enums;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class OrderConfirmFragment extends Fragment implements View.OnClickListener {
    FragmentOrderConfirmBinding binding;
    private Order order;
    AlertDialog loadingDialog, noInternetConnection;
    NavController navController;

    public OrderConfirmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noInternetConnection = DialogUtils.buildNoInternetConnection(getActivity());
        Bundle arguments = getArguments();
        if (arguments != null) {
            String json = arguments.getString(Constants.KEY_ORDER_MODEL);
            order = new Gson().fromJson(json, Order.class);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_confirm, container, false);
        binding.setOnClick(this);
        loadingDialog = DialogUtils.buildLoadingDialog(getContext());
        if (order == null) {
            navController.popBackStack();
        }
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        navController = Navigation.findNavController(view);
        switch (view.getId()) {
            case R.id.tv_back:
                navController.popBackStack();
                break;
            case R.id.tv_confirm:
                confirm();
                break;
        }
    }

    public void confirm() {
        loadingDialog.show();
        RestOptions options = RestOptions.builder()
                .addHeaders(Collections.singletonMap("Authorization", CentriApp.idToken))
                .addPath("/orders/" + CentriApp.userId)
                .addBody(new Gson().toJson(order).getBytes())
                .build();

        Amplify.API.post(options,
                response -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismiss();
                            Bundle bundle = new Bundle();
                            final String msg = getString(R.string.order_confirm_success_notice, "Ford Propane");
                            bundle.putString(com.centri.centri_master_app.fragments.SuccessFragment.MSG, msg);
                            bundle.putString(com.centri.centri_master_app.fragments.SuccessFragment.BUTTON_TITLE, "Back to Main Dashboard");
                            bundle.putSerializable(com.centri.centri_master_app.fragments.SuccessFragment.NAV_TYPE, Enums.SuccessNavigationType.DASHBOARD);
                            navController.navigate(R.id.action_orderConfirmFragment_to_orderSuccessFragment, bundle);

                        }
                    });
                },
                error -> {
                    loadingDialog.dismiss();
                    Log.e("AmplifyAPI", error.getCause().toString());
                    //Handles no internet connection
                    if (error.getCause().toString().contains("UnknownHostException") ) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noInternetConnection.show();
                            }
                        });
                        return;
                    }
                }
        );
    }
}
