package cn.readsense.rscamera.use

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import cn.readsense.rscamera.use.util.checkSelfPermissionCompat
import cn.readsense.rscamera.use.util.requestPermissionsCompat
import com.example.android.basicpermissions.util.showToast
import kotlinx.android.synthetic.main.activity_main.*

const val PERMISSION_REQUEST_CAMERA = 0

private const val CAMERA_ID = 1

class MainActivity : AppCompatActivity() {

    private var PREVIEWWIDTH = 1280
    private var PREVIEWHEIGHT = 720

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        print("requestCode: " + requestCode)

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


    private fun openCamera() {
        if (checkSelfPermissionCompat(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showPreview()
        } else {
            requestPermissionsCompat(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }
    }

    fun showPreview() {
        cameraview.showCameraView(PREVIEWWIDTH, PREVIEWHEIGHT, CAMERA_ID)
        layout.showToast(
            String.format(
                "camera try open %d, preview size: %d*%d",
                CAMERA_ID,
                PREVIEWWIDTH,
                PREVIEWHEIGHT
            )
        )
        cameraview.layoutParams.width = PREVIEWWIDTH * 2 / 3
        cameraview.layoutParams.height = PREVIEWHEIGHT * 2 / 3
        cameraview.requestLayout()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        println(item.title)
        val split = item.title.split("*")
        val target_width = split[0].toInt()
        val target_height = split[1].toInt()
        if (target_width != PREVIEWWIDTH && target_height != PREVIEWHEIGHT) {
            PREVIEWWIDTH = target_width
            PREVIEWHEIGHT = target_height


            releaseCamera()
            openCamera()
        }
        return super.onOptionsItemSelected(item)
    }

}
