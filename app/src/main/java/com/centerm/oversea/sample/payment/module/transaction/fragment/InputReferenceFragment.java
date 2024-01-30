package com.centerm.oversea.sample.payment.module.transaction.fragment;

import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;

/**
 * @author qzhhh on 6/29/21 16:49
 */
public class InputReferenceFragment extends BaseInputInfoFragment {
    private static final String TAG = "InputReferenceFragment";


    @Override
    String getInputInfoTip() {
        return "Please Input Reference Number";
    }

    @Override
    int exceptLength() {
        return 12;
    }

    @Override
    void onConfirm() {
        getTransactionBundle().putString(SampleProcessTag.KEY_ORI_REFERENCE_NUMBER, getInfo());
        goNext();
    }
}
