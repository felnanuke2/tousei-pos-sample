package com.centerm.oversea.sample.payment.network;

/**
 * @author qiuchunhua@centerm.com
 * @date 2019/4/15 13:40
 */
public interface ConnectListener {
    void onConnected();

    void onDisconnected();

    void onException(Exception e);
}
