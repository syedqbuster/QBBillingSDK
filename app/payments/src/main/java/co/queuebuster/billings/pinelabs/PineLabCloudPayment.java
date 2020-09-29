package co.queuebuster.billings.pinelabs;

import android.util.Log;

import com.google.gson.JsonObject;

import co.queuebuster.billings.mintoak.BuildConfig;
import co.queuebuster.qbbillingutils.Check;
import co.queuebuster.qbbillingutils.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class PineLabCloudPayment {

    private static final String TAG = "PineLabCloudPayment";
    private final String BASE_URL = "https://www.plutuscloudserviceuat.in:8201/";
    private final String API_VERSION = "API/CloudBasedIntegration/V1/";
    private static PineLabCloudPayment pineLabCloudPayment;
    private Api api = RetrofitClient.getClientNormal(BASE_URL + API_VERSION).create(Api.class);
    private ErrorListener errorListener;
    private Loader loader;
    private static String IMEI; //Helper.getSerialNumber(QBApp.getInstance()).substring(0,14);//"e8297bed1b064f"


    public static  String SECURITY_TOKEN = "";
    public static  String MERCHANT_ID = "";
    public static  String STORE_POST_CODE = "";

    public static void configure(String securityToken, String merchantId, String storePostCode, String imei) {
        SECURITY_TOKEN = securityToken;
        MERCHANT_ID = merchantId;
        STORE_POST_CODE = storePostCode;
        IMEI = imei;

    }

    private PineLabCloudPayment() {
//             SECURITY_TOKEN = mChainInfo.getPineLabsSecurityToken();
//             MERCHANT_ID = mChainInfo.getPineLabsMerchantID();
//             STORE_POST_CODE = mChainInfo.getPineLabsPostCode();

    }

    public static PineLabCloudPayment getInstance() {
        if (pineLabCloudPayment == null) {
            pineLabCloudPayment = new PineLabCloudPayment();
        }
        return pineLabCloudPayment;
    }

    public BillingBuilder createBilling() {
        return new BillingBuilder();
    }

    public StatusBuilder statusBuilder() {
        return new StatusBuilder();
    }

    public CancelBuilder cancelBuilder() {
        return new CancelBuilder();
    }

    public VoidBuilder voidBuilder() {
        return new VoidBuilder();
    }

    public PineLabCloudPayment withErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
        return pineLabCloudPayment;
    }

    public PineLabCloudPayment setLoader(Loader loader) {
        this.loader = loader;
        return pineLabCloudPayment;
    }

    private static JsonObject createBillingTransactonJson(String transactionNumber, String sequenceNumber, String amount, String merchantID, String securityToken, String merchantStorePosCode, String userName) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("TransactionNumber", transactionNumber);
        jsonObject.addProperty("SequenceNumber", Check.parseInt(sequenceNumber));
        jsonObject.addProperty("AllowedPaymentMode", "1"); //CARD
        jsonObject.addProperty("Amount", Check.parseDouble(amount) * 100);
        jsonObject.addProperty("UserID", userName);
        jsonObject.addProperty("MerchantID", merchantID);
        jsonObject.addProperty("SecurityToken", securityToken);
        jsonObject.addProperty("IMEI", IMEI);
        jsonObject.addProperty("MerchantStorePosCode", merchantStorePosCode);
        //jsonObject.addProperty("TotalInvoiceAmount",transactionNumber);

        return jsonObject;
    }

    private static JsonObject createStatusJson(String merchantID, String securityToken, String merchantStorePosCode, String plutusTransactionReferenceID, String userName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MerchantID", merchantID);
        jsonObject.addProperty("SecurityToken", securityToken);
        jsonObject.addProperty("IMEI", IMEI);
        jsonObject.addProperty("UserID", userName);
        jsonObject.addProperty("MerchantStorePosCode", merchantStorePosCode);
        jsonObject.addProperty("PlutusTransactionReferenceID", plutusTransactionReferenceID);

        return jsonObject;
    }

    private static JsonObject createCancellationJson(String merchantID, String securityToken, String merchantStorePosCode, String plutusTransactionReferenceID, String amount) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MerchantID", merchantID);
        jsonObject.addProperty("SecurityToken", securityToken);
        jsonObject.addProperty("IMEI", IMEI);
        jsonObject.addProperty("MerchantStorePosCode", merchantStorePosCode);
        jsonObject.addProperty("PlutusTransactionReferenceID", plutusTransactionReferenceID);
        jsonObject.addProperty("Amount", Check.parseDouble(amount) * 100);

        return jsonObject;
    }

    private void handleError(Object o) {
        String error = "Something Went Wrong";

        if (o instanceof Throwable) {
            Throwable t = (Throwable) o;
            error = t.getLocalizedMessage() != null && !t.getLocalizedMessage().isEmpty() ? t.getLocalizedMessage() : "Something went wrong";

        } else if (o instanceof String) {
            error = (String) o;
        } else if (o instanceof Response) {
            Response response = (Response) o;
            try {
                error = response.errorBody().string() + " status code: " + response.code();
            } catch (Exception e) {
                e.printStackTrace();
                error = "Error " + e.getMessage() + " status code: " + response.code();
            }
        }

        if(BuildConfig.DEBUG ) Log.e(TAG, "handleError: " + error);
        if(errorListener != null) errorListener.onPineLabCloudPaymentError(error);
        if(pineLabCloudPayment.loader != null) pineLabCloudPayment.loader.onLoading(false);
    }

    public static class BillingBuilder {
        private String transactionNumber;
        private String sequenceNumber;
        private String amount;
        private String userName;
        private String merchantID = PineLabCloudPayment.MERCHANT_ID;
        private String securityToken = PineLabCloudPayment.SECURITY_TOKEN;
        private String merchantStorePosCode = PineLabCloudPayment.STORE_POST_CODE;

        private BillingBuilder() {
        }

        public BillingBuilder setTransactionNumber(String transactionNumber) {
            this.transactionNumber = transactionNumber;
            return this;
        }

        public BillingBuilder setSequenceNumber(String sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
            return this;
        }

        public BillingBuilder setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public BillingBuilder setMerchantID(String merchantID) {
            this.merchantID = merchantID;
            return this;
        }

        public BillingBuilder setSecurityToken(String securityToken) {
            this.securityToken = securityToken;
            return this;
        }

        public BillingBuilder setMerchantStorePosCode(String merchantStorePosCode) {
            this.merchantStorePosCode = merchantStorePosCode;
            return this;
        }

        public BillingBuilder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        private JsonObject buildJson() {
            return createBillingTransactonJson(transactionNumber, sequenceNumber, amount, merchantID, securityToken, merchantStorePosCode,userName);
        }

        /**
         * <b>UploadBilledTransaction API</b>
         * @param apiResponse
         */
        public void uploadBilledTransaction(ApiResponse<String> apiResponse) {
            if(pineLabCloudPayment.loader != null) pineLabCloudPayment.loader.onLoading(true);
            pineLabCloudPayment.api.uploadBilledTransaction(buildJson()).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(pineLabCloudPayment.loader != null) pineLabCloudPayment.loader.onLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        if(response.body().has("ResponseCode")) {
                            int responseCode = response.body().get("ResponseCode").getAsInt();
                            //Error
                            if(responseCode == 1) {
                                String responseMessage = response.body().get("ResponseMessage").getAsString();
                                pineLabCloudPayment.handleError(responseMessage);
                            }
                            else {
                                String PlutusTransactionReferenceID = response.body().get("PlutusTransactionReferenceID").getAsString();
                                apiResponse.onResponse(PlutusTransactionReferenceID);
                            }
                        }
                    } else {
                        pineLabCloudPayment.handleError(response);
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    pineLabCloudPayment.handleError(t);
                }
            });
        }
    }

    public static class VoidBuilder {
        private String transactionNumber = "";
        private String sequenceNumber = "1";
        private String amount;
        private String userName;
        private String plutusTransactionReferenceID;
        private String merchantID = PineLabCloudPayment.MERCHANT_ID;
        private String securityToken = PineLabCloudPayment.SECURITY_TOKEN;
        private String merchantStorePosCode = PineLabCloudPayment.STORE_POST_CODE;

        private VoidBuilder() {
        }

        public VoidBuilder setTransactionNumber(String transactionNumber) {
            this.transactionNumber = transactionNumber;
            return this;
        }

        public VoidBuilder setSequenceNumber(String sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
            return this;
        }

        public VoidBuilder setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public VoidBuilder setMerchantID(String merchantID) {
            this.merchantID = merchantID;
            return this;
        }

        public VoidBuilder setSecurityToken(String securityToken) {
            this.securityToken = securityToken;
            return this;
        }

        public VoidBuilder setMerchantStorePosCode(String merchantStorePosCode) {
            this.merchantStorePosCode = merchantStorePosCode;
            return this;
        }

        public VoidBuilder setPlutusTransactionReferenceID(String plutusTransactionReferenceID) {
            this.plutusTransactionReferenceID = plutusTransactionReferenceID;
            return this;
        }

        public VoidBuilder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        private JsonObject buildJson() {
            JsonObject jsonObject = createBillingTransactonJson(transactionNumber, sequenceNumber, amount, merchantID, securityToken, merchantStorePosCode, userName);
            jsonObject.addProperty("TxnType", 1);
            jsonObject.addProperty("OriginalPlutusTransactionReferenceID", plutusTransactionReferenceID);
            return jsonObject;
        }

        /**
         * <b>UploadBilledTransaction API</b>
         * @param apiResponse
         */
        public void voidBilledTransaction(ApiResponse<JsonObject> apiResponse) {
            if(pineLabCloudPayment.loader != null) pineLabCloudPayment.loader.onLoading(true);
            pineLabCloudPayment.api.uploadBilledTransaction(buildJson()).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(pineLabCloudPayment.loader != null) pineLabCloudPayment.loader.onLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        if(response.body().has("ResponseCode")) {
                            int responseCode = response.body().get("ResponseCode").getAsInt();
                            //Error
                            if(responseCode == 1) {
                                String responseMessage = response.body().get("ResponseMessage").getAsString();
                                pineLabCloudPayment.handleError(responseMessage);
                            }
                            else {
//                                String PlutusTransactionReferenceID = response.body().get("PlutusTransactionReferenceID").getAsString();
//                                apiResponse.onResponse(PlutusTransactionReferenceID);
                                apiResponse.onResponse(response.body());
                            }
                        }
                    } else {
                        pineLabCloudPayment.handleError(response);
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    pineLabCloudPayment.handleError(t);
                }
            });
        }
    }

    public static class StatusBuilder {

        private String merchantID = PineLabCloudPayment.MERCHANT_ID;
        private String securityToken = PineLabCloudPayment.SECURITY_TOKEN;
        private String merchantStorePosCode = PineLabCloudPayment.STORE_POST_CODE;;
        private String plutusTransactionReferenceID;
        private String userName;

        public StatusBuilder setMerchantID(String merchantID) {
            this.merchantID = merchantID;
            return this;
        }

        public StatusBuilder setSecurityToken(String securityToken) {
            this.securityToken = securityToken;
            return this;
        }

        public StatusBuilder setMerchantStorePosCode(String merchantStorePosCode) {
            this.merchantStorePosCode = merchantStorePosCode;
            return this;
        }

        public StatusBuilder setPlutusTransactionReferenceID(String plutusTransactionReferenceID) {
            this.plutusTransactionReferenceID = plutusTransactionReferenceID;
            return this;
        }

        public StatusBuilder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        private JsonObject buildJson() {
            return createStatusJson(merchantID, securityToken, merchantStorePosCode, plutusTransactionReferenceID, userName);
        }

        /**
         * <b>GetStatus API</b>
         * @param apiResponse
         */
        public void getStatus(ApiResponse<JsonObject> apiResponse) {
            if(pineLabCloudPayment.loader != null) pineLabCloudPayment.loader.onLoading(true);
            pineLabCloudPayment.api.getStatus(buildJson()).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(pineLabCloudPayment.loader != null) pineLabCloudPayment.loader.onLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        if(response.body().has("ResponseCode")) {
                            int responseCode = response.body().get("ResponseCode").getAsInt();
                            //Error
                            if(responseCode == 1) {
                                String responseMessage = response.body().get("ResponseMessage").getAsString();
                                pineLabCloudPayment.handleError(responseMessage);
                            }
                            else {
                                apiResponse.onResponse(response.body());
                            }
                        }
                    } else {
                        pineLabCloudPayment.handleError(response);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    pineLabCloudPayment.handleError(t);
                }
            });
        }
    }

    public static class CancelBuilder {
        private String merchantID = PineLabCloudPayment.MERCHANT_ID;
        private String securityToken = PineLabCloudPayment.SECURITY_TOKEN;
        private String merchantStorePosCode = PineLabCloudPayment.STORE_POST_CODE;
        private String plutusTransactionReferenceID;
        private String amount;

        public CancelBuilder setMerchantID(String merchantID) {
            this.merchantID = merchantID;
            return this;
        }

        public CancelBuilder setSecurityToken(String securityToken) {
            this.securityToken = securityToken;
            return this;
        }

        public CancelBuilder setMerchantStorePosCode(String merchantStorePosCode) {
            this.merchantStorePosCode = merchantStorePosCode;
            return this;
        }

        public CancelBuilder setPlutusTransactionReferenceID(String plutusTransactionReferenceID) {
            this.plutusTransactionReferenceID = plutusTransactionReferenceID;
            return this;
        }

        public CancelBuilder setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        private JsonObject buildJson() {
            return createCancellationJson(merchantID, securityToken, merchantStorePosCode, plutusTransactionReferenceID, amount);
        }

        /**
         * <b>CancelTransaction API</b>
         * @param apiResponse
         */
        public void cancelTransaction(ApiResponse<String> apiResponse) {
            if(pineLabCloudPayment.loader != null) pineLabCloudPayment.loader.onLoading(true);
            pineLabCloudPayment.api.cancelTransaction(buildJson()).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(pineLabCloudPayment.loader != null) pineLabCloudPayment.loader.onLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        if(response.body().has("ResponseCode")) {
                            int responseCode = response.body().get("ResponseCode").getAsInt();
                            String responseMessage = response.body().get("ResponseMessage").getAsString();
                            //Error
                            if(responseCode == 1) {
                                pineLabCloudPayment.handleError(responseMessage);
                            }
                            else {
                                apiResponse.onResponse(responseMessage);
                            }
                        }
                    } else {
                        pineLabCloudPayment.handleError(response);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    pineLabCloudPayment.handleError(t);
                }
            });
        }
    }

    public interface ApiResponse<T> {
        void onResponse(T t);
    }

    public interface Loader {
        void onLoading(boolean isLoading);
    }

    public interface ErrorListener {
        void onPineLabCloudPaymentError(String error);
    }

    interface Api {
        @POST("UploadBilledTransaction")
        Call<JsonObject> uploadBilledTransaction(@Body JsonObject jsonObject);

        @POST("GetCloudBasedTxnStatus")
        Call<JsonObject> getStatus(@Body JsonObject jsonObject);

        @POST("CancelTransaction")
        Call<JsonObject> cancelTransaction(@Body JsonObject jsonObject);
    }

}
