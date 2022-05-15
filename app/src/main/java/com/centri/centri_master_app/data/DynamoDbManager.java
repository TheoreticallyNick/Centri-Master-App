package com.centri.centri_master_app.data;/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Add Device Activity prompts the user to add a device.
 * Once the user inputs device name and device ID, they are taken to the Authentication Fragment
 * to enter the 6 digit code sent to their email address for authentication. Once the authentication
 * done, they are taken to success , where they are prompted
 * to return to the Dashboard screen.
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-02-05

package com.centri.mypropane.data;

import android.os.Handler;
import android.os.Looper;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.mobileconnectors.dynamodbv2.document.DeleteItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.PutItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.centri.mypropane.interfaces.DynamoDbCallback;
import com.centri.mypropane.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DynamoDbManager {
    private static DynamoDbManager instance;
    private AmazonDynamoDBClient dynamoDBClient;
    private AWSCredentials credentials;
    private Executor executor;
    private Handler handler;
    public static String TABLE_NAME = "Centri_Main";

    private DynamoDbManager(AWSCredentials cd) {
        dynamoDBClient = new AmazonDynamoDBClient(cd);
        dynamoDBClient.setRegion(Region.getRegion(Regions.US_EAST_1));
        this.credentials = cd;
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    synchronized public static DynamoDbManager getInstance(AWSCredentials cd) {
        if (instance == null) { //if there is no DynamoDB instance, create a new instance
            instance = new DynamoDbManager(cd);
        }
        return instance; //else return the existing instance
    }

    synchronized public static DynamoDbManager getInstance(AWSCredentials cd, boolean force) {
        if (force) { //if there is no DynamoDB instance, create a new instance
            instance = null;
        }
        return getInstance(cd);
    }

    public Table getTableByName(String name) {
        return Table.loadTable(dynamoDBClient, name);
    }

    public Document insert(Document document) {
        Table table = Table.loadTable(dynamoDBClient, TABLE_NAME);
        PutItemOperationConfig config = new PutItemOperationConfig();
        config.withReturnValues(ReturnValue.ALL_OLD);
        return table.putItem(document, config);
    }

    /*
    public void getDevice(String deviceId, DynamoDbCallback<Document> callback) {
        callback.onStarted();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Table table = Table.loadTable(dynamoDBClient, TABLE_NAME);
                    Document item = table.getItem(new Primitive("DEV#" + deviceId), new Primitive("STATUS#" + deviceId));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(item);
                        }
                    });
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }

            }
        });
    }
     */
/*
    public void addDevice(Document userDev, Document devUser, DynamoDbCallback<Document> callback) {
        callback.onStarted();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Table table = Table.loadTable(dynamoDBClient, TABLE_NAME);
                    PutItemOperationConfig config = new PutItemOperationConfig();
                    config.withReturnValues(ReturnValue.ALL_OLD);
                    UpdateItemOperationConfig updateConfig = new UpdateItemOperationConfig();
                    updateConfig.withReturnValues(ReturnValue.UPDATED_NEW);
                    final Document udev = table.putItem(userDev, config);
                    final Document devU = table.putItem(devUser, config);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (udev != null && devU != null)
                                callback.onSuccess(udev);
                            else
                                callback.onSuccess(null);
                        }
                    });
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }

            }
        });
    }

 */

    /*
    public void getDeviceTimeSeriesData(String deviceId, String start, String end, DynamoDbCallback<List<Map<String, AttributeValue>>> callback) {
        callback.onStarted();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
                    expressionAttributeValues.put(":id", new AttributeValue().withS("DEV#" + deviceId));
                    //expressionAttributeValues.put(":start", new AttributeValue().withS(start));
                    expressionAttributeValues.put(":end", new AttributeValue().withS(end));

                    QueryRequest queryRequest = new QueryRequest()
                            .withTableName(TABLE_NAME)
                            .withKeyConditionExpression("PK = :id AND SK <= :end")
                            .withLimit(2190)
                            .withExpressionAttributeValues(expressionAttributeValues);

                    QueryResult queryResult = dynamoDBClient.query(queryRequest);


                    ScanRequest scanRequest = new ScanRequest()
                            .withTableName(TABLE_NAME)
                            .withFilterExpression("PK = :id AND SK between :start AND :end")// inclusive start and end
                            //.withProjectionExpression("ts, id, lvl")
                            .withExpressionAttributeValues(expressionAttributeValues);
                    ScanResult scan = dynamoDBClient.scan(scanRequest);


                    List<Map<String, AttributeValue>> items = queryResult.getItems();


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(items);
                        }
                    });
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }

            }
        });
    }
     */

    /*
    public void getDeviceLists(String userId, DynamoDbCallback<List<Map<String, AttributeValue>>> callback) {
        callback.onStarted();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
                    expressionAttributeValues.put(":id", new AttributeValue().withS("USER#" + userId));
                    expressionAttributeValues.put(":start", new AttributeValue().withS("DEV#"));

                    QueryRequest queryRequest = new QueryRequest()
                            .withTableName(TABLE_NAME)
                            .withKeyConditionExpression("PK = :id AND begins_with(SK,:start)")
                            .withExpressionAttributeValues(expressionAttributeValues);

                    QueryResult queryResult = dynamoDBClient.query(queryRequest);


                    ScanRequest scanRequest = new ScanRequest()
                            .withTableName(TABLE_NAME)
                            .withFilterExpression("PK = :id AND begins_with(SK,:start)")// inclusive start and end
                            //.withProjectionExpression("PK, SK, Altitude")
                            .withExpressionAttributeValues(expressionAttributeValues);
                    ScanResult scan = dynamoDBClient.scan(scanRequest);


                    List<Map<String, AttributeValue>> items = queryResult.getItems();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(items);
                        }
                    });
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }

            }
        });
    }
    */

    /*
    public void deleteDevice(String userId, String deviceId, @NotNull DynamoDbCallback<Document> callback) {
        callback.onStarted();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Table table = Table.loadTable(dynamoDBClient, TABLE_NAME);
                    DeleteItemOperationConfig config = new DeleteItemOperationConfig();
                    config.setReturnValue(ReturnValue.ALL_OLD);
                    Document uDev = table.deleteItem(new Primitive("USER#" + userId), new Primitive("DEV#" + deviceId));
                    Document devU = table.deleteItem(new Primitive("DEV#" + deviceId), new Primitive("USER#" + userId));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(uDev);
                        }
                    });
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }

            }
        });
    }

     */

    /*
    public void getDeviceProvider(String deviceId, DynamoDbCallback<Document> callback) {
        callback.onStarted();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Table table = Table.loadTable(dynamoDBClient, TABLE_NAME);
                    Document item = table.getItem(new Primitive("DEV#" + deviceId), new Primitive("STATUS#" + deviceId));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(item);
                        }
                    });
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }

            }
        });
    }

}
        */
