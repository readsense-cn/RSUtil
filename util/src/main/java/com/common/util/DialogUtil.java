package com.common.util;

import android.app.AlertDialog;
import android.content.Context;

/**
 * @author liyaotang
 * @date 2018/8/31
 */
public class DialogUtil {

    public static void showAlert(Context context, CharSequence title, CharSequence message, CharSequence btnTitle){
        new AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(btnTitle, null)
        .show();
    }

}
