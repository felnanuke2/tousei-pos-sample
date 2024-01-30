package com.centerm.oversea.sample.payment.module.menu.adapter;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.centerm.oversea.sample.payment.module.menu.bean.Item;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author qzhhh on 2020/7/23 15:19
 */
public class MenuItemRecycleAdapter extends RecyclerView.Adapter<MenuItemRecycleAdapter.MyViewHolder> {
    private List<Item> list;
    private ItemClickListener mListener;
    private int iconWidth = -1;
    private int iconHeight = -1;

    public MenuItemRecycleAdapter(List<Item> list, ItemClickListener listener) {
        this.list = list;
        mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        return new MyViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setContent(list.get(position).content, list.get(position).resId);
        final Item item = list.get(position);
        if (list.get(position).fontSize > 0) {
            holder.mTextView.setTextSize(list.get(position).fontSize);
        }
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }

        public void setContent(String content, int resId) {
            if (mTextView != null) {
                mTextView.setText(content);
                setTextViewGravity(mTextView);
                Drawable drawable = mTextView.getContext().getDrawable(resId);
                if (iconHeight != -1 && iconWidth != -1) {
                    drawable.setBounds(0, 0, iconWidth, iconHeight);
                } else {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                }

                mTextView.setCompoundDrawables(null, drawable, null, null);
            }
        }

        private void setTextViewGravity(final TextView tv) {

            ViewTreeObserver vto = tv.getViewTreeObserver();

            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override

                public boolean onPreDraw() {
                    if (tv.getLineCount() == 1) {
                        tv.setGravity(Gravity.CENTER);

                    } else {
                        tv.setGravity(Gravity.LEFT | Gravity.CENTER);

                    }
                    return true;
                }

            });

        }
    }

    public interface ItemClickListener {
        void onClick(Item item);
    }

    public void setIconWidth(int iconWidth, int iconHeight) {
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }
}
