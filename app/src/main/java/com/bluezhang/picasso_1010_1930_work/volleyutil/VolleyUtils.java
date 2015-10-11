package com.bluezhang.picasso_1010_1930_work.volleyutil;


import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bluezhang.picasso_1010_1930_work.imagesutil.ImageUtils;

public class VolleyUtils {
	public static RequestQueue mQueue;
	public static ImageLoader mLoader;

	private static LruCache<String, Bitmap> lruCache;// 强引用缓存，一级缓存，特点：使用最近最少使用算法，将旧数据移除，为新数据提供空间
	private static Map<String, SoftReference<Bitmap>> softCache;// 软引用缓存，二级缓存

	public static RequestQueue getQueue(Context context) {
		if (mQueue == null) {
			mQueue = Volley.newRequestQueue(context);
		}
		return mQueue;
	}

	/**
	 * 解析数据的接口
	 * 
	 * @author blueZhang
	 * 
	 * @param <T>
	 */
	public interface MyParser<T> {
		public void getJson(String response);
	}

	/**
	 * 将数据下载后，再进行解析，在Parser接口中写解析的方法
	 * 
	 * @param url
	 *            下载文本的url地址
	 * @param context
	 *            上下文
	 * @param parser
	 *            解析数据的接口
	 */
	public static <T> void getJsonString(String url, final Context context,final MyParser<T> parser)
	{
		StringRequest request = new StringRequest(url, new Listener<String>() 
		{
			public void onResponse(String response) 
			{
				parser.getJson(response);
			}
		}, null);
		getQueue(context).add(request);
	}

	/**
	 * 根据网址将图片加载到布局上面去
	 * 
	 * @param context
	 *            上下文
	 * @param path
	 *            图片网址
	 * @param imageview
	 *            要设置图片的ImageView对象
	 * @param defaultImageResId
	 *            默认图片
	 * @param errorImageResId
	 *            出现错误后显示在ImageView上的图片
	 */
	public static void setImage(Context context, String path,ImageView imageview, int defaultImageResId, int errorImageResId)
	{
		ImageLoader imgLoader = getLoader(context);
		imgLoader.get(path, ImageLoader.getImageListener(imageview,
				defaultImageResId, errorImageResId));
	}
	/**
	 * 根据网址将图片加载到布局上面去
	 *
	 * @param context
	 *            上下文
	 * @param path
	 *            图片网址
	 * @param imageview
	 *            要设置图片的ImageView对象
	 * @param defaultImageResId
	 *            默认图片
	 * @param errorImageResId
	 *            出现错误后显示在ImageView上的图片
	 * @param MAX_HEIGHT
	 * 			 最大高度
	 * @param MAX_WIDTH
	 * 			最大宽度
	 */
	public static void setImage(Context context, String path,ImageView imageview, int defaultImageResId, int errorImageResId,int MAX_WIDTH,int MAX_HEIGHT)
	{
		ImageLoader imgLoader = getLoader(context);
		imgLoader.get(path, ImageLoader.getImageListener(imageview,
				defaultImageResId, errorImageResId),MAX_WIDTH,MAX_HEIGHT);
	}

	public static ImageLoader getLoader(Context context) {
		if (mLoader == null) {
			// 实例化二级缓存
			softCache = new HashMap<String, SoftReference<Bitmap>>();

			// 实例化一级缓存
			lruCache = new LruCache<String, Bitmap>(2 * 1024 * 1024) {// 缓存的内存空间为2M
				protected int sizeOf(String key, Bitmap value) {
					// 计算存放成员的大小，返回字节大小

					return value.getRowBytes() * value.getHeight();// 图片大小

				};

				@Override
				protected void entryRemoved(boolean evicted, String key,Bitmap oldValue, Bitmap newValue) {
					// 移除旧成员
					if (evicted)
					{
						// 将移除的成员存放到二级缓存中
						softCache.put(key, new SoftReference<Bitmap>(oldValue));// 将移除的成员存放到二级缓存中
					}

					super.entryRemoved(evicted, key, oldValue, newValue);
				}

			};

			// 实例化图片加载器
			mLoader = new ImageLoader(getQueue(context),
					new ImageLoader.ImageCache() {

						public void putBitmap(String url, Bitmap bitmap) {
							// 将图片存放在缓存中的方法
							// 将图片存放在一级缓存中
							lruCache.put(url, bitmap);
							// 将图片也存放在扩展卡
							try {
								ImageUtils.saveImg(url, bitmap);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						public Bitmap getBitmap(String url) {
							// 从缓存中读取图片的方法
							// 先从一级缓存中获取
							Bitmap b = lruCache.get(url);
							if (b == null) {
								// 从二级缓存中读取
								SoftReference<Bitmap> reference = softCache
										.get(url);
								if (reference != null) {// 二级缓存中如果存在
									b = reference.get();
									if (b != null) {
										// 将图片对象存放到一级缓存中
										lruCache.put(url, b);
										// 从二级缓存中移除
										softCache.remove(reference);
									} else {// 从三级缓存中读取--扩展卡
										b = ImageUtils.getImg(url);
										if (b != null) {
											// 将图片存放到一级缓存中
											lruCache.put(url, b);
										}
									}
								}

							}
							return b;
						}
					});

		}
		return mLoader;
	}
}
