package com.centerm.oversea.sample.payment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author qzhhh on 6/24/21 14:21
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Handler mHandler;

    protected abstract int getLayoutId();

    protected abstract void init();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        handleStatusBar();
        mHandler = new Handler(Looper.getMainLooper());
        init();
    }

    private void handleStatusBar() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    public void toast(final String msg) {
        runOnUiThread(() -> Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_LONG).show());
    }

    public void dialog(String tip, String message, DialogInterface.OnClickListener onClickListener) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(BaseActivity.this)
                    .setTitle(tip)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Confirm",  onClickListener)
                    .create().show();
        });
    }

    public Handler getHandler() {
        return mHandler;
    }
}
