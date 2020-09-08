package cn.readsense.module.camera1;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by dou on 2017/11/6.
 */

public class PreviewSurfaceView extends SurfaceView implements SurfaceHolder.Callback {


    private SurfaceHolder mSurfaceHolder;
    Camera1Controller cameraController;

    public PreviewSurfaceView(Context context, Camera1Controller cameraController) {
        super(context);
        mSurfaceHolder = getHolder();
        this.cameraController = cameraController;
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
        mSurfaceHolder.addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        cameraController.setPreviewDisplay(mSurfaceHolder);
        cameraController.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraController.stopPreview();
        cameraController.releaseCamera();
    }
}
