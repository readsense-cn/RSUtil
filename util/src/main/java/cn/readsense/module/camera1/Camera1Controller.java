package cn.readsense.module.camera1;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;


/**
 * Created by dou on 2017/11/6.
 */

public class Camera1Controller implements ICameraController {

    private static final String TAG = "Camera1Controller";

    private Camera camera = null;
    private Camera.Parameters parameters;
    private int preview_width, preview_height;

    private int facing;

    private boolean isWithBufferCallback = false;//是否使用了带缓冲区的回调

    public Camera1Controller() {
    }

    public void openCamera(int cameraFacing) {
        try {
            facing = cameraFacing;
            if (camera != null) {
                stopPreview();
                releaseCamera();
            }
            camera = Camera.open(cameraFacing);
            parameters = camera.getParameters();
        } catch (Exception e) {
            throw new Error(String.format("open camera %d failed: %s", cameraFacing, e.getMessage()));
        }
    }

    public void setParamPreviewSize(int width, int height) {
        preview_width = width;
        preview_height = height;
        parameters.setPreviewSize(width, height);
    }

    public void setParamEnd() {
        if (camera != null && parameters != null) {
            camera.setParameters(parameters);
        }
    }

    public void setDisplayOrientation(Context context, int result) {
        if (result % 90 != 0)
            result = getCameraDisplayOrientation(context, facing);
        if (camera != null)
            camera.setDisplayOrientation(result);
    }

    public void setPreviewDisplay(SurfaceHolder holder) {
        try {
            if (camera != null)
                camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPreviewTexture(SurfaceTexture mTexture) {
        try {
            if (camera != null)
                camera.setPreviewTexture(mTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addPreviewCallback(Camera.PreviewCallback callback) {
        isWithBufferCallback = true;
        camera.addCallbackBuffer(new byte[preview_width * preview_height * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8]);
        camera.setPreviewCallbackWithBuffer(callback);
    }

    public void removePreviewCallback() {
        if (camera != null) {
            isWithBufferCallback = false;
            camera.setPreviewCallbackWithBuffer(null);
        }
    }


    public void startPreview() {
        if (camera != null) {
            camera.startPreview();
        }
    }

    public void stopPreview() {
        if (camera != null) {
            this.removePreviewCallback();
            camera.stopPreview();
        }
    }


    public void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }


    private int getCameraDisplayOrientation(Context context, int cameraId) {
        int result = 0;
        try {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
            short degrees = 0;
            switch (rotation) {
                case 0:
                    degrees = 0;
                    break;
                case 1:
                    degrees = 90;
                    break;
                case 2:
                    degrees = 180;
                    break;
                case 3:
                    degrees = 270;
            }

            if (info.facing == 1) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean hasCameraDevice(Context ctx) throws Exception {
        // Check if device policy has disabled the camera.

        DevicePolicyManager dpm = (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
        final boolean hasSystemFeature = ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        if (dpm.getCameraDisabled(null) || !hasSystemFeature) {
            throw new Exception(String.format("Found No Camera Feature"));
        }
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras == 0) {
            throw new Exception(String.format("Found NoCameraException"));
        }

        return true;
    }

    @Override
    public boolean hasCameraFacing(int facing) {
        int number_of_camera = Camera.getNumberOfCameras();
        for (int i = 0; i < number_of_camera; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == facing)
                return true;
        }
        return false;
    }

    @Override
    public Camera.Size getOptimalPreviewSize(int width, int height) {
        Camera.Size optimalSize = null;
        double minHeightDiff = Double.MAX_VALUE;
        double minWidthDiff = Double.MAX_VALUE;
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        if (sizes == null) return null;
        //找到宽度差距最小的
        for (Camera.Size size : sizes) {
            if (Math.abs(size.width - width) < minWidthDiff) {
                minWidthDiff = Math.abs(size.width - width);
            }
        }
        //在宽度差距最小的里面，找到高度差距最小的
        for (Camera.Size size : sizes) {
            if (Math.abs(size.width - width) == minWidthDiff) {
                if (Math.abs(size.height - height) < minHeightDiff) {
                    optimalSize = size;
                    minHeightDiff = Math.abs(size.height - height);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void printSupportPreviewSize() {
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        for (int i = 0; i < previewSizes.size(); i++) {
            Camera.Size size = previewSizes.get(i);
            Log.i(TAG, "previewSizes:width = " + size.width + " height = " + size.height);
        }

    }

    @Override
    public void printSupportPictureSize() {
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        for (int i = 0; i < pictureSizes.size(); i++) {
            Camera.Size size = pictureSizes.get(i);
            Log.i(TAG, "pictureSizes:width = " + size.width
                    + " height = " + size.height);
        }
    }

    public int getPreview_width() {
        return preview_width;
    }

    public int getPreview_height() {
        return preview_height;
    }

    public int getFacing() {
        return facing;
    }
}
