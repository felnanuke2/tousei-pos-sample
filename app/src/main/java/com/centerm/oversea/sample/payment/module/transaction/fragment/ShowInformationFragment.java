package com.centerm.oversea.sample.payment.module.transaction.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.centerm.oversea.sample.payment.R;
import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;

/**
 * @author qzhhh on 7/1/21 10:38
 */
public class ShowInformationFragment extends BaseTransactionFragment implements View.OnClickListener {
    private LinearLayout mLayout;
    private Button mConfirmButton;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_show_info;
    }

    @Override
    protected void init(View view) {
        mLayout = view.findViewById(R.id.info_list_ll);
        mConfirmButton = view.findViewById(R.id.confirm_btn);
        mConfirmButton.setOnClickListener(this);
        initInformation();
        String amount = getTransactionBundle().getString(SampleProcessTag.KEY_ORI_AMOUNT);
        getTransactionBundle().putString(SampleProcessTag.KEY_AMOUNT, amount);
    }

    @Override
    public void onClick(View v) {
        goNext();
    }

    private void initInformation() {
        addLine("Ori Type", "SALE");
        addLine("Ori Voucher", getTransactionBundle().getString(SampleProcessTag.KEY_ORI_VOUCHER));
        addLine("Ori Amount", "$ " + getTransactionBundle().getString(SampleProcessTag.KEY_ORI_AMOUNT));
        addLine("Ori Date", getTransactionBundle().getString(SampleProcessTag.KEY_ORI_DATE));
    }

    private void addLine(String type, String value) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_show_info_item, mLayout, false);
        ((TextView) (view.findViewById(R.id.type_tv))).setText(type);
        ((TextView) (view.findViewById(R.id.value_tv))).setText(value);
        mLayout.addView(view);
    }
}
