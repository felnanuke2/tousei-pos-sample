package com.centerm.oversea.sample.payment.module.transaction.fragment;

import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;

/**
 * @author qzhhh on 6/29/21 16:49
 */
public class InputVoucherFragment extends BaseInputInfoFragment {
    private static final String TAG = "InputVoucherFragment";


    @Override
    String getInputInfoTip() {
        return "Please Input Voucher";
    }

    @Override
    int exceptLength() {
        return 6;
    }

    @Override
    void onConfirm() {
        getTransactionBundle().putString(SampleProcessTag.KEY_ORI_VOUCHER, getInfo());

        //Simulated data
        getTransactionBundle().putString(SampleProcessTag.KEY_ORI_AMOUNT, "100.00");
        getTransactionBundle().putString(SampleProcessTag.KEY_ORI_DATE, "2021/07/01 12:08:39");
        goNext();
    }
}
