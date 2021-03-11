package com.common.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast统一管理类
 * 
 */
public class T
{

	private T()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static boolean isShow = true;

	/**
	 * 短时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, CharSequence message)
	{
		if (isShow)
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 短时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, int message)
	{
		if (isShow)
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 长时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, CharSequence message)
	{
		if (isShow)
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * 长时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, int message)
	{
		if (isShow)
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * 自定义显示Toast时间
	 * 
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, CharSequence message, int duration)
	{
		if (isShow)
			Toast.makeText(context, message, duration).show();
	}

	/**
	 * 自定义显示Toast时间
	 * 
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, int message, int duration)
	{
		if (isShow)
			Toast.makeText(context, message, duration).show();
	}
	
	private static Context mContext;
	
	private static int mDuration = Toast.LENGTH_SHORT;
	
	public static void setContext(Context context) {
		mContext = context.getApplicationContext();
	}
	
	/** 默认Toast.LENGTH_SHORT
	 * @param duration LENGTH_SHORT 或者  LENGTH_LONG，其他都设置为LENGTH_SHORT */
	public static void setDuration(int duration) {
		if (duration == Toast.LENGTH_LONG)
			mDuration = Toast. LENGTH_LONG;
		else
			mDuration = Toast.LENGTH_SHORT;
	}
	
	public static void show(CharSequence message) {
		if (mContext != null)
			Toast.makeText(mContext, message, mDuration).show();
	}
	
	public static void showShort(CharSequence message) {
		if (mContext != null)
			Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}
	
	public static void showLong(CharSequence message) {
		if (mContext != null)
			Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
	}
	
	public static void show(int resId) {
		if (mContext != null)
			Toast.makeText(mContext, resId, mDuration).show();
	}
	
	public static void showShort(int resId) {
		if (mContext != null)
			Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
	}
	
	public static void showLong(int resId) {
		if (mContext != null)
			Toast.makeText(mContext, resId, Toast.LENGTH_LONG).show();
	}

}