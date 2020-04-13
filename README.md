## Android 工具类整理

### 最新版本1.3.0
添加依赖
```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
implementation 'com.github.readsense-cn:RSUtil:1.4.0'
```
20200413: 增加坐标系转换接口，并归一化绘制坐标
将预览坐标系下的坐标转换至实际view坐标系下
![WechatIMG2.jpg](https://i.loli.net/2020/04/13/tXzbhiJ3pH6m4Bw.jpg)
```
cameraView.getDrawPositionX(float in, float w, boolean flip_x);
cameraView.getDrawPositionY(float in, float w, boolean flip_x);
```


20200408：精简应用端camera代码
一键应用
```
class MainActivity : BaseCoreActivity() {
    override fun getLayoutId(): Int {
        requestPermissions(Manifest.permission.CAMERA)//声明权限
        return R.layout.activity_main;//声明布局文件
    }

    override fun initView() {
        cameraview.showToast("长按可弹出配置页");
        addLifecycleObserver(cameraview)//注册camera生命周期
    }
}
```

20200408:
1. 移除SurfaceView预览
2. 新增CameraParams用于管理摄像头参数
3. 长按view，调出配置参数页面

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
#### 1. 切换摄像头

```
//改变camera id后释放重新show
cameraView.getCameraParams().facing = Camera.CameraInfo.CAMERA_FACING_BACK;
```
#### 2. 任意角度旋转摄像头预览方向
```kotlin
//show之前，设定display角度，支持（0, 90, 180, 270）
//输入非90倍数将根据activity方向以及设备自动适应，可能不太灵
cameraView.getCameraParams().oritationDisplay = 0;
```
#### 3. 修改预览分辨率
```
//根据摄像头支持的分辨率，release后重新show
cameraView.getCameraParams().previewSize.previewWidth = 640;
cameraView.getCameraParams().previewSize.previewHeight = 480;
```
#### 4. 摄像头预览左右镜像
```
cameraView.getCameraParams().filp = true;
```

#### 5. 预览不拉伸任意比例展示
设定cameraview指定宽高后，预览图像，若图像拉伸启用参数
```
//是否横向拉伸参数，非横即竖
cameraView.getCameraParams().scaleWidth = true;
```

#### 6. 回调数据
注册回调后再启动摄像头
```
cameraView.addPreviewFrameCallback(new CameraView.PreviewFrameCallback() {
    @Override
    public Object analyseData(byte[] bytes) {
        //接收yuv数据流，处理后的结果return,该方法执行在子线程
        return null;
    }

    @Override
    public void analyseDataEnd(Object o) {
        //接收analyseData方法返回结果，该方法执行在主线程
    }
});
```
#### 7. 提供预览界面上绘制必要信息的DrawView
```kotlin
//showCameraView之前配置DrawView
cameraView.setDrawView();
//获取drawView，类型为SurfaceView，可以直接在这个Surface绘制必要的信息
SurfaceView drawView = cameraView.getDrawView();
Canvas canvas = drawView.getHolder().lockCanvas();
if (canvas != null) {
    canvas.drawColor(0, PorterDuff.Mode.CLEAR);

    //...插入绘制代码

    drawView.getHolder().unlockCanvasAndPost(canvas);
}
```
#### 8. 长按camearview，弹出配置栏进行动态配置, 确认生效
![WechatIMG4.jpg](https://i.loli.net/2020/04/09/soCmZOxq5GKnv3w.jpg)

### END




