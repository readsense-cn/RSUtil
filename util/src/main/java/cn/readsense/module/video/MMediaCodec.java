package cn.readsense.module.video;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MMediaCodec {
    //解码后显示的surface及其宽高
    private SurfaceHolder holder;
    private int width, height;
    //解码器
    private MediaCodec mCodec;
    private MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

    // 需要解码的类型
    private final static String MIME_TYPE = "video/avc"; // H.264 Advanced Video
    private final static int TIME_INTERNAL = 5;

    //解码帧数
    private int decode_frame_count = 0;

    //根据帧率获取的解码每帧需要休眠的时间,根据实际帧率进行操作
    private static final int PRE_FRAME_TIME = 1000 / 24;
    //按帧用来缓存h264数据
    private ArrayList<byte[]> frameList;
    //缓存最多的帧数
    private static final int MAX_FRAME_SIZE = 100;

    //停止输入h264
    private boolean isFinish = false;

    /**
     * 初始化解码器
     *
     * @param holder 用于显示视频的surface
     * @param width  surface宽
     * @param height surface高
     */
    public MMediaCodec(SurfaceHolder holder, int width, int height) {
        this.holder = holder;
        this.width = width;
        this.height = height;
        frameList = new ArrayList<>();
    }

    public MMediaCodec(SurfaceHolder holder) {
        this(holder, holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
    }

    public void startCodec() {

        //初始化MediaFormat
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE,
                width, height);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1000000);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        mediaFormat
                .setInteger(
                        MediaFormat.KEY_COLOR_FORMAT,
                        MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        String mime = mediaFormat.getString(MediaFormat.KEY_MIME);

        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);

        try {
            //根据需要解码的类型创建解码器
            mCodec = MediaCodec.createDecoderByType(mime);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //配置MediaFormat以及需要显示的surface
        mCodec.configure(mediaFormat, holder.getSurface(), null, 0);
        //开始解码
        mCodec.start();

        //开启解码线程
        new DecodeThread().start();
    }


    public boolean decodeFrame(byte[] buf, int offset, int length) {
        // 获取输入buffer index
        ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
        //-1表示一直等待；0表示不等待；其他大于0的参数表示等待毫秒数
        int inputBufferIndex = mCodec.dequeueInputBuffer(-1);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            //清空buffer
            inputBuffer.clear();
            //put需要解码的数据
            inputBuffer.put(buf, offset, length);
            //解码
            mCodec.queueInputBuffer(inputBufferIndex, 0, length, decode_frame_count * TIME_INTERNAL, 0);
            decode_frame_count++;

        } else {
            return false;
        }

        // 获取输出buffer index
        int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 100);
        //视频发生了变化
        if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            MediaFormat oformat = mCodec.getOutputFormat();
            width = oformat.getInteger(MediaFormat.KEY_WIDTH);
            height = oformat.getInteger(MediaFormat.KEY_HEIGHT);
        }
        //循环解码，直到数据全部解码完成
        while (outputBufferIndex >= 0) {
//            Image image = mCodec.getOutputImage(outputBufferIndex);
//            byte[] yuv = getDataFromImage(image, COLOR_FormatNV21);

            mCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
        return true;
    }

    /**
     * 停止解码，释放解码器
     */
    public void stopCodec() {

        try {
            mCodec.stop();
            mCodec.release();
            mCodec = null;
        } catch (Exception e) {
            e.printStackTrace();
            mCodec = null;
        }
    }

    //修眠
    private void sleepThread(long startTime, long endTime) {
        //根据读文件和解码耗时，计算需要休眠的时间
        long time = PRE_FRAME_TIME - (endTime - startTime);
        if (time > 0) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void addFrame(byte[] frame) {
        frameList.add(frame);
        //当长度多于MAX_FRAME_SIZE时,休眠2秒，避免OOM
        if (frameList.size() > MAX_FRAME_SIZE) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //停止输入
    public void finishAddFrame() {
        isFinish = true;
    }

    /**
     * 解码线程
     */
    private class DecodeThread extends Thread {
        @Override
        public void run() {
            super.run();
            long start;

            while (!isFinish || (frameList != null && frameList.size() > 0)) {
                start = System.currentTimeMillis();
                if (frameList != null && frameList.size() > 0) {
                    decodeFrame(frameList.get(0), 0, frameList.get(0).length);
                    //移除已经解码的数据
                    frameList.remove(0);
                }
                //休眠
                sleepThread(start, System.currentTimeMillis());
            }
        }
    }
}