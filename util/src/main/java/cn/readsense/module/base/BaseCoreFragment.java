package cn.readsense.module.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.readsense.module.basemvp.BasePresenter;
import cn.readsense.module.basemvp.BaseView;


public abstract class BaseCoreFragment extends Fragment implements BaseView {
    public View rootView;
    public LayoutInflater inflater;
    Unbinder unbinder;

    public BasePresenter basePresenter;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;
        if (rootView == null) {
            rootView = inflater.inflate(this.getLayoutId(), container, false);
            unbinder = ButterKnife.bind(this, rootView);
            initView();
            basePresenter = initPresenter();
            if (basePresenter != null)
                getLifecycle().addObserver(basePresenter);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unbinder.unbind();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected BasePresenter initPresenter() {
        return null;
    }

}
