package com.centerm.oversea.sample.payment.module.transaction.step;

import android.util.Log;

import com.centerm.oversea.sample.payment.module.transaction.fragment.BaseTransactionFragment;
import com.centerm.oversea.sample.payment.module.transaction.fragment.CheckCardFragment;
import com.centerm.oversea.sample.payment.module.transaction.fragment.InputAmountFragment;
import com.centerm.oversea.sample.payment.module.transaction.fragment.InputOriDateFragment;
import com.centerm.oversea.sample.payment.module.transaction.fragment.InputPinFragment;
import com.centerm.oversea.sample.payment.module.transaction.fragment.InputReferenceFragment;
import com.centerm.oversea.sample.payment.module.transaction.fragment.InputVoucherFragment;
import com.centerm.oversea.sample.payment.module.transaction.fragment.MasterPwdFragment;
import com.centerm.oversea.sample.payment.module.transaction.fragment.OnlineFragment;
import com.centerm.oversea.sample.payment.module.transaction.fragment.ResultFragment;
import com.centerm.oversea.sample.payment.module.transaction.fragment.ScanFragment;
import com.centerm.oversea.sample.payment.module.transaction.fragment.ShowInformationFragment;
import com.centerm.oversea.sample.payment.module.transaction.fragment.SignFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentManager;

/**
 * @author qzhhh on 6/24/21 17:02
 */
public class TransStepFragmentManger {
    private static final String TAG = "TransStepFragmentManger";
    private FragmentManager mFragmentManager;
    private int layoutId;


    private BaseStepFragment mCurrentFragment;
    private List<Class<BaseStepFragment>> mFragmentList;

    public static TransStepFragmentManger getInstance() {
        return Inner.INSTACE;
    }

    public boolean initFlow(String transCode, FragmentManager fragmentManager, int layoutId) {
        if (transCode.equals("VOID")) {
            mFragmentList = Inner.VOID;
        } else if (transCode.equals("REFUND")) {
            mFragmentList = Inner.REFUND;
        } else if (transCode.equals("SCAN")) {
            mFragmentList = Inner.SCAN;
        } else {
            mFragmentList = Inner.SALE;
        }
        this.mFragmentManager = fragmentManager;
        this.layoutId = layoutId;
        return true;
    }

    public void startProcess() throws JumpStepException {
        if (mFragmentList.size() == 0) {
            return;
        }
        jumpFragment(mFragmentList.get(0));
    }

    private void jumpFragment(Class<BaseStepFragment> clazz) throws JumpStepException {
        try {
            BaseStepFragment baseStepFragment = clazz.newInstance();
            if (baseStepFragment instanceof OnlineFragment
                    || baseStepFragment instanceof ScanFragment) {
                mFragmentManager.beginTransaction().add(layoutId, baseStepFragment).commitAllowingStateLoss();
            } else {
                mFragmentManager.beginTransaction().replace(layoutId, baseStepFragment).commitAllowingStateLoss();
            }
            mCurrentFragment = baseStepFragment;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JumpStepException();
        }
    }

    private int getIndex(String className) {
        int currentIndex = -1;
        for (Class<BaseStepFragment> baseStepFragmentClass : mFragmentList) {
            Log.i(TAG, "getIndex: baseStepFragmentClass:" + baseStepFragmentClass.getName());
            if (className.equals(baseStepFragmentClass.getName())) {
                currentIndex = mFragmentList.indexOf(baseStepFragmentClass);
                break;
            }
        }
        return currentIndex;
    }

    public void gotoNext() throws JumpStepException {
        int index = getIndex(mCurrentFragment.getClass().getName());
        if (index == -1 || index + 1 >= mFragmentList.size()) {
            return;
        }
        jumpFragment(mFragmentList.get(index + 1));
    }

    public void gotoNext(String condition) throws JumpStepException {
        Log.i(TAG, "gotoNext condition: " + condition);
        String nextStepClassName = mCurrentFragment.getNextStep(condition);
        Log.d(TAG, "gotoNext condition: nextStepClassName:" + nextStepClassName);
        if (nextStepClassName == null) {
            gotoNext();
            return;
        }
        int index = getIndex(nextStepClassName);
        Log.d(TAG, "gotoNext condition: index:" + index);
        if (index == -1 || index >= mFragmentList.size()) {
            return;
        }
        jumpFragment(mFragmentList.get(index));
    }

    public BaseTransactionFragment getCurrentFragment() {
        return (BaseTransactionFragment) mCurrentFragment;
    }

    public static class Inner {
        public static TransStepFragmentManger INSTACE = new TransStepFragmentManger();
        public static List<Class<BaseStepFragment>> SALE = new ArrayList() {{
            add(InputAmountFragment.class);
            add(CheckCardFragment.class);
            add(InputPinFragment.class);
            add(OnlineFragment.class);
            add(SignFragment.class);
            add(ResultFragment.class);
        }};
        public static List<Class<BaseStepFragment>> VOID = new ArrayList() {{
            add(MasterPwdFragment.class);
            add(InputVoucherFragment.class);
            add(ShowInformationFragment.class);
            add(CheckCardFragment.class);
            add(InputPinFragment.class);
            add(OnlineFragment.class);
            add(SignFragment.class);
            add(ResultFragment.class);
        }};

        public static List<Class<BaseStepFragment>> REFUND = new ArrayList() {{
            add(MasterPwdFragment.class);
            add(CheckCardFragment.class);
            add(InputReferenceFragment.class);
            add(InputOriDateFragment.class);
            add(InputAmountFragment.class);
            add(OnlineFragment.class);
            add(ResultFragment.class);
        }};

        public static List<Class<BaseStepFragment>> SCAN = new ArrayList() {{
            add(InputAmountFragment.class);
            add(ScanFragment.class);
            add(OnlineFragment.class);
            add(ResultFragment.class);
        }};


    }

}
