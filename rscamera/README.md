## Android Camera简易使用方案
### 权限
```
<uses-permission android:name="android.permission.CAMERA" />
```
Android 6.0以上需要动态申请权限
### 使用
链接rscamera，在指定页面位置添加控件
```xml
<cn.readsense.rscamera.camera.CameraView
    android:id="@+id/cameraview"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

### 支持功能
#### 1. 分别使用SurfaceView、TextureView渲染预览画面
```kotlin
//默认使用SurfaceView渲染
cameraview.showCameraView(
    PREVIEWWIDTH,
    PREVIEWHEIGHT,
    CAMERA_ID
)
//使用TextureView渲染
cameraview.showCameraView(
    PREVIEWWIDTH,
    PREVIEWHEIGHT,
    CAMERA_ID, CameraView.PREVIEWMODE_TEXTUREVIEW
)
```
#### 2. 切换摄像头

```kotlin
//改变camera id后释放重新show
cameraview.releaseCamera()
cameraview.showCameraView(
    PREVIEWWIDTH,
    PREVIEWHEIGHT,
    CAMERA_ID
)
```
#### 3. 任意角度旋转摄像头预览方向
```kotlin
//show之前，设定display角度，支持（0, 90, 180, 270）
//输入非90倍数将根据activity方向以及设备自动适应，可能不太灵
cameraview.setOritationDisplay(90)

cameraview.showCameraView(
    PREVIEWWIDTH,
    PREVIEWHEIGHT,
    CAMERA_ID, CameraView.PREVIEWMODE_TEXTUREVIEW
)
```
#### 4. 修改预览分辨率
```kotlin
//根据摄像头支持的分辨率，release后重新show
cameraview.releaseCamera()
PREVIEWWIDTH = 640
PREVIEWHEIGHT = 480
cameraview.showCameraView(
    PREVIEWWIDTH,
    PREVIEWHEIGHT,
    CAMERA_ID
)
```
#### 5. 摄像头预览左右镜像
* 仅支持使用TextureView渲染


```kotlin
cameraview.showCameraView(
    PREVIEWWIDTH,
    PREVIEWHEIGHT,
    CAMERA_ID, CameraView.PREVIEWMODE_TEXTUREVIEW
)
if (cameraview.showView is PreviewTextureView) {
	(cameraview.showView as PreviewTextureView).setConfigureTransform(
	    PREVIEWWIDTH,
	    PREVIEWHEIGHT, true)
}
```
#### 6. 预览不拉伸任意比例展示
* 仅支持使用TextureView渲染


首先设定cameraview到指定宽高，然后调用setConfigureTransform变换

```kotlin
cameraview.layoutParams.width = 400
cameraview.layoutParams.height = 300
cameraview.requestLayout()

(cameraview.showView as PreviewTextureView).setConfigureTransform(
    PREVIEWWIDTH,
    PREVIEWHEIGHT, flip
)
```
#### 7. 回调数据
注册回调后再启动摄像头
```kotlin
cameraview.addPreviewFrameCallback(object : PreviewFrameCallback {
	//接收yuv数据流，处理后的结果return,该方法执行在子线程
    override fun analyseData(data: ByteArray?): Any {
        return 0
    }
	//接收analyseData方法返回结果，该方法执行在主线程
    override fun analyseDataEnd(t: Any?) {

    }
})
```
#### 8. 提供预览界面上绘制必要信息的DrawView
```kotlin
//showCameraView之前配置DrawView
cameraview.setDrawView()

//获取drawView，类型为SurfaceView，可以直接在这个Surface绘制必要的信息
val drawView = cameraview.drawView
val canvas = drawView.holder.lockCanvas()
if (canvas != null) {
    canvas.drawColor(0, PorterDuff.Mode.CLEAR)

    //...插入绘制代码

    drawView.holder.unlockCanvasAndPost(canvas)
}
```

### END

