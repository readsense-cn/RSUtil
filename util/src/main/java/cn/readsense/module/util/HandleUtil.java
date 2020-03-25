package cn.readsense.module.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by mac on 16/6/20.
 */
public class HandleUtil {

    public static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable runnable) {
        HANDLER.post(runnable);
    }

    public static void runOnUiThreadDelay(Runnable runnable, long delayMillis) {
        HANDLER.postDelayed(runnable, delayMillis);
    }

    public static void removeRunable(Runnable runnable) {
        HANDLER.removeCallbacks(runnable);
    }
}
