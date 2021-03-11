package com.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 常规 Adapter 框架
 *
 * @author liyaotang
 * @date 2020-03-15
 */
public class ListViewBaseAdapter<T> extends BaseAdapter {

    protected final String TAG = getClass().getSimpleName();

    private List<T> data;

    private Context mContext;

    private LayoutInflater layoutInflater;

    public ListViewBaseAdapter(Context context) {
        this.data = new ArrayList<>();
        this.mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
    }

    public ListViewBaseAdapter(List<T> data, Context context) {
        this.data = (data == null) ? new ArrayList<T>() : data;
        this.mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
    }

    /**
     * 替换所有数据
     *
     * @param data 数据
     * @return true，起码添加了一个数据，否则，返回false
     */
    public boolean setData(List<T> data) {
        this.data.clear();
        return appendData(data);
    }

    /**
     * 追加数据并且通知更新UI
     *
     * @param data 数据
     * @return true，起码追加了一个数据，否则，返回false
     */
    public boolean appendData(List<T> data) {
        if (data == null || data.isEmpty()) return false;

        this.data.addAll(data);
        notifyDataSetChanged();
        return true;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public T getItem(int position) {
        return data == null ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 参考代码，请勿调用super.getView(...)
     **/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
        String text = (String) getItem(position);

        Holder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_id, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }

        holder.textView.setText(text);

        return convertView;
        */
        return convertView;
    }


    /*
    private static class Holder {
        Holder(View parentView) {
            textView = parentView.findViewById(R.id.text_view_id);
        }

        TextView textView;
    }
    */
}