package cn.readsense.rscamera.use

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import cn.readsense.rscamera.camera.CameraView
import cn.readsense.rscamera.camera.CameraView.PreviewFrameCallback
import cn.readsense.rscamera.camera.PreviewTextureView
import cn.readsense.rscamera.use.util.checkSelfPermissionCompat
import cn.readsense.rscamera.use.util.requestPermissionsCompat
import com.example.android.basicpermissions.util.showToast
import kotlinx.android.synthetic.main.activity_main.*

const val PERMISSION_REQUEST_CAMERA = 0

private const val CAMERA_ID_BACK = 1
private const val CAMERA_ID_FRONT = 0

class MainActivity : AppCompatActivity() {

    private var PREVIEWWIDTH = 1280
    private var PREVIEWHEIGHT = 720
    private var CAMERA_ID = CAMERA_ID_BACK

    private val rates = floatArrayOf(0f, 3 / 2f, 1f, 2 / 3f)
    private val rates_str = arrayOf("全屏", "3:2", "1:1", "2:3")

    private var rate_positon = 0
    private var flip = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        my_toolbar.title = "ICamera"
        my_toolbar.inflateMenu(R.menu.toolbar)
        setSupportActionBar(my_toolbar)
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

        cameraview.addPreviewFrameCallback(object : PreviewFrameCallback {

            override fun analyseData(data: ByteArray?): Any {
                return 0
            }

            override fun analyseDataEnd(t: Any?) {

            }

        })

        cameraview.showCameraView(
            PREVIEWWIDTH,
            PREVIEWHEIGHT,
            CAMERA_ID, CameraView.PREVIEWMODE_TEXTUREVIEW
        )

        var rate_info = "不支持修改预览比例，否则会拉伸图像"

        if (cameraview.showView is PreviewTextureView) {
            rate_info = rates_str[rate_positon]
            if (rate_positon != 0) {
                var minLength = Math.min(PREVIEWWIDTH * 2 / 3, PREVIEWHEIGHT * 2 / 3)

                cameraview.layoutParams.width = (minLength * rates[rate_positon]).toInt()
                cameraview.layoutParams.height = minLength
                cameraview.requestLayout()
            } else {
                cameraview.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
                cameraview.layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT
                cameraview.requestLayout()
            }

            (cameraview.showView as PreviewTextureView).setConfigureTransform(
                PREVIEWWIDTH,
                PREVIEWHEIGHT, flip
            )

        } else {
            cameraview.layoutParams.width = PREVIEWWIDTH * 2 / 3
            cameraview.layoutParams.height = PREVIEWHEIGHT * 2 / 3
            cameraview.requestLayout()
        }


        layout.showToast(
            String.format(
                "try open %d, size: %d*%d, %s",
                CAMERA_ID,
                PREVIEWWIDTH,
                PREVIEWHEIGHT, rate_info
            )
        )


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_settings1,
            R.id.action_settings2,
            R.id.action_settings3
            -> {
                val split = item.title.split("*")
                val target_width = split[0].toInt()
                val target_height = split[1].toInt()
                if (target_width != PREVIEWWIDTH && target_height != PREVIEWHEIGHT) {
                    PREVIEWWIDTH = target_width
                    PREVIEWHEIGHT = target_height
                    releaseCamera()
                    openCamera()
                }
            }

            R.id.action_changecamera -> {
                CAMERA_ID = if (CAMERA_ID == CAMERA_ID_BACK) CAMERA_ID_FRONT else CAMERA_ID_BACK
                releaseCamera()
                openCamera()
            }

            R.id.action_changerate -> {

                rate_positon++
                if (rate_positon > rates.size - 1) rate_positon = 0
                releaseCamera()
                openCamera()
            }

            R.id.action_filp_x -> {
                flip = !flip
                releaseCamera()
                openCamera()
            }
        }
        return super.onOptionsItemSelected(item)

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                layout.showToast(R.string.camera_permission_granted)
                showPreview()
            } else {
                // Permission request was denied.
                layout.showToast(R.string.camera_permission_denied)
            }
        }
    }
}
