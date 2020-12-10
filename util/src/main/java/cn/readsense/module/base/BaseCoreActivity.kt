package cn.readsense.module.base

import android.app.ProgressDialog
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import cn.readsense.module.permissions.PermissionListener
import cn.readsense.module.permissions.PermissionsUtil
import cn.readsense.module.util.DisplayUtil
import cn.readsense.module.util.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseCoreActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    var progressDialog: ProgressDialog? = null
    private var screenWidth = 0
    private var screenHeight = 0
    private var permissions: MutableList<String> = mutableListOf()
    private val lifecycleObservers: MutableList<LifecycleObserver> = mutableListOf()
    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = ProgressDialog(baseContext)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)
        screenWidth = DisplayUtil.getScreenWidth(baseContext)
        screenHeight = DisplayUtil.getScreenHeight(baseContext)
        val layoutId = getLayoutId()
        if (layoutId != 0) {
            setContentView(layoutId)
            if (!PermissionsUtil.hasPermission(baseContext, *permissions.toTypedArray())) {
                PermissionsUtil.requestPermission(baseContext, object : PermissionListener {
                    override fun permissionGranted(permission: Array<String>) {
                        registerViewAndObserve()
                    }

                    override fun permissionDenied(permission: Array<String>) {
                        ToastUtils.show("相关请同意权限！！")
                    }
                }, *permissions.toTypedArray())
            } else {
                registerViewAndObserve()
            }
        } else {
            throw Error("internal error: layoutId = 0!!")
        }
    }

    private fun registerViewAndObserve() {
        initView()
        if (lifecycleObservers.size > 0) {
            for (observer in lifecycleObservers) {
                lifecycle.addObserver(observer)
            }
        }
    }

    fun requestPermissions(vararg permissions: String) {
        this.permissions.clear()
        for (permission in permissions)
            this.permissions.add(permission)

    }

    fun addLifecycleObserver(lifecycleObserver: LifecycleObserver) {
        if (!lifecycleObservers!!.contains(lifecycleObserver)) {
            lifecycleObservers.add(lifecycleObserver)
        } else {
            throw Error("internal error: observer already input!!")
        }
    }

    fun showToast(msg: String?) {
        ToastUtils.show(msg)
    }

    fun showProgress(msg: String?) {
        progressDialog!!.setMessage(msg)
        progressDialog!!.show()
    }

    fun dismissProgress() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initView()

    /**
     * 添加fragment
     *
     * @param fragment
     * @param frameId
     */
    protected fun addFragment(fragment: BaseCoreFragment, @IdRes frameId: Int) {
        Utils.checkNotNull(fragment)
        supportFragmentManager.beginTransaction()
            .add(frameId, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .commitAllowingStateLoss()
    }

    /**
     * 替换fragment
     *
     * @param fragment
     * @param frameId
     */
    protected fun replaceFragment(fragment: BaseCoreFragment, @IdRes frameId: Int) {
        Utils.checkNotNull(fragment)
        supportFragmentManager.beginTransaction()
            .replace(frameId, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .commitAllowingStateLoss()
    }

    /**
     * 隐藏fragment
     *
     * @param fragment
     */
    protected fun hideFragment(fragment: BaseCoreFragment) {
        Utils.checkNotNull(fragment)
        supportFragmentManager.beginTransaction()
            .hide(fragment)
            .commitAllowingStateLoss()
    }

    /**
     * 显示fragment
     *
     * @param fragment
     */
    protected fun showFragment(fragment: BaseCoreFragment) {
        Utils.checkNotNull(fragment)
        supportFragmentManager.beginTransaction()
            .show(fragment)
            .commitAllowingStateLoss()
    }

    /**
     * 移除fragment
     *
     * @param fragment
     */
    protected fun removeFragment(fragment: BaseCoreFragment) {
        Utils.checkNotNull(fragment)
        supportFragmentManager.beginTransaction()
            .remove(fragment)
            .commitAllowingStateLoss()
    }

    /**
     * 弹出栈顶部的Fragment
     */
    protected fun popFragment() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }
}