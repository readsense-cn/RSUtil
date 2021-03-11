package com.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    /** 获取当前时间字串，格式 yyyy-MM-dd HH:mm:ss */
    public static String getTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

}
