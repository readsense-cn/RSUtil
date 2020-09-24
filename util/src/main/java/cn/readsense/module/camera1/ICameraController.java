package cn.readsense.module.camera1;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;


public interface ICameraController {

    void openCamera(int cameraFacing);

    void setParamPreviewSize(int width, int height);

    void setParamEnd();

    void setDisplayOrientation(Context context, int result);

    void setPreviewDisplay(SurfaceHolder holder);

    void setPreviewTexture(SurfaceTexture holder);

    void addPreviewCallback(Camera.PreviewCallback callback);

    void removePreviewCallback();

    void startPreview();

    void stopPreview();

    void releaseCamera();

    boolean hasCameraDevice(Context ctx) throws Exception;

    boolean hasCameraFacing(int facing);

    void printSupportPreviewSize();

    void printSupportPictureSize();

    Camera.Size getOptimalPreviewSize(int width, int height);

    void startRecord(String saveFileName);

    void stopRecord();
}
