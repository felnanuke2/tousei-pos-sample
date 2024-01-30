package com.centerm.oversea.sample.payment.module.transaction.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.centerm.oversea.sample.payment.R;
import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;
import com.pos.sdk.DevicesFactory;
import com.pos.sdk.scan.IScanCallback;
import com.pos.sdk.scan.IScanner;

/**
 * @author qzhhh on 7/1/21 15:59
 */
public class ScanFragment extends BaseTransactionFragment {
    private static final String TAG = "ScanFragment";
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_online;
    }

    @Override
    protected void init(View view) {
        Bundle scanParams = new Bundle();
        //camera id, 1 is back, 0 is front. default is back.
        scanParams.putInt(IScanner.CAMERA_ID, 1);
        //torch switch. default false
        scanParams.putBoolean(IScanner.TORCH, false);
        //scanning timeout. default 60000ms.
        scanParams.putInt(IScanner.TIMEOUT, 30_000);
        //beep after scanning finish. default false
        scanParams.putBoolean(IScanner.BEEP, true);
        //scanning continuously. default false.
        scanParams.putBoolean(IScanner.CONTINUOUS, false);

        DevicesFactory.getDeviceManager().getScanDevice().scan(scanParams, new IScanCallback.Stub() {
            @Override
            public void onSuccess(byte[] bytes) throws RemoteException {
                if (bytes == null || bytes.length == 0) {
                    goFailedResult(-100 + "", "Scan Failed");
                    return;
                }
                getTransactionBundle().putString(SampleProcessTag.KEY_SCAN_RESULT, new String(bytes));
                goNext();
            }

            @Override
            public void onFailed(int i, String s) throws RemoteException {
                Log.i(TAG, "onFailed: ");
                goFailedResult(i + "", s);
            }
        });

    }
}
