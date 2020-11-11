package cn.readsense.module.camera1.v2

import android.hardware.Camera

class CameraParams private constructor(
    var facing: Int,
    var w: Int,
    var h: Int, var flipXY: Boolean, var previewMode: Int
) {
    var previewCallback: Camera.PreviewCallback? = null

    class Builder {
        private var facing: Int = -1
        private var w: Int = -1
        private var h: Int = -1
        private var previewMode: Int = 0
        private var flipXY: Boolean = false

        fun setFacing(_facing: Int = Camera.CameraInfo.CAMERA_FACING_BACK): Builder {
            facing = _facing
            return this
        }

        fun setPreviewSize(
            _w: Int = 640,
            _h: Int = 480
        ): Builder {
            w = _w
            h = _h
            return this
        }

        fun setFlipXY(_flipXY: Boolean = false): Builder {
            flipXY = _flipXY
            return this
        }

        fun setPreviewMode(_previewMode: Int = 0): Builder {
            previewMode = _previewMode
            return this
        }


        fun build() = CameraParams(facing, w, h, flipXY, previewMode)

    }


}