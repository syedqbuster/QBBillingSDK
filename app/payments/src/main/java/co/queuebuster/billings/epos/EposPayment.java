package co.queuebuster.billings.epos;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * {@link EposPayment} is class used to handle payment of ePOS Application
 */
public class EposPayment {

    private static final String EPOS_PACKAGE = "com.pinelabs.epos";
    private static final String EPOS_ACTION = "com.pinelabs.masterapp.SERVER";
    private static final int MESSAGE_CODE = 1001;
    private static final String BILLING_REQUEST_TAG = "MASTERAPPREQUEST";
    private static final String BILLING_RESPONSE_TAG = "MASTERAPPRESPONSE";

    private static Messenger mServerMessenger;
    private Builder builder;
    private Listener listener;

    private EposPayment(Builder builder) {
        this.builder = builder;
    }

    /**
     * <b>configure(Context)</b> It is required to configure once to initiate
     * ePOS transaction. Make sure you have configured in your Application.
     */
    public static void configure(Context context) {

        Intent intent = new Intent();
        intent.setAction(EPOS_ACTION);
        intent.setPackage(EPOS_PACKAGE);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mServerMessenger = new Messenger(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }

    /**
     * @param listener set your Listener to get response in your class.
     */
    public EposPayment setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * <b>initiate</b>
     * Do not forget to call this method. It will initiate your transaction
     * and give you response in your {@link Listener}.
     */
    public void initiate() {
        _initiate(builder.getRequestJson(builder.transactionType).toString());
    }


    /**
     * <b>checkStatus</b>
     * Check your payment status to call this method.
     */
    public void checkStatus() {
        JSONObject jsonObject = builder.getRequestJson(_getStatusTxnType()); //5563 check status code
        _initiate(jsonObject.toString());
    }

    private void _initiate(String requestJson) {

        Log.i("EPOS_REQUEST_JSON", requestJson);

        if (mServerMessenger == null) {
            if (listener != null) listener.onError("Epos is not configure", -1);
            return;
        }

        Message message = Message.obtain(null, MESSAGE_CODE);
        Bundle data = new Bundle();
        data.putString(BILLING_REQUEST_TAG, requestJson);
        message.setData(data);
        message.replyTo = new Messenger(new IncomingHandler(listener));
        try {
            mServerMessenger.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) listener.onError(e.getLocalizedMessage(), -1);
        }
    }

    private int _getStatusTxnType() {
        switch (builder.transactionType) {
            case TransactionType.CARD_SALE:
                return TransactionType.CARD_GET_STATUS;
            case TransactionType.UPI_SALE:
            case TransactionType.BHARAT_QR_SALE:
                return TransactionType.UPI_GET_STATUS;
            case TransactionType.WALLET_SALE: //ALSO FOR PHONE PE AND FREE CHARGE
                return TransactionType.WALLET_GET_STATUS;
        }

        return -1;
    }

    public interface Listener {
        void onResponse(String responseJson, int responseCode, String responseMsg);

        void onError(String errorMsg, int errorCode);
    }

    private static class IncomingHandler extends Handler {

        private Listener listener;

        IncomingHandler(Listener listener) {
            this.listener = listener;
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String responseStr = bundle.getString(BILLING_RESPONSE_TAG);

            Log.i("EPOS_RESPONSE_JSON", responseStr);

            try {
                JSONObject jsonObject = new JSONObject(responseStr);
                JSONObject responseHeader = jsonObject.getJSONObject("Response");
                int requestCode = responseHeader.getInt("ResponseCode");
                String responseMsg = responseHeader.getString("ResponseMsg");
                if (listener != null) listener.onResponse(responseStr, requestCode, responseMsg);
            } catch (JSONException e) {
                e.printStackTrace();
                listener.onError(e.getMessage(), -1);
            }


        }
    }

    /**
     * {@link ResponseCode} For all API successful responses, Response Code will be set to zero.
     * More error-codes will be added as per specific scenarios.
     */
    public interface ResponseCode {
        int TRANSACTION_INITIATED_CHECK_GET_STATUS = 100;
        int REQUEST_TIME_OUT = 20;
        int APPROVED = 0;
        int APP_NOT_ACTIVATED = 1;
        int ALREADY_ACTIVATED = 2;
        int INVALID_METHOD_ID = 3;
        int INVALID_USER_PIN = 4;
        int USER_BLOCKED_FOR_MAX_ATTEMPT = 5;
        int PERMISSION_DENIED = 6;
        int INVALID_DATA_FORMAT = 7; //SALE CHARGE FAILED
    }

    /**
     * The type of payment to be followed
     */
    public interface TransactionType {

        /**
         * <b>CARD_SALE</b> for card payment
         * To Check Status use {@link TransactionType#CARD_GET_STATUS}
         * {@link BankCode#HDFC_BHARAT_QR} will be used.
         */
        int CARD_SALE = 5561;
        /**
         * <b>CARD_VOID</b> for void card payment
         */
        int CARD_VOID = 5562;
        /**
         * <b>CARD_GET_STATUS</b> to check status of {@link TransactionType#CARD_SALE}
         */
        int CARD_GET_STATUS = 5563;
        /**
         * <b>BANK_EMI_SALE</b> for Bank EMI
         * Bank Code {@link BankCode#HDFC_BHARAT_QR} will be use i.e 1
         * Check Status: Not Provided
         */
        int BANK_EMI_SALE = 5566;
        /**
         * <b>BRAND_EMI_SALE</b> for Brand EMI
         * Bank Code {@link BankCode#HDFC_BHARAT_QR} will be use i.e 1
         * Check Status: Not Provided
         */
        int BRAND_EMI_SALE = 5567;
        /**
         * <b>RESEND_SMS</b> for Resend SMS for Payment
         */
        int RESEND_SMS = 5564;
        /**
         * <b>BHARAT_QR_SALE</b> for BQR Sale
         * To Check Status use {@link TransactionType#UPI_GET_STATUS}
         * {@link BankCode#HDFC_UPI} will be used.
         */
        int BHARAT_QR_SALE = 5123;
        /**
         * <b>UPI_SALE</b> for card payment
         * To Check Status use {@link TransactionType#UPI_GET_STATUS}
         * {@link BankCode#HDFC_BHARAT_QR} will be used.
         */
        int UPI_SALE = 5120;
        /**
         * <b>UPI_GET_STATUS</b>
         * To check status of {@link TransactionType#UPI_SALE}, {@link TransactionType#BHARAT_QR_SALE}
         */
        int UPI_GET_STATUS = 5122;
        /**
         * <b>UPI_VOID</b> for void UPI payment
         */
        int UPI_VOID = 5121;
        /**
         * <b>WALLET_SALE</b> for Wallet Sale
         * To Check Status use {@link TransactionType#WALLET_GET_STATUS}
         * Bank Code {@link BankCode#WALLET_PHONEPE} or {@link BankCode#WALLET_FREECHARGE} will be used.
         */
        int WALLET_SALE = 5102;
        int WALLET_LOAD = 5103;
        int WALLET_VOID = 5104;
        /**
         * <b>WALLET_GET_STATUS</b>
         * To check status of {@link TransactionType#WALLET_SALE}
         */
        int WALLET_GET_STATUS = 5112;
        /**
         * <b>AIRTEL_MONEY</b> to pay through Airtel Mondey
         * To check status : Not Provided
         * Bank Code : Not Use.
         */
        int AIRTEL_MONEY = 5127;
    }

    /**
     * he acquirer bank code / Host Type to which transaction will be routed
     */
    public interface BankCode {
        /**
         * <b>WALLET_FREECHARGE</b> use this bank code on {@link TransactionType#WALLET_SALE}
         */
        int WALLET_FREECHARGE = 103;
        /**
         * <b>WALLET_PHONEPE</b> use this bank code on {@link TransactionType#WALLET_SALE}
         */
        int WALLET_PHONEPE = 105;
        /**
         * <b>HDFC_BHARAT_QR</b> use this bank code on {@link TransactionType#CARD_SALE}
         */
        int HDFC_BHARAT_QR = 1;
        /**
         * <b>HDFC_UPI</b> use this bank code on {@link TransactionType#UPI_SALE}
         */
        int HDFC_UPI = 2;
    }

    /**
     * <b>{@link Builder}</b> class is used to create payment information
     * To Create {@link EposPayment} class reference, use Builder class
     * add all required information then use {@link Builder#create()} method to create {@link EposPayment} instance.
     */
    public static class Builder {

        private String applicationID;
        private String userID;
        private String billingRefNo;
        private String invoiceNumber;
        private double paymentAmount;
        private int transactionType;
        private int bankCode = -1;
        private String roc;
        private String batchNo;

        JSONObject getRequestJson(int transactionType) {

            try {
                JSONObject mainJson = new JSONObject();

                //HEADER
                JSONObject headerJson = new JSONObject();
                headerJson.put("ApplicationId", applicationID);
                headerJson.put("UserId", userID);
                headerJson.put("MethodId", "1001");
                headerJson.put("VersionNo", "1.0");

                mainJson.put("Header", headerJson);

                //DETAIL
                JSONObject detailJson = new JSONObject();
                detailJson.put("TransactionType", transactionType);
                detailJson.put("BillingRefNo", billingRefNo);
                detailJson.put("PaymentAmount", paymentAmount * 100);

                if (bankCode != -1)
                    detailJson.put("BankCode", bankCode);

                if (invoiceNumber != null) {
                    detailJson.put("InvoiceNo", invoiceNumber);
                }

                if (roc != null) {
                    detailJson.put("Roc", roc);
                }

                if (batchNo != null) {
                    detailJson.put("BatchNo", batchNo);
                }

                mainJson.put("Detail", detailJson);

                return mainJson;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * @param billingRefNo it is reference to your unique purchaseId i.e orderID
         *                     Transaction reference number
         *                     from external application.
         *                     EPOS will use this value for
         *                     printing on charge slip.
         */
        public Builder setBillingRefNo(String billingRefNo) {
            this.billingRefNo = billingRefNo;
            return this;
        }

        /**
         * @param paymentAmount Amount to be charged to
         *                      customer
         *                      Do not multiply your amount with 100)}
         */
        public Builder setPaymentAmount(double paymentAmount) {
            this.paymentAmount = paymentAmount;
            return this;
        }

        /**
         * @param transactionType The type of payment
         *                        transaction to be processed.
         *                        {@link TransactionType}
         *                        ------------------------------------------------
         *                        Transaction type Description  :  TransactionType
         *                        -----------------------------------------------
         *                        PG@POS Credit/Debit Card Sale :    5561
         *                        PG@POS Void                   :    5562
         *                        PG@POS Get Status             :    5563
         *                        PG@POS Bank EMI Sale          :    5566
         *                        PG@POS Brand EMI Sale         :    5567
         *                        PG@POS Resend SMS             :    5564
         *                        Bharat QR Sale                :    5123
         *                        UPI Sale                      :    5120
         *                        UPI / BQR Get Status          :    5122
         *                        UPI / BQR Void                :    5121
         *                        Wallet Sale                   :    5102
         *                        Wallet Load                   :    5103
         *                        Wallet Void                   :    5104
         *                        Wallet get status             :    5112
         *                        Airtel Money                  :    5127
         */
        public Builder setTransactionType(int transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        /**
         * @param bankCode The acquirer bank code / Host
         *                 Type to which transaction will
         *                 be routed. Optional in case of
         *                 sale transaction if Automatic
         *                 Acquirer Selection is chosen
         *                 {@link BankCode}
         *                 -----------------------------
         *                 Wallet Name      :  Bank Code
         *                 -----------------------------
         *                 Free Charge      :  103
         *                 Phone Pe         :  105
         *                 -----------------------------
         *                 Host Name        :  Bank Code
         *                 -----------------------------
         *                 HDFC Bharat QR   :   1
         *                 HDFC UPI         :   2
         */
        public Builder setBankCode(int bankCode) {
            this.bankCode = bankCode;
            return this;
        }

        /**
         * @param applicationID set your applicationID provided during integration time
         */
        public Builder setApplicationID(String applicationID) {
            this.applicationID = applicationID;
            return this;
        }

        /**
         * @param userID userID of your ePOS account
         */
        public Builder setUserID(String userID) {
            this.userID = userID;
            return this;
        }

        /**
         * @param invoiceNumber set Invoice number i.e transactionID to void
         */
        public Builder setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
            return this;
        }

        public Builder setRoc(String roc) {
            this.roc = roc;
            return this;
        }

        public Builder setBatchNo(String batchNo) {
            this.batchNo = batchNo;
            return this;
        }

        /***
         * It is used to create {@link EposPayment} instance
         * @return {@link EposPayment}
         */
        public EposPayment create() {
            return new EposPayment(this);
        }
    }
}
