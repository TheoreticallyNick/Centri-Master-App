package com.centri.centri_master_app.data;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.centri.centri_master_app.CentriApp;
import com.centri.centri_master_app.interfaces.ApiCallback;
import com.centri.centri_master_app.utils.LogUtil;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ApiManager {
    private static ApiManager instance;
    private Executor executor;
    private Handler handler;
    String TAG = ApiManager.class.getSimpleName();

    private ApiManager() {
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    synchronized public static ApiManager getInstance() {
        if (instance == null) { //if there is no DynamoDB instance, create a new instance
            instance = new ApiManager();
        }
        return instance; //else return the existing instance
    }

    synchronized public static ApiManager getInstance(boolean force) {
        if (force) { //if there is no DynamoDB instance, create a new instance
            instance = null;
        }
        return getInstance();
    }

    public void getDeviceList(ApiCallback<List<Map<String, String>>> callback) {
        LogUtil.d(TAG, "Called getDeviceList");
        callback.onStarted();
        LogUtil.d(TAG, "Executing getDeviceList API Call");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                RestOptions options = RestOptions.builder()
                        .addPath("/user-manager/" + CentriApp.userId + "/user-devices")
                        .build();

                Amplify.API.get(options,
                    response -> {
                        LogUtil.d(TAG, "Response Received from getDeviceList API Call");
                        List<Map<String, String>> deviceList = new ArrayList<Map<String, String>>();
                        JSONObject responseJson = null;

                        try {
                            responseJson = response.getData().asJSONObject();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONArray resultsArray = null;

                        try {
                            resultsArray = responseJson.getJSONArray("Results");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (resultsArray != null) {

                            for (int i = 0; i < resultsArray.length(); i++) {
                                Map<String, String> deviceMap = new HashMap<String, String>();

                                String deviceID = null;
                                try {
                                    deviceID = resultsArray.getJSONObject(i).getString("SK").substring(4);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String deviceName = null;
                                try {
                                    deviceName = resultsArray.getJSONObject(i).getString("DeviceName");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                deviceMap.put("DeviceID", new String(deviceID));
                                deviceMap.put("DeviceName", new String(deviceName));

                                deviceList.add(deviceMap);
                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    LogUtil.d(TAG, "Sending Processed API Response to Callback");
                                    callback.onSuccess(deviceList);
                                }
                            });

                        } else {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess(deviceList);
                                }
                            });
                        }
                    },
                    apiFailure -> {
                        Log.e("MyAmplifyApp", "GET failed.", apiFailure);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(apiFailure);
                            }
                        });
                    }
                );
            }
        });
    }



    public void getDeviceStatus(String deviceId, ApiCallback<Map<String, String>> callback) {
        LogUtil.d(TAG, "Called getDeviceStatus");
        callback.onStarted();
        LogUtil.d(TAG, "Executing getDeviceStatus API Call");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                RestOptions options = RestOptions.builder()
                        .addPath("/device-status/" + deviceId + "/full-status")
                        .build();

                Amplify.API.get(options,
                        response -> {
                            Map<String, String> deviceStatusMap = new HashMap<String, String>();
                            JSONObject responseJson = null;

                            try {
                                responseJson = response.getData().asJSONObject();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JSONArray resultsArray = null;

                            try {
                                resultsArray = responseJson.getJSONArray("Results");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            for (int i = 0; i < resultsArray.length(); i++) {

                                String deviceAuth = null;
                                String deviceLevel = null;
                                String deviceStreet = null;
                                String deviceUsage = null;
                                String deviceFillDate = null;

                                try {
                                    deviceAuth = resultsArray.getJSONObject(i).getString("DeviceAuth");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    deviceLevel = resultsArray.getJSONObject(i).getString("DeviceLevel");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    deviceStreet = resultsArray.getJSONObject(i).getString("DeviceStreet");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                deviceStatusMap.put("DeviceAuth", deviceAuth);
                                deviceStatusMap.put("DeviceLevel", deviceLevel);
                                deviceStatusMap.put("DeviceStreet", deviceStreet);

                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess(deviceStatusMap);
                                }
                            });
                        },
                        apiFailure -> {
                            Log.e("MyAmplifyApp", "GET failed.", apiFailure);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError(apiFailure);
                                }
                            });
                        }
                );
            }
        });
    }

    public void getDeviceAuth(String deviceId, ApiCallback<Map<String, String>> callback) {
        LogUtil.d(TAG, "Called getDeviceAuth");
        callback.onStarted();
        LogUtil.d(TAG, "Executing getDeviceAuth API Call");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                RestOptions options = RestOptions.builder()
                        .addPath("/device-manager/" + deviceId + "/device-auth")
                        .build();

                Amplify.API.get(options,
                        response -> {
                            Map<String, String> deviceAuthMap = new HashMap<String, String>();
                            JSONObject responseJson = null;

                            try {
                                responseJson = response.getData().asJSONObject();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JSONArray resultsArray = null;

                            try {
                                resultsArray = responseJson.getJSONArray("Results");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            for (int i = 0; i < resultsArray.length(); i++) {

                                String deviceAuth = null;

                                try {
                                    deviceAuth = resultsArray.getJSONObject(i).getString("DeviceAuth");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                deviceAuthMap.put("DeviceAuth", deviceAuth);

                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess(deviceAuthMap);
                                }
                            });
                        },
                        apiFailure -> {
                            Log.e("MyAmplifyApp", "GET failed.", apiFailure);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError(apiFailure);
                                }
                            });
                        }
                );
            }
        });
    }

    public void postPairDevice(String deviceId, String userId, String deviceName, ApiCallback<String> callback) {
        LogUtil.d(TAG, "Called postPairDevice");
        callback.onStarted();
        LogUtil.d(TAG, "Executing postPairDevice API Call");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> queryParams = new HashMap<String, String>();
                queryParams.put("user-id", userId);
                queryParams.put("device-name", deviceName);


                RestOptions options = RestOptions.builder()
                        .addPath("/device-manager/" + deviceId + "/pair-device")
                        .addQueryParameters(queryParams)
                        .build();

                Amplify.API.post(options,
                    response -> {

                        JSONObject responseJson;
                        String results_string = null;
                        String confirm;


                        try {
                            responseJson = response.getData().asJSONObject();
                            results_string = responseJson.getString("Results").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        confirm = results_string;

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(confirm);
                            }
                        });
                    },
                    apiFailure -> {
                        Log.e("MyAmplifyApp", "POST failed.", apiFailure);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(apiFailure);
                            }
                        });
                    }
                );
            }
        });
    }

    public void getDeviceTimeseries(String deviceId, String days, ApiCallback<JSONObject> callback) {
        LogUtil.d(TAG, "Called getDeviceTimeseries");
        callback.onStarted();
        LogUtil.d(TAG, "Executing getDeviceTimeseries API Call");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> queryParams = new HashMap<String, String>();
                queryParams.put("days", days);


                RestOptions options = RestOptions.builder()
                        .addPath("/telemetry/" + deviceId + "/device-level")
                        .addQueryParameters(queryParams)
                        .build();

                Amplify.API.get(options,
                        response -> {
                            //List<Map<String, String>> timeseriesData = new ArrayList<Map<String, String>>();
                            ArrayList<Entry> timeseriesData = new ArrayList<Entry>();
                            JSONObject responseJson = null;

                            try {
                                responseJson = response.getData().asJSONObject();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JSONObject resultsObject = null;

                            try {
                                resultsObject = responseJson.getJSONObject("Results");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (resultsObject != null) {

                                JSONObject finalResultsObject = resultsObject;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onSuccess(finalResultsObject);
                                    }
                                });

                            } else {
                                JSONObject finalResultsObject = resultsObject;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onSuccess(finalResultsObject);
                                    }
                                });
                            }
                        },
                        apiFailure -> {
                            Log.e("MyAmplifyApp", "POST failed.", apiFailure);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError(apiFailure);
                                }
                            });
                        }
                );
            }
        });
    }

    public void postUnpairDevice(String deviceId, String userId, ApiCallback<String> callback) {
        LogUtil.d(TAG, "Called postUnpairDevice");
        callback.onStarted();
        LogUtil.d(TAG, "Executing postUnpairDevice API Call");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> queryParams = new HashMap<String, String>();
                queryParams.put("user-id", userId);

                RestOptions options = RestOptions.builder()
                        .addPath("/device-manager/" + deviceId + "/unpair-device")
                        .addQueryParameters(queryParams)
                        .build();

                Amplify.API.post(options,
                    response -> {

                        JSONObject responseJson;
                        String results_string = null;
                        String confirm;


                        try {
                            responseJson = response.getData().asJSONObject();
                            results_string = responseJson.getString("Results").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        confirm = results_string;

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(confirm);
                            }
                        });
                    },
                    apiFailure -> {
                        Log.e("MyAmplifyApp", "POST failed.", apiFailure);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(apiFailure);
                            }
                        });
                    }
                );
            }
        });
    }

}
