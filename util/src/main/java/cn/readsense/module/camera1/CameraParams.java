package cn.readsense.module.camera1;

import android.hardware.Camera;

public class CameraParams {
    public int facing = Camera.CameraInfo.CAMERA_FACING_BACK;
    public int oritationDisplay = 0;
    public Size previewSize = new Size(640, 480);
    public boolean filp;
    public boolean scaleWidth;

    public CameraParams() {
    }

    public class Size {
        public int previewWidth = 640;
        public int previewHeight = 480;

        public Size(int previewWidth, int previewHeight) {
            this.previewWidth = previewWidth;
            this.previewHeight = previewHeight;
        }
    }
}
