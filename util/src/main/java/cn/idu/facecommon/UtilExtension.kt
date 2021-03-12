package cn.idu.facecommon

import java.io.File

fun File.isEndOfImg(): Boolean {
    return name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".png") || name.endsWith(".jpeg")
}