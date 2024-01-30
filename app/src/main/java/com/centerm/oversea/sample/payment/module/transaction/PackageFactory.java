package com.centerm.oversea.sample.payment.module.transaction;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.centerm.oversea.sample.payment.application.CentermPosApplication;
import com.centerm.oversea.sample.payment.constant.TransCode;
import com.centerm.oversea.sample.payment.module.transaction.constants.SampleProcessTag;
import com.centerm.oversea.sample.payment.utils.DataHelperUtils;
import com.centerm.oversea.sample.payment.utils.SharePreferenceUtils;
import com.common.a8583.IsoConfigParser;
import com.common.a8583.MessageFactory;
import com.common.a8583.bean.FormatInfo;
import com.common.a8583.bean.FormatInfoFactory;
import com.common.a8583.enums.IsoMessageMode;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author qzhhh on 6/25/21 17:13
 */
public class PackageFactory {
    public static final String KEY_POS_SERIAL = "KEY_POS_SERIAL";
    public static String TPDU = "6012121310" + "123456789123";
    public static final String TEST_RESPONSE = "601212131012345678912302107020000002C080101649997799999462900000000000000000050000083030313233343536373831323334353637383930313233343531353600101234567890";

    public static byte[] packMessage(Bundle bundle) {
        String transcode = bundle.getString(SampleProcessTag.KEY_TRANS_CODE);
        Map<String, String> map = new HashMap<>();
        map.put("headerdata", TPDU);
        switch (transcode) {
            case TransCode.SALE:
            case TransCode.VOID:
            case TransCode.REFUND:
            case TransCode.SCAN:
                map.put("iso_f0", "0200");
                map.put("iso_f2", bundle.getString(SampleProcessTag.KEY_CARD_NUMBER));
                map.put("iso_f3", "000000");
                map.put("iso_f4", DataHelperUtils.formatAmount(bundle.getString(SampleProcessTag.KEY_AMOUNT)));
                map.put("iso_f11", getPosSerial(CentermPosApplication.getContext(), bundle));
                map.put("iso_f35", bundle.getString(SampleProcessTag.KEY_TRACK2_DATA));
                map.put("iso_f41", "12345678");
                map.put("iso_f42", "123456789012345");
                map.put("iso_f49", "156");
                if (!TextUtils.isEmpty(bundle.getString(SampleProcessTag.KEY_FIELD55_EMV_DATA))) {
                    map.put("iso_f55", bundle.getString(SampleProcessTag.KEY_FIELD55_EMV_DATA));
                }
                map.put("iso_f60", "1234567890");

                break;
            default:
                break;
        }
        if (map.size() != 0) {
            return packMessage(map);
        }
        return null;
    }

    private static byte[] packMessage(Map<String, String> map) {
        try {
            IsoConfigParser parser = new IsoConfigParser();
            InputStream is = CentermPosApplication.getContext().getAssets().open("iso_config.xml");
            FormatInfoFactory formatInfoFactory = parser.parseFromInputStream(is);
            FormatInfo formatInfo = formatInfoFactory.getFormatInfo("COMMON", IsoMessageMode.PACK);
            return MessageFactory.getIso8583Message().packTrns(map, formatInfo).getAllMessageByteData();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unpack message exception: " + e.getMessage());
        }
        return null;
    }

    public static Map<String, String> unpack(byte[] message) {
        if (message == null) {
            System.out.println("Unpack message is null, Please double check it");
            return null;
        }
        try {
            IsoConfigParser parser = new IsoConfigParser();
            InputStream is = CentermPosApplication.getContext().getAssets().open("iso_config.xml");
            FormatInfoFactory formatInfoFactory = parser.parseFromInputStream(is);
            FormatInfo formatInfo = formatInfoFactory.getFormatInfo("COMMON", IsoMessageMode.UNPACK);
            return MessageFactory.getIso8583Message().unPackTrns(message, formatInfo);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unpack message exception: " + e.getMessage());
        }
        return null;
    }


    public static String getPosSerial(Context context, Bundle bundle) {
        int serial = SharePreferenceUtils.getPrefInt(context, KEY_POS_SERIAL, 1);
        if (serial == 999999) {
            serial = 1;
        } else {
            serial++;
        }
        SharePreferenceUtils.setPrefInt(context, KEY_POS_SERIAL, serial);
        String voucher = String.format(Locale.CHINA, "%06d", serial);
        bundle.putString(SampleProcessTag.KEY_VOUCHER, voucher);
        return voucher;
    }
}
