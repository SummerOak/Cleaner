package com.chedifier.cleaner.base;

/**
 * Created by Administrator on 2017/8/17.
 */

public class StringUtils {

    public static boolean contains(String a,String b){
        return a != null && a.contains(b);
    }

    public static boolean containsIgnoreCase(String a,String b){
        return a != null && b != null && a.toLowerCase().contains(b.toLowerCase());
    }

}
