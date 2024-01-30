package com.centerm.oversea.sample.payment.module.transaction.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.centerm.oversea.sample.payment.R;
import com.centerm.oversea.sample.payment.constant.TransCode;
import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;
import com.centerm.oversea.sample.payment.utils.DataHelperUtils;
import com.pos.sdk.DevicesFactory;
import com.pos.sdk.emv.CommonTrackData;
import com.pos.sdk.emv.EmvTransParam;
import com.pos.sdk.emv.EmvTransType;
import com.pos.sdk.emv.OnCheckCardResult;

import static com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag.KEY_CARD_NUMBER;
import static com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag.KEY_CHECK_CARD_TYPE;
import static com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag.KEY_TRACK2_DATA;

/**
 * @author qzhhh on 6/24/21 16:02
 */
public class CheckCardFragment extends BaseTransactionFragment {
    private TextView mAmountTextView;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_check_card;
    }

    @Override
    protected void init(View view) {
        mAmountTextView = view.findViewById(R.id.amount_tv);
        String amount = getTransactionBundle().getString(SampleProcessTag.KEY_AMOUNT);
        if (TextUtils.isEmpty(amount)) {
            mAmountTextView.setVisibility(View.GONE);
        } else {
            mAmountTextView.setText("$" + amount);
        }
        searchCard();
    }

    private void searchCard() {
        getEmvKernelDevice().checkCard(true, true, true, 30000, new OnCheckCardResult() {
            @Override
            public void onFindMagCard(CommonTrackData commonTrackData) {
                getTransactionBundle().putString(KEY_CHECK_CARD_TYPE, "MSR");
                if (commonTrackData.getCardNo() != null) {
                    getTransactionBundle().putString(KEY_CARD_NUMBER, new String(commonTrackData.getCardNo()));
                }
                if (commonTrackData.getTrack2() != null) {
                    getTransactionBundle().putString(KEY_TRACK2_DATA, new String(commonTrackData.getTrack2()));
                }
                if (commonTrackData.getExpire() != null) {
                    getTransactionBundle().putString(KEY_TRACK2_DATA, new String(commonTrackData.getExpire()));
                }
                DevicesFactory.getDeviceManager().getBeepDevice().beep(0);
                goNext();
            }

            @Override
            public void onFindICCard() {
                getTransactionBundle().putString(KEY_CHECK_CARD_TYPE, "IC");
                showLoadingDialog("Reading Contact Card Information....");
                startEmvProcess(getEmvTransType(), createEmvTransParam(true));
            }

            @Override
            public void onFindRFCard() {
                getTransactionBundle().putString(KEY_CHECK_CARD_TYPE, "RF");
                showLoadingDialog("Reading Contactless Card Information....");
                startEmvProcess(getEmvTransType(), createEmvTransParam(false));
            }

            @Override
            public void onError(int i, String s) {
                goFailedResult(i + "", "Check card error " + s);
            }
        });
    }

    private EmvTransParam createEmvTransParam(boolean isICCard) {
        String amount = getTransactionBundle().getString(SampleProcessTag.KEY_AMOUNT);
        return new EmvTransParam.Builder()
                .setAmount(DataHelperUtils.formatAmount(amount))
                .setEmvFlow(isICCard ? EmvTransParam.EmvFlow.IC : EmvTransParam.EmvFlow.RF)
                .setForceOnline(true)
                .create();
    }

    private EmvTransType getEmvTransType() {
        String code = getTransactionBundle().getString(SampleProcessTag.KEY_TRANS_CODE);
        if (code.equals(TransCode.SALE)) {
            return EmvTransType.SALE_GOODS;
        } else {
            return EmvTransType.QUERY;
        }
    }


}
