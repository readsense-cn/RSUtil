package cn.module.rscamera.use

import android.Manifest
import android.hardware.Camera
import cn.readsense.module.base.BaseCoreActivity
import cn.readsense.module.camera1.CameraView
import cn.readsense.module.camera1.CameraView.PreviewFrameCallback
import java.io.FileOutputStream

class Camera1Activity : BaseCoreActivity() {
    private lateinit var cameraView: CameraView

    override fun getLayoutId(): Int {
        requestPermissions(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return R.layout.activity_camera1
    }

    override fun initView() {

        val fos = FileOutputStream("$filesDir/test.yuv")
        cameraView = findViewById(R.id.cameraview)
        cameraView.cameraParams.facing = Camera.CameraInfo.CAMERA_FACING_BACK
        cameraView.cameraParams.isFilp = false
        cameraView.cameraParams.isScaleWidth = true
        cameraView.cameraParams.previewSize.previewWidth = 1920
        cameraView.cameraParams.previewSize.previewHeight = 1080
        cameraView.addPreviewFrameCallback(object : PreviewFrameCallback {
            override fun analyseData(data: ByteArray): Any {
                fos.write(data)
                return 0
            }

            override fun analyseDataEnd(t: Any) {}
        })
        addLifecycleObserver(cameraView)

        cameraView.setOnClickListener {
            cameraView.releaseCamera()
            fos.flush()
            fos.close()
            finish()
        }
    }


}