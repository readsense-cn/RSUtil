package cn.readsense.module.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

//通用版本adapter
public abstract class IduAdapter<T> extends BaseAdapter{

    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> items;
    protected final int mItemLayoutId;

    public IduAdapter(Context context, List<T> mDatas, int itemLayoutId)
    {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        if(mDatas==null){
            mDatas = new ArrayList<T>();
        }
        this.items = mDatas;
        this.mItemLayoutId = itemLayoutId;
    }
    public IduAdapter(Context context, List<T> mDatas, int itemLayoutId, int line_count){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        if(mDatas==null){
            mDatas = new ArrayList<T>();
        }
        this.items = mDatas;
        this.mItemLayoutId = itemLayoutId;
    }
    public IduAdapter(Context context, int itemLayoutId)
    {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.items = new ArrayList<T>();
        this.mItemLayoutId = itemLayoutId;
    }

    public void addItem(int position,T items){
        this.items.add(position, items);
        notifyDataSetChanged();
    }
    public void replaceItem(int position,T items){
        this.items.set(position, items);
        notifyDataSetChanged();
    }
    public void addItem(T t){
        this.items.add(t);
        notifyDataSetChanged();
    }
    public void removeItem(int position){
        this.items.remove(position);
        notifyDataSetChanged();
    }

    public void addMoreItems(List<T> newItems) {
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void removeAllItems() {
        this.items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public T getItem(int position)
    {
        try {
            return items.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final IduViewHolder viewHolder = getViewHolder(position, convertView,parent);
        convert(viewHolder, getItem(position),position);
        return viewHolder.getConvertView();
    }

    public abstract void convert(IduViewHolder helper, T item, int position);

    private IduViewHolder getViewHolder(int position, View convertView, ViewGroup parent){
        return IduViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);
    }
}
