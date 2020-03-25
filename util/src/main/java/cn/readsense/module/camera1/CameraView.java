package cn.readsense.module.camera1;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by dou on 2017/11/7.
 */

public class CameraView extends RelativeLayout {

    private static final String TAG = "CameraView";
    private static final int MAIN_RET = 0x101;
    private static final int THREAD_RET = 0x102;
    public static final int PREVIEWMODE_SURFACEVIEW = 0x103;
    public static final int PREVIEWMODE_TEXTUREVIEW = 0x104;
    private int mode;
    private Context context;
    private int oritationDisplay = -1;

    private int PREVIEW_WIDTH;
    private int PREVIEW_HEIGHT;
    private int FACING;

    private byte buffer[];
    private byte temp[];
    private boolean isBufferready = false;
    private boolean is_thread_run = true;
    private final Object Lock = new Object();
    private PreviewFrameCallback previewFrameCallback;
    CameraController cameraController;

    HandlerThread handlerThread;
    Handler handler;
    Handler handlerMain;

    private SurfaceView drawView;
    private PreviewSurfaceView previewSurfaceView;
    private PreviewTextureView previewTextureView;

    private Paint paint;


    public SurfaceView getDrawView() {
        return drawView;
    }

    public void setDrawView() {
        drawView = new SurfaceView(context);
        drawView.setZOrderOnTop(true);
        drawView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public CameraView(Context context) {
        this(context, null);
    }


    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setBackgroundColor(Color.BLACK);
        llog("CameraView init");
    }

    public View getShowView() {
        switch (mode) {
            case PREVIEWMODE_SURFACEVIEW:
                return previewSurfaceView;
            case PREVIEWMODE_TEXTUREVIEW:
                return previewTextureView;
        }
        return null;
    }


    int vw, vh;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        vw = w;
        vh = h;
    }

    public void showCameraView(int width, int height, int facing) {
        showCameraView(width, height, facing, PREVIEWMODE_SURFACEVIEW);
    }

    public void showCameraView(int width, int height, int facing, final int mode) {

        cameraController = new CameraController();
        this.mode = mode;
        try {
            PREVIEW_WIDTH = width;
            PREVIEW_HEIGHT = height;
            buffer = null;
            temp = null;
            FACING = facing;

            cameraController.hasCameraDevice(context);

        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (mode) {
            case PREVIEWMODE_SURFACEVIEW:
                settingSurfaceView();
                break;
            case PREVIEWMODE_TEXTUREVIEW:
                settingTextureView();
                break;
        }

        try {

            cameraController.openCamera(FACING);

            Camera.Size prewSize = cameraController.getOptimalPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
            if (prewSize.width != width) {
                prewSize = null;
            }
            if (prewSize != null) {

                cameraController.setParamPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
                try {
                    cameraController.setDisplayOrientation(context, oritationDisplay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (previewFrameCallback != null)
                    cameraController.addPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(byte[] data, Camera camera) {//数据预览回掉

                            if (System.currentTimeMillis() - time > 1000) {
                                frameRate = frameCount;
                                frameCount = 0;
                                time = System.currentTimeMillis();
                                llog("onPreviewFrame frameRate: " + frameRate + " prew:" + mode);
                            }
                            frameCount++;
                            camera.addCallbackBuffer(data);
                            synchronized (Lock) {
                                System.arraycopy(data, 0, buffer, 0, data.length);
                                isBufferready = true;
                            }
                        }
                    });

                cameraController.setParamEnd();
            } else {
                releaseCamera();
                Toast.makeText(context, String.format(Locale.CHINA, "can not find preview size %d*%d", PREVIEW_WIDTH, PREVIEW_WIDTH), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(context, String.format(Locale.CHINA, "open camera failed, CamreaId: %d!", FACING), Toast.LENGTH_SHORT).show();
        }
    }

    int frameCount = 0;
    int frameRate = 0;
    long time = 0;


    private void settingSurfaceView() {
        if (getChildCount() != 0) removeAllViews();
        addCallback();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        previewSurfaceView = new PreviewSurfaceView(context, cameraController);
        addView(previewSurfaceView, params);

        if (drawView != null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(drawView, params);
        }
    }

    private void settingTextureView() {
        if (getChildCount() != 0) removeAllViews();
        addCallback();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        previewTextureView = new PreviewTextureView(context, cameraController, PREVIEW_WIDTH, PREVIEW_HEIGHT);
        addView(previewTextureView, params);

        if (drawView != null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(drawView, params);
        }
    }

    private void addCallback() {

        if (previewFrameCallback != null) {
            if (buffer == null)
                buffer = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
            if (temp == null)
                temp = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];

            handlerMain = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == MAIN_RET) {
                        if (previewFrameCallback != null)
                            previewFrameCallback.analyseDataEnd(msg.obj);
                    }
                }
            };

            //run data analyse
            handlerThread = new HandlerThread("camera-thread-" + System.currentTimeMillis());
            //开启一个线程
            handlerThread.start();
            is_thread_run = true;
            //在这个线程中创建一个handler对象
            handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == THREAD_RET) {
                        //这个方法是运行在 handler-thread 线程中的 ，可以执行耗时操作
                        while (is_thread_run) {

                            if (!isBufferready) {
                                try {
                                    Thread.sleep(28);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                continue;
                            }
                            synchronized (Lock) {
                                System.arraycopy(buffer, 0, temp, 0, buffer.length);
                                isBufferready = false;
                            }
                            if (previewFrameCallback != null) {
                                Object o = previewFrameCallback.analyseData(temp);
                                Message msg1 = new Message();
                                msg1.what = MAIN_RET;
                                msg1.obj = o;
                                handlerMain.sendMessage(msg1);
                            }
                        }
                    }
                }
            };
            handler.sendEmptyMessage(THREAD_RET);

        }


    }

    public void releaseCamera() {
        is_thread_run = false;

        if (cameraController != null) {
            removeDataCallback();
            cameraController.stopPreview();
            cameraController.releaseCamera();
        }
        removeAllViews();
        if (handler != null)
            handler.removeMessages(0);
        if (handlerMain != null)
            handlerMain.removeMessages(0);
        if (handlerThread != null)
            handlerThread.quit();

    }

    private void removeDataCallback() {
        this.previewFrameCallback = null;
        cameraController.removePreviewCallbackWithBuffer();
    }

    public void addPreviewFrameCallback(PreviewFrameCallback callback) {
        this.previewFrameCallback = callback;
    }

    public void setExposureCompensation(int limit) {
        cameraController.setExposureCompensation(limit);
    }

    public interface PreviewFrameCallback {
        Object analyseData(byte[] data);

        void analyseDataEnd(Object t);
    }

    public void setOritationDisplay(int oritationDisplay) {
        this.oritationDisplay = oritationDisplay;
    }

    boolean debug = true;

    private void llog(String msg) {
        if (!debug)
            Log.d(TAG, msg);
    }
}
