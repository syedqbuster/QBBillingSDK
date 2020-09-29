package co.queuebuster.qbbillingsdk;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import co.queuebuster.billings.epos.EposPayment;
import co.queuebuster.billings.ezetap.EzetapPayment;
import co.queuebuster.billings.ezetap.EzetapPaymentSuccess;
import co.queuebuster.billings.mintoak.MintOakPayment;
import co.queuebuster.billings.pinelabs.PineLabCloudPayment;
import co.queuebuster.qbbillingutils.QbUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /**
     * EXAMPLE OF MINTOAK PAYMENT
     */
    MintOakPayment mintOakPayment;
    private void mintoakPaymentExample() {

        final String myRef = String.valueOf(System.currentTimeMillis());

        mintOakPayment = new MintOakPayment.Builder(this)
                .setAmount(20)
                .setInvoiceNumber(myRef)
                .setPaymentMode(MintOakPayment.PaymentMode.UPI)
                .setListener(new MintOakPayment.Listener() {
                    @Override
                    public void onResponse(String status, String ivNumber, String paymentMode, String jsonStr) {

                        if(status.equalsIgnoreCase("txnSuccess")) {
                            // Next
                        }
                        else {
                            //check stauts after some time
                            //To check status
                            mintOakPayment.checkStatus(myRef, ivNumber);
                        }
                    }

                    @Override
                    public void onFailed(String errorMessage) {

                    }
                })
                .build();

        //to pay through app
        mintOakPayment.pay(); //App to App

        //to pay through api
        mintOakPayment.payThroughApi(); //App to Api

    }


    /**
     * EXAMPLE OF EPOS PAYMENT
     */
    boolean isFirstTimeEposStatus = true;
    private String ePosInvoiceNumber;
    private void handleEposPayment(String orderID, final double amount, int bankCode, int txnType) {

        //Handling EPOS Payment
        final EposPayment eposPayment = new EposPayment.Builder()
                .setApplicationID("PINE_LAB_KEY")
                .setUserID("Queuebuster")
                .setTransactionType(txnType) //CARD
                .setBillingRefNo(orderID)
                .setBankCode(bankCode)
                .setPaymentAmount(amount) //Do Not multiply by 100
                .create();


        //LISTENER
        eposPayment.setListener(new EposPayment.Listener() {

            @Override
            public void onResponse(String responseJson, int responseCode, String responseMsg) {

                if (responseCode == EposPayment.ResponseCode.TRANSACTION_INITIATED_CHECK_GET_STATUS) {


                    //Saving invoice number initially
                    if (isFirstTimeEposStatus) {
                        try {
                           String ePosInvoiceNumber = new JSONObject(responseJson).getJSONObject("Detail").getString("InvoiceNumber");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        isFirstTimeEposStatus = false;
                    }


                    //Show Dialog To Check Status

                    eposPayment.checkStatus();

                } else if (responseCode == EposPayment.ResponseCode.APPROVED) {
                    // dismiss status dialog
                    ParseJSON(responseJson);
                } else
                    QbUtils.showToast(MainActivity.this ,responseMsg);

                Log.i(TAG, responseJson);
            }

            @Override
            public void onError(String errorMsg, int errorCode) {
                //CHECK ERROR
                QbUtils.showToast(MainActivity.this,errorMsg);
            }
        });

        //INITIATE TXN
        eposPayment.initiate();
    }

    public void ParseJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject responseHeader = jsonObject.getJSONObject("Response");
            if (responseHeader.getString("ResponseMsg").equalsIgnoreCase("APPROVED")) {
                JSONObject detailJSON = jsonObject.getJSONObject("Detail");
                String cardNumber = "";
                String cardType = "";
                String cardExp = "";
                String transactionNo = "";
                String referenceID = "";
                String acquirerCode = "";
                String approvalCode = "";
                if (detailJSON.has("CardNumber"))
                    cardNumber = detailJSON.getString("CardNumber");
                if (detailJSON.has("CardType"))
                    cardType = detailJSON.getString("CardType");
                if (detailJSON.has("ExpiryDate"))
                    cardExp = detailJSON.getString("ExpiryDate");
                if (detailJSON.has("InvoiceNumber"))
                    transactionNo = detailJSON.getString("InvoiceNumber");
                if (detailJSON.has("BillingRefNo"))
                    referenceID = detailJSON.getString("BillingRefNo");
                if (detailJSON.has("AcquiringBankCode"))
                    acquirerCode = detailJSON.getString("AcquiringBankCode");
                if (detailJSON.has("ApprovalCode"))
                    approvalCode = detailJSON.getString("ApprovalCode");
                if (detailJSON.has("CouponCode")) {
                    String pineLabCouponCode = detailJSON.getString("CouponCode");
                }
                if (detailJSON.has("BatchNumber")) {
                    String batchNo = detailJSON.getString("BatchNumber");
                }
                if (detailJSON.has("PineLabsRoc")) {
                    String roc = detailJSON.getString("PineLabsRoc");
                }
                if (detailJSON.has("AcquirerName")) {
                    cardType = detailJSON.getString("AcquirerName");
                }

                // DO NEXT
            } else {
                int requestCode = responseHeader.getInt("ResponseCode");
                double  amount = 00d; //FIND FROM EDIT TEXT
                if (responseHeader.getString("ResponseMsg").equalsIgnoreCase("TRANSACTION INITIATED CHECK GET STATUS")) {
                    //CHECK STATUS DIALOG

                }
                Toast.makeText(this, responseHeader.getString("ResponseMsg"), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * EXAMPLE OF EZETAP DEVICE PAYMENT
     * @param amount
     */
    private void payThroughEzeTap(double amount) {

        //FIRST CHECK IF BLUETOOH IS ENALBE OR NOT
        boolean isEanble =  EzetapPayment.isBluetoothEnable();

        //TO ENABLE BLUETOOTH
        EzetapPayment.enableBluetooth(this,EzetapPayment.BLUETOOTH_REQUEST);

        EzetapPayment.with(this)
                .setAppKey("EZETAP_APP_KEY")
                .setLoginInfo("USERNAME", EzetapPayment.EnvType.DEMO, EzetapPayment.TxnType.LOGIN)
                .setPayment("USERNAME", EzetapPayment.EnvType.DEMO, EzetapPayment.TxnType.SALE, amount, "REF_ID")
                .setPaymentListener(new EzetapPayment.PaymentListener() {
                    @Override
                    public void onDeviceResponse(String response, String msg) {
                        //tvProcessing.setText(msg);
                    }

                    @Override
                    public void onTransactionFailed(String response, String failedMsg) {

                    }

                    @Override
                    public void onFinalPaymentResonse(String paymentResponse) {
                        handleEzetapResponse(paymentResponse);
                    }
                }).connect();
    }

    private void handleEzetapResponse(String response) {
        try {
            //convert json using gson to this class
            EzetapPaymentSuccess paymentSuccess =  new Gson().fromJson(response, EzetapPaymentSuccess.class);
            if (paymentSuccess != null) {
                //do next
            }
        } catch (Exception e) {

        }
    }

    private String pineSeqNumber = ""; //UNIQUE_REF_ID
    private void payThroughPineLabCloud(String amount, String txnNumber) {
        PineLabCloudPayment.getInstance()
                .withErrorListener(new PineLabCloudPayment.ErrorListener() {
                    @Override
                    public void onPineLabCloudPaymentError(String error) {
                        showToast(error);
                    }
                })
                .setLoader(new PineLabCloudPayment.Loader() {
                    @Override
                    public void onLoading(boolean isLoading) {
                        //show loader
                    }
                })
                .createBilling()
                .setUserName("USERNAME")
                .setTransactionNumber(txnNumber)
                .setSequenceNumber(pineSeqNumber)
                .setAmount(amount)
                .setMerchantID(PineLabCloudPayment.MERCHANT_ID)
                .setSecurityToken(PineLabCloudPayment.SECURITY_TOKEN)
                .setMerchantStorePosCode(PineLabCloudPayment.STORE_POST_CODE)
                .uploadBilledTransaction(new PineLabCloudPayment.ApiResponse<String>() {
                    @Override
                    public void onResponse(String id) {
                        checkPinelabCloudStatus(id);
                    }
                });
    }

    private void checkPinelabCloudStatus(String id) {
        PineLabCloudPayment.getInstance()
                .withErrorListener(new PineLabCloudPayment.ErrorListener() {
                    @Override
                    public void onPineLabCloudPaymentError(String error) {

                    }
                })
                .setLoader(new PineLabCloudPayment.Loader() {
                    @Override
                    public void onLoading(boolean isLoading) {

                    }
                })
                .statusBuilder()
                .setUserName("USERNAME")
                .setPlutusTransactionReferenceID(id)
                .getStatus(new PineLabCloudPayment.ApiResponse<JsonObject>() {
                    @Override
                    public void onResponse(JsonObject jsonObject) {
                        if (jsonObject.has("ResponseMessage")) {
                            int responseCode = jsonObject.get("ResponseCode").getAsInt();
                            String responseMessage = jsonObject.get("ResponseMessage").getAsString();
                            if (responseCode == 0) {
                                JsonArray transactionData = jsonObject.get("TransactionData").getAsJsonArray();
                                if (transactionData != null) {
                                    String cardNumber = "";
                                    String cardType = "";
                                    String cardExp = "";
                                    String transactionNo = "";
                                    String referenceID = String.valueOf(pineSeqNumber);
                                    ;
                                    String acquirerCode = "";
                                    String approvalCode = "";
                                    for (int i = 0; i < transactionData.size(); i++) {
                                        JsonObject detailJSON = transactionData.get(i).getAsJsonObject();

                                        if (detailJSON.get("Tag").getAsString().equalsIgnoreCase("Card Number"))
                                            cardNumber = detailJSON.get("Value").getAsString();
                                        if (detailJSON.get("Tag").getAsString().equalsIgnoreCase("Card Type"))
                                            cardType = detailJSON.get("Value").getAsString();
                                        if (detailJSON.get("Tag").getAsString().equalsIgnoreCase("Expiry Date"))
                                            cardExp = detailJSON.get("Value").getAsString();
                                        ;
                                        if (detailJSON.get("Tag").getAsString().equalsIgnoreCase("Acquirer Id"))
                                            acquirerCode = detailJSON.get("Value").getAsString();
                                        ;
                                        if (detailJSON.get("Tag").getAsString().equalsIgnoreCase("ApprovalCode"))
                                            approvalCode = detailJSON.get("Value").getAsString();
                                        ;
                                    }


                                    //Do Next
                                }
                            } else {
                                showToast(responseMessage);
                            }
                        }

                    }
                });
    }


    public void showToast(String txt) {
        QbUtils.showToast(this,txt);
    }

}