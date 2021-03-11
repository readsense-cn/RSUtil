package cn.idu.facecommon

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import cn.readsense.module.camera1.CameraView
import cn.readsense.module.util.DLog
import readsense.api.enity.YMFace


fun CameraView.clear() {
    val canvas: Canvas = this.drawView.holder.lockCanvas() ?: return
    canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR)
    this.drawView.holder.unlockCanvasAndPost(canvas)
}

fun CameraView.drawFace(face: YMFace) {
    drawFace(face, false)
}

fun CameraView.drawFace(face: YMFace, flipX: Boolean) {
    drawFace(face.rect, flipX)
}

fun CameraView.drawFace(rect: FloatArray, flipX: Boolean) {
    val canvas: Canvas = this.drawView.holder.lockCanvas() ?: return
    canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR)
    val rectTemp = floatArrayOf(rect[0], rect[1], rect[2], rect[3])
    rectTemp[0] = getDrawPositionX(rectTemp[0], rectTemp[2], flipX)
    rectTemp[1] = getDrawPositionY(rectTemp[1], rectTemp[3], false)
    rectTemp[2] = rectTemp[0] + rectTemp[2] * scale
    rectTemp[3] = rectTemp[1] + rectTemp[3] * scale
    canvas.drawRect(rectTemp[0], rectTemp[1], rectTemp[2], rectTemp[3], paint)
    this.drawView.holder.unlockCanvasAndPost(canvas)
}

