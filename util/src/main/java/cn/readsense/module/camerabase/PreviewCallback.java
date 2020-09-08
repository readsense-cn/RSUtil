package cn.readsense.module.camerabase;

/**
 * 预览回调数据
 */
public interface PreviewCallback {

    void onPreviewFrame(byte[] data);
}
