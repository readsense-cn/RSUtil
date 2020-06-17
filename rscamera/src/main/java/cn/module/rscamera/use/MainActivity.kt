package cn.module.rscamera.use

import android.Manifest
import cn.readsense.module.base.BaseCoreActivity
import cn.readsense.module.camera1.CameraView
import com.example.android.basicpermissions.util.showToast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseCoreActivity() {

    override fun getLayoutId(): Int {
        requestPermissions(Manifest.permission.CAMERA)
        return R.layout.activity_main;
    }

    override fun initView() {
        cameraview.showToast("长按可弹出配置页")

        cameraview.addPreviewFrameCallback(object : CameraView.PreviewFrameCallback {

            override fun analyseDataEnd(t: Any?) {

            }

            override fun analyseData(data: ByteArray?): Any? {

                return null
            }

        })
        addLifecycleObserver(cameraview)
    }
}
