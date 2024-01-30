package com.centerm.oversea.sample.payment.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.pos.util.HexUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * author: linwanliang</br>
 * date:2016/7/20</br>
 */
public class DataHelperUtils {
    private static final String TAG = "DataHelperUtils";

    /**
     * 根据指定长度在字符串末尾补F
     *
     * @param str 需要在末尾补F的字符串
     * @param len 补充后的长度
     * @return 返回补F后的字符串
     */
    public static String addFlast(String str, int len) {
        String reStr = "";
        if (str.length() > len) {
            return str;
        }
        while (str.length() < len) {
            str += "F";
        }
        reStr = str;
        return reStr;
    }

    /**
     * Map集合复制
     */
    public static void copyMap(Map<String, String> resource, Map<String, String> target) {
        if (resource == null || target == null) {
            return;
        }
        for (String key : resource.keySet()) {
            target.put(key, resource.get(key));
        }
    }

    /**
     * 保留两位小数。该方法有局限性，数值不允许超过9999999999999.99
     * 示例：1.1==》1.10,3==》3.00,1.23==》1.23,2.567==》2.56
     *
     * @param d 需要格式化的数值
     * @return 保留2位小数
     */
    public static String saved2Decimal(double d) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(d);
    }

//    /**
//     * 格式化金额，转换成8583接口报文要求的金额格式
//     *
//     * @param d 数值
//     * @return 1.1==》000000000110，1.23==》000000000123
//     */
//    public static String formatAmount(double d) {
//        String str = saved2Decimal(d).replace(".", "");
//        StringBuilder stringBuilder = new StringBuilder();
//        if (str.length() < 12) {
//            for (int i = 0; i < 12 - str.length(); i++) {
//                stringBuilder.append("0");
//            }
//        }
//        stringBuilder.append(str);
//        return stringBuilder.toString();
//    }

    /**
     * 格式化金额，转换成8583接口报文要求的金额格式
     *
     * @param amt 数值0.01
     * @return 1.1==》000000000110，1.23==》000000000123
     */
    public static String formatAmount(String amt) {
        return formatAmountCommon(3, amt);
    }


    public static String formatToXLen(long num, int x) {
        String numStr = String.valueOf(num);
        return formatToXLen(numStr, x);
    }

    public static String formatToXLen(String num, int x) {
        int len = num.length();
        int count = x - len;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < count; i++) {
            buffer.append("0");
        }
        buffer.append(num);
        return buffer.toString();
    }

    /**
     * 用来金额存储的函数，工程中所有格式化金额 不到前端展示的金额处理函数
     *
     * @param amt
     * @return
     */

    public static String formatAmountToUsed(String amt) {
        return formatAmountCommon(1, amt);
    }

    /**
     * 显示界面上面或者打印的统一入口显示转换函数
     * 为了加逗号
     *
     * @param amt
     * @return
     */
    public static String formatAmountToShow(String amt) {
        return formatAmountCommon(2, amt);
    }

    /**
     * @param type 1-->used  2-->show 3-->iso4
     * @param amt
     * @return
     */
    public static String formatAmountCommon(int type, String amt) {
        try {
            if (amt != null && amt.contains(",")) {
                amt = amt.replace(",", "");
            }
            switch (type) {
                case 1:
                    //去掉金额里面可能存在的逗号
                    if (amt != null && amt.length() == 12) {
                        return formatIsoF4(amt);
                    } else {
                        try {
                            double d = Double.valueOf(amt);
                            return saved2Decimal(d);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return amt;
                case 2:
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
                case 3:
                    String str = saved2Decimal(Double.valueOf(amt)).replace(".", "");
                    StringBuilder stringBuilder = new StringBuilder();
                    if (str.length() < 12) {
                        for (int i = 0; i < 12 - str.length(); i++) {
                            stringBuilder.append("0");
                        }
                    }
                    stringBuilder.append(str);
                    return stringBuilder.toString();
                default:
                    return amt;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amt;
    }


    public static String formatIsoF4(String isoF4) {
        if (TextUtils.isEmpty(isoF4) || isoF4.length() != 12) {
            return isoF4;
        }
        long i = Long.valueOf(isoF4.substring(0, 10));
        return i + "." + isoF4.substring(10, 12);
    }

    /**
     * 将4域金额转换成double数据
     */
    public static double parseIsoF4(String isoF4) {
        String str = formatIsoF4(isoF4);
        return Double.parseDouble(str);
    }

    public static double formatDouble(double amt) {
        BigDecimal bg = new BigDecimal(amt);
        double calculateResult = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return calculateResult;
    }

    /**
     * 卡号脱敏操作
     *
     * @param cardNo 卡号
     * @return 脱敏后的卡号
     */
    public static String shieldCardNo(String cardNo) {
        if (TextUtils.isEmpty(cardNo) || cardNo.length() < 11) {
            return cardNo;
        }
        String front = cardNo.substring(0, 6);
        String behind = cardNo.substring(cardNo.length() - 4, cardNo.length());
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(front);
        for (int i = 0; i < cardNo.length() - 10; i++) {
            sBuilder.append("*");
        }
        sBuilder.append(behind);
        return sBuilder.toString();
    }


    /**
     * 将卡号中间部分用*代替显示
     */
    public static String formatCardno(String cardno) {
        if (cardno == null || cardno.length() < 13 || "".equals(cardno)) {
            return cardno;
        }
        String midString = "*******************************".substring(0,
                cardno.length() - 10);
        String preString = cardno.substring(0, 6);
        String lasString = cardno.substring(cardno.length() - 4,
                cardno.length());
        return preString + midString + lasString;
    }

    /**
     * 将扫码的扫码支付号中间部分用*代替显示
     */
    public static String formatScanCode(String scanCode) {
        if (scanCode == null || "".equals(scanCode)) {
            return scanCode;
        }
        String midString = "*******************************".substring(0,
                scanCode.length() - 6);
        String preString = scanCode.substring(0, 3);
        String lasString = scanCode.substring(scanCode.length() - 3,
                scanCode.length());
        return preString + midString + lasString;
    }

    /**
     * 将扫码的扫码支付号中间部分用*代替显示
     */
    public static String formatScanCode(String scanCode, int count) {
        if (scanCode == null || scanCode.length() < 13 || "".equals(scanCode)) {
            return scanCode;
        }
        if (count > 30) {
            count = 30;
        }
        String midString = "*******************************".substring(0,
                count);
        String preString = scanCode.substring(0, 6);
        String lasString = scanCode.substring(scanCode.length() - 4);
        return preString + midString + lasString;
    }

    /**
     * 格式化卡号内容，用于显示或打印
     *
     * @param cardno        卡号
     * @param seperateCount 分隔符间隔多少字符
     * @param seperateChar  分隔字符
     * @return 格式化后的内容
     */
    public static String formatCardNumBySeperateorAdv(final String cardno, final int seperateCount, final char
            seperateChar) {
        if (TextUtils.isEmpty(cardno) || seperateCount == 0) {
            return cardno;
        }
        StringBuilder stringBuffer = new StringBuilder(100);
        for (int index = 0; index < cardno.length(); ) {
            if (index + seperateCount >= cardno.length()) {
                stringBuffer.append(cardno.substring(index));
                break;
            } else {
                stringBuffer.append(cardno.substring(index, index + seperateCount)).append(seperateChar);
                index += seperateCount;
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 4个数字2个空格进行银行卡号内容格式化
     *
     * @param cardno 银行卡号
     * @return 格式化后的内容
     */
    public static String formatCardNumBySpace(final String cardno) {
        return formatCardNumBySeperateorAdv(cardno, 4, ' ');
    }

    public static String extractName(String track1) {
        if (TextUtils.isEmpty(track1)) {
            return track1;
        }
        String[] strs = track1.split("\\^");
        if (strs.length < 2) {
            return strs[0];
        } else {
            String str = strs[1];
            if (!TextUtils.isEmpty(str)) {
                return str.trim();
            }
        }
        return track1;
    }

    public static String subZeroAndDot(String s) {
        if (s != null) {
            if (s.indexOf(".") > 0) {
                s = s.replaceAll("0+?$", "");//去掉多余的0
                s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
            }
        } else {
            s = "";
        }
        return s;
    }


    /**
     * Compress image by size, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     */
    public static Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if (os.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 100, os);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = 3;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
//      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    public static Bitmap resize(Bitmap bitmap, int newW, int newH) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        // 缩放图片的尺寸
        float scaleWidth = (float) newW / bitmapWidth;
        float scaleHeight = (float) newH / bitmapHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 产生缩放后的Bitmap对象
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
        // save file
     /*   if (!bitmap.isRecycled()) {
            bitmap.recycle();//记得释放资源，否则会内存溢出
        }*/
       /* if (!resizeBitmap.isRecycled()) {
            resizeBitmap.recycle();
        }*/
        return resizeBitmap;
    }

    /**
     * 右补空格
     *
     * @param content 原内容
     * @param length  长度
     */
    public static String fillRightSpace(String content, int length) {
        StringBuilder sBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(content)) {
            sBuilder.append(content);
        }
        int l = sBuilder.toString().length();
        if (l < length) {
            for (int i = 0; i < (length - l); i++) {
                sBuilder.append(" ");
            }
        }
        return sBuilder.toString();
    }

    /**
     * 左补0
     *
     * @param content 原内容
     * @param length  长度
     */
    public static String fillLeftZero(String content, int length) {
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

    public static Map<String, String> hexAscTlv2Map(String hexAscStr, int tLen, int lLen) {
        Map<String, String> map = new HashMap<>();
        int index = 0;
        try {
            while (index < hexAscStr.length()) {
                String tag = new String(hexAscStr.substring(index, index + tLen * 2));
                index += (tLen * 2);
                String len = new String(hexAscStr.substring(index, index + lLen * 2));
                index += (lLen * 2);
                String value = new String(HexUtils.hexStringToByte(hexAscStr.substring(index, index + Integer.valueOf
                        (len) * 2)), "GBK");
                index += Integer.valueOf(len) * 2;
                map.put(tag, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 匹配Luhn算法：可用于检测银行卡卡号（主要用于手输卡号校验）
     *
     * @param cardNo 银行卡号
     * @return 是否校验通过
     * 參考行業標準 mod 10 校驗算法 Luhn algorithm
     */
    public static boolean matchLuhn(String cardNo) {
        int[] cardNoArr = new int[cardNo.length()];
        for (int i = 0; i < cardNo.length(); i++) {
            cardNoArr[i] = Integer.valueOf(String.valueOf(cardNo.charAt(i)));
        }
        for (int i = cardNoArr.length - 2; i >= 0; i -= 2) {
            cardNoArr[i] <<= 1;
            cardNoArr[i] = cardNoArr[i] / 10 + cardNoArr[i] % 10;
        }
        int sum = 0;
        for (int i = 0; i < cardNoArr.length; i++) {
            sum += cardNoArr[i];
        }
        return sum % 10 == 0;
    }

    public static boolean checkStringLen(String info, int len) {
        if (TextUtils.isEmpty(info)) {
            return false;
        }
        if (info.length() != len) {
            return false;
        }
        return true;
    }

    public static String getInputModePrint(String inputMode) {
        if (TextUtils.isEmpty(inputMode)) {
            return "";
        }
        String inputModePrint = "";
        if (inputMode.startsWith("07") || inputMode.startsWith("98")) {
            inputModePrint = "C";
        } else if (inputMode.startsWith("05")) {
            inputModePrint = "I";
        } else if (inputMode.startsWith("02")) {
            inputModePrint = "S";
        } else if (inputMode.startsWith("01")) {
            inputModePrint = "M";
        } else {
            //无卡交易
            inputModePrint = "N";
        }
        return inputModePrint;
    }

    /**
     * 转换时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getFormatStringDate(String dateTime) {
        //START modify by lwl 20190320
        //增加非空判断
        if (TextUtils.isEmpty(dateTime)) {
            return "";
        }
        //END
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date date = sdf.parse(dateTime);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String dateString = formatter.format(date);
            return dateString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    /**
     * 转换时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getFormatStringDate1(String dateTime) {
        //START modify by lwl 20190320
        //增加非空判断
        if (TextUtils.isEmpty(dateTime)) {
            return "";
        }
        //END
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        try {
            Date date = sdf.parse(dateTime);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String dateString = formatter.format(date);
            return dateString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    /**
     * 转换时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getPrintDateExpired(String dateTime) {
        if (TextUtils.isEmpty(dateTime)) {
            return dateTime;
        }
        if (dateTime.length() > 4) {
            dateTime = dateTime.substring(0, 4);
        }
//        dateTime = "20" + dateTime;
        return dateTime;
    }


    /**
     * 获取当前时间
     *
     * @return返回字符串格式 yyyyMMddHHmmss
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }

    /**
     * 脱敏关键数据
     *
     * @param info   待脱敏
     * @param before 保留的前几位
     * @param after  保留的后几位
     * @return 脱敏数据
     */
    public static String maskInfo(String info, int before, int after) {
        if (info == null) {
            return null;
        } else {
            int len = info.length();
            if (len <= before + after) {
                return info;
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append(info.substring(0, before));

                for (int i = 0; i < len - before - after; ++i) {
                    builder.append('*');
                }

                builder.append(info.substring(len - after, len));
                return builder.toString();
            }
        }
    }

    /**
     * 校验日期是否符合格式（包括日期是否存在现实）
     * 注意：未输入年份的情况下，2月的29号为不合法
     *
     * @param date   日期
     * @param format 格式（MMdd/yyyyMM等）
     * @return true 日期格式正确
     */
    public static boolean checkDate(String date, String format) {
        DateFormat df = new SimpleDateFormat(format, Locale.CHINA);
        Date d;
        try {
            d = df.parse(date);
        } catch (Exception e) {
            //如果不能转换,肯定是错误格式
            return false;
        }
        String s1 = df.format(d);
        // 转换后的日期再转换回String,如果不等,逻辑错误.如format为"yyyy-MM-dd",date为
        // "2006-02-31",转换为日期后再转换回字符串为"2006-03-03",说明格式虽然对,但日期
        // 逻辑上不对.
        return date.equals(s1);
    }

    /**
     * 校验MMdd日期是否符合格式（包括日期是否存在现实）
     *
     * @param date 日期
     * @return true 日期格式正确
     */
    public static boolean checkDate(String date) {
        //补充年份，是2月29号合法
        if (TextUtils.isEmpty(date) || date.length() != 4) {
            return false;
        }
        date = "2008" + date;
        return checkDate(date, "yyyyMMdd");
    }

    /**
     * 获取当年份
     *
     * @return返回字符串格式
     */
    public static String getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "";
    }

    /**
     * 获取当前日期
     *
     * @return返回字符串格式 yyyyMMdd
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }


    /**
     * 获取某个文件夹下的所有文件
     *
     * @param path
     */
    public static ArrayList<String> getAllFileName(String path) {
        ArrayList<String> fileNameList = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                fileNameList.add(tempList[i].getName());
            }
        }
        return fileNameList;
    }

    /**
     * 添加两字节报文长度
     *
     * @param message 原报文
     * @return 新报文
     */
    public static byte[] addMessageLen(byte[] message) {
        int iLen = message.length;
        byte[] targets = new byte[]{(byte) (iLen / 256), (byte) (iLen % 256)};
        byte[] msg = new byte[iLen + 2];
        // 拷贝长度
        System.arraycopy(targets, 0, msg, 0, 2);
        // 拷贝报文
        System.arraycopy(message, 0, msg, 2, iLen);
        return msg;
    }

    /**
     * 去除两字节报文长度
     *
     * @param message 原报文
     * @return 新报文
     */
    public static byte[] removeMessageLen(byte[] message) {
        if (message == null || message.length < 2) {
            return message;
        }
        byte[] msg = new byte[message.length - 2];
        System.arraycopy(message, 2, msg, 0, msg.length);
        return msg;
    }

    /**
     * 将float类型的数据转换成以3位逗号隔开的字符串，并且保留两位有效数字
     *
     * @param data
     * @return
     */
    public static String formatAmountToComma(String data) {
        try {
            boolean isNeedPlus = false;
            String formatStr;
            double dataF = Double.valueOf(data);
            if (data.contains(".")) {
                if (data.indexOf(".") == data.length() - 2) {
                    //小数点在倒数第二位 0.9
                    formatStr = "#,##0.0";
                } else if (data.indexOf(".") == data.length() - 3) {
                    //小数点在倒数第三位 0.99
                    formatStr = "#,##0.00";
                } else {
                    isNeedPlus = true;
                    formatStr = "#,###";
                }
            } else {
                formatStr = "#,###";
            }
            DecimalFormat df = new DecimalFormat(formatStr);
            String result = df.format(dataF);
            if (isNeedPlus){
                result += ".";
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}
