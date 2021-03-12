package cn.idu.facecommon

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import cn.readsense.module.util.BitmapUtil
import cn.readsense.module.util.DLog
import readsense.api.core.*
import readsense.api.enity.YMFace
import readsense.jni.RSFaceJni
import java.io.File
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis


class RSScope private constructor(private val context: Context) {

    companion object : SingletonHolder<RSScope, Context>(::RSScope)

    private val rsLicense: RSLicense = RSLicense.getInstance(context, "", "").apply {
        DLog.d(RSFaceJni.getSDKVersion())
        init()
        DLog.d("init ret: $handle")
    }

    private val initNpu = true
    private val rsTrack: RSTrack = RSTrack(rsLicense).apply {
        if (!initNpu)
            init()
        else {
            initNpu(640, 480)
            DLog.d("init track handle: $handle")
        }
    }

    fun runTrack(data: ByteArray, previewWidth: Int, previewHeight: Int, i: Int): List<YMFace> {
        return if (!initNpu)
            rsTrack.runTrack(data, previewWidth, previewHeight, i)
        else
            rsTrack.runTrackNpu(data, previewWidth, previewHeight, i)
    }

    private val rsDetect: RSDetect = RSDetect(rsLicense).apply { init() }

    fun runDetect(bitmap: Bitmap): List<YMFace> {
        return rsDetect.runDetect(bitmap, 0)
    }

    private val rsHelmet: RSHelmet = RSHelmet(rsLicense).apply {
        init()
    }

    fun getHelmet(
            data: ByteArray,
            previewWidth: Int,
            previewHeight: Int,
            i: Int,
            landmark: FloatArray
    ): Int {
        return rsHelmet.getHelmet(data, previewWidth, previewHeight, i, landmark)
    }

    fun getHelmet(bitmap: Bitmap, rect: FloatArray): Int {
        return rsHelmet.getHelmetBitmap(bitmap, 0, rect)
    }

    private val rsLivenessDetect = RSLivenessDetect(rsLicense).apply {
        init()
        initInfrared()
    }

    fun livenessDetect(
            data: ByteArray,
            previewWidth: Int,
            previewHeight: Int,
            i: Int,
            rect: FloatArray
    ): Boolean {
        var ret: Int
        val cost = measureTimeMillis {
            ret = rsLivenessDetect.runLivenessDetect(data, previewWidth, previewHeight, i, rect)
        }
        DLog.d("runLivenessDetect cost: $cost ret: $ret t:${Thread.currentThread().name} ${Thread.currentThread().id}")
        return ret == 1
    }

    fun livenessDetectInfrared(
            data: ByteArray,
            previewWidth: Int,
            previewHeight: Int,
            i: Int,
            rect: FloatArray
    ): Boolean {
        var ret: Int
        val cost = measureTimeMillis {
            ret = rsLivenessDetect.runLivenessDetectInfrared(
                    data,
                    previewWidth,
                    previewHeight,
                    i,
                    rect
            )
        }
        DLog.d("runLivenessDetectInfrared cost: $cost ret: $ret t:${Thread.currentThread().name} ${Thread.currentThread().id}")
        return ret == 1
    }

    private val rsDeepFace: RSDeepFace = RSDeepFace(rsLicense).apply { init() }

    fun getDeepFaceFeature(
            data: ByteArray,
            previewWidth: Int,
            previewHeight: Int,
            i: Int,
            rect: FloatArray
    ): FloatArray? {
        val deepFaceFeature: FloatArray?
        val cost = measureTimeMillis {
            deepFaceFeature =
                    rsDeepFace.getDeepFaceFeature(data, previewWidth, previewHeight, i, rect)
        }
        DLog.d("getDeepFaceFeature cost: $cost")
        return deepFaceFeature
    }

    fun recoge(
            data: ByteArray,
            previewWidth: Int,
            previewHeight: Int,
            i: Int,
            rect: FloatArray
    ): String? {
        getDeepFaceFeature(data, previewWidth, previewHeight, i, rect)?.apply {
            val persons = rsFaceRecognition.findSimilarPerson(this)
            if (persons.isNotEmpty() && persons.size > 0) {
                DLog.d("recoEnd: uuid:${persons[0].uuid} id:${persons[0].person_id} confidence:${persons[0].confidence}")
                if (persons[0].confidence >= 75)
                    return persons[0].uuid
            } else {
                DLog.d("recoEnd: db is null")
            }

        }
        return null
    }

    private val rsDeepFaceBitmap: RSDeepFace = RSDeepFace(rsLicense).apply { init() }

    private val rsFaceRecognition: RSFaceRecognition =
            RSFaceRecognition(rsLicense, context.filesDir.absolutePath).apply {
                init()

                thread(start = true) {
                    Thread.sleep(100)
                    //TODO for test
                    if (File("/sdcard/rego/dou.jpeg").exists()) {
                        resetAlbum()
                        registerBitmap("dou", "/sdcard/rego/dou.jpeg")
                    }
                }

            }

    fun registerBitmap(uuid: String, path: String): Int {
        rsFaceRecognition.getPersonIdByUuid(uuid).apply {
            if (this > 0) {
                rsFaceRecognition.personDelete(this)
                DLog.d("已存在uuid：$uuid ，person：$this 删除!")
            }
        }
        return BitmapUtil.decodeScaleImage(path, 1000, 1000).let { bitmap ->

            rsDetect.runDetect(bitmap, 0)?.apply {
                if (isNotEmpty()) {
                    val face = findMaxFace()
                    //TODO 一系列判定, 判定当前人脸是否复合注册要求
                    val feature = rsDeepFaceBitmap.getDeepFaceFeature(bitmap, 0, face.rect)
                    return rsFaceRecognition.personCreateWithName(feature, uuid)
                }
            }
            -1
        }
    }

    private val rsAlign: RSAlign = RSAlign(rsLicense).apply {
        init()
    }

    fun alignFace(face: YMFace, data: ByteArray,
                  previewWidth: Int,
                  previewHeight: Int,
                  i: Int) {
        val newFace = rsAlign.runFaceAlign(data, previewWidth, previewHeight, i, face.rect)
        face.blur = newFace.blur
        face.brightness = newFace.brightness
    }


    fun clear() {
        rsLicense.unInit()
        if (!initNpu)
            rsTrack.unInit()
        else
            rsTrack.unInitNpu()
        rsDetect.unInit()
        rsHelmet.unInit()
        rsLivenessDetect.unInit()
        rsLivenessDetect.unInitInfrared()
        rsDeepFace.unInit()
        rsDeepFaceBitmap.unInit()
        rsAlign.unInit()
    }
}