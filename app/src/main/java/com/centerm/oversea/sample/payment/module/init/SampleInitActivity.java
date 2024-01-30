package com.centerm.oversea.sample.payment.module.init;

import android.content.Intent;
import android.util.Log;

import com.centerm.oversea.sample.payment.BaseActivity;
import com.centerm.oversea.sample.payment.R;
import com.centerm.oversea.sample.payment.module.menu.MainMenuActivity;

import androidx.lifecycle.ViewModelProvider;


/**
 * @author qzhhh on 4/24/21 11:47
 */
public class SampleInitActivity extends BaseActivity {
    private static final String TAG = "SampleInitActivity";
    private SampleInitViewModel mInitViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_initialization;
    }

    @Override
    protected void init() {
        mInitViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(SampleInitViewModel.class);
        mInitViewModel.startInitProcess();
        observe();
    }

    /**
     * observe the result of initialization
     */
    private void observe() {
        mInitViewModel.initFinished.observe(this, isFinished -> {
            Log.i(TAG, "observe: isFinished:" + isFinished);
            if (!isFinished) {
                dialog("Tip",
                        "Failed to initialization\n" + mInitViewModel.getErrorCode(),
                        (dialog, which) -> {
                            System.exit(0);
                        });
            } else {
                Log.i(TAG, "SampleInitActivity: success");
                getHandler().postDelayed(() -> {
                    Intent intent = new Intent(SampleInitActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                    SampleInitActivity.this.finish();
                }, 1000);

            }
        });
    }
}
