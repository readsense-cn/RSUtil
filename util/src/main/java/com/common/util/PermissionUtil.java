package com.common.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * @author liyaotang
 * @date 2018/11/28
 */
public class PermissionUtil {

//    private void  checkPermission(Context activity)
//    {
//        // 检查权限是否获取（android6.0及以上系统可能默认关闭权限，且没提示）
//        PackageManager pm = activity.getPackageManager();
//        boolean permission_readStorage = (PackageManager.PERMISSION_GRANTED ==
//                pm.checkPermission("android.permission.READ_EXTERNAL_STORAGE", "packageName"));
//        boolean permission_writeStorage = (PackageManager.PERMISSION_GRANTED ==
//                pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "packageName"));
//        boolean permission_caremera = (PackageManager.PERMISSION_GRANTED ==
//                pm.checkPermission("android.permission.RECORD_AUDIO", "packageName"));
//
//        if (!(permission_readStorage && permission_writeStorage && permission_caremera)) {
//            ActivityCompat.requestPermissions(AudioRecordActivity.this, new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.RECORD_AUDIO,
//            }, 0x01);
//        }
//    }

    public static boolean checkPermission(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(permission, context.getPackageName()));
    }
}
