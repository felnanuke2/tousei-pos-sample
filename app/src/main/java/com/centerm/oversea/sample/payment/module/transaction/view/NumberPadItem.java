package com.centerm.oversea.sample.payment.module.transaction.view;

/**
 * @author qzhhh on 2019-11-12 16:54
 */
public class NumberPadItem {
    private int itemRes;
    private String inputText;

    public NumberPadItem(int itemRes, String inputText) {
        this.itemRes = itemRes;
        this.inputText = inputText;
    }

    public int getItemRes() {
        return itemRes;
    }

    public String getInputText() {
        return inputText;
    }
}
