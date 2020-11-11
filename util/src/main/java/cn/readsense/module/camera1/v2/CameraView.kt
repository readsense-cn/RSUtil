package cn.readsense.module.camera1.v2

import android.content.Context
import android.hardware.Camera
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import cn.readsense.module.R

class CameraView(context: Context, attributes: AttributeSet?) :
    GLSurfaceView(context, attributes) {
    val cameraRenderer: CameraSurfaceRenderer

    constructor(context: Context) : this(context, null)

    init {
        setEGLContextClientVersion(3)
        cameraRenderer = CameraSurfaceRenderer(this)
        setRenderer(cameraRenderer)
        this.renderMode = RENDERMODE_WHEN_DIRTY

        val typeArray = context.obtainStyledAttributes(attributes, R.styleable.CameraPreView)
        val facing =
            typeArray.getInt(R.styleable.CameraPreView_facing, Camera.CameraInfo.CAMERA_FACING_BACK)
        val w = typeArray.getInt(R.styleable.CameraPreView_w, 640)
        val h = typeArray.getInt(R.styleable.CameraPreView_h, 480)
        val flipXY = typeArray.getBoolean(R.styleable.CameraPreView_flipXY, false)
        val previewMode = typeArray.getInt(R.styleable.CameraPreView_previewMode, 0)
        typeArray.recycle()
        val params = CameraParams.Builder()
            .setFacing(facing)
            .setPreviewSize(w, h)
            .setFlipXY(flipXY)
            .setPreviewMode(previewMode)
            .build()
        cameraRenderer.params = params
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopCamera()
    }

    fun stopCamera() {
        cameraRenderer.stopCamera()
    }

    fun openCamera() {
        cameraRenderer.openCamera()
    }
}
