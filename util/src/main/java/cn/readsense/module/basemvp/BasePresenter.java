package cn.readsense.module.basemvp;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public abstract class BasePresenter<V extends BaseView, M extends BaseModel> implements LifecycleObserver {

    private V v;
    private M m;

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void bindView(V v) {
        this.v = v;
        this.m = initModel();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void unBindView(V v) {
        this.v = null;
        if (m != null) {
            m.free();
        }
    }


    public M initModel() {
        return null;
    }
}
