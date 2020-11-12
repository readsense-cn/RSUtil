package cn.readsense.module.camera1.v2

import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import cn.readsense.module.gleshelper.BufferUtils
import cn.readsense.module.gleshelper.ShaderUtil
import cn.readsense.module.gleshelper.TextureUtils
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraSurfaceRenderer constructor(private val glSurfaceView: GLSurfaceView) :
    GLSurfaceView.Renderer {


    private var uTextureSamplerLocation: Int = 0

    private val context = glSurfaceView.context
    private var program: Int = -1
    private var textureId: Int = -1

    private val vertexBuffer: FloatBuffer
    private var textureVertexBuffers: MutableList<FloatBuffer>
    private val vertexIndexBuffer: ShortBuffer

    //顶点坐标
    private val position_vertex = floatArrayOf(
        -1f, 1f,
        -1f, -1f,
        1f, -1f,
        1f, 1f
    )

    private val VERTEX_INDEX = shortArrayOf(
        0, 1, 2,
        0, 2, 3
    )

    private var camera: Camera? = null
    private var surfaceTexture: SurfaceTexture? = null
    private var viewW: Int = 0
    private var viewH: Int = 0

    init {
        vertexBuffer = BufferUtils.newFloatBuffer(position_vertex)
        textureVertexBuffers = mutableListOf()
        for (texVertex in TextureUtils.tex_vertexs) {
            textureVertexBuffers.add(BufferUtils.newFloatBuffer(texVertex))
        }

        vertexIndexBuffer = BufferUtils.newShortBuffer(VERTEX_INDEX)
    }

    lateinit var params: CameraParams

    fun openCamera() {
        if (null == surfaceTexture) return
        stopCamera()
        camera = Camera.open(params.facing)
        val parameters = camera!!.parameters
        parameters!!.setPreviewSize(params.w, params.h)
        camera?.parameters = parameters

        camera?.setPreviewTexture(surfaceTexture)
        camera?.addCallbackBuffer(
            ByteArray(
                params.w * params.h * ImageFormat.getBitsPerPixel(
                    ImageFormat.NV21
                ) / 8
            )
        )
        camera?.setPreviewCallbackWithBuffer { data: ByteArray, camera: Camera ->
            camera.addCallbackBuffer(data)
            params.previewCallback?.onPreviewFrame(data, camera)
        }

        camera?.startPreview()
        automaticFocusing()
    }

    fun stopCamera() {
        camera?.stopPreview()
        camera?.setPreviewCallbackWithBuffer(null)
        camera?.release()
        camera = null
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f)
        program = ShaderUtil.createProgram(
            ShaderUtil.loadFromAssetsFile("camera/v.glsl", context.resources),
            ShaderUtil.loadFromAssetsFile("camera/f.glsl", context.resources)
        )
        //获取Shader中定义的变量在program中的位置
        uTextureSamplerLocation = GLES30.glGetUniformLocation(program, "yuvTexSampler")
        uMatrixLocation = GLES30.glGetUniformLocation(program, "u_Matrix")

        textureId = TextureUtils.loacCameraTexture()
        surfaceTexture = SurfaceTexture(textureId)
        surfaceTexture?.setOnFrameAvailableListener {
            glSurfaceView.requestRender()
        }
        openCamera()
    }

    private val mMatrix = FloatArray(16)
    private var uMatrixLocation: Int = 0

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        viewW = width
        viewH = height
        automaticFocusing()
    }


    //根据view大小调整渲染矩阵坐标，避免拉伸图像
    private fun automaticFocusing() {
        if (viewW == 0 || viewH == 0 || params.w == 0 || params.h == 0) return

        //判定图像宽高是否对应iw和ih
        var imgW = params.w
        var imgH = params.h

        if (params.flipXY) {
            imgW = params.h
            imgH = params.w
        }

        val apex_view = viewW.toFloat() / viewH.toFloat()
        val apex_i = imgW.toFloat() / imgH.toFloat()
        var aspectRatio = 1f
        if (apex_view >= apex_i) {//图像宽度拉伸viewW.toFloat() / imgW.toFloat()倍至viewW，对应的高度截取中间部分用于显示
            aspectRatio = viewH.toFloat() / (imgH.toFloat() * (viewW.toFloat() / imgW.toFloat()))
            Matrix.orthoM(mMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        } else {//图像高度拉伸viewH.toFloat() / imgH.toFloat()倍至viewH，对应的高度截取中间部分用于显示
            aspectRatio = viewW.toFloat() / (imgW.toFloat() * (viewH.toFloat() / imgH.toFloat()))
            Matrix.orthoM(mMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        }
        /**
         *两种方法来避免拉伸情况
         * 1. 利用正交投影，将多出的部分图像转移到可视范围之外，计算较简单，重复渲染
         * 2. 裁剪纹理坐标，通过计算仅显示视野范围内的纹理，计算较复杂，渲染优化
         */

    }

    override fun onDrawFrame(gl: GL10?) {
        surfaceTexture?.updateTexImage()

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glUseProgram(program)

        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        //将此纹理单元床位片段着色器的uTextureSampler外部纹理采样器
        GLES30.glUniform1i(uTextureSamplerLocation, 0)

        GLES30.glEnableVertexAttribArray(0)//输入顶点坐标
        GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, 0, vertexBuffer)

        GLES30.glEnableVertexAttribArray(1)//输入纹理坐标
        GLES30.glVertexAttribPointer(
            1,
            2,
            GLES30.GL_FLOAT,
            false,
            0,
            textureVertexBuffers[params.previewMode]
        )

        // 绘制
        GLES30.glDrawElements(
            GLES30.GL_TRIANGLES,
            VERTEX_INDEX.size,
            GLES30.GL_UNSIGNED_SHORT,
            vertexIndexBuffer
        )
        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)


    }
}