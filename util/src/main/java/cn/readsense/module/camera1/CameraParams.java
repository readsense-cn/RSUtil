package cn.readsense.module.camera1;

import android.hardware.Camera;

import java.util.Map;

import cn.readsense.module.base.BaseApp;
import cn.readsense.module.util.SPUtils;

public class CameraParams {
    private int facing = Camera.CameraInfo.CAMERA_FACING_BACK;

    private int oritationDisplay = 0;
    private Size previewSize = new Size(640, 480);
    private boolean filp;
    private boolean scaleWidth;

    public CameraParams() {
        invokeSp();
    }

    public class Size {
        private int previewWidth = 640;
        private int previewHeight = 480;

        Size(int previewWidth, int previewHeight) {
            this.previewWidth = previewWidth;
            this.previewHeight = previewHeight;
        }

        public int getPreviewWidth() {
            return previewWidth;
        }

        public void setPreviewWidth(int previewWidth) {
            this.previewWidth = previewWidth;
        }

        public int getPreviewHeight() {
            return previewHeight;
        }

        public void setPreviewHeight(int previewHeight) {
            this.previewHeight = previewHeight;
        }
    }

    public void setFacing(int facing) {
        this.facing = facing;
        invokeSp();
    }

    public void setOritationDisplay(int oritationDisplay) {
        this.oritationDisplay = oritationDisplay;

        if (BaseApp.getAppContext() != null) {
            String sp_name = "readsense_camera_params_" + facing;
            SPUtils.put(sp_name, "oritationDisplay", this.oritationDisplay);
        }
    }

    public void setPreviewSize(Size previewSize) {
        this.previewSize = previewSize;
    }

    public void setFilp(boolean filp) {
        this.filp = filp;
        if (BaseApp.getAppContext() != null) {
            String sp_name = "readsense_camera_params_" + facing;
            SPUtils.put(sp_name, "filp", this.filp);
        }
    }

    public void setScaleWidth(boolean scaleWidth) {
        this.scaleWidth = scaleWidth;
        if (BaseApp.getAppContext() != null) {
            String sp_name = "readsense_camera_params_" + facing;
            SPUtils.put(sp_name, "scaleWidth", this.scaleWidth);
        }
    }

    public int getFacing() {
        return facing;
    }

    public int getOritationDisplay() {
        return oritationDisplay;
    }

    public Size getPreviewSize() {
        return previewSize;
    }

    public boolean isFilp() {
        return filp;
    }

    public boolean isScaleWidth() {
        return scaleWidth;
    }

    void invokeSp() {
        if (BaseApp.getAppContext() != null) {
            String sp_name = "readsense_camera_params_" + facing;
            filp = SPUtils.getBoolean(sp_name, "filp");
            scaleWidth = SPUtils.getBoolean(sp_name, "scaleWidth");
            oritationDisplay = SPUtils.getInt(sp_name, "oritationDisplay");
        }
    }
}
