package com.centerm.oversea.sample.payment.module.transaction.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.centerm.oversea.sample.payment.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;


/**
 * author: wanliang527</br>
 * date:2016/7/28</br>
 */
public class NumberPad extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "NumberPad";
    private final static int DEFAULT_COLUMNS = 3;
    private final static int DEFAULT_BLUE_STYLE_COLUMNS = 4;
    private List<NumberPadItem> mUserArrayList = new ArrayList<>();
    private final static List<NumberPadItem> DEFAULT_NUMBER_PAD_ARRAY_LIST = new ArrayList<>();
    private int mOrientation = HORIZONTAL;
    private TextView showView;
    private DecimalFormat formatter = new DecimalFormat("#0.00");
    private Context context;
    //DEFAULT_COLUMNS;
    private int columns = DEFAULT_COLUMNS;
    private KeyCallback callback;
    private ContentCallBack contentCallBack;
    /**
     * 元为单位
     */
    private boolean isYuanType = false;
    /**
     * 确认按键事件码
     */
    public static final String ACTION_ENTER = "\r";
    /**
     * 扫码
     */
    public static final String ACTION_SCAN = "S";
    /**
     * 银行卡
     */
    public static final String ACTION_BANK = "B";
    /**
     * 删除
     */
    public static final String ACTION_DEL = "DEL";
    /**
     * 小数点
     */
    public static final String ACTION_DOT = "DOT";
    /**
     * 清除
     */
    public static final String ACTION_AC = "AC";
    public static final String ACTION_NOTHING = "";

    /**
     * 初始化键盘数据
     */
    static {
        DEFAULT_NUMBER_PAD_ARRAY_LIST.clear();
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_number_pad_1, "1"));
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_number_pad_4, "4"));
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_number_pad_7, "7"));
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_number_pad_dot, ACTION_DOT));
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_number_pad_2, "2"));
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_number_pad_5, "5"));
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_number_pad_8, "8"));
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_number_pad_0, "0"));
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_number_pad_3, "3"));
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_number_pad_6, "6"));
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_number_pad_9, "9"));
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_number_pad_no_meaning, ACTION_NOTHING));
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_number_pad_delele, ACTION_DEL));
        DEFAULT_NUMBER_PAD_ARRAY_LIST.add(new NumberPadItem(R.drawable.sample_bg_positive_btn_pressed, ACTION_ENTER));
    }

    public NumberPad(Context context) {
        super(context);
        initView(context);
    }

    public NumberPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public NumberPad(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(value = Build.VERSION_CODES.LOLLIPOP)
    public NumberPad(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        removeAllViews();
        this.context = context;
        if (mUserArrayList == null || mUserArrayList.size() == 0) {
            mUserArrayList = DEFAULT_NUMBER_PAD_ARRAY_LIST;
        }
        Log.d("qqq", "initView:  mUserArrayList :" + mUserArrayList.get(1).getInputText());
        formatter.setRoundingMode(RoundingMode.FLOOR);
        Log.d("qqq", "initView:  mUserArrayList mOrientation:" + mOrientation);
        setOrientation(mOrientation);
        setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        int start = 0;
        int total = mUserArrayList.size();
        int row = 0;
        Log.d("qqq", "initView:  mUserArrayList columns:" + columns);
        while (start < total) {
            int end = start + columns;
            if (end > total) {
                end = total;
            }
            addView(createRows(context, row, mUserArrayList.subList(start, end)), createParams());
            start = end;
            row++;
            if (start < total) {
                float size = context.getResources().getDimension(R.dimen.common_divider_size);
                if (size < 1) {
                    size = 1;
                }
                addView(createDivider(), mOrientation == VERTICAL ? new ViewGroup.LayoutParams(-1, (int) size) : new ViewGroup.LayoutParams((int) size, -1));
            }
        }
    }

    private LinearLayout createRows(Context context, int rowIndex, List<NumberPadItem> resIds) {
        if (context == null || resIds == null || resIds.size() == 0) {
            return null;
        }
        int tag = 0;
        View view = null;
        LinearLayout row = new LinearLayout(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row.setOrientation(mOrientation == HORIZONTAL ? VERTICAL : HORIZONTAL);
        if (mOrientation == HORIZONTAL) {
            row.addView(createDivider(), new ViewGroup.LayoutParams(-1, (int) 1));
        }
        for (int i = 0; i < resIds.size(); i++) {
            view = null;
            if ((rowIndex % (columns - 1)) != 0 || rowIndex == 0) {
                tag = rowIndex + i * (columns - 1);
            }
            if (rowIndex == (columns - 1)) {
                tag = columns * (columns - 1) + i;
            }
            if (tag == mUserArrayList.size() - 1) {
                view = inflater.inflate(R.layout.sample_item_num_pad_enter, null);
            } else {
                view = createItem(inflater, resIds.get(i).getItemRes());
            }
            Log.i("qqq", "createRows: " + i + " tag:" + tag);
            view.setOnClickListener(this);
            view.setTag(resIds.get(i));
            view.setLayoutParams(createParams());
            row.addView(view);
            if (i != resIds.size() - 1) {
                float size = context.getResources().getDimension(R.dimen.common_divider_size);
                if (size < 1) {
                    size = 1;
                }
                row.addView(createDivider(), mOrientation == VERTICAL ? new ViewGroup.LayoutParams((int) size, -1) : new ViewGroup.LayoutParams(-1, (int) size));
            }
        }
        return row;
    }

    private LayoutParams createParams() {
        LayoutParams param = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, 1.0f);
        return param;
    }

    private View createDivider() {
        View view = new View(context);
        view.setBackgroundColor(ContextCompat.getColor(context, mOrientation == VERTICAL ? R.color.pad_divider : R.color
                .common_divider));
        return view;
    }

    private View createItem(LayoutInflater inflater, int resId) {
        View view = inflater.inflate(R.layout.sample_item_num_pad, null);
        ImageButton item = view.findViewById(R.id.num_item);
        if (resId > 0) {
            item.setImageResource(resId);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() == null) {
            Log.e(TAG, "onClick: view's tag is null");
            return;
        }
        NumberPadItem tag = (NumberPadItem) view.getTag();
        Log.d("qqq", "onClick: " + tag.getInputText());
        if (callback != null) {
            callback.onPressKey(tag.getInputText());
        }
        if (showView != null) {
            String showContent = showView.getText().toString();
            showContent = showContent.replaceAll(",", "");
            if (contentCallBack != null) {
                showContent = contentCallBack.onFormatContent(showContent, tag.getInputText());
            } else {
                showContent = formatAmountToShow(showContent, tag.getInputText());
            }
            showView.setText(showContent);
        }
    }

    private String formatAmountToShow(String showContent, String inputText) {
        if (!isYuanType) {
            return formatCent(showContent, inputText);
        } else {
            return formatYuan(showContent, inputText);
        }
    }


    public void bindShowView(TextView view) {
        this.showView = view;
        boolean result = showView != null && (showView.getText().toString().length() == 0
                || "0.00".equals(showView.getText().toString()));
        if (result) {
            showView.setText("0.00");
        }
        setDefaultValue();
    }

    private void setDefaultValue() {
        //初始值
        if (showView != null) {
            String showContent;
            if (!isYuanType) {
                showContent = "0.00";
            } else {
                showContent = "0";
            }
            if (contentCallBack != null) {
                showContent = contentCallBack.onFormatContent(showContent, ACTION_AC);
            }
            showView.setText(showContent);

        }
    }


    public void setCallback(KeyCallback callback) {
        this.callback = callback;
    }


    public void setContentCallBack(ContentCallBack contentCallBack) {
        this.contentCallBack = contentCallBack;
    }

    public interface KeyCallback {
        void onPressKey(String i);
    }

    public interface ContentCallBack {
        String onFormatContent(String nowContent, String newAddContent);

    }

    private int getResourceId(Context context) {
        return -1;
    }


    private int getThemeResourceId(int attrName) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attrName, value, true);
        return value.resourceId;
    }

    public void setSupportScanType(boolean supportScanType) {
        mOrientation = supportScanType ? VERTICAL : HORIZONTAL;
        //重新刷新界面
        mUserArrayList = null;
        columns = supportScanType ? DEFAULT_COLUMNS : DEFAULT_BLUE_STYLE_COLUMNS;
        initView(context);
    }

    public void setYuanType(boolean yuanType) {
        isYuanType = yuanType;
        setDefaultValue();
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    /**
     * 分为单位的计数
     *
     * @param keyValue
     */
    private String formatCent(String showContent, String keyValue) {
        double value = Double.valueOf(showContent);
        String changedText = showContent;
        switch (keyValue) {
            //删除
            case ACTION_DEL:
                if (value == 0.0) {
                    return showContent;
                }
                changedText = formatter.format(value * 0.1) + "";
                break;
            //小数点
            case ACTION_DOT:
            case ACTION_ENTER:
            case ACTION_BANK:
            case ACTION_SCAN:
            case ACTION_NOTHING:
                break;
            //其他数字字符
            default:
                //修改默认为分最大值的逻辑，目前是有问题的修改之，之前只判断8位就拦截 最大只可以999999.99
                //add by qzh
                String s = showContent.replace(".", "");
                String after = s + keyValue;
                if (after.length() > 12) {
                    Log.e("qqq", "formatCent: error number length");
                }
                String afterAmount = fillLeftZero(after, 12);
                if (isTooMuchAmount(formatToShow(afterAmount))) {
                    return showContent;
                }
                if (value == 0.0) {
                    changedText = Integer.valueOf(String.valueOf(keyValue)) * 0.01 + "";
                } else {
                    changedText = (value * 10 + Integer.valueOf(String.valueOf(keyValue)) * 0.01) + "";
                }
                break;
        }
        changedText = formatter.format(Double.valueOf(changedText));
        return changedText;
    }

    /**
     * 元为单位的计数
     *
     * @param keyValue
     */
    private String formatYuan(String showContent, String keyValue) {
        String changedText = showContent;
        switch (keyValue) {
            //删除
            case ACTION_DEL:
                if (showContent.length() - 1 > 0) {
                    if (showContent.length() - 2 > 0 && showContent.indexOf(".") == showContent.length() - 2) {
                        changedText = showContent.substring(0, showContent.length() - 2);
                    } else {
                        changedText = showContent.substring(0, showContent.length() - 1);
                    }
                } else if (showContent.length() - 1 == 0) {
                    changedText = "0";
                }
                break;
            //小数点
            case ACTION_DOT:
                if (!changedText.contains(".") && showContent.length() < 8) {
                    changedText = showContent + ".";
                }
                break;
            case ACTION_ENTER:
            case ACTION_BANK:
            case ACTION_SCAN:
                break;
            //其他数字字符
            default:
                if (isTooMuchAmount(showContent + keyValue)) {
                    return showContent;
                }

                if ("0".equals(showContent)) {
                    showContent = "";
                }
                if (changedText.contains(".")) {
                    if (changedText.indexOf(".") == changedText.length() - 3) {
                        return showContent;
                    } else {
                        changedText = showContent + keyValue;
                    }
                } else {
                    changedText = showContent + keyValue;
                }

                break;
        }
        return changedText;
    }

    /**
     * 判断金额是否超过定义的最大输入范围
     *
     * @param text
     * @return
     */
    private boolean isTooMuchAmount(String text) {
        try {
            double amount = Double.valueOf(text);
            return amount > 9999999.99;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void setUserArrayList(List<NumberPadItem> userArrayList) {
        this.mUserArrayList = userArrayList;
    }

    public final class Builder {
        private int columns = DEFAULT_COLUMNS;
        private int orientation = HORIZONTAL;
        private List<NumberPadItem> userArrays = DEFAULT_NUMBER_PAD_ARRAY_LIST;

        public void build(NumberPad numberPad) {
            if (numberPad == null) {
                Log.e(TAG, "build NumberPad error,NumberPad is null");
                return;
            }
            numberPad.setColumns(columns);
            numberPad.setOrientation(orientation);
            numberPad.setUserArrayList(userArrays);
            initView(context);
        }

        public Builder setColumns(int columns) {
            this.columns = columns;
            return this;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setUserArrays(List<NumberPadItem> userArrays) {
            this.userArrays = userArrays;
            return this;
        }

    }


    private String fillLeftZero(String content, int length) {
        if (TextUtils.isEmpty(content)) {
            return content;
        }
        int l = content.length();
        if (l < length) {
            for (int i = 0; i < (length - l); i++) {
                content = "0" + content;
            }
        }
        return content;
    }

    private String formatToShow(String amt) {
        //设置返回的金额
        String amtFormat = amt;
        try {
            //4域的转换
            if (amt != null && amt.length() == 12) {
                amtFormat = formatIsoF4(amt);
            } else {
                double d = Double.valueOf(amt);
                amtFormat = saved2Decimal(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amtFormat;
    }

    private String formatIsoF4(String isoF4) {
        if (TextUtils.isEmpty(isoF4) || isoF4.length() != 12) {
            return isoF4;
        }
        long i = Long.valueOf(isoF4.substring(0, 10));
        return i + "." + isoF4.substring(10, 12);
    }

    private String saved2Decimal(double d) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(d);
    }
}