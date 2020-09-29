package co.queuebuster.qbbillingutils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.SocketTimeoutException;

import co.queuebuster.billings.mintoak.BuildConfig;
import retrofit2.Response;

public class QbUtils {

    private static final String TAG = "Utils";

    public static void showToast(Context context, String mag) {
        Toast.makeText(context, mag, Toast.LENGTH_SHORT).show();
    }

    public static String handleError(Object o, int errorCode) {
        String error = "Something Went Wrong";

        if (o instanceof Throwable) {
            Throwable t = (Throwable) o;

            if (t instanceof SocketTimeoutException) {
                error = "Network Timeout";
            } else
                error = t.getLocalizedMessage() != null && !t.getLocalizedMessage().isEmpty() ? t.getLocalizedMessage() : "Something went wrong";

        } else if (o instanceof String) {
            error = (String) o;
        } else if (o instanceof Response) {
            Response response = (Response) o;
            try {
                assert response.errorBody() != null;
                String jsonStr = response.errorBody().string();
                JSONObject jsonObject = new JSONObject(jsonStr);
                if (jsonObject.has("message"))
                    error = jsonObject.getString("message");
                else
                    error = response.errorBody().string() + " status code: " + response.code();

            } catch (Exception e) {
                e.printStackTrace();
                error = "Error " + e.getMessage() + " status code: " + response.code();
            }
        }

        if (BuildConfig.DEBUG) Log.e(TAG, "handleError: " + error);

        return "Error: " + error + " Error Code: " + errorCode;
    }
}
