package com.bluezhang.picasso_1010_1930_work.imagesutil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

public class ImageUtils {
	public static final String CACHEDIR = Environment
			.getExternalStorageDirectory() + "/blueZhang/images";

	public static boolean isMounted() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	/**
	 * 将Bitmap图片保存到存储卡中
	 * 
	 * @param url
	 * @param bitmap
	 * @throws IOException
	 */
	public static void saveImg(String url, Bitmap bitmap) throws IOException {
		if (!isMounted())
			return;

		File dir = new File(CACHEDIR);
		if (!dir.exists())
			dir.mkdirs();

		// 将图片对象写入到指定输出流中
		FileOutputStream fos = new FileOutputStream(new File(dir, getName(url)));
		bitmap.compress(getFormat(url), 100, fos);

	}

	/**
	 * 获取图片的格式
	 * 
	 * @param url
	 */
	public static CompressFormat getFormat(String url) {
		String fileName = getName(url);
		if (fileName.endsWith("png")) {
			return CompressFormat.PNG;
		}
		return CompressFormat.JPEG;
	}

	public static void saveImg(String url, byte[] bytes) throws IOException {
		if (!isMounted())
			return;

		File dir = new File(CACHEDIR);
		if (!dir.exists())
			dir.mkdirs();

		FileOutputStream fos = new FileOutputStream(new File(dir, getName(url)));
		fos.write(bytes);
		fos.close();

	}

	public static Bitmap getImg(String url) {
		if (!isMounted())
			return null;

		File imgFile = new File(CACHEDIR, getName(url));
		if (imgFile.exists()) {
			return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		}

		return null;
	}

	public static String getName(String url) {
		String a = url.substring(0,url.lastIndexOf("/"));
		
		
		return a.substring(a.lastIndexOf("/") + 1)+".jpg";
	}

	public String getName(String url, int end) {
		return url.substring(url.lastIndexOf("/") + 1, end);
	}

}
