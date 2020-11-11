package cn.module.rscamera.use;

import android.Manifest;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import cn.readsense.module.base.BaseCoreActivity;
import cn.readsense.module.camera1.CameraView;
import cn.readsense.module.util.BitmapUtil;

public class Camera1Activity extends BaseCoreActivity {


    @BindView(R.id.cameraview)
    CameraView cameraView;

    @Override
    protected int getLayoutId() {
        requestPermissions(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO);
        return R.layout.activity_camera1;
    }

    @Override
    protected void initView() {

        cameraView.getCameraParams().setFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
        cameraView.getCameraParams().setFilp(false);
        cameraView.getCameraParams().setScaleWidth(true);
        cameraView.getCameraParams().getPreviewSize().setPreviewWidth(1920);
        cameraView.getCameraParams().getPreviewSize().setPreviewHeight(1080);
        cameraView.addPreviewFrameCallback(new CameraView.PreviewFrameCallback() {
            @Override
            public Object analyseData(byte[] data) {
                return null;
            }

            @Override
            public void analyseDataEnd(Object t) {

            }
        });
//        cameraView.showCameraView();
        addLifecycleObserver(cameraView);

    }

}