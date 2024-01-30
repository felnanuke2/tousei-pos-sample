package com.centerm.oversea.sample.payment.module.transaction.fragment;

import android.util.Log;
import android.view.View;

import com.centerm.oversea.sample.payment.R;
import com.centerm.oversea.sample.payment.module.transaction.constants.CConfigs;
import com.centerm.oversea.sample.payment.module.transaction.view.NumberPad;
import com.centerm.oversea.sample.payment.module.transaction.view.PassWordInputView;

/**
 * @author qzhhh on 6/29/21 16:49
 */
public class MasterPwdFragment extends BaseTransactionFragment {
    private static final String TAG = "MasterPwdFragment";
    private NumberPad mNumberPad;
    private PassWordInputView mPwdInput;
    private StringBuilder mInputPwd;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_verify_master_password;
    }

    @Override
    protected void init(View view) {
        mInputPwd = new StringBuilder();
        mPwdInput = view.findViewById(R.id.pwd_input);
        mNumberPad = view.findViewById(R.id.number_pad);
        mNumberPad.setSupportScanType(false);
        mNumberPad.setCallback(key -> {
            Log.i(TAG, "init: " + mInputPwd.toString());
            if (NumberPad.ACTION_ENTER.equals(key)) {
                if (CConfigs.MASTER_PASSWORD.equals(mInputPwd.toString())) {
                    goNext();
                } else {
                    toast("Master Pwd is incorrect");
                }
            } else if (NumberPad.ACTION_DEL.equals(key)) {
                int len = mInputPwd.length();
                if (len > 0) {
                    mInputPwd.deleteCharAt(len - 1);
                    mPwdInput.setNowSelectedCount(mInputPwd.length());
                }
            } else if (NumberPad.ACTION_DOT.equals(key) || "".equals(key)) {

            } else {
                if (mInputPwd.length() < CConfigs.MASTER_PASSWORD.length()) {
                    mInputPwd.append(key);
                    mPwdInput.setNowSelectedCount(mInputPwd.length());
//                    if (mInputPwd.length() == EmvConfig.MASTER_PASSWORD.length()) {
//                        if (EmvConfig.MASTER_PASSWORD.equals(mInputPwd.toString())) {
//                            goNext();
//                        } else {
//                            toast("Master Pwd is incorrect");
//                        }
//                    }
                }


            }
        });
    }

}
