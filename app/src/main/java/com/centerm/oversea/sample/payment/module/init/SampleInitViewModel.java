package com.centerm.oversea.sample.payment.module.init;

import android.util.Log;

import com.centerm.oversea.sample.payment.application.CentermPosApplication;
import com.pos.sdk.DeviceManager;
import com.pos.sdk.DevicesFactory;
import com.pos.sdk.callback.ResultCallback;
import com.pos.sdk.pinpad.PinpadDevice;
import com.pos.util.HexUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author qzhhh on 6/24/21 14:12
 */
public class SampleInitViewModel extends ViewModel {
    private static final String TAG = "SampleInitViewModel";
    public MutableLiveData<Boolean> initFinished = new MutableLiveData<>();
    private String mErrorCode = "Unknown Exception";

    public void startInitProcess() {
        DevicesFactory.create(CentermPosApplication.getContext(), new ResultCallback<DeviceManager>() {
            @Override
            public void onFinish(DeviceManager deviceManager) {
                Log.i(TAG, "onFinish: " + deviceManager);
                deviceManager.getEmvKernelDevice().abortEmv();
                importTestPinPadKey(deviceManager);
                boolean resultAid = NormalParameterHelper.importAids();
                boolean resultCapk = NormalParameterHelper.importCapks();
                if (resultAid && resultCapk) {
                    initFinished.postValue(true);
                } else {
                    mErrorCode = "resultAid: " + resultAid + "\n" + "resultCapk: " + resultCapk;
                    initFinished.postValue(false);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError: " + i + " errorMsg:" + s);
                mErrorCode = "ErrorCode:" + i + " ErrorMsg:" + s;
                initFinished.postValue(false);
            }
        });
    }


    public String getErrorCode() {
        return mErrorCode;
    }


    private void importTestPinPadKey(DeviceManager deviceManager) {
        PinpadDevice pinpadDevice = deviceManager.getPinpadDevice();
        boolean res = pinpadDevice.loadTmk(0, HexUtils.hexStringToByte("12345678900987654321012345678932"));
        boolean res1 = pinpadDevice.loadTmkEncryptedPik(0, 0, HexUtils.hexStringToByte("12345678900987654321012345678932"));
        Log.i(TAG, "importTestPinPadKey: " + res + " " + res1);
    }
}
