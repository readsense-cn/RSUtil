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

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.Locale;

/**
 * Created by dou on 2017/11/7.
 */

public class CameraView extends RelativeLayout implements LifecycleObserver {

    private static final String TAG = "CameraView";
    private static final int MAIN_RET = 0x101;
    private static final int THREAD_RET = 0x102;
    private static final int THREAD_OPENCAMERA = 0x103;

    private Context context;
    private CameraParams cameraParams;

    public CameraParams getCameraParams() {
        return cameraParams;
    }

    public void setCameraParams(CameraParams cameraParams) {
        this.cameraParams = cameraParams;
    }


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
    private PreviewTextureView previewTextureView;
    private Paint paint;

    private ToolWindow toolWindow;

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


    public void showToolWindow() {
        if (toolWindow == null)
            toolWindow = new ToolWindow(new ToolWindow.WindowEventListener() {
                @Override
                public void eventEnd() {
                    releaseCamera();
                    showCameraView();
                }
            }, context, cameraParams);
        toolWindow.showWindow();
    }


    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setBackgroundColor(Color.BLACK);
        llog("CameraView init");
        cameraController = new CameraController();
        cameraParams = new CameraParams();
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showToolWindow();
                return false;
            }
        });
    }

    public View getShowView() {
        return previewTextureView;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void showCameraView() {

        try {
            buffer = null;
//            temp = null;

            cameraController.hasCameraDevice(context);
//            initCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
        settingTextureView();

    }

    int frameCount = 0;
    int frameRate = 0;
    long time = 0;

    private void initCamera() {
        llog("initCamera thread:" + Thread.currentThread().getId());
        cameraController.openCamera(cameraParams.getFacing());
        try {


            Camera.Size prewSize = cameraController.getOptimalPreviewSize(
                    cameraParams.getPreviewSize().getPreviewWidth(),
                    cameraParams.getPreviewSize().getPreviewHeight()
            );
            if (prewSize.width != cameraParams.getPreviewSize().getPreviewWidth() || prewSize.height != cameraParams.getPreviewSize().getPreviewHeight()) {
                prewSize = null;
            }
            if (prewSize != null) {

                cameraController.setParamPreviewSize(
                        cameraParams.getPreviewSize().getPreviewWidth(),
                        cameraParams.getPreviewSize().getPreviewHeight()
                );
                try {
                    cameraController.setDisplayOrientation(context, cameraParams.getOritationDisplay());
                    cameraController.setParamEnd();
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
                                llog("onPreviewFrame frameRate: " + frameRate + " " + Thread.currentThread().getId());
                            }
                            frameCount++;
                            camera.addCallbackBuffer(data);


                            synchronized (Lock) {
                                System.arraycopy(data, 0, buffer, 0, data.length);
                                isBufferready = true;
                                handler.sendEmptyMessage(THREAD_RET);
//                                Object o = previewFrameCallback.analyseData(buffer);
//                                Message msg1 = new Message();
//                                msg1.what = MAIN_RET;
//                                msg1.obj = o;
//                                handlerMain.sendMessage(msg1);
                            }
                        }
                    });

            } else {
                releaseCamera();
                Toast.makeText(context, String.format(Locale.CHINA, "can not find preview size %d*%d",
                        cameraParams.getPreviewSize().getPreviewWidth(),
                        cameraParams.getPreviewSize().getPreviewHeight()), Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, String.format(Locale.CHINA, "open camera failed, CamreaId: %d! " + e.getMessage(), cameraParams.getFacing()), Toast.LENGTH_SHORT).show();
        }
    }

    private void settingTextureView() {
        if (getChildCount() != 0) removeAllViews();
        addCallback();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                previewTextureView = new PreviewTextureView(context, cameraController,
                        cameraParams.getPreviewSize().getPreviewWidth(),
                        cameraParams.getPreviewSize().getPreviewHeight());
                addView(previewTextureView, params);

                if (drawView != null) {
                    params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    addView(drawView, params);
                }

                if (previewTextureView != null)
                    previewTextureView.setConfigureTransform(
                            cameraParams.isScaleWidth() ? cameraParams.getPreviewSize().getPreviewWidth() : cameraParams.getPreviewSize().getPreviewHeight(),
                            cameraParams.isScaleWidth() ? cameraParams.getPreviewSize().getPreviewHeight() : cameraParams.getPreviewSize().getPreviewWidth(), cameraParams.isFilp());

            }
        }, 300);

    }

    private void addCallback() {

        if (previewFrameCallback != null) {
            if (buffer == null)
                buffer = new byte[
                        cameraParams.getPreviewSize().getPreviewWidth() *
                                cameraParams.getPreviewSize().getPreviewHeight() * 3 / 2];
            if (temp == null)
                temp = new byte[
                        cameraParams.getPreviewSize().getPreviewWidth() *
                                cameraParams.getPreviewSize().getPreviewHeight() * 3 / 2];

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
//            is_thread_run = true;
            //在这个线程中创建一个handler对象
            handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case THREAD_RET:
                            //这个方法是运行在 handler-thread 线程中的 ，可以执行耗时操作
                            while (is_thread_run) {

                                if (!isBufferready) {
                                    try {
                                        Thread.sleep(1);
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
                                break;
                            }
                            break;
                        case THREAD_OPENCAMERA:


                            break;
                    }

                }
            };
            handler.sendEmptyMessage(THREAD_OPENCAMERA);
            initCamera();
//            handler.sendEmptyMessageDelayed(THREAD_RET, 1000);
        } else {
            initCamera();
        }


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void releaseCamera() {
//        is_thread_run = false;

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

    boolean debug = true;

    private void llog(String msg) {
        if (debug)
            Log.d(TAG, msg);
    }

    public boolean hasCameraFacing(int facing) {
        return cameraController.hasCameraFacing(facing);
    }

    public float getScale() {
        if (getShowView() instanceof PreviewTextureView) {
            return ((PreviewTextureView) getShowView()).getScale();
        }
        System.out.println("Only PreviewTextureView can invoke getScale！！");
        return 1;
    }

    public float getDrawPositionX(float in, float w, boolean flip_x) {
        if (flip_x) {
            if (cameraParams.getOritationDisplay() % 180 == 0) {
                in = cameraParams.getPreviewSize().getPreviewWidth() - in - w;
                in -= cameraParams.getPreviewSize().getPreviewWidth() >> 1;
            } else {
                in = cameraParams.getPreviewSize().getPreviewHeight() - in - w;
                in -= cameraParams.getPreviewSize().getPreviewHeight() >> 1;
            }
        } else {
            if (cameraParams.getOritationDisplay() % 180 == 0) {
                in -= cameraParams.getPreviewSize().getPreviewWidth() >> 1;
            } else {
                in -= cameraParams.getPreviewSize().getPreviewHeight() >> 1;
            }
        }

        in *= getScale();
        in += previewTextureView.vw >> 1;
        return in;
    }

    public float getDrawPositionY(float in, float h, boolean flip_y) {
        if (flip_y) {
            if (cameraParams.getOritationDisplay() % 180 == 0) {
                in = cameraParams.getPreviewSize().getPreviewHeight() - in - h;
                in -= cameraParams.getPreviewSize().getPreviewHeight() >> 1;
            } else {
                in = cameraParams.getPreviewSize().getPreviewWidth() - in - h;
                in -= cameraParams.getPreviewSize().getPreviewWidth() >> 1;
            }
        } else {
            if (cameraParams.getOritationDisplay() % 180 == 0) {
                in -= cameraParams.getPreviewSize().getPreviewHeight() >> 1;
            } else {
                in -= cameraParams.getPreviewSize().getPreviewWidth() >> 1;
            }
        }

        in *= getScale();
        in += previewTextureView.vh >> 1;

        return in;
    }


}
