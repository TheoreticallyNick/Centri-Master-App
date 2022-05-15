package com.centri.centri_master_app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.core.Amplify;
import com.centri.centri_master_app.R;
import com.centri.centri_master_app.data.models.Order;
import com.centri.centri_master_app.databinding.FragmentOrderDetailBinding;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.DialogUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

public class OrderDetailFragment extends Fragment implements View.OnClickListener {
    String TAG = OrderDetailFragment.class.getSimpleName();
    FragmentOrderDetailBinding binding;
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private Order order = new Order();
    AlertDialog loadingDialog;
    NavController navController;

    public OrderDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_detail, container, false);
        binding.setOnClick(this);
        loadingDialog = DialogUtils.buildLoadingDialog(getContext());
        binding.setLifecycleOwner(getViewLifecycleOwner());
        isLoading.observe(getViewLifecycleOwner(), value -> {
            if (value) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.svBody.setVisibility(View.GONE);
                binding.rlOrderBottom.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.svBody.setVisibility(View.VISIBLE);
                binding.rlOrderBottom.setVisibility(View.VISIBLE);
            }

        });
        fetchUserAttributes();
        return binding.getRoot();
    }

    private void fetchUserAttributes() {
        isLoading.setValue(true);
        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    Log.i("AuthDemo", "User attributes = " + attributes.toString());
                    for (AuthUserAttribute attribute : attributes) {
                        AuthUserAttributeKey key = attribute.getKey();
                        if (key.equals(AuthUserAttributeKey.email())) {
                            order.UserEmail = attribute.getValue();
                        } else if (key.equals(AuthUserAttributeKey.familyName())) {
                            order.UserLast = attribute.getValue();
                        } else if (key.equals(AuthUserAttributeKey.name())) {
                            order.UserFirst = attribute.getValue();
                        } else if (key.equals(AuthUserAttributeKey.phoneNumber())) {
                            order.UserPhone = attribute.getValue();
                        }
                    }
                    isLoading.postValue(false);
                },
                error -> {
                    Log.e("AuthDemo", "Failed to fetch user attributes.", error);
                    isLoading.postValue(false);
                }
        );
    }

    @Override
    public void onClick(View view) {
        navController = Navigation.findNavController(view);
        switch (view.getId()) {
            case R.id.tv_back:
                Activity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                }
                break;
            case R.id.tv_next:
                sendOrder();
                break;
            case R.id.btnInfo:
                navController.navigate(R.id.requestMonitorFragment);
                break;
        }
    }

    public void sendOrder() {
        String street = binding.txtOrderStreet.getText().toString();
        if (street.isEmpty()) {
            binding.txtOrderStreet.setError("Required");
            binding.txtOrderStreet.requestFocus();
            return;
        }
        order.OrderStreet = street;
        String city = binding.txtOrderCity.getText().toString();
        if (city.isEmpty()) {
            binding.txtOrderCity.setError("Required");
            binding.txtOrderCity.requestFocus();
            return;
        }
        order.OrderCity = city;
        String state = binding.txtOrderState.getText().toString();
        if (state.isEmpty()) {
            binding.txtOrderState.setError("Required");
            binding.txtOrderState.requestFocus();
            return;
        }
        order.OrderState = state;
        String zip = binding.txtOrderZip.getText().toString();
        if (zip.isEmpty()) {
            binding.txtOrderZip.setError("Required");
            binding.txtOrderZip.requestFocus();
            return;
        }
        order.OrderZip = zip;
        String size = binding.txtOrderTankSize.getText().toString();
        if (size.isEmpty()) {
            binding.txtOrderTankSize.setError("Required");
            binding.txtOrderTankSize.requestFocus();
            return;
        }
        order.OrderTankSize = size;
        String level = binding.txtOrderTankLevel.getText().toString();
        if (level.isEmpty()) {
            binding.txtOrderTankLevel.setError("Required");
            binding.txtOrderTankLevel.requestFocus();
            return;
        }
        order.OrderTankLevel = level;
        Bundle bundle = new Bundle();
        String json = new Gson().toJson(order);
        bundle.putString(Constants.KEY_ORDER_MODEL, json);
        navController.navigate(R.id.action_orderDetailFragment_to_orderConfirmFragment, bundle);

    }
}
