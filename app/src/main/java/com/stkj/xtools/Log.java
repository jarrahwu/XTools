package com.stkj.xtools;

/**
 * Created by jarrah on 2015/4/29.
 */
public class Log {

    public static void e(Object o) {
        if(Constant.DBG) {
            android.util.Log.e(Constant.TAG, o.toString());
        }
    }

    public static void from(Object from, Object o) {

        if(o == null || from == null) {
            return;
        }

        if(Constant.DBG) {
            android.util.Log.e(Constant.TAG + String.format("Class : %s", from.getClass().getName()), o.toString());
        }
    }

    public static void from(Object from, String o) {

        if(o == null || from == null) {
            return;
        }

        if(Constant.DBG) {
            android.util.Log.e(Constant.TAG + String.format("Class : %s", from.getClass().getName()), o);
        }
    }

}
