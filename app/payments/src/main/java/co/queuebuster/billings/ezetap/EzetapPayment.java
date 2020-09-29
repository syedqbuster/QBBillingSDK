package co.queuebuster.billings.ezetap;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EzetapPayment {

    private static final String TAG = "EzetapPayment";

    private static String APP_KEY = "";
    private String EZETAP_BLUETOOTH_NAME = "d200_5B007426";
    private BluetoothAdapter mBluetoothAdapter;
    private Activity activity;
    private BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private PaymentListener paymentListener;
    private String paymentJson = "";
    private String loginJson = "";
    private String voidJson = "";
    private boolean wasBlutoothNotStarted = false;
    public static final int BLUETOOTH_REQUEST = 111;


    public static EzetapPayment with(Activity activity) {
        return new EzetapPayment(activity);
    }

    public void connect() {
        _connect();
    }

    public EzetapPayment setAppKey(String appKey) {
        APP_KEY = appKey;
        return this;
    }

    public EzetapPayment setPaymentListener(@Nullable PaymentListener paymentListener) {
        this.paymentListener = paymentListener;
        return this;
    }

    public EzetapPayment setPayment(String username, String envType, String txnType, double amount, String externalRefNumber) {
        this.paymentJson = createPaymentJson(username,envType,txnType,amount,externalRefNumber).toString();
        return this;
    }

    public EzetapPayment setVoidPayment(String txnID, String username, String envType, String txnType) {
        this.voidJson = createVoidJson(username,envType,txnType,txnID).toString();
        return this;
    }

    public EzetapPayment setLoginInfo(String username, String envType, String txnType) {
        this.loginJson = createLoginJson(username,envType,txnType).toString();
        return this;
    }

    public interface PaymentListener {
        default void onDeviceResponse(String response, String msg) {

        }

        default void onFinalPaymentResonse(String paymentResponse) {

        }
        default void onVoidSuccess(String response) {

        }
        default void onError(String err) {
        }

        void onTransactionFailed(String response, String failedMsg);
    }

    public static boolean isBluetoothEnable() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    public static void enableBluetooth(Activity activity, int requestCode) {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBluetooth, requestCode);
    }

    public static void enableBluetooth(Fragment fragment, int requestCode) {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        fragment.startActivityForResult(enableBluetooth, requestCode);
    }

    public static class TxnType {
        public final static String LOGIN = "LOGIN";
        public final static String SALE = "SALE";
        public final static String VOID = "VOID";
    }

    public static class EnvType {
        public final static String DEMO = "DEMO";
        public final static String PROD = "PROD";
    }

    private EzetapPayment(Activity activity) {
        this.activity = activity;
    }

    private EzetapPayment _connect() {
        try {
            findBT();
        } catch (IOException e) {
            e.printStackTrace();
            if(paymentListener != null)
                paymentListener.onError("Error : " + e);
        }
        return this;
    }

    private EzetapPayment login() {
        try {
            sendData(loginJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    private void startPayment() {
        try {
            sendData(paymentJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void voidPayment() {
        try {
            sendData(voidJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onConnected() {
        makePayment();
    }

    private void makePayment() {
        if(!paymentJson.isEmpty()) {
            startPayment();
        }
        else if(!voidJson.isEmpty()) {
            voidPayment();
        }
    }

    private void onBTNotConnected() {
        //todo show dialog to pair device
        if(!wasBlutoothNotStarted) showBluetoothDialog();
    }

    private void onLoginSuccess() {
        startPayment();
    }

    private static JSONObject createLoginJson(String username, String envType, String txnType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("appKey", APP_KEY);
            jsonObject.put("EnvType", envType);
            jsonObject.put("TxnType", txnType);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JSONObject();
    }

    private static JSONObject createPaymentJson(String username, String envType, String txnType, double amount, String externalRefNumber) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("EnvType", envType);
            jsonObject.put("TxnType", txnType);
            jsonObject.put("appKey", APP_KEY);
            jsonObject.put("username", username);
            jsonObject.put("amount", amount);
            jsonObject.put("externalRefNumber", externalRefNumber);
            jsonObject.put("paymentMode", "CARD");
            jsonObject.put("nounce", "c5815 3c7-b263-4acc-b9be-a4aafb0b5987");  //O

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private static JSONObject createVoidJson(String username, String envType, String txnType, String txnId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("EnvType", envType);
            jsonObject.put("TxnType", txnType);
            jsonObject.put("appKey", APP_KEY);
            jsonObject.put("username", username);
            jsonObject.put("txnID", txnId);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    void findBT() throws IOException {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "findBT: No bluetooth adapter available");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBluetooth, 0);
            wasBlutoothNotStarted = true;
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(EZETAP_BLUETOOTH_NAME)) {
                    mmDevice = device;
                    break;
                }
            }
        }

        if (mmDevice == null) {
            if(paymentListener != null) paymentListener.onError("Device is not paired");
            onBTNotConnected();
        } else {
            openBT();
        }

        Log.e(TAG, "findBT: Bluetooth device not found");
    }

    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        //beginListenForData();

        new Thread(){
            @Override
            public void run() {
                read();
            }
        }.start();

        onConnected();

        Log.d(TAG, "openBT: Bluetooth Open");
    }

    void sendData(String msg) throws IOException {
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        Log.d(TAG, "sendData:" + msg);
    }

    void closeBT() {
        try {
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            Log.d(TAG, "closeBT: Bluetooth Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    StringBuilder paymentBuilder = new StringBuilder();
    private boolean startAppending = false;
    void read() {
        byte[] buffer = new byte[4096];
        int bytes;
        Handler handler = new Handler(Looper.getMainLooper());

        // Keep looping to listen for received messages
        while (mmInputStream != null) {
            try {
                bytes = mmInputStream.read(buffer);            //read bytes from input buffer
                String readMessage = new String(buffer, 0, bytes);
                Log.e(TAG, "run: " + readMessage );

                if(readMessage.contains("LOGIN SUCCESS")) {
                    handler.postDelayed(this::onLoginSuccess,1000);
                }

                if(!startAppending) {
                    JSONObject checkJson = null;
                    try {
                        checkJson = new JSONObject(readMessage);
                        if(checkJson.has("msgCode")) {
                            int msgCode = checkJson.getInt("msgCode");
                            if(msgCode == -211) {
                                handler.post(this::login);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                if(!paymentJson.isEmpty()) {
                    if(readMessage.contains("PAYMENT SUCCESS")) {
                        startAppending = true;
                    }
                    if(startAppending) {
                        paymentBuilder.append(readMessage);
                    }
                    if(readMessage.contains("twoStepConfirmPreAuth")) {
                        //completed
                        closeBT();
                        startAppending = false;
                        handler.post(() -> paymentListener.onFinalPaymentResonse(paymentBuilder.toString().trim()));
                    }
                }
                else if(!voidJson.isEmpty()) {
                    if(readMessage.contains("VOIDED SUCCESSFULLY")) {
                        //successfully voided
                        handler.post(() -> paymentListener.onVoidSuccess(readMessage));
                        closeBT();
                    }
                }

                if(readMessage.contains("TXN FAILED")) {
                    try {
                        JSONObject jsonObject = new JSONObject(readMessage);
                        if(jsonObject.getJSONObject("serverResp").has("message")) {
                            String failedMsg = jsonObject.getJSONObject("serverResp").getString("message");
                            handler.post(() -> paymentListener.onTransactionFailed(readMessage, failedMsg));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(readMessage.contains("PROCESS CANCELLED")) {
                    try {
                        JSONObject jsonObject = new JSONObject(readMessage);
                        if(jsonObject.has("msg")) {
                            String failedMsg = jsonObject.getString("msg");
                            handler.post(() -> paymentListener.onTransactionFailed(readMessage, failedMsg));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                handler.post(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(readMessage);
                        if(paymentListener != null) paymentListener.onDeviceResponse(readMessage, jsonObject.has("msg") ? jsonObject.getString("msg") : "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                });

                if(readMessage.contains("FAILED") || readMessage.contains("PROCESS CANCELLED")) {
                    handler.post(() -> paymentListener.onError(readMessage));
                    closeBT();
                }

            } catch (IOException e) {
                break;
            }
        }
    }

    private void showBluetoothDialog() {

        if(mBluetoothAdapter == null)
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null) {
            //No Bluetooth
            return;
        }

        List<String> deivceName = new ArrayList<>();

        for(BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
            deivceName.add(device.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,deivceName);
        //mBluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        BroadcastReceiver receiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    deivceName.add(device.getName());
                    adapter.notifyDataSetChanged();

                    Log.d(TAG, "onReceive:" + device.getName());
                }
            }
        };

        activity.registerReceiver(receiver,filter);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Select Bluetooth");

        LinearLayout main = new LinearLayout(activity);
        main.setOrientation(LinearLayout.VERTICAL);

        ListView listView = new ListView(activity);
        listView.setAdapter(adapter);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        main.addView(listView,layoutParams);

        alertDialog.setView(main);
        //alertDialog.setCancelable(true);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(activity, "name: " + deivceName.get(position), Toast.LENGTH_SHORT).show();
                EZETAP_BLUETOOTH_NAME = deivceName.get(position);

                alertDialog.create().dismiss();

                new Handler().postDelayed(() -> {
                    try {
                        findBT();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, 1000);

            }
        });

        alertDialog.setPositiveButton("Scan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBluetoothAdapter.startDiscovery();
            }
        });

        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(activity, "cancel", Toast.LENGTH_SHORT).show();
            }
        });


        alertDialog.show();
    }
}
