package com.centerm.oversea.sample.payment.module.transaction.fragment;

import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.centerm.oversea.sample.payment.R;

/**
 * @author qzhhh on 6/29/21 16:49
 */
public abstract class BaseInputInfoFragment extends BaseTransactionFragment implements View.OnClickListener {
    private static final String TAG = "BaseInputInfoFragment";
    private TextView mTipTextView;
    private EditText mInputEditText;
    private Button mConfirmButton;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_input_information;
    }

    @Override
    protected void init(View view) {
        mTipTextView = view.findViewById(R.id.tip_tv);
        mInputEditText = view.findViewById(R.id.info_et);
        mConfirmButton = view.findViewById(R.id.confirm_btn);
        mTipTextView.setText(getInputInfoTip());
        mInputEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(exceptLength())});
        mConfirmButton.setOnClickListener(this);
    }

    abstract String getInputInfoTip();

    abstract int exceptLength();

    abstract void onConfirm();

    protected String getInfo() {
        return mInputEditText.getText().toString();
    }

    @Override
    public void onClick(View v) {
        if (getInfo().length() != exceptLength()) {
            toast("excepted length is " + exceptLength());
            return;
        }
        onConfirm();
    }

}
