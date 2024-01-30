package com.centerm.oversea.sample.payment.module.transaction.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.centerm.oversea.sample.payment.constant.TransCode;
import com.centerm.oversea.sample.payment.module.transaction.IEmvHandle;
import com.centerm.oversea.sample.payment.module.transaction.TransactionActivity;
import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;
import com.centerm.oversea.sample.payment.module.transaction.step.BaseStepFragment;
import com.centerm.oversea.sample.payment.module.transaction.view.ViewUtils;
import com.centerm.oversea.sample.payment.utils.DataHelperUtils;
import com.pos.sdk.DevicesFactory;
import com.pos.sdk.emv.EmvEndEvent;
import com.pos.sdk.emv.EmvKernelDevice;
import com.pos.sdk.emv.EmvTransParam;
import com.pos.sdk.emv.EmvTransType;
import com.pos.sdk.emv.TransResult;
import com.pos.util.EmvTlvUtils;
import com.pos.util.HexUtils;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import static com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag.KEY_CARD_NUMBER;
import static com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag.KEY_EXPIRE_DATE;
import static com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag.KEY_TRACK2_DATA;

/**
 * @author qzhhh on 6/24/21 16:02
 */
public abstract class BaseTransactionFragment extends BaseStepFragment implements IEmvHandle {
    private static final String TAG = "BaseTransactionFragment";
    public static final String CONDITION_PINPAD = "CONDITION_PINPAD";
    public static final String CONDITION_ONLINE = "CONDITION_ONLINE";
    public static final String CONDITION_FAILED = "CONDITION_FAILED";
    public static final String CONDITION_SUCCESS = "CONDITION_SUCCESS";

    private void initConditionMap() {
        conditionMap = new HashMap<>(16);
        conditionMap.put(CONDITION_PINPAD, InputPinFragment.class.getName());
        conditionMap.put(CONDITION_ONLINE, OnlineFragment.class.getName());
        conditionMap.put(CONDITION_FAILED, ResultFragment.class.getName());
        conditionMap.put(CONDITION_SUCCESS, ResultFragment.class.getName());
    }


    protected abstract int getLayoutId();

    protected abstract void init(View view);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), null);
        init(view);
        initConditionMap();
        return view;
    }

    @Override
    public void onRequestAmount(int type) {
        Log.i(TAG, "onRequestAmount: " + type);
        String amount = getTransactionBundle().getString(SampleProcessTag.KEY_AMOUNT);
        getEmvKernelDevice().importAmount(DataHelperUtils.formatAmount(amount));
    }

    @Override
    public void onRequestAccount() {
        Log.i(TAG, "onRequestAccount: ");
        hideLoadingDialog();
        String[] account = new String[]{"Default", "Savings", "Cheque/Debit", "Credit"};
        showSelectDialog("Please select account", account, (dialog, which) -> {
            dialog.dismiss();
            getEmvKernelDevice().importAccountType(which + 1);
        });
    }

    @Override
    public void onRequestTipsConfirm(byte[] title, byte[] content) {
        Log.i(TAG, "onRequestTipsConfirm: ");
        hideLoadingDialog();
        showConfirmDialog(new String(title), new String(content), (dialog, result) -> {
            dialog.dismiss();
            getEmvKernelDevice().importMsgConfirmRes(result);
        });
    }

    @Override
    public void onRequestAidSelect(int var1, String[] aids) {
        Log.i(TAG, "onRequestAidSelect: ");
        hideLoadingDialog();
        showSelectDialog("Please select aid", aids, (dialog, which) -> {
            dialog.dismiss();
            getEmvKernelDevice().importAidSelectRes(which);
        });
    }

    @Override
    public void onRequestEcashTipsConfirm() {
        Log.i(TAG, "onRequestEcashTipsConfirm: ");
        getEmvKernelDevice().importECashTipConfirmRes(false);
    }

    @Override
    public void onConfirmCardInfo(String cardNumber) {
        Log.i(TAG, "onConfirmCardInfo: " + cardNumber);
        hideLoadingDialog();
        readCardInfo(cardNumber);
        DevicesFactory.getDeviceManager().getBeepDevice().beep(0);
        String code = getTransactionBundle().getString(SampleProcessTag.KEY_TRANS_CODE);
        if (code.equals(TransCode.SALE)) {
            dialog("Please confrim card number:", cardNumber, (dialog, which) -> {
                Log.i(TAG, "onConfirmCardInfo: " + which);
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    getEmvKernelDevice().importConfirmCardInfoRes(true);
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    finishProcess();
                }
            });
        } else {
            goNext();
        }
    }

    @Override
    public void onRequestPin(boolean isOnline, String amount) {
        Log.i(TAG, "onRequestPin: ");
        hideLoadingDialog();
        getTransactionBundle().putBoolean(SampleProcessTag.KEY_REQUEST_ONLINE_PIN, isOnline);
        goNext(CONDITION_PINPAD);
    }

    @Override
    public void onRequestUserAuth(int type, byte[] msg) {
        Log.i(TAG, "onRequestUserAuth: ");
        hideLoadingDialog();
        showConfirmDialog("Please User Auth", "type:" + type + "\ncontent:" + new String(msg), (dialog, result) -> {
            dialog.dismiss();
            getEmvKernelDevice().importUserAuthRes(result);
        });
    }

    @Override
    public void onRequestVoiceTipConfirm() {
        Log.i(TAG, "onRequestVoiceTipConfirm: ");
        getEmvKernelDevice().importIssuerVoiceReference((byte) 1);

    }

    @Override
    public void onConfirmFinalSelect(byte[] aid) {
        Log.i(TAG, "onConfirmFinalSelect: ");
        getEmvKernelDevice().importFinalAidConfigsSelect(false, aid);

    }

    @Override
    public void onSignatureRequest() {
        Log.i(TAG, "onSignatureRequest: ");
        getEmvKernelDevice().importResultOfSignatureRequest(true);
    }

    @Override
    public void onCvmFlagVerify() {
        Log.i(TAG, "onCvmFlagVerify: ");
        getEmvKernelDevice().importResultOfCvmFlagVerify(true);
    }

    @Override
    public void onRequestOnline() {
        Log.i(TAG, "onRequestOnline: ");
        hideLoadingDialog();
        String[] tags = new String[]{"9F26", "9F27", "9F10", "9F37", "9F36",
                "95", "9A", "9C", "9F02", "5F2A", "82", "9F1A", "9F03",
                "9F33", "9F34", "9F35", "9F1E", "84", "9F09", "9F41", "9F63"};
        byte[] tlv = getEmvKernelDevice().readEmvKernelData(tags);
        getTransactionBundle().putString(SampleProcessTag.KEY_FIELD55_EMV_DATA, HexUtils.bcd2str(tlv));
        goNext(CONDITION_ONLINE);
    }

    @Override
    public void onTransResult(byte code, String msg) {
        Log.i(TAG, "onTransResult: " + msg);
        hideLoadingDialog();
        if (code == TransResult.TRANS_RESULT_APPROVE) {
            goSuccessResult();
            return;
        }
        String resultMessage = "Unknown reason";
//        if (){
//
//        }
        switch (code) {
            case TransResult.TRANS_RESULT_REFUSE:
                resultMessage = "Transaction refused";
                break;
            case TransResult.TRANS_RESULT_TERMINATE:
                resultMessage = "Transaction terminated";
                break;
            case TransResult.TRANS_RESULT_FALLBACK:
                resultMessage = "Transaction fallback";
                break;
            case TransResult.TRANS_RESULT_OTHER_INTERFACES:
                resultMessage = "Please check other interface";
                break;
            case TransResult.TRANS_RESULT_UNKNOWN_EVENT:
                resultMessage = "Unknown Event";
                break;
            case TransResult.TRANS_RESULT_NOT_EXPECT_EVENT:
                resultMessage = "Not Expected Event";
                break;
            default:
                break;

        }
        try {
            EmvEndEvent emvEndEvent = new EmvEndEvent(msg);
            goFailedResult(Integer.toHexString(emvEndEvent.getInnerCode()) + "", resultMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(int code, String msg) {
        Log.i(TAG, "onError: ");
        hideLoadingDialog();
        goFailedResult(code + "", "EMV:" + msg);
    }


    public void toast(final int msg) {
        if (requireActivity() instanceof TransactionActivity) {
            ((TransactionActivity) requireActivity()).getHandler().post(() -> {
                Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show();
            });
        }
    }

    public void toast(final String msg) {
        getHandler().post(() -> {
            Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show();
        });
    }

    public void dialog(String tip, String message, DialogInterface.OnClickListener listener) {
        getHandler().post(() -> {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(tip)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Confirm", listener)
                    .setNegativeButton("Cancel", listener)
                    .create().show();
        });
    }


    public void goNext() {
        if (requireActivity() instanceof TransactionActivity) {
            ((TransactionActivity) requireActivity()).goNext();
        }
    }

    public void goNext(String condition) {
        if (requireActivity() instanceof TransactionActivity) {
            ((TransactionActivity) requireActivity()).goNext(condition);
        }
    }

    public void goSuccessResult() {
        getTransactionBundle().putBoolean(SampleProcessTag.KEY_RESULT_FLAG, true);
        goNext(CONDITION_SUCCESS);
    }

    public void goFailedResult(String errorCode, String errorMsg) {
        getTransactionBundle().putBoolean(SampleProcessTag.KEY_RESULT_FLAG, false);
        getTransactionBundle().putString(SampleProcessTag.KEY_ERROR_CODE, errorCode);
        getTransactionBundle().putString(SampleProcessTag.KEY_ERROR_MESSAGE, errorMsg);
        goNext(CONDITION_FAILED);
    }

    public EmvKernelDevice getEmvKernelDevice() {
        if (requireActivity() instanceof TransactionActivity) {
            return ((TransactionActivity) requireActivity()).getEmvKernelDevice();
        }
        return null;
    }

    public void startEmvProcess(EmvTransType emvTransType, EmvTransParam transParam) {
        if (requireActivity() instanceof TransactionActivity) {
            ((TransactionActivity) requireActivity()).startEmvProcess(emvTransType, transParam);
        }
    }

    public Bundle getTransactionBundle() {
        if (requireActivity() instanceof TransactionActivity) {
            return ((TransactionActivity) requireActivity()).getTransactionBundle();
        }
        throw new IllegalStateException("please check the activity whether is TransactionActivity");
    }

    public void finishProcess() {
        if (requireActivity() instanceof TransactionActivity) {
            ((TransactionActivity) requireActivity()).finishTransaction();
        }
    }

    public Handler getHandler() {
        if (requireActivity() instanceof TransactionActivity) {
            return ((TransactionActivity) requireActivity()).getHandler();
        }
        throw new IllegalStateException("please check the activity whether is TransactionActivity");
    }

    public void showConfirmDialog(String title, String message, ViewUtils.IConfirmDialogCallback callback) {
        getHandler().post(() -> {
            ViewUtils.createTipAlertDialog(getContext(), title, message, callback);
        });
    }

    public void showSelectDialog(String title, String[] message, DialogInterface.OnClickListener callback) {
        getHandler().post(() -> {
            ViewUtils.createSelectDialog(getContext(), title, message, callback);
        });
    }

    public void showLoadingDialog(String message) {
        getHandler().post(() -> {
            ViewUtils.showLoadingDialog(getContext(), message);
        });
    }

    public void hideLoadingDialog() {
        getHandler().post(() -> {
            ViewUtils.hideLoadingDialog();
        });
    }

    private void readCardInfo(String cardNumber) {
        getTransactionBundle().putString(KEY_CARD_NUMBER, cardNumber);
        byte[] tlv = getEmvKernelDevice().readEmvKernelData(new String[]{"57", "5F24"});
        if (tlv != null && tlv.length != 0) {
            Map<String, String> map = EmvTlvUtils.tlvToStringMap(tlv);
            if (map.containsKey("57")) {
                String track2 = map.get("57");
                if (!TextUtils.isEmpty(track2)) {
                    getTransactionBundle().putString(KEY_TRACK2_DATA, track2);
                }
            }
            if (map.containsKey("5F24")) {
                String expireDate = map.get("5F24");
                if (!TextUtils.isEmpty(expireDate)) {
                    getTransactionBundle().putString(KEY_EXPIRE_DATE, expireDate);
                }
            }
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishProcess();
            return true;
        }
        return false;
    }
}
