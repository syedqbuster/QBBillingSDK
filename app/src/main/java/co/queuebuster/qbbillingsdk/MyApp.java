package co.queuebuster.qbbillingsdk;

import android.app.Application;

import co.queuebuster.billings.epos.EposPayment;
import co.queuebuster.billings.mintoak.MintOakPayment;
import co.queuebuster.billings.pinelabs.PineLabCloudPayment;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //CONFIGURE MINTOAK
        MintOakPayment.configure("6GGqIJeroRVwQi3RGT5nOAsf2r","a4e11920212a47d85358bb86ba750f37","9820181246");

        //CONFIGURE EPOS
        EposPayment.configure(getApplicationContext()); //Better to configure in App Class

        //CONFIGURE PINELABS CLOUD, pass inforamtion from chain
        PineLabCloudPayment.configure("","","","");
    }
}
