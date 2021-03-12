package cn.idu.facecommon

import android.graphics.Rect
import android.graphics.YuvImage
import cn.readsense.module.util.DLog
import readsense.api.enity.YMFace
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.abs

fun List<YMFace>.findMaxFace(): YMFace {

    var maxIndex = 0
    var maxRectLength = 0f
    for (i in this.indices) {
        if (this[i].rect[2] >= maxRectLength) {
            maxIndex = i
            maxRectLength = this[i].rect[2]
        }
    }
    return this[maxIndex]
}


fun YMFace.faceIsPositive(): Boolean {
    return abs(headpose[0]) <= 30 &&
            (headpose[1] >= -35 && headpose[1] <= 20) &&
            abs(headpose[2]) <= 30
}

/**
 * widthIsWdith: 宽高是否翻转
 */
fun YMFace.faceInScreenCenter(width: Int, height: Int, widthIsWdith: Boolean): Boolean {
    val limit = 10
    if (widthIsWdith)
        return !(rect[0] <= limit || rect[1] <= limit || (rect[0] + rect[2] >= width - limit) || (rect[1] + rect[3] >= height - limit))
    return !(rect[0] <= limit || rect[1] <= limit || (rect[0] + rect[2] >= height - limit) || (rect[1] + rect[3] >= width - limit))
}

fun YMFace.faceIsClear(): Boolean {
    //TODO 光线判定-过亮过暗拒绝 0表示正常，1表示过暗，2表示过亮，3表示阴阳脸
    if (brightness != 0f) {
        DLog.d("光线不符合要求: $brightness")
        return false
    }
    //TODO 运动模糊判定
    if (blur >= 0.25) {
        DLog.d("人脸过于模糊：$blur")
        return false
    }
    return true
}

fun ByteArray.saveFromPreview(save_path: String?, iw: Int, ih: Int) {
    saveFromPreview(17, save_path, iw, ih)
}

fun ByteArray.saveFromPreview(imageFormat: Int, save_path: String?, iw: Int, ih: Int) {
    var outStream: FileOutputStream? = null
    try {
        val yuvimage = YuvImage(this, imageFormat, iw, ih, null as IntArray?)
        val baos = ByteArrayOutputStream()
        yuvimage.compressToJpeg(Rect(0, 0, iw, ih), 100, baos)
        outStream = FileOutputStream(save_path)
        outStream.write(baos.toByteArray())
        outStream.close()
    } catch (var16: IOException) {
        var16.printStackTrace()
    } finally {
        if (outStream != null) {
            try {
                outStream.close()
                outStream = null
            } catch (var15: IOException) {
                var15.printStackTrace()
            }
        }
    }
}