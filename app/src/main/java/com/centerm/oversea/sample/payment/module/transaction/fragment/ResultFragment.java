package com.centerm.oversea.sample.payment.module.transaction.fragment;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.centerm.oversea.sample.payment.R;
import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;
import com.centerm.oversea.sample.payment.utils.DataHelperUtils;
import com.centerm.oversea.sample.payment.utils.SharePreferenceUtils;
import com.centerm.oversea.sample.payment.utils.SlipModelUtils;
import com.pos.sdk.DevicesFactory;
import com.pos.sdk.constant.DeviceErrorCode;
import com.pos.sdk.printer.IPrinterResultListener;
import com.pos.sdk.printer.PrinterDevice;
import com.pos.sdk.printer.PrinterState;
import com.pos.sdk.printer.param.BitmapPrintItemParam;
import com.pos.sdk.printer.param.PrintItemAlign;
import com.pos.sdk.printer.param.TextPrintItemParam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.pos.sdk.printer.param.BaseTextPrintItemParam.PrinterConfigTag.TAG_PRINT_GRAY;

/**
 * @author qzhhh on 6/24/21 16:02
 */
public class ResultFragment extends BaseTransactionFragment {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
    private PrinterDevice mPrinterDevice;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_result;
    }

    @Override
    protected void init(View view) {
        boolean result = getTransactionBundle().getBoolean(SampleProcessTag.KEY_RESULT_FLAG, false);
        ImageView iconImageView = view.findViewById(R.id.icon_result_iv);
        TextView messageTextView = view.findViewById(R.id.title_tv);
        LinearLayout showInfoLayout = view.findViewById(R.id.result_info_ll);
        if (result) {
            iconImageView.setImageResource(R.drawable.sample_success);
            messageTextView.setText("SUCCESS");
            String type = getTransactionBundle().getString(SampleProcessTag.KEY_TRANS_CODE);
            addLine(showInfoLayout, "Amount", "$" + getTransactionBundle().getString(SampleProcessTag.KEY_AMOUNT));
            addLine(showInfoLayout, "Type", type);
            addLine(showInfoLayout, "Voucher", getTransactionBundle().getString(SampleProcessTag.KEY_VOUCHER));
            if (!type.equals("SCAN")) {
                addLine(showInfoLayout, "Card Number", DataHelperUtils.maskInfo(getTransactionBundle().getString(SampleProcessTag.KEY_CARD_NUMBER), 6, 4));
            }
            addLine(showInfoLayout, "Trade Date", format.format(new Date()));
            mPrinterDevice = DevicesFactory.getDeviceManager().getPrintDevice();
            print();
        } else {
            iconImageView.setImageResource(R.drawable.sample_failed);
            messageTextView.setText("FAILED");
            addLine(showInfoLayout, "Error Code", getTransactionBundle().getString(SampleProcessTag.KEY_ERROR_CODE));
            addLine(showInfoLayout, "Error Message", getTransactionBundle().getString(SampleProcessTag.KEY_ERROR_MESSAGE));
        }
        Button button = view.findViewById(R.id.confirm_btn);
        button.setOnClickListener(v -> {
            finishProcess();
        });

    }

    private void addLine(LinearLayout layout, String type, String value) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_show_info_item, layout, false);
        ((TextView) (view.findViewById(R.id.type_tv))).setText(type);
        ((TextView) (view.findViewById(R.id.value_tv))).setText(value);
        layout.addView(view);
    }

    private void print() {
        if (!TextUtils.isEmpty(Build.MODEL) && Build.MODEL.contains("K5")) {
            return;
        }
        showLoadingDialog("Printing...");
        SlipModelUtils slip = new SlipModelUtils();
        try {
            PrinterState printerState = mPrinterDevice.getPrinterState();
            if (printerState.getStateCode() != DeviceErrorCode.DEVICE_PRINTER.DEVICE_OK) {
                toast(printerState.getStateMsg());
                hideLoadingDialog();
                dialog("Printer Error", printerState.getStateMsg(), (dialog, which) -> {
                    dialog.dismiss();
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        print();
                    }

                });
                return;
            }
            mPrinterDevice.clearBufferArea();
            mPrinterDevice.addTextPrintItem(slip.addTextOnePrint("RECEIPT", true, 32, PrintItemAlign.CENTER));
            addLineHa(slip);
            mPrinterDevice.addMultipleTextPrintItem(slip.addLeftAndRightItem("MERCHANT NAME:", "Test merchant", null));
            mPrinterDevice.addMultipleTextPrintItem(slip.addLeftAndRightItem("TERMINAL NO.", "00020004", null));
            mPrinterDevice.addMultipleTextPrintItem(slip.addLeftAndRightItem("OPERATOR NO.", "01", null));
            mPrinterDevice.addMultipleTextPrintItem(slip.addLeftAndRightItem("ACQUIRER:", "25350344", null));
            mPrinterDevice.addMultipleTextPrintItem(slip.addLeftAndRightItem("ISSUER:", "90880060", null));
            String type = getTransactionBundle().getString(SampleProcessTag.KEY_TRANS_CODE);
            if (!type.equals("SCAN")) {
                mPrinterDevice.addMultipleTextPrintItem(slip.addLeftAndRightItem("CARD NO.", DataHelperUtils.maskInfo(getTransactionBundle().getString(SampleProcessTag.KEY_CARD_NUMBER), 6, 4), -1, 28, new float[]{1, 3}));
            }
            mPrinterDevice.addMultipleTextPrintItem(slip.addLeftAndRightItem("EXP DATE:", "9912", null));
            mPrinterDevice.addTextPrintItem(slip.addTextOnePrint("TRANS TYPE:", false, -1, PrintItemAlign.LEFT));
            mPrinterDevice.addTextPrintItem(slip.addTextOnePrint(type, true, 36, PrintItemAlign.RIGHT));
            mPrinterDevice.addMultipleTextPrintItem(slip.addLeftAndRightItem("BATCH NO.:", "000114", null));
            mPrinterDevice.addMultipleTextPrintItem(slip.addLeftAndRightItem("VOUCHER NO.", "000060", -1, 28, new float[]{1, 1}));
            mPrinterDevice.addMultipleTextPrintItem(slip.addLeftAndRightItem("REF NO.", "200507443660", -1, 28, new float[]{1, 1}));
            mPrinterDevice.addMultipleTextPrintItem(slip.addLeftAndRightItem("TRANS TIME.", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), null));
            mPrinterDevice.addMultipleTextPrintItem(slip.addLeftAndRightItem("AMOUNT", "$ " + getTransactionBundle().getString(SampleProcessTag.KEY_AMOUNT), 28, 28, null));
            addLineHa(slip);
            addExtraInfo(slip, SampleProcessTag.KEY_ORI_VOUCHER, "Ori Voucher");
            addExtraInfo(slip, SampleProcessTag.KEY_ORI_REFERENCE_NUMBER, "Ori Reference Number");
            addExtraInfo(slip, SampleProcessTag.KEY_ORI_DATE, "Ori Date");
            mPrinterDevice.addTextPrintItem(slip.addTextOnePrint("SIGNATURE:", false, 16, PrintItemAlign.LEFT));
            putSignature();
            mPrinterDevice.addTextPrintItem(slip.addTextOnePrint("I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICES", false, 16, PrintItemAlign.CENTER));
            mPrinterDevice.addTextPrintItem(addLines(5));
            Bundle bundle = new Bundle();
            try {
                String gray = SharePreferenceUtils.getPrefString(getContext(), "GRAY", "20");
                bundle.putInt(TAG_PRINT_GRAY, Integer.valueOf(gray));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPrinterDevice.print(bundle, new IPrinterResultListener.Stub() {
                @Override
                public void onPrintFinish() throws RemoteException {
                    hideLoadingDialog();
                }

                @Override
                public void onPrintError(int i, String s) throws RemoteException {
                    toast("Printer exception: error id :" + i + " message:" + s);
                    hideLoadingDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addLineHa(SlipModelUtils slip) {
        mPrinterDevice.addTextPrintItem(slip.addTextOnePrint("--------------------------------", true, -1, PrintItemAlign.CENTER));
    }

    private TextPrintItemParam addLines(int lines) {
        StringBuffer sb = new StringBuffer();
        TextPrintItemParam t1 = new TextPrintItemParam();
        for (int i = 0; i < lines; i++) {
            sb.append("       \n");
        }
        t1.setContent(sb.toString());
        return t1;
    }

    private void putSignature() {
        byte[] sign = getTransactionBundle().getByteArray(SampleProcessTag.KEY_SIGN_BITMAP);
        if (sign == null) {
            mPrinterDevice.addTextPrintItem(addLines(3));
        } else {
            BitmapPrintItemParam bitmapPrintItemParam = new BitmapPrintItemParam();
            bitmapPrintItemParam.setBitmap(sign);
            bitmapPrintItemParam.setWidth(384);
            bitmapPrintItemParam.setHeight(180);
            mPrinterDevice.addBitmapPrintItem(bitmapPrintItemParam);
        }
    }

    private void addExtraInfo(SlipModelUtils slip, String tag, String type) {
        String ori = getTransactionBundle().getString(tag);
        if (!TextUtils.isEmpty(ori)) {
            mPrinterDevice.addTextPrintItem(slip.addTextOnePrint(type + ": " + ori, false, 16, PrintItemAlign.LEFT));
        }
    }
}
