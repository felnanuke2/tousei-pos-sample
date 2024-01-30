package com.centerm.oversea.sample.payment.module.transaction.fragment;

import android.app.DatePickerDialog;
import android.view.KeyEvent;
import android.view.View;

import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;

import java.util.Calendar;
import java.util.Locale;

import static android.content.DialogInterface.BUTTON_NEGATIVE;

/**
 * @author qzhhh on 6/29/21 16:49
 */
public class InputOriDateFragment extends BaseInputInfoFragment {
    private static final String TAG = "InputVoucherFragment";

    @Override
    protected void init(View view) {
        super.init(view);
        Calendar dateAndTime = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
            String mm = String.format(Locale.getDefault(), "%02d", month + 1);
            String dd = String.format(Locale.getDefault(), "%02d", dayOfMonth);
            String date = mm + dd;
            getTransactionBundle().putString(SampleProcessTag.KEY_ORI_DATE, date);
            goNext();
        }, dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setCancelable(false);
        datePickerDialog.setButton(BUTTON_NEGATIVE, "", (dialog, which) -> {

        });
        datePickerDialog.show();
        datePickerDialog.setOnKeyListener((dialog, keyCode, event) -> {
            switch (keyCode) {
                case KeyEvent.KEYCODE_HOME:
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_MENU:
                    return true;
                default:
                    return false;
            }
        });
    }

    @Override
    String getInputInfoTip() {
        return "Please Input Original Date";
    }

    @Override
    int exceptLength() {
        return 4;
    }

    @Override
    void onConfirm() {
        getTransactionBundle().putString(SampleProcessTag.KEY_ORI_DATE, getInfo());
        goNext();
    }
}
