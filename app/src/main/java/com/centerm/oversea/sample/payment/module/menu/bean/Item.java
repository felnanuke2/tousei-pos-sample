package com.centerm.oversea.sample.payment.module.menu.bean;

/**
 * @author qzhhh on 2020/7/28 11:27
 */
public class Item {
    public String content;
    public int resId;
    public int fontSize = -1;
    public Class targetClass;

    public Item(String content, int resId, Class targetClass) {
        this.content = content;
        this.resId = resId;
        this.targetClass = targetClass;
    }
}