package cn.module.rscamera.use;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import cn.readsense.module.util.DLog;

public class LuminosityAnalyzer implements ImageAnalysis.Analyzer {

    @Override
    public void analyze(@NonNull ImageProxy image) {
        DLog.d("analyze");
    }
}
