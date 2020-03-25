package cn.readsense.module.video;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import cn.readsense.module.util.DLog;

public class H264FileRead extends Thread {

    //解码器
    private MMediaCodec mMediaCodec;
    //文件路径
    private String path;
    //文件读取完成标识
    private boolean isFinish = false;
    //这个值用于找到第一个帧头后，继续寻找第二个帧头，如果解码失败可以尝试缩小这个值
    private static final int FRAME_MIN_LEN = 20;
    //一般H264帧大小不超过200k,如果解码失败可以尝试增大这个值
    private static final int FRAME_MAX_LEN = 300 * 1024;


    /**
     * 初始化解码器
     *
     * @param mediaCodec 解码 Util
     * @param path       文件路径
     */
    public H264FileRead(MMediaCodec mediaCodec, String path) {
        this.mMediaCodec = mediaCodec;
        this.path = path;


    }

    /**
     * 寻找指定 buffer 中 h264 头的开始位置
     *
     * @param data   数据
     * @param offset 偏移量
     * @param max    需要检测的最大值
     * @return h264头的开始位置 ,-1表示未发现
     */
    private int findHead(byte[] data, int offset, int max) {
        int i;
        for (i = offset; i <= max; i++) {
            //发现帧头
            if (isHead(data, i))
                break;
        }
        //检测到最大值，未发现帧头
        if (i == max) {
            i = -1;
        }
        return i;
    }

    /**
     * 判断是否是I帧/P帧头:
     * 00 00 00 01 65    (I帧)
     * 00 00 00 01 61 / 41   (P帧)
     * 00 00 00 01 67    (SPS)
     * 00 00 00 01 68    (PPS)
     *
     * @param data   解码数据
     * @param offset 偏移量
     * @return 是否是帧头
     */
    private boolean isHead(byte[] data, int offset) {
        boolean result = false;
        // 00 00 00 01 x
        if (data[offset] == 0x00 && data[offset + 1] == 0x00
                && data[offset + 2] == 0x00 && data[3] == 0x01 && isVideoFrameHeadType(data[offset + 4])) {
            result = true;
        }
        // 00 00 01 x
        if (data[offset] == 0x00 && data[offset + 1] == 0x00
                && data[offset + 2] == 0x01 && isVideoFrameHeadType(data[offset + 3])) {
            result = true;
        }
        return result;
    }

    /**
     * I帧或者P帧
     */
    private boolean isVideoFrameHeadType(byte head) {
        return head == (byte) 0x65 || head == (byte) 0x61 || head == (byte) 0x41
                || head == (byte) 0x67 || head == (byte) 0x68;
    }

    @Override
    public void run() {
        super.run();
        File file = new File(path);
        //判断文件是否存在
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                //保存完整数据帧
                byte[] frame = new byte[FRAME_MAX_LEN];
                //当前帧长度
                int frameLen = 0;
                //每次从文件读取的数据
                byte[] readData = new byte[10 * 1024];
                //循环读取数据
                while (!isFinish) {
                    if (fis.available() > 0) {
                        int readLen = fis.read(readData);
                        //当前长度小于最大值
                        if (frameLen + readLen < FRAME_MAX_LEN) {
                            //将readData拷贝到frame
                            System.arraycopy(readData, 0, frame, frameLen, readLen);
                            //修改frameLen
                            frameLen += readLen;
                            //寻找第一个帧头
                            int headFirstIndex = findHead(frame, 0, frameLen);
                            while (headFirstIndex >= 0 && isHead(frame, headFirstIndex)) {
                                //寻找第二个帧头
                                int headSecondIndex = findHead(frame, headFirstIndex + FRAME_MIN_LEN, frameLen);
                                //如果第二个帧头存在，则两个帧头之间的就是一帧完整的数据
                                if (headSecondIndex > 0 && isHead(frame, headSecondIndex)) {
//                                    DLog.d("TAG", "headSecondIndex:" + headSecondIndex);
                                    //发送给解码器
                                    mMediaCodec.addFrame(Arrays.copyOfRange(frame, headFirstIndex, headSecondIndex));

                                    //截取headSecondIndex之后到frame的有效数据,并放到frame最前面
                                    byte[] temp = Arrays.copyOfRange(frame, headSecondIndex, frameLen);
                                    System.arraycopy(temp, 0, frame, 0, temp.length);
                                    //修改frameLen的值
                                    frameLen = temp.length;
                                    //继续寻找数据帧
                                    headFirstIndex = findHead(frame, 0, frameLen);
                                } else {
                                    //找不到第二个帧头
                                    headFirstIndex = -1;
                                }
                            }
                        } else {
                            //如果长度超过最大值，frameLen置0
                            frameLen = 0;
                        }
                    } else {
                        //文件读取结束
                        isFinish = true;
                        mMediaCodec.finishAddFrame();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DLog.d("TAG", "File not found");
        }
    }

    //手动终止读取文件，结束线程
    public void stopThread() {
        isFinish = true;
    }


    /** for test
     *
     * SurfaceHolder holder = surface.getHolder();

     holder.addCallback(new SurfaceHolder.Callback() {
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    if (mediaCodec == null) {
    mediaCodec = new MMediaCodec(holder);
    mediaCodec.startCodec();
    }
    if (h264FileRead == null) {
    //解码线程第一次初始化
    h264FileRead = new H264FileRead(mediaCodec, path);
    h264FileRead.start();
    }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    if (mediaCodec != null) {
    mediaCodec.stopCodec();
    mediaCodec = null;
    }
    if (h264FileRead != null && h264FileRead.isAlive()) {
    h264FileRead.stopThread();
    h264FileRead = null;
    }
    }
    });
     */

}