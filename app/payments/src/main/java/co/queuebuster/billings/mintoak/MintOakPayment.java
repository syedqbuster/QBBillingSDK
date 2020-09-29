package co.queuebuster.billings.mintoak;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.StringDef;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import co.queuebuster.qbbillingutils.HexUtil;
import co.queuebuster.qbbillingutils.QbUtils;
import co.queuebuster.qbbillingutils.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public class MintOakPayment {

    private static final String TAG = "MintOakPayment";
    public static final int PAYMENT_REQUEST = 900;
    private Builder builder;
    private Cipher cipher;
    private SecretKeySpec skeySpec;

    private static String API_KEY;  //"6GGqIJeroRVwQi3RGT5nOAsf2r";
    private static String CRYPTO_KEY; //"a4e11920212a47d85358bb86ba750f37";
    private static String TERMINAL_ID; //"9820181246";
    private final String URL = BuildConfig.MINTOAK_API_URL;


    /**
     * Call this method when you application start.
     * @param apiKey Provide by MintOak
     * @param terminalId Provide by MintOak
     * @param cryptoKey Provide by MintOak
     */
    public static void configure(String apiKey, String terminalId, String cryptoKey) {
        API_KEY = apiKey;
        TERMINAL_ID = terminalId;
        CRYPTO_KEY = cryptoKey;
    }

    private MintOakPayment(Builder builder) {
        this.builder = builder;
    }

    /***
     * Call this method to start payment
     * Then call {@link MintOakPayment#setResult(int, int, Intent)} in onActivityResult()
     * after that you will get the response in {@link Listener#onResponse(String, String, String, String)} )}
     */
    public void pay() {
        if (builder.activity != null) {
            builder.activity.startActivityForResult(createIntent(), PAYMENT_REQUEST);
        } else if (builder.fragment != null) {
            builder.fragment.startActivityForResult(createIntent(), PAYMENT_REQUEST);
        } else {
            if (builder.listener != null) builder.listener.onFailed("Fragment or Activity is null");
            Log.e("Error", "Fragment or Activity is null");
        }
    }

    /**
     * Call this method to pay through mintoak api
     * after that you will get the response in {@link Listener#onResponse(String, String, String, String)} )}
     */
    public void payThroughApi() {

        //ENCRYPT PAYLOAD
        String requestMsg = encrypt(apiPaymentPayload(),CRYPTO_KEY);

        //RequestBody
        JsonObject reqBody = new JsonObject();
        reqBody.addProperty("requestMsg",requestMsg);
        reqBody.addProperty("terminalID",TERMINAL_ID);


        RetrofitClient.getClientNormal(URL)
                .create(Api.class)
                .getNotificationForPayments(API_KEY,reqBody)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            String responseMsg = response.body().get("responseMsg").getAsString();

                            String decryptedResp = decrypt(responseMsg,CRYPTO_KEY);
                            try {
                                JSONObject jsonObject = new JSONObject(decryptedResp);
                                String status = jsonObject.getString("status");
                                String respMsg = jsonObject.getString("respMsg");
                                String mintoakTxnid = jsonObject.getString("mintoakTxnId");

                                if(builder.listener != null)
                                    builder.listener.onResponse(status,mintoakTxnid,builder.paymentMode,decryptedResp);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                if(builder.listener != null) builder.listener.onFailed(e.getMessage());
                            }

                        }else {
                            String error = QbUtils.handleError(response,response.code());
                            if(builder.listener != null) builder.listener.onFailed(error);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        String error = QbUtils.handleError(t,-1);
                        if(builder.listener != null) builder.listener.onFailed(error);
                    }
                });

    }


    /***
     *  Call this method in onActivityResult()
     *  Bundle[{ivNumber=1598440234, Status=InProgress}]
     */
    public void setResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYMENT_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            String sStatus = data.getStringExtra("Status");
            String sIvNumber = data.getStringExtra("ivNumber");

            if (builder.listener != null)
                builder.listener.onResponse(sStatus, sIvNumber, builder.paymentMode, null);


        } else if (requestCode == PAYMENT_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            if (builder.listener != null) builder.listener.onFailed("Payment Cancelled");
        }
    }

    /***
     * The calling program can check on the status of an earlier transaction by querying the Mintoak Server
     * It is Api Call so use progressbar before calling this method.
     */
    public void checkStatus(String clientRefId, String mintoakTxnid) {

        //SAMPLE DATA
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("clientRefId", clientRefId);
        jsonObject.addProperty("mintoakTxnid", mintoakTxnid);

        //ENCRYPTING
        String requestMsg = encrypt(jsonObject.toString(), CRYPTO_KEY);

        //ENCRYPTED REQUEST
        JsonObject encObj = new JsonObject();
        encObj.addProperty("requestMsg", requestMsg);
        encObj.addProperty("terminalID", TERMINAL_ID);


        RetrofitClient
                .getClientNormal(URL)
                .create(Api.class)
                .checkPaymentStatus(API_KEY, encObj)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String responseMsg = response.body().get("responseMsg").getAsString();

                            //DECRYPTING RESPONSE
                            String responseData = decrypt(responseMsg, CRYPTO_KEY);
                            try {
                                JSONObject json = new JSONObject(responseData);
                                String status = json.getString("txnStatus");
                                String mintoakTxnid = json.getString("mintoakTxnId");
                                if (builder.listener != null)
                                    builder.listener.onResponse(status, mintoakTxnid, builder.paymentMode, responseData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (builder.listener != null)
                                    builder.listener.onFailed(e.getMessage());
                            }

                        } else {
                            String error = QbUtils.handleError(response,response.code());
                            if(builder.listener != null) builder.listener.onFailed(error);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        String error = QbUtils.handleError(t,-1);
                        if(builder.listener != null) builder.listener.onFailed(error);

                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "Check Status Error " + t.getMessage());
                        }
                    }
                });
    }

    private Intent createIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, builder.remarks);
        sendIntent.putExtra("amount", String.format(Locale.getDefault(), "%.2f", builder.amount));
        sendIntent.putExtra("invoiceNum", builder.invoiceNumber);
        sendIntent.putExtra("mobNum", builder.mobileNumber);
        sendIntent.putExtra("paymentMode", builder.paymentMode);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.transaction.mintoak");
        return sendIntent;
    }

    private String apiPaymentPayload() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("amount", String.format(Locale.getDefault(), "%.2f", builder.amount));
        jsonObject.addProperty("paymentMode", builder.paymentMode);
        jsonObject.addProperty("terminalID", TERMINAL_ID);
        jsonObject.addProperty("clientRefId", builder.invoiceNumber);

        if(builder.mobileNumber != null)
            jsonObject.addProperty("mobNum", builder.mobileNumber);

        return  jsonObject.toString();
    }

    /**
     * This method is used to init encryption
     *
     * @param key
     * @throws Exception
     */
    private  void initEncrypt(String key) throws Exception {
        try {
            skeySpec = new SecretKeySpec(HexUtil.HexfromString(key), "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(1, skeySpec);
        } catch (NoSuchAlgorithmException nsae) {
            throw new Exception("Invalid Java Version");
        } catch (NoSuchPaddingException nse) {
            throw new Exception("Invalid Key");
        }
    }

    /**
     * This method is used to init decryption
     *
     * @param key
     * @throws Exception
     */
    private void initDecrypt(String key) throws Exception {
        try {
            skeySpec = new SecretKeySpec(HexUtil.HexfromString(key), "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(2, skeySpec);

        } catch (NoSuchAlgorithmException nsae) {
            throw new Exception("Invalid Java Version");
        } catch (NoSuchPaddingException nse) {
            throw new Exception("Invalid Key");
        }
    }

    private String encrypt(String payload, String encKey) {
        try {
            initEncrypt(encKey);

            byte encstr[] = cipher.doFinal(payload.getBytes());
            String encData = HexUtil.HextoString(encstr);
            return URLEncoder.encode(encData);
        } catch (BadPaddingException nse) {
            return  payload;
        } catch (Exception e) {
            e.printStackTrace();
            return payload;
        }
    }

    private String decrypt(String payload, String decKey) {
        try {

            initDecrypt(decKey);

            byte[] encstr = cipher.doFinal(HexUtil.HexfromString(payload));
            return new String(encstr);
        } catch (BadPaddingException | IllegalBlockSizeException nse) {
            return payload;
        } catch (Exception e) {
            e.printStackTrace();
            return payload;
        }
    }

    private interface Api {

        /**
         * @param apiKey      6GGqIJeroRVwQi3RGT5nOAsf2r
         * @param requestBody Send request body in encrypted form with below key
         *                    Encrypted Request
         *                    ------------------------------
         *                    {
         *                    "requestMsg":"43DCA91AF91B588B90E29A32463657BC115B66F5871468D60943B4EF6
         *                    604C3C118D241193BA95B329960574321BAB80F1BE6D594B1768EAB6B95E7E2",
         *                    "terminalID":"12345678"
         *                    }
         *                    <p>
         *                    Sample Decrypted data:
         *                    ---------------------------------------------------
         *                    {"clientRefId":"12345",”mintoakTxnid”:”3434934787”}
         * @return it will return jsonObject and responseMsg key contains encrypted data
         * Encrypted Response
         * ------------------------------
         * {"responseMsg":"0543AD0135ECC477680AA58651EE5F539C97D6487FDBDFE3E54B8D6
         * D810976DED391175C458E900DE66D36C3BBCA738DE01ECD828619904C6F6D4EA90FF
         * EC063"}
         * <p>
         * Decrypted response data for
         * --------------------------------
         * {
         * "mintoakTxnid": "123124385",
         * "txnStatus": "SaleFailed",
         * "txnMessage": "PIN verification failed",
         * "clientRefId": "13083171324",
         * "transactionTime": "2018-11-12 12:34:12",
         * "amount": "5.0",
         * "paymentMode": "card",
         * "terminalID": "12345678",
         * "invoiceNum": "12412 48172414",
         * "cardType": "VISA",
         * "creditDebitCardType": "DD"
         * }
         */
        @POST("getPaymentStatus")
        Call<JsonObject> checkPaymentStatus(@Header("apiKey") String apiKey, @Body JsonObject requestBody);

        /**
         * This API is used to trigger the Notification for the particular payment mode in Mintoak
         * Transaction App.
         *
         * @param apiKey      Secret Key shared with the partner required for API calls to the Mintoak Server
         * @param requestBody A JsonObject with key
         *                    <b>requestMsg</b> Encrypted value of the payLoad value
         *                    <b>terminalID</b> Mpos terminalID of the terminal linked to the POS
         *                    <p>
         *                    payLoad Parameters: amount(M), paymentMode(M), terminalID(M), mobNum(O), invoiceNum(O), clientRefId(M),
         *                    extraParam(O)
         *                    <p>
         *                    ENCRYPTED REQUEST
         *                    ------------------
         *                    {
         *                    "requestMsg": "43DCA91AF91B588B90E29A32463657BC115B66F5871468D60943B4EF660 4C3C118D241193BA95B329960574321BAB80F1BE6D594B1768EAB6B95E7E2",
         *                    "terminalID": "12345678"
         *                    }
         *                    <p>
         *                    DECRYPTED PAYLOAD REQUEST
         *                    -------------------------
         *                    {
         *                    "amount": "1",
         *                    "paymentMode": "card",
         *                    "terminalID": "12345678",
         *                    "mobNum": "9742359585",
         *                    "invoice Num": "985231456",
         *                    "clientRefId": "2019231712",
         *                    "extraParam": {
         *                    "param1": "value1",
         *                    "param2": "valu e2"
         *                    }
         *                    }
         * @return A jsonObject with key <b>responseMsg</b> Encrypted value of the responsePayload value
         * Decrypted Response Keys
         * -----------------------
         * mintoakTxnId: Mintoak’s Unique ID for the transaction
         * status: Status of the Request sent to Mintoak server
         * respMsg: Response of the notification status
         * <p>
         * ENCRYPTED RESPONSE
         * -------------------
         * {"responseMsg":"0543AD0135ECC477680AA58651EE5F539C97D6487FDBDFE3E54B8D6D
         * 810976DED391175C458E900DE66D36C3BBCA738DE01ECD828619904C6F6D4EA90FFEC0
         * 63"}
         * <p>
         * Decrypted response data for
         * --------------------------
         * Success case:
         * {“mintoakTxnid”:”123834631512”,"status":"Success","respMsg":"Notification Sent"}
         * <p>
         * Possible Failure cases:
         * {“mintoakTxnid”:”123834631512”,"status":"SaleFailed","respMsg":"Notification Sending
         * Failed"}
         * <p>
         * {“mintoakTxnid”:”123834631512”,"status":"SaleFailed","respMsg":"Sorry! Some internal
         * Server Error occurred, Please try after some time"}
         * @apiNote In Case of Terminal ID not available then response will come in plain JSON request as below
         * <p>
         * {"status":"Failed","respMsg":"Invalid Terminal ID"}
         */
        @POST("getNotificationForPayments")
        Call<JsonObject> getNotificationForPayments(@Header("apiKey") String apiKey, @Body JsonObject requestBody);

        /**
         * This API is used to provide the list of terminalId which is present on the requested location along
         * with list of payment mode service enabled.
         *
         * @param apiKey      Secret Key shared with the partner required for API calls to the Mintoak Server
         * @param requestBody A Json Object with <b>location</b> key (Location name should be
         *                    as provided by Mintoak)
         *                    <p>
         *                    Sample Request data:
         *                    ------------------------
         *                    {"location":"Kudlur gate"}
         *
         * @return Return a json object with keys
         *
         * Case 1: Location details were invalid
         * {
         *   "status": "Success",
         *   "respMsg": "Success",
         *   "tids": [
         *     {
         *       "tid": "7506453341",
         *       "status": "User account has been blocked, please contact Mintoak support!"
         *     },
         *     {
         *       "tid": "7875311455",
         *       "status": "User account has been blocked, please contact Mintoak support!"
         *     }
         *   ]
         * }
         *
         * case 2: If Tid is blocked/terminated/suspended
         * {
         *   "status": "Success",
         *   "respMsg": "Success",
         *   "tids": [
         *     {
         *       "tid": "7506453341",
         *       "status": "User account has been blocked, please contact Mintoak support!"
         *     },
         *     {
         *       "tid": "7875311455",
         *       "status": "User account has been blocked, please contact Mintoak support!"
         *     }
         *   ]
         * }
         *
         * case 3: If tid is active
         * {
         *   "status": "Success",
         *   "respMsg": "Success",
         *   "tids": [
         *     {
         *       "tid": "7506453341",
         *       "status": "User account has been blocked, please contact Mintoak support!"
         *     },
         *     {
         *       "tid": "7875311455",
         *       "paymentModes": [
         *         SMSPay,
         *         BharatQR,
         *         Cash,
         *         Card,
         *         UPI
         *       ],
         *       "status": "Success"
         *     }
         *   ]
         * }
         */
        @POST("getLocationDetails")
        Call<JsonObject> getLocationDetails(@Header("apiKey") String apiKey, @Body JsonObject requestBody);

    }

    public interface Listener {
        /***
         * @param jsonStr You will get this response after payment
         * {
         *   "mintoakTxnid": "123124385",
         *   "txnStatus": "SaleFailed",
         *   "txnMessage": "Approved or completed successfully",
         *   "clientRefId": "13083171324",
         *   "transactionTime": "2018-11-12 12:34:12",
         *   "amount": "5.0",
         *   "paymentMode": "card",
         *   "terminalID": "12345678",
         *   "invoiceNum": "1241248172414",
         *   "cardType": "VISA",
         *   "creditDebitCardTyp e": "DD"
         * }
         */
        void onResponse(String status, String ivNumber, @PaymentMode String paymentMode, String jsonStr);

        /***
         * @param errorMessage Any type of error.
         */
        void onFailed(String errorMessage);
    }

    @StringDef({PaymentMode.CARD, PaymentMode.UPI, PaymentMode.BHARATQR, PaymentMode.CASHatPOS, PaymentMode.CASH, PaymentMode.SMSPAY, PaymentMode.WALLET, PaymentMode.VOID})
    public @interface PaymentMode {
        String CARD = "CARD";
        String UPI = "UPI";
        String BHARATQR = "BHARATQR";
        String CASHatPOS = "CASH@POS";
        String CASH = "CASH";
        String SMSPAY = "SMSPAY";
        String WALLET = "WALLET";
        String VOID = "VOID";
    }

    /**
     * Call this function to check if app is installed in your phone or not.
     *
     * @return <b>true</b> if app is installed else <b>false</b>
     */
    public static boolean isAppInstalled(PackageManager
                                                 packageManager) {
        try {
            packageManager.getPackageInfo("com.transaction.mintoak", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static class Builder {

        private String remarks;
        private double amount;
        private String invoiceNumber;
        private String mobileNumber;
        @PaymentMode
        private String paymentMode;
        private Listener listener;

        private Activity activity;
        private Fragment fragment;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder(Fragment fragment) {
            this.fragment = fragment;
        }

        public Builder setAmount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
            return this;
        }

        public Builder setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
            return this;
        }

        public Builder setPaymentMode(@PaymentMode String paymentMode) {
            this.paymentMode = paymentMode;
            return this;
        }

        public Builder setRemarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public Builder setListener(Listener listener) {
            this.listener = listener;
            return this;
        }

        public MintOakPayment build() {
            return new MintOakPayment(this);
        }

    }


    /*****
     *    #Example of payment
     *
     *
     *    PAYMENT REQUEST
     *    --------------------------------------------------------------------
     *    MintOakPayment mintOakPayment = new MintOakPayment.Builder(activity)
     *                 .setPaymentMode(PaymentMode.CARD)
     *                 .setAmount(100.0)
     *                 .setInvoiceNumber("ORD1234567")
     *                 .setRemarks("This is payment")
     *                 .setListener(new Listener() {
     *                     @Override
     *                     public void onResponse(String jsonStr) {
     *                            //  GET RESPONSE HERE
     *                     }
     *
     *                     @Override
     *                     public void onFailed(String errorMessage) {
     *
     *                     }
     *                 })
     *                 .build();
     *
     *
     *   CALL THIS METHOD TO INITIATE PAYMENT
     *   -----------------------------------------------------------------------
     *   mintOakPayment.pay();
     *
     *
     *  CALL THIS METHOD IN onActivityResult and you will get the response
     *  into onResponse Listener
     *  ------------------------------------------------------------------------
     *  mintOakPayment.setResult(1,1,data);
     *
     *
     *
     */
}
