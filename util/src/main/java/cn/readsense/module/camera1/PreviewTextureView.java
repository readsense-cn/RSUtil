package cn.readsense.module.camera1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;

public class PreviewTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    ICameraController cameraController;
    Context context;
    int buffer_width;
    int buffer_height;
    int vw;
    int vh;
    private boolean flip = false;

    public float getScale() {
        return scale;
    }

    private float scale = 1f;


    public PreviewTextureView(Context context, ICameraController cameraController, int PREVIEW_WIDTH, int PREVIEW_HEIGHT) {
        super(context);
        this.cameraController = cameraController;
        this.context = context;
        setSurfaceTextureListener(this);
        buffer_width = PREVIEW_WIDTH;
        buffer_height = PREVIEW_HEIGHT;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
         cameraController.setPreviewTexture(surface);
        cameraController.startPreview();
        configureTransform(width, height);
        vw = width;
        vh = height;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        configureTransform(width, height);
        vw = width;
        vh = height;
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        cameraController.stopPreview();
        cameraController.releaseCamera();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private boolean selfConfig = false;

    public void setConfigureTransform(int bufferWidth, int bufferHeight) {
        setConfigureTransform(bufferWidth, bufferHeight, false);
    }

    public void setConfigureTransform(int bufferWidth, int bufferHeight, boolean flip) {
        this.buffer_width = bufferWidth;
        this.buffer_height = bufferHeight;
        selfConfig = true;
        this.flip = flip;
        configureTransform(vw, vh);
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = (Activity) context;
        if (null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, buffer_width, buffer_height);
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (selfConfig) {
            bufferRect = new RectF(0, 0, buffer_width, buffer_height);
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            scale = Math.max(
                    (float) viewHeight / buffer_height,
                    (float) viewWidth / buffer_width);
            if (flip)
                matrix.postScale(-scale, scale, centerX, centerY);
            else
                matrix.postScale(scale, scale, centerX, centerY);
        } else {
            if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
                bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
                centerX = viewRect.centerX();
                centerY = viewRect.centerY();
                matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
                scale = Math.max(
                        (float) viewHeight / buffer_height,
                        (float) viewWidth / buffer_width);
                matrix.postScale(scale, scale, centerX, centerY);
                matrix.postRotate(90 * (rotation - 2), centerX, centerY);
            } else if (Surface.ROTATION_180 == rotation) {
                matrix.postRotate(180, centerX, centerY);
            }

        }
        setTransform(matrix);
    }
}
