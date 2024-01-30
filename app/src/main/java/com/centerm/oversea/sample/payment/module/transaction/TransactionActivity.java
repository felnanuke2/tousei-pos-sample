package com.centerm.oversea.sample.payment.module.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.centerm.oversea.sample.payment.BaseActivity;
import com.centerm.oversea.sample.payment.R;
import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;
import com.centerm.oversea.sample.payment.module.transaction.fragment.BaseTransactionFragment;
import com.centerm.oversea.sample.payment.module.transaction.step.JumpStepException;
import com.centerm.oversea.sample.payment.module.transaction.step.TransStepFragmentManger;
import com.pos.sdk.DevicesFactory;
import com.pos.sdk.emv.EmvKernelDevice;
import com.pos.sdk.emv.EmvTransParam;
import com.pos.sdk.emv.EmvTransType;

import java.lang.reflect.Method;

import androidx.lifecycle.ViewModelProvider;

/**
 * @author qzhhh on 6/24/21 15:58
 */
public class TransactionActivity extends BaseActivity {
    private static final String TAG = "TransactionActivity";
    public static final String TAG_TRANS_CODE = "TAG_TRANS_CODE";
    private EmvKernelDevice mEmvKernelDevice;
    private BaseTransactionFragment mCurrentFragment;
    private TransactionViewModel mTransactionViewModel;
    private TransStepFragmentManger mStepFragmentManger;
    private Handler mHandler;
    private Bundle mTransactionBundle = new Bundle();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transaction;
    }

    @Override
    protected void init() {
        mHandler = new Handler(Looper.getMainLooper());
        mEmvKernelDevice = DevicesFactory.getDeviceManager().getEmvKernelDevice();
        mTransactionViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(TransactionViewModel.class);
        mTransactionViewModel.init(mEmvKernelDevice);
        mStepFragmentManger = TransStepFragmentManger.getInstance();
        Intent intent = getIntent();
        String transCode = intent.getStringExtra(TAG_TRANS_CODE);
        mTransactionBundle.putString(SampleProcessTag.KEY_TRANS_CODE, transCode);
        ((TextView) findViewById(R.id.title)).setText(transCode);
        boolean result = mStepFragmentManger.initFlow(transCode, getSupportFragmentManager(), R.id.content_fl);
        if (!result) {
            Log.i(TAG, "init: false");
            toast("Failed to init transaction: " + transCode);
            this.finish();
        }
        try {
            mStepFragmentManger.startProcess();
            mCurrentFragment = mStepFragmentManger.getCurrentFragment();
        } catch (JumpStepException e) {
            e.printStackTrace();
            toast("Failed to jump, please check it");
        }
    }

    @Override
    public Handler getHandler() {
        return mHandler;
    }

    public EmvKernelDevice getEmvKernelDevice() {
        return mEmvKernelDevice;
    }

    public void startEmvProcess(EmvTransType emvTransType, EmvTransParam transParam) {
        obverseEmvState();
        mTransactionViewModel.startEmvProcess(emvTransType, transParam);
    }

    private void obverseEmvState() {
        runOnUiThread(() -> {
            mTransactionViewModel.emvState.observe(this, state -> {
                Log.i(TAG, "obverseEmvState: " + state);
                if (mCurrentFragment == null) {
                    Log.i(TAG, "obverseEmvState: mCurrentFragment == null");
                    mTransactionBundle.putString(SampleProcessTag.KEY_ERROR_CODE, "BT78");
                    mTransactionBundle.putString(SampleProcessTag.KEY_ERROR_MESSAGE, "Current page is wrong");
                    goNext(BaseTransactionFragment.CONDITION_FAILED);
                    return;
                }
                Method[] methods = mCurrentFragment.getClass().getMethods();
                for (Method method : methods) {
                    if (method.getName().equals(state)) {
                        try {
                            method.invoke(mCurrentFragment, mTransactionViewModel.getExtra().toArray());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
    }

    public void goNext() {
        try {
            mStepFragmentManger.gotoNext();
            mCurrentFragment = mStepFragmentManger.getCurrentFragment();
        } catch (JumpStepException e) {
            e.printStackTrace();
        }
    }

    public void goNext(String condition) {
        try {
            mStepFragmentManger.gotoNext(condition);
            mCurrentFragment = mStepFragmentManger.getCurrentFragment();
        } catch (JumpStepException e) {
            e.printStackTrace();
        }
    }

    public Bundle getTransactionBundle() {
        return mTransactionBundle;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEmvKernelDevice.stopCheckCard();
        mEmvKernelDevice.abortEmv();
    }

    public void finishTransaction() {
        mEmvKernelDevice.stopCheckCard();
        mEmvKernelDevice.abortEmv();
        this.finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: " + keyCode + " mCurrentFragment:" + mCurrentFragment);
        if (mCurrentFragment != null) {
            boolean b = mCurrentFragment.onKeyDown(keyCode, event);
            Log.i(TAG, "onKeyDown: b:" + b);
            return b;
        }
        return super.onKeyDown(keyCode, event);
    }
}
