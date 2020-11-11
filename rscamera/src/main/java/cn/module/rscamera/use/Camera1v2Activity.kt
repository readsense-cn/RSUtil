package cn.module.rscamera.use

import android.Manifest
import android.hardware.Camera
import android.view.WindowManager
import butterknife.BindView
import cn.readsense.module.base.BaseCoreActivity
import cn.readsense.module.camera1.v2.CameraView

class Camera1v2Activity : BaseCoreActivity() {

    @BindView(R.id.cameraview)
    var cameraView: CameraView? = null
    override fun getLayoutId(): Int {
        requestPermissions(Manifest.permission.CAMERA)
        return R.layout.activity_camera1v2
    }

    override fun initView() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        cameraView?.cameraRenderer?.params?.previewCallback =
            Camera.PreviewCallback { data: ByteArray, camera: Camera ->
                camera.addCallbackBuffer(data)
            }
    }
}