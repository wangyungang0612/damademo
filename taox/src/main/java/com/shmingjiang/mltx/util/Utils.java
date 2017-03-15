package com.shmingjiang.mltx.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by familywei on 2015/12/14.
 */
public class Utils {

    // 使用这个转换函数   PLC机内直接显示数字 而不是ASCII码了
    public static byte[] strToBinNum(String str) {
        // “1234”
        List<Integer> list = new ArrayList<Integer>();
        //   String num = Integer.toBinaryString(Integer.parseInt(str));
        String num = Integer.toBinaryString(Float.floatToIntBits(Integer.parseInt(str)));
        // System.out.println(num);

        int i = num.length() % 8;
        for (int j = 0; j < 8 - i; j++) {
            num = "0" + num;
        }
        //  System.out.println(num);

        int m = 0;
        int n = 8;

        for (int j = 0; j < num.length() / 8; j++) {

            list.add(Integer.parseInt(num.substring(m, n), 2));
            //System.out.println(num.substring(0, 8));
            //System.out.println(num.substring(8, 16));
            m = n;
            n = n + 8;
        }

        byte[] bytes = new byte[4];
        //  int r = 0;

        for (int k = 0; k < list.size(); k++) {
            //++r;
            bytes[k] = list.get(k).byteValue();
        }

        for (int k = 0; k < bytes.length; k++) {
            System.out.println(bytes[k] + "");
        }


        return bytes;
    }

    //整数
    public static byte strToIntNum(String str) {
        List<Integer> list = new ArrayList<Integer>();
        String num = Integer.toBinaryString(Integer.parseInt(str));
//        String num = Integer.toBinaryString(Float.floatToIntBits(Integer.parseInt(str)));
        // System.out.println(num);
        int i = num.length() % 8;
        for (int j = 0; j < 8 - i; j++) {
            num = "0" + num;
        }
        //  System.out.println(num);
        int m = 0;
        int n = 8;
        for (int j = 0; j < num.length() / 8; j++) {
            list.add(Integer.parseInt(num.substring(m, n), 2));
            //System.out.println(num.substring(0, 8));
            //System.out.println(num.substring(8, 16));
            m = n;
            n = n + 8;
        }

        byte[] bytes = new byte[4];
        //  int r = 0;
        for (int k = 0; k < list.size(); k++) {
            //++r;
            bytes[k] = list.get(k).byteValue();
        }
        for (int k = 0; k < bytes.length; k++) {
            System.out.println(bytes[k] + "");
        }
        return bytes[0];
    }

    public static byte[] hwdToByteArr(String hwd) {
        String s = hwd;
        if ((hwd.length() < 4) && (hwd.length() >= 0)) {
            for (int i = 0; i < 4 - hwd.length(); i++) {
                s = "0" + s;
            }

        } else if (hwd.length() == 4) {
            return hwd.getBytes();
        }
        return s.getBytes();
    }

    public static List<Integer> str2BinNum(String str) {
        List<Integer> list = new ArrayList<>();
        char[] strchar = str.toCharArray();
        String result = "";
        if ((str.length() < 4) && (str.length() >= 0)) {
            for (int i = 0; i < 4 - str.length(); i++) {
                list.add(0);
            }
        }
        for (int i = 0; i < strchar.length; i++) {
            result += Integer.toBinaryString(strchar[i]);
        }
        divTheBinstr(result, list);
        return list;
    }

    public static void divTheBinstr(String str, List<Integer> list) {
        int size = str.length();
        int len = size / 8 + 1;
        int m = size % 8;
        Integer.parseInt(str.substring(0, m), 2);
        list.add(Integer.parseInt(str.substring(0, m), 2));
        // str.substring(m+1, m+9);

        int k = m + 1;
        int n = m + 8;
        for (int i = 0; i < len - 1; i++) {
            list.add(Integer.parseInt(str.substring(k, n), 2));
            k = n + 1;
            n = n + 8;
        }
    }

    public static byte[] intList2ByteArr(List<Integer> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i).byteValue();
        }
        return bytes;
    }
}
