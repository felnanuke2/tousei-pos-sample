package com.centerm.oversea.sample.payment.module.transaction;

/**
 * @author qzhhh on 6/24/21 16:10
 */
public interface IEmvHandle {
    void onRequestAmount(int type);

    void onRequestAccount();

    void onRequestTipsConfirm(byte[] title, byte[] content);

    void onRequestAidSelect(int var1, String[] aids);

    void onRequestEcashTipsConfirm();

    void onConfirmCardInfo(String cardNumber);

    void onRequestPin(boolean isOnline, String amount);

    void onRequestUserAuth(int type, byte[] msg);

    void onRequestVoiceTipConfirm();

    void onConfirmFinalSelect(byte[] aid);

    void onSignatureRequest();

    void onCvmFlagVerify();

    void onRequestOnline();

    void onTransResult(byte code, String msg);

    void onError(int code, String msg);
}
