package cn.readsense.module.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import cn.readsense.module.util.SPUtils;

/**
 * Created by dou on 2017/12/11.
 */

public class BaseApp extends Application {

    private static BaseApp app;

    public static Context getAppContext() {
        return app;
    }

    public static Resources getAppResources() {
        return app.getResources();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        SPUtils.init(this);
        Utils.init(this);

    }
}
