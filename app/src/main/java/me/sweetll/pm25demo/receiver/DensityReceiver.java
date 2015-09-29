package me.sweetll.pm25demo.receiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by sweet on 15-9-23.
 */
public class DensityReceiver extends ResultReceiver {
    private Receiver receiver;

    public DensityReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public interface Receiver{
        public void onReceiverResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) {
            receiver.onReceiverResult(resultCode, resultData);
        }
    }
}
