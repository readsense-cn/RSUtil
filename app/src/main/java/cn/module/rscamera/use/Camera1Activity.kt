package cn.module.rscamera.use

import android.Manifest
import android.hardware.Camera
import cn.readsense.module.base.BaseCoreActivity
import cn.readsense.module.camera1.CameraView
import cn.readsense.module.camera1.CameraView.PreviewFrameCallback
import java.io.FileOutputStream

class Camera1Activity : BaseCoreActivity() {

    override fun getLayoutId(): Int {
        requestPermissions(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return R.layout.activity_camera1
    }

    override fun initView() {

        val cameraView1 = findViewById<CameraView>(R.id.cameraview1)
        openCamera(cameraView1, Camera.CameraInfo.CAMERA_FACING_BACK, 0F, 0F,180)

        val cameraView2 = findViewById<CameraView>(R.id.cameraview2)
        openCamera(cameraView2, Camera.CameraInfo.CAMERA_FACING_FRONT, 640F, 0F)

    }

    private fun openCamera(cameraView: CameraView, facing: Int, x: Float, y: Float, oritationDisplay:Int=0) {

        cameraView.cameraParams.facing = facing
        cameraView.cameraParams.isFilp = false
        cameraView.cameraParams.isScaleWidth = true
        cameraView.cameraParams.oritationDisplay = oritationDisplay
        cameraView.cameraParams.previewSize.previewWidth = 640
        cameraView.cameraParams.previewSize.previewHeight = 480
        cameraView.layoutParams.width = 640
        cameraView.layoutParams.height = 480


        cameraView.x = x
        cameraView.y = y
        cameraView.addPreviewFrameCallback(object : PreviewFrameCallback {
            override fun analyseData(data: ByteArray): Any {
                return 0
            }

            override fun analyseDataEnd(t: Any) {}
        })
        addLifecycleObserver(cameraView)

    }


}