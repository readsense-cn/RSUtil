## Android 工具类整理

### 最新版本1.2.2
添加依赖
```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
implementation 'com.github.readsense-cn:RSUtil:1.2.2'
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

### [RSCamera相机极简应用](https://github.com/readsense-cn/RSUtil/tree/master/rscamera)
1. 分别使用SurfaceView、TextureView渲染预览画面
2. 切换摄像头
3. 任意角度旋转摄像头预览方向
4. 修改预览分辨率
5. 摄像头预览左右镜像
6. 预览不拉伸任意比例展示
7. 回调数据
8. 提供预览界面上绘制必要信息的DrawView


