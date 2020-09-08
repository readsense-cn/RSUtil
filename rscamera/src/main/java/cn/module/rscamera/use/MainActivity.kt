package cn.module.rscamera.use

import android.Manifest
import android.util.Size
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import cn.readsense.module.base.BaseCoreActivity

class MainActivity : BaseCoreActivity() {

    override fun getLayoutId(): Int {
        requestPermissions(Manifest.permission.CAMERA)
        return R.layout.activity_main
    }

    override fun initView() {


        val viewFinder: PreviewView = findViewById(R.id.previewView)

        val cameraProviderFeature = ProcessCameraProvider.getInstance(context)

        cameraProviderFeature.addListener(Runnable {

            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            val preview = Preview.Builder().apply {
                setTargetResolution(Size(640, 480))
                setTargetRotation(Surface.ROTATION_180)
            }.build().apply {
                setSurfaceProvider(viewFinder.createSurfaceProvider())
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .apply {

                }.build().apply {
                    setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        LuminosityAnalyzer()
                    )
                }

            cameraProvider.unbindAll()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

        }, ContextCompat.getMainExecutor(context))


    }
}
