package cn.module.rscamera.use;

import android.Manifest;
import android.hardware.Camera;
import android.view.View;

import cn.readsense.module.base.BaseCoreActivity;
import cn.readsense.module.camera1.CameraView;
import cn.readsense.module.util.DLog;

public class Camera1Activity extends BaseCoreActivity {


    CameraView cameraView;

    @Override
    protected int getLayoutId() {
        requestPermissions(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return R.layout.activity_camera1;
    }

    boolean sleep = false;

    @Override
    protected void initView() {
        cameraView = findViewById(R.id.cameraview);
        cameraView.getCameraParams().setFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
        cameraView.getCameraParams().setFilp(false);
        cameraView.getCameraParams().setScaleWidth(true);
        cameraView.getCameraParams().getPreviewSize().setPreviewWidth(640);
        cameraView.getCameraParams().getPreviewSize().setPreviewHeight(480);
        cameraView.addPreviewFrameCallback(new CameraView.PreviewFrameCallback() {
            @Override
            public Object analyseData(byte[] data) {
                if (sleep) {
                    try {
                        DLog.d("sleep 1");
                        Thread.sleep(100);
                        DLog.d("sleep 2");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            public void analyseDataEnd(Object t) {

            }
        });
//        cameraView.showCameraView();
        addLifecycleObserver(cameraView);

        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sleep = !sleep;
            }
        });

    }

}