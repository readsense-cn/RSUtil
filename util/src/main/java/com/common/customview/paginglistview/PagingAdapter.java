package com.common.customview.paginglistview;

import android.content.Context;

import com.common.adapter.ListViewBaseAdapter;

import java.util.List;

/**
 * 列表分页适配器，配合 {@link PagingListView} 使用
 *
 * @author liyaotang
 * @date 2020-04-04
 */
public class PagingAdapter<T> extends ListViewBaseAdapter<T> {

    /** 每页大小 */
    private int pageSize = 10;

    /** 页码 */
    private int pageCount = 0;

    public PagingAdapter(Context context) {
        super(context);
    }

    public PagingAdapter(List<T> data, Context context) {
        super(data, context);
    }

    /** 获取页码，默认是0 */
    public int getPageCount() {
        return pageCount;
    }

    @Override
    public boolean setData(List<T> data) {
        boolean b = super.setData(data);
        pageCount = b ? 1 : 0;
        return b;
    }

    /**
     * 增加一页的数据，页码增1
     * @param data 新一页的数据
     */
    synchronized public void appendOnePageData(List<T> data) {
        if (appendData(data)) pageCount++;
    }
}
