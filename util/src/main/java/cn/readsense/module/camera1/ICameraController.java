package cn.readsense.module.camera1;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;


public interface ICameraController {


    void openCamera(int cameraFacing);

    void setParamPreviewSize(int width, int height);

    void setParamEnd();

    void setDisplayOrientation(Context context, int result);

    void setDisplayOrientation(Context context);

    void setPreviewDisplay(SurfaceHolder holder);

    void addPreviewCallback(Camera.PreviewCallback callback);

    void removePreviewCallback();

    void addPreviewCallbackWithBuffer(Camera.PreviewCallback callback);

    void removePreviewCallbackWithBuffer();

    void startPreview();

    void stopPreview();

    void releaseCamera();

    boolean hasCameraDevice(Context ctx) throws Exception;

    boolean hasCameraFacing(int facing);

    boolean hasSupportSize(int width, int height);

    void printSupportPreviewSize();

    void printSupportPictureSize();

    Camera.Size getOptimalPreviewSize(int width, int height);
}
