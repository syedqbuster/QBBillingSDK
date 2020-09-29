
package co.queuebuster.billings.ezetap;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class ServerResp {

    @SerializedName("acquirerCode")
    private String mAcquirerCode; 
    @SerializedName("acquisitionId")
    private String mAcquisitionId;
    @SerializedName("acquisitionKey")
    private String mAcquisitionKey;
    @SerializedName("additionalAmount")
    private Double mAdditionalAmount;
    @SerializedName("additionalParamJson")
    private String mAdditionalParamJson;
    @SerializedName("amount")
    private Double mAmount;
    @SerializedName("amount Additional")
    private Double mAmountAdditional;
    @SerializedName("amountCashBack")
    private Double mAmountCashBack;
    @SerializedName("amountOriginal")
    private Double mAmountOriginal;
    @SerializedName("apps")
    private List<Object> mApps;
    @SerializedName("authCode")
    private String mAuthCode;
    @SerializedName("batchNumber")
    private String mBatchNumber;
    @SerializedName("callT C")
    private Boolean mCallTC;
    @SerializedName("callbackEnabled")
    private Boolean mCallbackEnabled;
    @SerializedName("cardHolderCurrencyExponent")
    private Double mCardHolderCurrencyExponent;
    @SerializedName("cardLastFourDigit")
    private String mCardLastFourDigit;
    @SerializedName("cardTxnTypeDesc")
    private String mCardTxnTypeDesc;
    @SerializedName("cardType")
    private String mCardType;
    @SerializedName("chargeSlipD ate")
    private String mChargeSlipDAte;
    @SerializedName("createdTime")
    private Double mCreatedTime;
    @SerializedName("currencyCode")
    private String mCurrencyCode;
    @SerializedName("customerNam eAvailable")
    private Boolean mCustomerNamEAvailable;
    @SerializedName("customerName")
    private String mCustomerName;
    @SerializedName("customerReceiptUrl")
    private String mCustomerReceiptUrl;
    @SerializedName("dccOpted")
    private Boolean mDccOpted;
    @SerializedName("deviceSerial")
    private String mDeviceSerial;
    @SerializedName("displayPAN")
    private String mDisplayPAN;
    @SerializedName("dxMode")
    private String mDxMode;
    @SerializedName("externalDevice")
    private Boolean mExternalDevice;
    @SerializedName("externalRefNumber")
    private String mExternalRefNumber;
    @SerializedName("formattedPan")
    private String mFormattedPan;
    @SerializedName("invoiceNumber")
    private String mInvoiceNumber;
    @SerializedName("issuerCode")
    private String mIssuerCode;
    @SerializedName("merchantCode")
    private String mMerchantCode;
    @SerializedName("merchantName")
    private String mMerchantName;
    @SerializedName("mid")
    private String mMid;
    @SerializedName("nameOnCard")
    private String mNameOnCard;
    @SerializedName("nonceStatus")
    private String mNonceStatus;
    @SerializedName("orderNu mber")
    private String mOrderNuMber;
    @SerializedName("orgCode")
    private String mOrgCode;
    @SerializedName("pa ymentCardType")
    private String mPaYmentCardType;
    @SerializedName("payerName")
    private String mPayerName;
    @SerializedName("paymentCardBin")
    private String mPaymentCardBin;
    @SerializedName("paymentCardBrand")
    private String mPaymentCardBrand;
    @SerializedName("paymentMode")
    private String mPaymentMode;
    @SerializedName("pgInvoiceNumber")
    private String mPgInvoiceNumber;
    @SerializedName("postingDate")
    private Double mPostingDate;
    @SerializedName("processCode")
    private String mProcessCode;
    @SerializedName("readableChargeSlipDate")
    private String mReadableChargeSlipDate;
    @SerializedName("receiptUrl")
    private String mReceiptUrl;
    @SerializedName("refundable")
    private Boolean mRefundable;
    @SerializedName("reverseReferenceNumber")
    private String mReverseReferenceNumber;
    @SerializedName("rrNu mber")
    private String mRrNuMber;
    @SerializedName("sessionKey")
    private String mSessionKey;
    @SerializedName("settlementStatus")
    private String mSettlementStatus;
    @SerializedName("signReqd")
    private Boolean mSignReqd;
    @SerializedName("signable")
    private Boolean mSignable;
    @SerializedName("signatureId")
    private String mSignatureId;
    @SerializedName("states")
    private List<String> mStates;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("success")
    private Boolean mSuccess;
    @SerializedName("tcMode")
    private String mTcMode;
    @SerializedName("tid")
    private String mTid;
    @SerializedName("tipA djusted")
    private Boolean mTipADjusted;
    @SerializedName("tipEnabled")
    private Boolean mTipEnabled;
    @SerializedName("totalAmo unt")
    private Double mTotalAmoUnt;
    @SerializedName("txnId")
    private String mTxnId;
    @SerializedName("txnMetadata")
    private List<Object> mTxnMetadata;
    @SerializedName("txnType")
    private String mTxnType;
    @SerializedName("txnTypeDesc")
    private String mTxnTypeDesc;
    @SerializedName("userAgreement")
    private String mUserAgreement;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("voidable")
    private Boolean mVoidable;

    public String getAcquirerCoDe() {
        return mAcquirerCode;
    }

    public void setAcquirerCoDe(String acquirerCoDe) {
        mAcquirerCode = acquirerCoDe;
    }

    public String getAcquisitionId() {
        return mAcquisitionId;
    }

    public void setAcquisitionId(String acquisitionId) {
        mAcquisitionId = acquisitionId;
    }

    public String getAcquisitionKey() {
        return mAcquisitionKey;
    }

    public void setAcquisitionKey(String acquisitionKey) {
        mAcquisitionKey = acquisitionKey;
    }

    public Double getAdditionalAmount() {
        return mAdditionalAmount;
    }

    public void setAdditionalAmount(Double additionalAmount) {
        mAdditionalAmount = additionalAmount;
    }

    public String getAdditionalParamJson() {
        return mAdditionalParamJson;
    }

    public void setAdditionalParamJson(String additionalParamJson) {
        mAdditionalParamJson = additionalParamJson;
    }

    public Double getAmount() {
        return mAmount;
    }

    public void setAmount(Double amount) {
        mAmount = amount;
    }

    public Double getAmountAdditional() {
        return mAmountAdditional;
    }

    public void setAmountAdditional(Double amountAdditional) {
        mAmountAdditional = amountAdditional;
    }

    public Double getAmountCashBack() {
        return mAmountCashBack;
    }

    public void setAmountCashBack(Double amountCashBack) {
        mAmountCashBack = amountCashBack;
    }

    public Double getAmountOriginal() {
        return mAmountOriginal;
    }

    public void setAmountOriginal(Double amountOriginal) {
        mAmountOriginal = amountOriginal;
    }

    public List<Object> getApps() {
        return mApps;
    }

    public void setApps(List<Object> apps) {
        mApps = apps;
    }

    public String getAuthCode() {
        return mAuthCode;
    }

    public void setAuthCode(String authCode) {
        mAuthCode = authCode;
    }

    public String getBatchNumber() {
        return mBatchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        mBatchNumber = batchNumber;
    }

    public Boolean getCallTC() {
        return mCallTC;
    }

    public void setCallTC(Boolean callTC) {
        mCallTC = callTC;
    }

    public Boolean getCallbackEnabled() {
        return mCallbackEnabled;
    }

    public void setCallbackEnabled(Boolean callbackEnabled) {
        mCallbackEnabled = callbackEnabled;
    }

    public Double getCardHolderCurrencyExponent() {
        return mCardHolderCurrencyExponent;
    }

    public void setCardHolderCurrencyExponent(Double cardHolderCurrencyExponent) {
        mCardHolderCurrencyExponent = cardHolderCurrencyExponent;
    }

    public String getCardLastFourDigit() {
        return mCardLastFourDigit;
    }

    public void setCardLastFourDigit(String cardLastFourDigit) {
        mCardLastFourDigit = cardLastFourDigit;
    }

    public String getCardTxnTypeDesc() {
        return mCardTxnTypeDesc;
    }

    public void setCardTxnTypeDesc(String cardTxnTypeDesc) {
        mCardTxnTypeDesc = cardTxnTypeDesc;
    }

    public String getCardType() {
        return mCardType;
    }

    public void setCardType(String cardType) {
        mCardType = cardType;
    }

    public String getChargeSlipDAte() {
        return mChargeSlipDAte;
    }

    public void setChargeSlipDAte(String chargeSlipDAte) {
        mChargeSlipDAte = chargeSlipDAte;
    }

    public Double getCreatedTime() {
        return mCreatedTime;
    }

    public void setCreatedTime(Double createdTime) {
        mCreatedTime = createdTime;
    }

    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        mCurrencyCode = currencyCode;
    }

    public Boolean getCustomerNamEAvailable() {
        return mCustomerNamEAvailable;
    }

    public void setCustomerNamEAvailable(Boolean customerNamEAvailable) {
        mCustomerNamEAvailable = customerNamEAvailable;
    }

    public String getCustomerName() {
        return mCustomerName;
    }

    public void setCustomerName(String customerName) {
        mCustomerName = customerName;
    }

    public String getCustomerReceiptUrl() {
        return mCustomerReceiptUrl;
    }

    public void setCustomerReceiptUrl(String customerReceiptUrl) {
        mCustomerReceiptUrl = customerReceiptUrl;
    }

    public Boolean getDccOpted() {
        return mDccOpted;
    }

    public void setDccOpted(Boolean dccOpted) {
        mDccOpted = dccOpted;
    }

    public String getDeviceSerial() {
        return mDeviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        mDeviceSerial = deviceSerial;
    }

    public String getDisplayPAN() {
        return mDisplayPAN;
    }

    public void setDisplayPAN(String displayPAN) {
        mDisplayPAN = displayPAN;
    }

    public String getDxMode() {
        return mDxMode;
    }

    public void setDxMode(String dxMode) {
        mDxMode = dxMode;
    }

    public Boolean getExternalDevice() {
        return mExternalDevice;
    }

    public void setExternalDevice(Boolean externalDevice) {
        mExternalDevice = externalDevice;
    }

    public String getExternalRefNumber() {
        return mExternalRefNumber;
    }

    public void setExternalRefNumber(String externalRefNumber) {
        mExternalRefNumber = externalRefNumber;
    }

    public String getFormattedPan() {
        return mFormattedPan;
    }

    public void setFormattedPan(String formattedPan) {
        mFormattedPan = formattedPan;
    }

    public String getInvoiceNumber() {
        return mInvoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        mInvoiceNumber = invoiceNumber;
    }

    public String getIssuerCode() {
        return mIssuerCode;
    }

    public void setIssuerCode(String issuerCode) {
        mIssuerCode = issuerCode;
    }

    public String getMerchantCode() {
        return mMerchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        mMerchantCode = merchantCode;
    }

    public String getMerchantName() {
        return mMerchantName;
    }

    public void setMerchantName(String merchantName) {
        mMerchantName = merchantName;
    }

    public String getMid() {
        return mMid;
    }

    public void setMid(String mid) {
        mMid = mid;
    }

    public String getNameOnCard() {
        return mNameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        mNameOnCard = nameOnCard;
    }

    public String getNonceStatus() {
        return mNonceStatus;
    }

    public void setNonceStatus(String nonceStatus) {
        mNonceStatus = nonceStatus;
    }

    public String getOrderNuMber() {
        return mOrderNuMber;
    }

    public void setOrderNuMber(String orderNuMber) {
        mOrderNuMber = orderNuMber;
    }

    public String getOrgCode() {
        return mOrgCode;
    }

    public void setOrgCode(String orgCode) {
        mOrgCode = orgCode;
    }

    public String getPaYmentCardType() {
        return mPaYmentCardType;
    }

    public void setPaYmentCardType(String paYmentCardType) {
        mPaYmentCardType = paYmentCardType;
    }

    public String getPayerName() {
        return mPayerName;
    }

    public void setPayerName(String payerName) {
        mPayerName = payerName;
    }

    public String getPaymentCardBin() {
        return mPaymentCardBin;
    }

    public void setPaymentCardBin(String paymentCardBin) {
        mPaymentCardBin = paymentCardBin;
    }

    public String getPaymentCardBrand() {
        return mPaymentCardBrand;
    }

    public void setPaymentCardBrand(String paymentCardBrand) {
        mPaymentCardBrand = paymentCardBrand;
    }

    public String getPaymentMode() {
        return mPaymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        mPaymentMode = paymentMode;
    }

    public String getPgInvoiceNumber() {
        return mPgInvoiceNumber;
    }

    public void setPgInvoiceNumber(String pgInvoiceNumber) {
        mPgInvoiceNumber = pgInvoiceNumber;
    }

    public Double getPostingDate() {
        return mPostingDate;
    }

    public void setPostingDate(Double postingDate) {
        mPostingDate = postingDate;
    }

    public String getProcessCode() {
        return mProcessCode;
    }

    public void setProcessCode(String processCode) {
        mProcessCode = processCode;
    }

    public String getReadableChargeSlipDate() {
        return mReadableChargeSlipDate;
    }

    public void setReadableChargeSlipDate(String readableChargeSlipDate) {
        mReadableChargeSlipDate = readableChargeSlipDate;
    }

    public String getReceiptUrl() {
        return mReceiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        mReceiptUrl = receiptUrl;
    }

    public Boolean getRefundable() {
        return mRefundable;
    }

    public void setRefundable(Boolean refundable) {
        mRefundable = refundable;
    }

    public String getReverseReferenceNumber() {
        return mReverseReferenceNumber;
    }

    public void setReverseReferenceNumber(String reverseReferenceNumber) {
        mReverseReferenceNumber = reverseReferenceNumber;
    }

    public String getRrNuMber() {
        return mRrNuMber;
    }

    public void setRrNuMber(String rrNuMber) {
        mRrNuMber = rrNuMber;
    }

    public String getSessionKey() {
        return mSessionKey;
    }

    public void setSessionKey(String sessionKey) {
        mSessionKey = sessionKey;
    }

    public String getSettlementStatus() {
        return mSettlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        mSettlementStatus = settlementStatus;
    }

    public Boolean getSignReqd() {
        return mSignReqd;
    }

    public void setSignReqd(Boolean signReqd) {
        mSignReqd = signReqd;
    }

    public Boolean getSignable() {
        return mSignable;
    }

    public void setSignable(Boolean signable) {
        mSignable = signable;
    }

    public String getSignatureId() {
        return mSignatureId;
    }

    public void setSignatureId(String signatureId) {
        mSignatureId = signatureId;
    }

    public List<String> getStates() {
        return mStates;
    }

    public void setStates(List<String> states) {
        mStates = states;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public Boolean getSuccess() {
        return mSuccess;
    }

    public void setSuccess(Boolean success) {
        mSuccess = success;
    }

    public String getTcMode() {
        return mTcMode;
    }

    public void setTcMode(String tcMode) {
        mTcMode = tcMode;
    }

    public String getTid() {
        return mTid;
    }

    public void setTid(String tid) {
        mTid = tid;
    }

    public Boolean getTipADjusted() {
        return mTipADjusted;
    }

    public void setTipADjusted(Boolean tipADjusted) {
        mTipADjusted = tipADjusted;
    }

    public Boolean getTipEnabled() {
        return mTipEnabled;
    }

    public void setTipEnabled(Boolean tipEnabled) {
        mTipEnabled = tipEnabled;
    }

    public Double getTotalAmoUnt() {
        return mTotalAmoUnt;
    }

    public void setTotalAmoUnt(Double totalAmoUnt) {
        mTotalAmoUnt = totalAmoUnt;
    }

    public String getTxnId() {
        return mTxnId;
    }

    public void setTxnId(String txnId) {
        mTxnId = txnId;
    }

    public List<Object> getTxnMetadata() {
        return mTxnMetadata;
    }

    public void setTxnMetadata(List<Object> txnMetadata) {
        mTxnMetadata = txnMetadata;
    }

    public String getTxnType() {
        return mTxnType;
    }

    public void setTxnType(String txnType) {
        mTxnType = txnType;
    }

    public String getTxnTypeDesc() {
        return mTxnTypeDesc;
    }

    public void setTxnTypeDesc(String txnTypeDesc) {
        mTxnTypeDesc = txnTypeDesc;
    }

    public String getUserAgreement() {
        return mUserAgreement;
    }

    public void setUserAgreement(String userAgreement) {
        mUserAgreement = userAgreement;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public Boolean getVoidable() {
        return mVoidable;
    }

    public void setVoidable(Boolean voidable) {
        mVoidable = voidable;
    }

}
