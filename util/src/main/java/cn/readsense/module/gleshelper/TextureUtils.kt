package cn.readsense.module.gleshelper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLUtils
import android.util.Log
import java.nio.ByteBuffer

object TextureUtils {

    //    companion object {
    private val TAG = "TextureUtils"
    private val tex_vertex = floatArrayOf(//原纹理
        0f, 0f,
        0f, 1f,
        1f, 1f,
        1f, 0f
    )

    public val tex_vertexs = Array(8) { FloatArray(8) }

    init {
        for ((index, value) in tex_vertex.withIndex()) {
            tex_vertexs[0][index] = value   //原纹理
            tex_vertexs[1][index] = if (index % 2 == 0) 1 - value else value //原纹理+左右翻转

            tex_vertexs[2][index] =//原纹理顺时针旋转90度
                if (index + 2 < tex_vertex.size) tex_vertex[index + 2] else tex_vertex[index + 2 - tex_vertex.size]
            tex_vertexs[3][index] =//原纹理顺时针旋转90度+左右翻转
                if (index % 2 == 0) 1 - tex_vertexs[2][index] else tex_vertexs[2][index]

            tex_vertexs[4][index] =//180
                if (index + 4 < tex_vertex.size) tex_vertex[index + 4] else tex_vertex[index + 4 - tex_vertex.size]
            tex_vertexs[5][index] =
                if (index % 2 == 0) 1 - tex_vertexs[4][index] else tex_vertexs[4][index]

            tex_vertexs[6][index] =//270
                if (index + 6 < tex_vertex.size) tex_vertex[index + 6] else tex_vertex[index + 6 - tex_vertex.size]
            tex_vertexs[7][index] =
                if (index % 2 == 0) 1 - tex_vertexs[6][index] else tex_vertexs[6][index]
        }

    }

    fun loadTexture(context: Context, resId: Int): Int {
        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) {
            Log.e(TAG, "Could not generate a new OpenGL textureId object.")
            return 0;
        }
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)
        if (bitmap == null) {
            Log.e(TAG, "Resource ID $resId could not be decoded.");
            GLES30.glDeleteTextures(1, textureIds, 0);
            return 0;
        }
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])
        //设置默认的纹理过滤参数
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_LINEAR
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_NEAREST
        )

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)

        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
        bitmap.recycle()
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        return textureIds[0]
    }

    fun loadTexturePixs(context: Context, resId: Int): Int {
        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) {
            Log.e(TAG, "Could not generate a new OpenGL textureId object.")
            return 0;
        }
        val options = BitmapFactory.Options()
        options.inScaled = false
        options.inPreferredConfig = Bitmap.Config.RGB_565
        options.inSampleSize = 1
        val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)
        if (bitmap == null) {
            Log.e(TAG, "Resource ID $resId could not be decoded.");
            GLES30.glDeleteTextures(1, textureIds, 0);
            return 0;
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])
        //设置默认的纹理过滤参数
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_LINEAR
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_NEAREST
        )

        val buf = ByteBuffer.allocate(bitmap.byteCount)
        bitmap.copyPixelsToBuffer(buf)
        buf.flip()
//            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D,
            0,
            GLES30.GL_RGB,
            bitmap.width,
            bitmap.height,
            0,
            GLES30.GL_RGB,
            GLES30.GL_UNSIGNED_SHORT_5_6_5,
            buf
        )

        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
        bitmap.recycle()
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        return textureIds[0]
    }

    fun loacCameraTexture(): Int {
        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)
        //绑定到外部纹理上
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIds[0]);
        //设置纹理过滤参数
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST
        )
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_LINEAR
        )
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_WRAP_S,
            GLES30.GL_CLAMP_TO_EDGE
        )
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_WRAP_T,
            GLES30.GL_CLAMP_TO_EDGE
        )
        //解除纹理绑定
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return textureIds[0]
    }
//    }
}