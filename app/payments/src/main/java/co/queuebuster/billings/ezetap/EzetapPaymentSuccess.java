
package co.queuebuster.billings.ezetap;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class EzetapPaymentSuccess {

    @SerializedName("finalMessage")
    private Long mFinalMessage;
    @SerializedName("msg")
    private String mMsg;
    @SerializedName("serverResp")
    private ServerResp mServerResp;
    @SerializedName("status")
    private int mStatus;

    public Long getFinalMessage() {
        return mFinalMessage;
    }

    public void setFinalMessage(Long finalMessage) {
        mFinalMessage = finalMessage;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    public ServerResp getServerResp() {
        return mServerResp;
    }

    public void setServerResp(ServerResp serverResp) {
        mServerResp = serverResp;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

}
