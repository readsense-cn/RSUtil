package cn.readsense.module.image;

import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.readsense.module.util.BitmapUtil;
import cn.readsense.module.util.HandleUtil;

public class ImageLoader {
    private static final ImageLoader ourInstance = new ImageLoader();
    private static ExecutorService executorService;

    private LruCache<String, Bitmap> mImageCache;

    static {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    private void exec(Runnable runnable) {
        executorService.submit(runnable);
    }

    public static ImageLoader getInstance() {
        return ourInstance;
    }

    private ImageLoader() {
        initImageCache();
    }

    private void initImageCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 12;

        mImageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    public void displayImage(final String path, final ImageView v) {
        displayImage(path, v, false);
    }

    public void displayImage(final String path, final ImageView v, final boolean circle) {

        Bitmap bitmap = mImageCache.get(path + "_" + circle);
        if (bitmap != null) {
            v.setImageBitmap(bitmap);
            return;
        }

        exec(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = BitmapUtil.decodeScaleImage(path, 1000, 1000);
                if (bitmap == null) return;
                if (circle) {
                    final Bitmap bitmap_dst = BitmapUtil.createCircleImage(bitmap, bitmap.getWidth());
                    mImageCache.put(path + "_" + circle, bitmap_dst);
                    HandleUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            v.setImageBitmap(bitmap_dst);
                        }
                    });
                } else {
                    mImageCache.put(path + "_" + circle, bitmap);
                    HandleUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            v.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        });
    }


}
