package cn.module.rscamera.use

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import cn.module.rscamera.use.util.checkSelfPermissionCompat
import cn.module.rscamera.use.util.requestPermissionsCompat
import cn.readsense.module.camera1.CameraView
import cn.readsense.module.camera1.PreviewTextureView
import cn.readsense.module.util.DialogUtil
import com.example.android.basicpermissions.util.showToast
import kotlinx.android.synthetic.main.activity_main.*

const val PERMISSION_REQUEST_CAMERA = 0


class MainActivity : AppCompatActivity() {

    private var PREVIEWWIDTH = 640
    private var PREVIEWHEIGHT = 480

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
    }

    private fun releaseCamera() {
        cameraview.releaseCamera()
    }

    private fun openCamera() {
        if (checkSelfPermissionCompat(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showPreview()
        } else {
            requestPermissionsCompat(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }
    }

    fun showPreview() {
        cameraview.setDrawView()

        cameraview.addPreviewFrameCallback(object : CameraView.PreviewFrameCallback {

            override fun analyseData(data: ByteArray?): Any {
                return 0
            }

            override fun analyseDataEnd(t: Any?) {

            }
        })

        cameraview.cameraParams.facing = Camera.CameraInfo.CAMERA_FACING_BACK
        cameraview.cameraParams.previewSize.previewWidth = PREVIEWWIDTH
        cameraview.cameraParams.previewSize.previewHeight = PREVIEWHEIGHT
        cameraview.cameraParams.oritationDisplay = 0

        cameraview.cameraParams.filp = false
        cameraview.cameraParams.scaleWidth = false

        cameraview.showCameraView()

        cameraview.showToast("长按可弹出配置页");
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                cameraview.showToast(R.string.camera_permission_granted)
                showPreview()
            } else {
                // Permission request was denied.
                cameraview.showToast(R.string.camera_permission_denied)
            }
        }
    }
}
