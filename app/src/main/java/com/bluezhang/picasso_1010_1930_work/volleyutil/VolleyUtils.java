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

	private static LruCache<String, Bitmap> lruCache;// ǿ���û��棬һ�����棬�ص㣺ʹ���������ʹ���㷨�����������Ƴ���Ϊ�������ṩ�ռ�
	private static Map<String, SoftReference<Bitmap>> softCache;// �����û��棬��������

	public static RequestQueue getQueue(Context context) {
		if (mQueue == null) {
			mQueue = Volley.newRequestQueue(context);
		}
		return mQueue;
	}

	/**
	 * �������ݵĽӿ�
	 * 
	 * @author blueZhang
	 * 
	 * @param <T>
	 */
	public interface MyParser<T> {
		public void getJson(String response);
	}

	/**
	 * ���������غ��ٽ��н�������Parser�ӿ���д�����ķ���
	 * 
	 * @param url
	 *            �����ı���url��ַ
	 * @param context
	 *            ������
	 * @param parser
	 *            �������ݵĽӿ�
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
	 * ������ַ��ͼƬ���ص���������ȥ
	 * 
	 * @param context
	 *            ������
	 * @param path
	 *            ͼƬ��ַ
	 * @param imageview
	 *            Ҫ����ͼƬ��ImageView����
	 * @param defaultImageResId
	 *            Ĭ��ͼƬ
	 * @param errorImageResId
	 *            ���ִ������ʾ��ImageView�ϵ�ͼƬ
	 */
	public static void setImage(Context context, String path,ImageView imageview, int defaultImageResId, int errorImageResId)
	{
		ImageLoader imgLoader = getLoader(context);
		imgLoader.get(path, ImageLoader.getImageListener(imageview,
				defaultImageResId, errorImageResId));
	}
	/**
	 * ������ַ��ͼƬ���ص���������ȥ
	 *
	 * @param context
	 *            ������
	 * @param path
	 *            ͼƬ��ַ
	 * @param imageview
	 *            Ҫ����ͼƬ��ImageView����
	 * @param defaultImageResId
	 *            Ĭ��ͼƬ
	 * @param errorImageResId
	 *            ���ִ������ʾ��ImageView�ϵ�ͼƬ
	 * @param MAX_HEIGHT
	 * 			 ���߶�
	 * @param MAX_WIDTH
	 * 			�����
	 */
	public static void setImage(Context context, String path,ImageView imageview, int defaultImageResId, int errorImageResId,int MAX_WIDTH,int MAX_HEIGHT)
	{
		ImageLoader imgLoader = getLoader(context);
		imgLoader.get(path, ImageLoader.getImageListener(imageview,
				defaultImageResId, errorImageResId),MAX_WIDTH,MAX_HEIGHT);
	}

	public static ImageLoader getLoader(Context context) {
		if (mLoader == null) {
			// ʵ������������
			softCache = new HashMap<String, SoftReference<Bitmap>>();

			// ʵ����һ������
			lruCache = new LruCache<String, Bitmap>(2 * 1024 * 1024) {// ������ڴ�ռ�Ϊ2M
				protected int sizeOf(String key, Bitmap value) {
					// �����ų�Ա�Ĵ�С�������ֽڴ�С

					return value.getRowBytes() * value.getHeight();// ͼƬ��С

				};

				@Override
				protected void entryRemoved(boolean evicted, String key,Bitmap oldValue, Bitmap newValue) {
					// �Ƴ��ɳ�Ա
					if (evicted)
					{
						// ���Ƴ��ĳ�Ա��ŵ�����������
						softCache.put(key, new SoftReference<Bitmap>(oldValue));// ���Ƴ��ĳ�Ա��ŵ�����������
					}

					super.entryRemoved(evicted, key, oldValue, newValue);
				}

			};

			// ʵ����ͼƬ������
			mLoader = new ImageLoader(getQueue(context),
					new ImageLoader.ImageCache() {

						public void putBitmap(String url, Bitmap bitmap) {
							// ��ͼƬ����ڻ����еķ���
							// ��ͼƬ�����һ��������
							lruCache.put(url, bitmap);
							// ��ͼƬҲ�������չ��
							try {
								ImageUtils.saveImg(url, bitmap);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						public Bitmap getBitmap(String url) {
							// �ӻ����ж�ȡͼƬ�ķ���
							// �ȴ�һ�������л�ȡ
							Bitmap b = lruCache.get(url);
							if (b == null) {
								// �Ӷ��������ж�ȡ
								SoftReference<Bitmap> reference = softCache
										.get(url);
								if (reference != null) {// �����������������
									b = reference.get();
									if (b != null) {
										// ��ͼƬ�����ŵ�һ��������
										lruCache.put(url, b);
										// �Ӷ����������Ƴ�
										softCache.remove(reference);
									} else {// �����������ж�ȡ--��չ��
										b = ImageUtils.getImg(url);
										if (b != null) {
											// ��ͼƬ��ŵ�һ��������
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
