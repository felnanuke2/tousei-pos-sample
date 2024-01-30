package com.centerm.oversea.sample.payment.module.transaction.fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.centerm.oversea.sample.payment.R;
import com.centerm.oversea.sample.payment.module.transaction.PackageFactory;
import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;
import com.centerm.oversea.sample.payment.network.ThreadPool;
import com.pos.util.HexUtils;

import java.util.Map;

import static com.centerm.oversea.sample.payment.module.transaction.PackageFactory.TEST_RESPONSE;

/**
 * @author qzhhh on 6/24/21 16:02
 */
public class OnlineFragment extends BaseTransactionFragment {
    private static final String TAG = "OnlineFragment";
    private boolean isDemo = true;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_online;
    }

    @Override
    protected void init(View view) {
        String transcode = getTransactionBundle().getString(SampleProcessTag.KEY_TRANS_CODE);
        showLoadingDialog("Communicating with server...");
        communicate();
    }

    private void communicate() {
        ThreadPool.execute(() -> {
            byte[] message = packMessage();
//            SocketClient client = new SocketClient("ip", 8888);
//            client.send(message);
            SystemClock.sleep(2000);
            byte[] responseMessage = HexUtils.hexStringToByte(TEST_RESPONSE);
            hideLoadingDialog();
            if (responseMessage == null) {
                goFailedResult("RC998", "Nothing return from server");
                return;
            }
            handleResult(unpackMessage(responseMessage));
        });
    }

    private byte[] packMessage() {
        byte[] message = PackageFactory.packMessage(getTransactionBundle());
        Log.i(TAG, "packMessage: request message:" + HexUtils.bcd2str(message));
        return message;
    }

    private Map<String, String> unpackMessage(byte[] response) {
        Map<String, String> unpack = PackageFactory.unpack(response);
        for (Map.Entry<String, String> entry : unpack.entrySet()) {
            Log.i(TAG, "unpackMessage:  field:" + entry.getKey() + " value:" + entry.getValue());
        }
        return unpack;
    }

    private void handleResult(Map<String, String> map) {
        String iso_f39 = map.get("iso_f39");
        String type = getTransactionBundle().getString(SampleProcessTag.KEY_CHECK_CARD_TYPE);
        if ("IC".equals(type) && !isDemo) {
            getEmvKernelDevice().importOnlineResp(true, map.get(iso_f39), map.get("iso_f39"));
        } else {
            if ("00".equals(iso_f39)) {
                getTransactionBundle().putBoolean(SampleProcessTag.KEY_RESULT_FLAG, true);
                goNext();
            } else {
                goFailedResult(iso_f39, "Server error code:" + iso_f39);
            }
        }
    }
}
