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
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.core.Amplify;
import com.centri.centri_master_app.CentriApp;
import com.centri.centri_master_app.R;
import com.centri.centri_master_app.databinding.FragmentRequestMonitorBinding;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.utils.Enums;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class RequestMonitorFragment extends Fragment implements View.OnClickListener {
    String TAG = RequestMonitorFragment.class.getSimpleName();
    FragmentRequestMonitorBinding binding;
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    AlertDialog loadingDialog, noInternetConnection;
    NavController navController;
    String userEmail, userFirst, userLast;

    public RequestMonitorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noInternetConnection = DialogUtils.buildNoInternetConnection(getActivity());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_request_monitor, container, false);
        binding.setOnClick(this);
        loadingDialog = DialogUtils.buildLoadingDialog(getContext());
        binding.setLifecycleOwner(getViewLifecycleOwner());
        isLoading.observe(getViewLifecycleOwner(), value -> {
            if (value) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.rlBottom.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.rlBottom.setVisibility(View.VISIBLE);
            }

        });
        fetchUserAttributes();
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        navController = Navigation.findNavController(view);
        switch (view.getId()) {
            case R.id.tv_back:
                boolean popBackStack = navController.popBackStack();
                if (!popBackStack) {
                    requireActivity().finish();
                }
                break;
            case R.id.tv_confirm:
                confirm();
                break;
        }
    }

    private void fetchUserAttributes() {
        isLoading.setValue(true);
        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    Log.i("AuthDemo", "User attributes = " + attributes.toString());
                    for (AuthUserAttribute attribute : attributes) {
                        AuthUserAttributeKey key = attribute.getKey();
                        if (key.equals(AuthUserAttributeKey.email())) {
                            userEmail = attribute.getValue();
                        } else if (key.equals(AuthUserAttributeKey.familyName())) {
                            userLast = attribute.getValue();
                        } else if (key.equals(AuthUserAttributeKey.name())) {
                            userFirst = attribute.getValue();
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

    public void confirm() {
        loadingDialog.show();
        RestOptions options = RestOptions.builder()
                .addHeaders(Collections.singletonMap("Authorization", CentriApp.idToken))
                .addPath("/device-request/" + CentriApp.userId)
                .addBody(("{\"UserEmail\": \"" + userEmail + "\"," +
                        "\"UserFirst\": \"" + userFirst + "\"," +
                        "\"UserLast\": \"" + userLast + "\"}").getBytes())
                .build();

        Amplify.API.post(options,
                response -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismiss();
                            Bundle bundle = new Bundle();
                            final String msg = "Your request has been sent. A member from our team will be in touch with you shortly.";
                            bundle.putString(com.centri.centri_master_app.fragments.SuccessFragment.MSG, msg);
                            bundle.putString(com.centri.centri_master_app.fragments.SuccessFragment.APP_BAR_TITLE, "Request Monitor Info");
                            bundle.putString(com.centri.centri_master_app.fragments.SuccessFragment.BUTTON_TITLE, "Back to Main Dashboard");
                            bundle.putSerializable(com.centri.centri_master_app.fragments.SuccessFragment.NAV_TYPE, Enums.SuccessNavigationType.DASHBOARD);
                            navController.navigate(R.id.action_requestMonitorFragment_to_orderSuccessFragment, bundle);
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
