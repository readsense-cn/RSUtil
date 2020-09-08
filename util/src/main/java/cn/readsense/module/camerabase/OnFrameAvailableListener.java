package cn.readsense.module.camerabase;

import android.graphics.SurfaceTexture;

public interface OnFrameAvailableListener {
    void onFrameAvailable(SurfaceTexture surfaceTexture);
}
