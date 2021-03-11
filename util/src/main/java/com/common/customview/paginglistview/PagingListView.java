package com.common.customview.paginglistview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 到达底部会触发更新，需要配合{@link PagingAdapter} 使用
 *
 * @author liyaotang
 * @date 2020-04-04
 */
public class PagingListView extends ListView {

    public interface OnRefreshListener {
        void onRefresh(int newPage);
    }

    private OnRefreshListener onRefreshListener;

    private PagingAdapter pagingAdapter;

    /** 是否允许下来加载 */
    private boolean refreshEnable;

    public PagingListView(Context context) {
        super(context);
        setOnScrollListener(onScrollListener);
    }

    public PagingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(onScrollListener);
    }

    public PagingListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnScrollListener(onScrollListener);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    /**
     * 区分 {@link #setAdapter(ListAdapter)}，下拉加载分页的功能，必须使用本方法设置 adapter
     *
     * @param pagingAdapter 分页适配器
     */
    public void setPagingAdapter(PagingAdapter pagingAdapter) {
        super.setAdapter(pagingAdapter);
        this.pagingAdapter = pagingAdapter;
    }

    /**
     * 设置加载状态，如果 {@link #refreshEnable} == true，则不会回调
     * {@link OnRefreshListener#onRefresh(int)}
     *
     * 正在加载当前一页数据的时候，为了避免重复加载，设置{@link #refreshEnable} 为 false，
     * 加载结束之后，重新设置 {@link #refreshEnable} 为 true
     **/
    public void setRefreshEnable(boolean refreshEnable) {
        this.refreshEnable = refreshEnable;
    }

    /**
     * 当前是否允许下来刷新操作
     */
    public boolean isRefreshEnable() {
        return this.refreshEnable;
    }

    OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                if (view.getLastVisiblePosition() == view.getCount() - 1
                        && !view.canScrollVertically(1)) {
                    if (pagingAdapter != null) {
                        int pageId = pagingAdapter.getPageCount();
                        // 触发刷新
                        if (onRefreshListener != null && refreshEnable)
                            onRefreshListener.onRefresh(pageId + 1);
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };
}
