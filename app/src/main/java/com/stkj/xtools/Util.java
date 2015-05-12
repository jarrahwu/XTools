package com.stkj.xtools;

import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


public class Util {



    public static void toast(String text) {
        Toast.makeText(XTool.getInstance().getContext(),
                text, Toast.LENGTH_SHORT).show();
    }

    public static void toast(int res) {
        Toast.makeText(XTool.getInstance().getContext(), res, Toast.LENGTH_SHORT).show();
    }


    public static boolean isInputEmpty(String input, String toast) {
        if (TextUtils.isEmpty(input)) {
            Util.toast(toast);
            return true;
        } else {
            return false;
        }
    }

    public static void hideSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
