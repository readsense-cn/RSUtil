package cn.readsense.module.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * @author cafe_wang
 * 通用的ViewHolder
 */
/* 使用方法:
 * 只看getView，其他方法都一样；首先调用ViewHolder的get方法，如果convertView为null
 * ，new一个ViewHolder实例，通过使用mInflater.inflate加载布局
 * ，然后new一个SparseArray用于存储View，最后setTag(this)；
 * //实例化一个viewHolder
       ViewHolder viewHolder = ViewHolder.get(mContext, convertView, parent,
               R.layout.item_single_str, position);
       //通过getView获取控件
       TextView tv = viewHolder.getView(R.id.id_tv_title);
       //使用
       tv.setText(mDatas.get(position));
       return viewHolder.getConvertView();
 */
public class IduViewHolder {
    private final SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    private IduViewHolder(Context context, ViewGroup parent, int layoutId,
                          int position)
    {
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        // setTag
        mConvertView.setTag(this);
    }

    /**
     * 拿到一个ViewHolder对象
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static IduViewHolder get(Context context, View convertView,
                                    ViewGroup parent, int layoutId, int position)
    {
        if (convertView == null)
        {
            return new IduViewHolder(context, parent, layoutId, position);
        }
        return (IduViewHolder) convertView.getTag();
    }

    public View getConvertView()
    {
        return mConvertView;
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId)
    {
        View view = mViews.get(viewId);
        if (view == null)
        {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public int getPosition()
    {
        return mPosition;
    }
}
