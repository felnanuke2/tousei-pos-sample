package com.centerm.oversea.sample.payment.module.transaction.fragment;

import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;

import com.centerm.oversea.sample.payment.R;
import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;
import com.centerm.oversea.sample.payment.module.transaction.view.SignatureView;

/**
 * @author qzhhh on 6/29/21 16:49
 */
public class SignFragment extends BaseTransactionFragment implements View.OnClickListener {
    private SignatureView mSignatureView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_signature;
    }

    @Override
    protected void init(View view) {
        mSignatureView = view.findViewById(R.id.sign_pad);
        view.findViewById(R.id.confirm_btn).setOnClickListener(this);
        view.findViewById(R.id.exit_btn).setOnClickListener(this);
        view.findViewById(R.id.clear_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_btn:
                if (!mSignatureView.getTouched()) {
                    dialog("Tip", "Please sign your name or click exit button", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    return;
                }
                getTransactionBundle().putByteArray(SampleProcessTag.KEY_SIGN_BITMAP, mSignatureView.getBitmap());
                goNext();
                break;
            case R.id.exit_btn:
                goNext();
                break;
            case R.id.clear_btn:
                mSignatureView.clear();
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goNext();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
