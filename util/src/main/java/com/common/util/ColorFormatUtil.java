package com.common.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author liyaotang
 * @date 2018/8/27
 *
 * 颜色空间操作工具
 *
 */
public class ColorFormatUtil {

    /**
     * yuv转bitmap
     * @param nv21 yuv数据源
     * @param width 图片宽度
     * @param height 图片高度
     * @return
     */
    public static Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
        Bitmap bitmap = null;
        try {
            YuvImage image = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 100, stream);
            bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static byte[] YU12ToNV12(byte[] YU12Buf, int width, int height) {
        if (YU12Buf.length != (width * height * 3 / 2)) {
            Log.e("YU12ToNV12", "YU12ToNV12: wrong size");
            return null;
        }

        byte[] NV12Buf = new byte[YU12Buf.length];

        // 复制Y域
        int ySize = width * height;
        System.arraycopy(YU12Buf, 0, NV12Buf, 0, ySize);

        // 复制U/V域，U域和V域的大小都是width * height / 4
        int uvSize = width * height / 4;
        for (int i = 0; i < uvSize; i ++) {
            // U
            NV12Buf[ySize + i * 2] = YU12Buf[ySize + i];
            // V
            NV12Buf[ySize + i * 2 + 1] = YU12Buf[ySize + uvSize + i];
        }

        return NV12Buf;
    }

    public static byte[] YU12ToNV21(byte[] YU12Buf, int width, int height) {
        if (YU12Buf.length != (width * height * 3 / 2)) {
            Log.e("YU12ToNV21", "YU12ToNV21: wrong size");
            return null;
        }

        byte[] NV21Buf = new byte[YU12Buf.length];

        // 复制Y域
        int ySize = width * height;
        System.arraycopy(YU12Buf, 0, NV21Buf, 0, ySize);

        // 复制U/V域，U域和V域的大小都是width * height / 4
        int uvSize = width * height / 4;
        for (int i = 0; i < uvSize; i ++) {
            // V
            NV21Buf[ySize + i * 2] = YU12Buf[ySize + uvSize + i];
            // U
            NV21Buf[ySize + i * 2 + 1] = YU12Buf[ySize + i];
        }

        return NV21Buf;
    }

    /**
     *
     * @param data
     * @param dst
     * @param width
     * @param height
     * @param dstWidth
     * @param dstHeight
     * @return
     */
    int cropYuv(byte[] data, byte[] dst, int width, int height,
                int dstWidth, int dstHeight) {

        int i, j;
        int h_div = 0, w_div = 0;
        w_div = (width - dstWidth) / 2;
        if (w_div % 2 != 0)
            w_div--;
        h_div= (height - dstHeight) / 2;
        if (h_div % 2 != 0)
            h_div--;
        //u_div = (height - dstHeight) / 4;
        int src_y_length = width *height;
        int dst_y_length = dstWidth * dstHeight;
        for (i = 0; i < dstHeight; i++)
            for (j = 0; j < dstWidth; j++) {
                dst[i * dstWidth + j] = data[(i + h_div) * width + j + w_div];
            }
        int index = dst_y_length;
        int src_begin = src_y_length + h_div * width / 4;
        int src_u_length = src_y_length / 4;
        int dst_u_length = dst_y_length / 4;
        for (i = 0; i < dstHeight / 2; i++)
            for (j = 0; j < dstWidth / 2; j++) {
                int p = src_begin + i *(width >> 1) + (w_div >> 1) + j;
                dst[index]= data[p];
                dst[dst_u_length+ index++] = data[p + src_u_length];
            }

        return 0;
    }


    /**
     * 从 NV21/NV12 的帧数据，截取目标区域的数据
     * 对于 dstRect 会进行取偶的操作，所以 dstRect 有可能发生改变
     *
     * @param src 源数据
     * @param srcWidth 源数据宽
     * @param srcHeight 源数据高
     * @param dstRect 目标区域，{x0, y0, width, height}
     * @return 目标区域数据，数据异常返回null
     */
    public static byte[] cropNV21(byte[] src, int srcWidth, int srcHeight, int[] dstRect) {
        if (src == null) return null;
        if (src.length != (srcHeight * srcWidth * 3 / 2)) return null;

        // dst 的左上坐标(x, y)，长宽w/h
        int x = dstRect[0];
        int y = dstRect[1];
        int w = dstRect[2];
        int h = dstRect[3];
        // 取偶
        dstRect[0] = x = x / 2 * 2;
        dstRect[1] = y = y / 2 * 2;
        dstRect[2] = w = w / 2 * 2;
        dstRect[3] = h = h / 2 * 2;

        if (x < 0 || y < 0 || w <= 0 || h <= 0) return null;
        if ((x + w) > srcWidth) return null;
        if ((y + h) > srcHeight) return null;

        // src y 数据长度
        int src_y_unit = srcWidth * srcHeight;
        // src y 数据长度
        int dst_y_unit = w * h;
        byte[] dst = new byte[dst_y_unit * 3 / 2];

        // 遍历dst，找到合适的数据填充进去
        for (int i = 0 ; i < h ; i++) {
            // copy y
            System.arraycopy(
                    src, srcWidth * (y + i) + x,
                    dst, w * i,
                    w
            );
            // copy uv
            if (i % 2 == 0) {
                System.arraycopy(
                        src, src_y_unit + srcWidth * ((y + i) / 2) + x,
                        dst, dst_y_unit + w * (i / 2),
                        w
                );
            }
        }

        return dst;
    }

    /** 对YU12 帧数据进行镜像处理（左右翻转） */
    public static byte[] YU12Mirror(byte[] data, int width, int height) {
        byte tempData;
        for (int i = 0; i < height * 3 / 2; i++) {
            for (int j = 0; j < width / 2; j++) {
                tempData = data[i * width + j];
                data[i * width + j] = data[(i + 1) * width - 1 - j];
                data[(i + 1) * width - 1 - j] = tempData;
            }

        }
        return data;
    }

}
