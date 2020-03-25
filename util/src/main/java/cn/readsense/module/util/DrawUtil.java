package cn.readsense.module.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.SurfaceView;

public class DrawUtil {

    public static void drawRect(SurfaceView outputView, float[] rect, Paint paint) {

        Canvas canvas = outputView.getHolder().lockCanvas();
        if (canvas == null) return;
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        if (rect != null)
            try {
                canvas.drawRect(rect[0], rect[1], rect[2], rect[3], paint);
            } catch (Exception e) {
                e.printStackTrace();
            }

        outputView.getHolder().unlockCanvasAndPost(canvas);
    }

    public static void drawRect(SurfaceView outputView, float[][] rects, Paint paint, String[] show) {

        Canvas canvas = outputView.getHolder().lockCanvas();
        if (canvas == null) return;
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        if (rects != null) {
            for (int i = 0; i < rects.length; i++) {
                final float[] rect = rects[i];
                try {
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(rect[0], rect[1], rect[2], rect[3], paint);
                    if (show[i] != null) {
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawText(show[i], rect[0], rect[1] - 30, paint);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        outputView.getHolder().unlockCanvasAndPost(canvas);
    }
}
