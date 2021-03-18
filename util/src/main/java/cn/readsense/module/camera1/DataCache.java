package cn.readsense.module.camera1;

import cn.readsense.module.util.DLog;

public class DataCache extends Thread {
    private final String tag;
    private final int width;
    private final int height;

    public DataCache(String tag, int width, int height) {
        this.tag = tag;
        this.width = width;
        this.height = height;
    }



    private void llog(String msg) {
        DLog.d(tag, msg);
    }

    @Override
    public void run() {
        super.run();
        DLog.d("start job: " + tag);
    }


}
