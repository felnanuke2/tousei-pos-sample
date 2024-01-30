package com.centerm.oversea.sample.payment.network;

import android.content.Context;
import android.widget.Toast;

/**
 * @author qiuchunhua@centerm.com
 * @date 2019/4/29 10:02
 */
public class ToastConnectListener implements ConnectListener {

    private Context context;

    public ToastConnectListener(Context activity) {
        context = activity;
    }

    @Override
    public void onConnected() {
        System.out.println("TCP Service connected");
    }

    @Override
    public void onDisconnected() {
        System.out.print("TCP service disconnected");
    }

    @Override
    public void onException(Exception e) {
        e.printStackTrace();
        Toast.makeText(context, "TCP service no found", Toast.LENGTH_LONG).show();
    }
}
