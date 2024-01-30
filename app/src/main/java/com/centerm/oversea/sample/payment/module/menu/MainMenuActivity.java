package com.centerm.oversea.sample.payment.module.menu;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.TextView;

import com.centerm.oversea.sample.payment.BaseActivity;
import com.centerm.oversea.sample.payment.R;
import com.centerm.oversea.sample.payment.constant.TransCode;
import com.centerm.oversea.sample.payment.module.menu.adapter.MenuItemRecycleAdapter;
import com.centerm.oversea.sample.payment.module.menu.bean.Item;
import com.centerm.oversea.sample.payment.module.transaction.TransactionActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.centerm.oversea.sample.payment.module.transaction.TransactionActivity.TAG_TRANS_CODE;


public class MainMenuActivity extends BaseActivity implements MenuItemRecycleAdapter.ItemClickListener {
    private RecyclerView mRecyclerView;
    private List<Item> mItemList;
    private MenuItemRecycleAdapter mAdapter;
    private TextView versionTextView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        mRecyclerView = findViewById(R.id.rv);
        versionTextView = findViewById(R.id.version_tv);
        versionTextView.setText(readVer());
        mItemList = initListData();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        mAdapter = new MenuItemRecycleAdapter(mItemList, this);
        mAdapter.setIconWidth(95, 85);
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<Item> initListData() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(TransCode.SALE, R.drawable.sample_icon_bank, null));
        list.add(new Item(TransCode.VOID, R.drawable.sample_icon_bank, null));
        list.add(new Item(TransCode.REFUND, R.drawable.sample_icon_bank, null));
        list.add(new Item(TransCode.SCAN, R.drawable.sample_icon_scan, null));
        return list;
    }

    private void changeItem(Item item, boolean isShow) {
        if (!isShow) {
            if (mItemList.contains(item)) {
                mItemList.remove(item);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            if (!mItemList.contains(item)) {
                mItemList.add(item);
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onClick(Item item) {
        if (item.targetClass == null) {
            Intent intent = new Intent(this, TransactionActivity.class);
            intent.putExtra(TAG_TRANS_CODE, item.content);
            startActivity(intent);
        } else {
            startActivity(new Intent(this, item.targetClass));
        }
    }

    public String readVer() {
        String verName = "UnKnown";
        try {
            verName = this.getPackageManager().getPackageInfo(
                    getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "Version:" + verName;
    }
}
