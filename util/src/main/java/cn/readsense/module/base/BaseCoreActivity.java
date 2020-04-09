package cn.readsense.module.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleObserver;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.readsense.module.permissions.PermissionListener;
import cn.readsense.module.permissions.PermissionsUtil;
import cn.readsense.module.util.DLog;
import cn.readsense.module.util.DisplayUtil;
import cn.readsense.module.util.ToastUtils;


public abstract class BaseCoreActivity extends AppCompatActivity {
    public ProgressDialog progressDialog;
    Unbinder unbinder;
    public Context context;

    int screenWidth, screenHeight;
    private String permissions[];
    private List<LifecycleObserver> lifecycleObservers = new ArrayList<>();

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        screenWidth = DisplayUtil.getScreenWidth(context);
        screenHeight = DisplayUtil.getScreenHeight(context);
        final int layoutId = getLayoutId();
        if (layoutId != 0) {
            setContentView(layoutId);

            if (permissions != null) {
                if (!PermissionsUtil.hasPermission(context, permissions)) {
                    PermissionsUtil.requestPermission(context, new PermissionListener() {
                        @Override
                        public void permissionGranted(@NonNull String[] permission) {
                            registerViewAndObserve();
                        }

                        @Override
                        public void permissionDenied(@NonNull String[] permission) {
                            ToastUtils.show("相关请同意权限！！");
                        }
                    }, permissions);
                } else {
                    registerViewAndObserve();
                }
            } else {
                registerViewAndObserve();
            }
        } else {
            throw new Error("internal error: layoutId = 0!!");
        }
    }

    private void registerViewAndObserve() {
        initView();
        if (lifecycleObservers != null && lifecycleObservers.size() > 0) {
            for (LifecycleObserver observer : lifecycleObservers) {
                getLifecycle().addObserver(observer);
            }
        }
    }


    public void requestPermissions(String... permissions) {
        this.permissions = permissions;
    }

    public void addLifecycleObserver(LifecycleObserver lifecycleObserver) {
        if (!lifecycleObservers.contains(lifecycleObserver)) {
            lifecycleObservers.add(lifecycleObserver);
        } else {
            throw new Error("internal error: observer already input!!");
        }
    }

    public void showToast(String msg) {
        ToastUtils.show(msg);
    }

    public void showProgress(String msg) {
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    public void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();


    /**
     * 添加fragment
     *
     * @param fragment
     * @param frameId
     */
    protected void addFragment(BaseCoreFragment fragment, @IdRes int frameId) {
        Utils.checkNotNull(fragment);
        getSupportFragmentManager().beginTransaction()
                .add(frameId, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commitAllowingStateLoss();

    }


    /**
     * 替换fragment
     *
     * @param fragment
     * @param frameId
     */
    protected void replaceFragment(BaseCoreFragment fragment, @IdRes int frameId) {
        Utils.checkNotNull(fragment);
        getSupportFragmentManager().beginTransaction()
                .replace(frameId, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commitAllowingStateLoss();

    }


    /**
     * 隐藏fragment
     *
     * @param fragment
     */
    protected void hideFragment(BaseCoreFragment fragment) {
        Utils.checkNotNull(fragment);
        getSupportFragmentManager().beginTransaction()
                .hide(fragment)
                .commitAllowingStateLoss();

    }


    /**
     * 显示fragment
     *
     * @param fragment
     */
    protected void showFragment(BaseCoreFragment fragment) {
        Utils.checkNotNull(fragment);
        getSupportFragmentManager().beginTransaction()
                .show(fragment)
                .commitAllowingStateLoss();

    }


    /**
     * 移除fragment
     *
     * @param fragment
     */
    protected void removeFragment(BaseCoreFragment fragment) {
        Utils.checkNotNull(fragment);
        getSupportFragmentManager().beginTransaction()
                .remove(fragment)
                .commitAllowingStateLoss();

    }


    /**
     * 弹出栈顶部的Fragment
     */
    protected void popFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }
}
