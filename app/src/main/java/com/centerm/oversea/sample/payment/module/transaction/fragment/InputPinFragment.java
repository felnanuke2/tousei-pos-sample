package com.centerm.oversea.sample.payment.module.transaction.fragment;

import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.centerm.oversea.sample.payment.R;
import com.centerm.oversea.sample.payment.constant.TransCode;
import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;
import com.centerm.oversea.sample.payment.utils.DataHelperUtils;
import com.pos.sdk.DevicesFactory;
import com.pos.sdk.pinpad.IPinPadPinCallback;
import com.pos.sdk.pinpad.PinPadInfo;
import com.pos.sdk.pinpad.PinpadDevice;
import com.pos.util.HexUtils;

import static com.pos.sdk.pinpad.PinPadInfo.EncrpyMode.MODE_TWO;
import static com.pos.sdk.pinpad.PinPadInfo.EncrpyMode.MODE_ZERO;

/**
 * @author qzhhh on 6/24/21 16:02
 */
public class InputPinFragment extends BaseTransactionFragment {
    private static final String TAG = "InputPinFragment";
    private TextView amountTv, cardNoTv;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pinpad;
    }

    @Override
    protected void init(View view) {
        amountTv = view.findViewById(R.id.amount_tv);
        cardNoTv = view.findViewById(R.id.card_no_tv);
        amountTv.setText("Amount : $" + getTransactionBundle().getString(SampleProcessTag.KEY_AMOUNT));
        String cardNo = getTransactionBundle().getString(SampleProcessTag.KEY_CARD_NUMBER);
        cardNoTv.setText("Card Number : " + DataHelperUtils.maskInfo(cardNo, 6, 4));
        getPin();
    }

    private void getPin() {
        boolean isOnlinePin = getTransactionBundle().getBoolean(SampleProcessTag.KEY_REQUEST_ONLINE_PIN, true);
        PinpadDevice device = DevicesFactory.getDeviceManager().getPinpadDevice();
        String cardNo = getTransactionBundle().getString(SampleProcessTag.KEY_CARD_NUMBER);
        PinPadInfo mPinPadInfo = PinPadInfo.builder(cardNo)
                .setPikId(0)
                .setShowInputBox(true)
                .setUseRandomKeybord(true)
                .setMinLength(0)
                .setMaxLength(12)
                .setBeep(true)
                .setCancelable(true)
                .setEncrpyMode(isOnlinePin ? MODE_ZERO : MODE_TWO)
                .setShowMask(true)
                .build();
        device.getPin(mPinPadInfo, new IPinPadPinCallback.Stub() {
            @Override
            public void onReadingPin(int i, String s) throws RemoteException {
                Log.i(TAG, "onReadingPin: " + i);
            }

            @Override
            public void onReadPinCancel() throws RemoteException {
                goFailedResult("BT123", "User cancel pin input");
            }

            @Override
            public void onReadPinSuccess(byte[] bytes) throws RemoteException {
                getTransactionBundle().putString(SampleProcessTag.KEY_PIN_BLOCK, HexUtils.bcd2str(bytes));
                String type = getTransactionBundle().getString(SampleProcessTag.KEY_CHECK_CARD_TYPE);
                String code = getTransactionBundle().getString(SampleProcessTag.KEY_TRANS_CODE);
                if (("IC".equals(type) || "RF".equals(type)) && code.equals(TransCode.SALE)) {
                    getEmvKernelDevice().importPin(isOnlinePin, bytes);
                } else {
                    goNext();
                }
            }

            @Override
            public void onError(int i, String s) throws RemoteException {
                goFailedResult(i + "", "Pinpad error:" + s);
            }
        });
    }
}
