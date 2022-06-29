package com.shnu.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    private final static String SALT = "1a2b3c4d";
    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    /**
     * 第一次加密
     * @param inputPass
     * @return
     */
    public static String inputPassToFromPass(String inputPass){
        String str = "" +SALT.charAt(0)+SALT.charAt(2) + inputPass + SALT.charAt(5)+SALT.charAt(4);
        return md5(str);
    }

    /**
     * 第二次加密
     * @param FromPass
     * @param salt
     * @return
     */
    public static String FromPassToDbPass(String FromPass,String salt){
        String str = salt.charAt(0)+salt.charAt(3)+FromPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);

    }

    /**
     * 整个加密过程
     * @param inputPass
     * @return
     */
    public static String inputToDbPass(String inputPass,String salt){
        String FromPass = inputPassToFromPass(inputPass);
        return FromPassToDbPass(FromPass,salt);
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFromPass("18000000000"));
        System.out.println(FromPassToDbPass(inputPassToFromPass("123456"),"123asdfa12"));
        System.out.println(inputToDbPass("123456","123asdfa12"));
    }

}
