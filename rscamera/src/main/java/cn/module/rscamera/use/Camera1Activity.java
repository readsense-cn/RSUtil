package cn.module.rscamera.use;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import cn.readsense.module.base.BaseCoreActivity;
import cn.readsense.module.camera1.CameraView;

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
        cameraView.getCameraParams().getPreviewSize().setPreviewWidth(640);
        cameraView.getCameraParams().getPreviewSize().setPreviewHeight(480);
        addLifecycleObserver(cameraView);

    }

    @OnClick(R.id.record)
    void luzhi(Button v) {

        if (v.getText().equals("录制")) {
            v.setText("结束");
            cameraView.startRecord("/sdcard/" + System.currentTimeMillis() + ".mp4");
        } else {
            v.setText("录制");
            cameraView.stopRecord();

        }
    }
}