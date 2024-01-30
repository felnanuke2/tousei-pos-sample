package com.centerm.oversea.sample.payment.module.transaction.fragment;

import android.view.View;
import android.widget.TextView;

import com.centerm.oversea.sample.payment.R;
import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;
import com.centerm.oversea.sample.payment.module.transaction.view.NumberPad;

/**
 * @author qzhhh on 6/24/21 16:02
 */
public class InputAmountFragment extends BaseTransactionFragment {


    private TextView mAmountTextView;
    private NumberPad mNumberPad;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_input_aoumnt;
    }

    @Override
    protected void init(View view) {
        mAmountTextView = view.findViewById(R.id.amount_tv);
        mNumberPad = view.findViewById(R.id.number_pad);
        mNumberPad.bindShowView(mAmountTextView);
        mNumberPad.setSupportScanType(false);
        mNumberPad.setCallback(key -> {
            if (key == NumberPad.ACTION_ENTER) {
                String amount = mAmountTextView.getText().toString();
                if (!checkAmount(amount)){
                    return;
                }
                getTransactionBundle().putString(SampleProcessTag.KEY_AMOUNT, amount);
                goNext();
            }
        });
    }

    private boolean checkAmount(String amount) {
        if (Double.valueOf(amount) == 0) {
            toast(R.string.tip_input_amount);
            return false;
        }
        return true;
    }


}
