package com.centerm.oversea.sample.payment.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;

/**
 * @author qiuchunhua@centerm.com
 * @date 2019/4/15 10:58
 */
public class SocketClient {

    private String TAG = this.getClass().getSimpleName();
    private SocketAddress address;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private boolean connectFlag = false;

    public SocketClient(String ipAddress, int port) {
        address = new InetSocketAddress(ipAddress, port);
    }

    private ConnectListener listener;

    public void connect(ConnectListener lis) {
        listener = lis;
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket();
                    socket.connect(address, 6000);
                    in = socket.getInputStream();
                    out = socket.getOutputStream();
                    connectFlag = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onConnected();
                            }
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    connectFlag = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onException(e);
                            }
                        }
                    });
                }
            }
        });
    }

    public void disconnect() {
        try {
            if (connectFlag) {
                in.close();
                out.close();
                socket.close();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onDisconnected();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        connectFlag = false;
    }

    public boolean send(final byte[] msg, final ResultCallback callback) {
        if (connectFlag && msg != null && msg.length != 0 && out != null) {
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    mSend(msg, callback);
                }
            });
            return true;
        }
        Log.i(TAG, "Send has be ignored. Please confirm that you has connected.");
        return false;
    }


    private synchronized void mSend(byte[] msg, final ResultCallback callback) {
        try {
            out.write(msg);
            out.flush();
            final byte[] temp = new byte[2048];
            final int read = in.read(temp);
            if (read > 0) {
                if (read == temp.length) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onReceive(temp);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onReceive(Arrays.copyOf(temp, read));
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private void runOnUiThread(final Runnable r) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    r.run();
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        });
    }
}
