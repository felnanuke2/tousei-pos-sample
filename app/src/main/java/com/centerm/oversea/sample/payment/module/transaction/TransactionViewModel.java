package com.centerm.oversea.sample.payment.module.transaction;

import android.os.RemoteException;

import com.pos.sdk.emv.EmvKernelDevice;
import com.pos.sdk.emv.EmvTransParam;
import com.pos.sdk.emv.EmvTransType;
import com.pos.sdk.emv.IEmvKernelListener;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author qzhhh on 6/24/21 16:08
 */
public class TransactionViewModel extends ViewModel {
    public MutableLiveData<String> emvState = new MutableLiveData<>();
    private EmvKernelDevice mEmvKernelDevice;
    private List<Object> mExtra = new ArrayList<>();

    public void init(EmvKernelDevice emvKernelDevice) {
        mEmvKernelDevice = emvKernelDevice;
    }

    public List<Object> getExtra() {
        return mExtra;
    }


    public void startEmvProcess(EmvTransType emvTransType, EmvTransParam transParam) {
        mEmvKernelDevice.processEmv(emvTransType, transParam, new IEmvKernelListener.Stub() {
            @Override
            public void onRequestAmount(int i) throws RemoteException {
                setParams(i);
                emvState.postValue(EmvStateConstants.onRequestAmount);
            }

            @Override
            public void onRequestAccount() throws RemoteException {
                setParams();
                emvState.postValue(EmvStateConstants.onRequestAccount);

            }

            @Override
            public void onRequestTipsConfirm(byte[] bytes, byte[] bytes1) throws RemoteException {
                setParams(bytes, bytes1);
                emvState.postValue(EmvStateConstants.onRequestTipsConfirm);
            }

            @Override
            public void onRequestAidSelect(int i, String[] strings) throws RemoteException {
                setParams(i, strings);
                emvState.postValue(EmvStateConstants.onRequestAidSelect);
            }

            @Override
            public void onRequestEcashTipsConfirm() throws RemoteException {
                setParams();
                emvState.postValue(EmvStateConstants.onRequestEcashTipsConfirm);
            }

            @Override
            public void onConfirmCardInfo(String s) throws RemoteException {
                setParams(s);
                emvState.postValue(EmvStateConstants.onConfirmCardInfo);
            }

            @Override
            public void onRequestPin(boolean b, String s) throws RemoteException {
                setParams(b, s);
                emvState.postValue(EmvStateConstants.onRequestPin);
            }

            @Override
            public void onRequestUserAuth(int i, byte[] bytes) throws RemoteException {
                setParams(i, bytes);
                emvState.postValue(EmvStateConstants.onRequestUserAuth);
            }

            @Override
            public void onRequestVoiceTipConfirm() throws RemoteException {
                setParams();
                emvState.postValue(EmvStateConstants.onRequestVoiceTipConfirm);
            }

            @Override
            public void onConfirmFinalSelect(byte[] bytes) throws RemoteException {
                setParams(bytes);
                emvState.postValue(EmvStateConstants.onConfirmFinalSelect);
            }

            @Override
            public void onSignatureRequest() throws RemoteException {
                setParams();
                emvState.postValue(EmvStateConstants.onSignatureRequest);
            }

            @Override
            public void onCvmFlagVerify() throws RemoteException {
                setParams();
                emvState.postValue(EmvStateConstants.onCvmFlagVerify);
            }

            @Override
            public void onRequestOnline() throws RemoteException {
                setParams();
                emvState.postValue(EmvStateConstants.onRequestOnline);
            }

            @Override
            public void onTransResult(byte b, String s) throws RemoteException {
                setParams(b, s);
                emvState.postValue(EmvStateConstants.onTransResult);
            }

            @Override
            public void onError(int i, String s) throws RemoteException {
                setParams(i, s);
                emvState.postValue(EmvStateConstants.onError);
            }
        });
    }

    public void setParams(Object... objList) {
        mExtra.clear();
        if (objList == null || objList.length == 0){
            return;
        }
        for (Object o : objList) {
            mExtra.add(o);
        }
    }
}
