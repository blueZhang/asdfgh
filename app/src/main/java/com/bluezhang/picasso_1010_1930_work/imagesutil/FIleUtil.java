package com.bluezhang.picasso_1010_1930_work.imagesutil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

/**
 * 
 * @author blueZhang
 * ��չ�����ļ������Ĺ����࣬����ͼƬĿ¼mnt/sdcard
 *
 */
public class FIleUtil {
	// �洢ͼƬ��Ŀ¼
		// Environment.getExternalStorageDirectory() :��ȡ�ⲿ�洢�ռ��Ŀ¼
		public static final String IMAGE_URL = Environment
				.getExternalStorageDirectory() + "/gp/images";

		public static final int FORMAT_PNG = 1;
		public static final int FORMAT_JPEG = 2;

		/**
		 * �ж���չ���Ƿ����
		 * 
		 * @return
		 */
		public static boolean isMounted() {
			String state = Environment.getExternalStorageState();
			return state.equals(Environment.MEDIA_MOUNTED);
		}
		
		/**
		 * �ж���չ����ʣ��ռ乻������
		 */
		public static boolean  isAble()
		{
			//�ļ�ϵͳ״̬�������
			StatFs fs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
			int count = fs.getFreeBlocks();//���е����ݿ����
			int size =  fs.getBlockSize();//����ÿ�����ݿ�Ĵ�С
			
			//ʣ��ռ��С
			long  total = count*size;//��λ���ֽ�
			int  t = (int) (total/1024/1024);
			if(t>2)
				return true;
			else
				return false;
		}
		

		/**
		 * ����ͼƬ����չ���Ĺ���
		 * 
		 * @throws IOException
		 */
		public static void saveImage(String url, byte[] data) throws IOException { // �ж���չ���Ƿ����
			if (!isMounted())
				return;
			// �жϴ洢Ŀ¼�Ƿ����
			File dir = new File(IMAGE_URL);
			if (!dir.exists())
				dir.mkdirs();
			// ��ͼƬ����д�뵽һ��ͼƬ�ļ�
			FileOutputStream fos = new FileOutputStream(new File(dir,
					getFileName(url)));
			fos.write(data);
			fos.close();
		}

		/**
		 * ����ͼƬ����չ���Ĺ���
		 * 
		 * @throws FileNotFoundException
		 */
		public static void saveImage(String url, Bitmap bitmap, int format)
				throws FileNotFoundException {
			// �ж���չ���Ƿ����
			if (!isMounted())
				return;
			// �жϴ洢Ŀ¼�Ƿ����
			File dir = new File(IMAGE_URL);
			if (!dir.exists())
				dir.mkdirs();
			// ��ͼƬ����д�뵽һ��ͼƬ�ļ�
			FileOutputStream fos = new FileOutputStream(new File(dir,
					getFileName(url)));

			// ͼƬ��ѹ�� CompressFormat.PNG:ѹ��֮��ĸ�ʽ
			bitmap.compress(format == 1 ? CompressFormat.PNG : CompressFormat.JPEG,
					100, fos);
		}

		/**
		 * ����չ����ȡͼƬ�Ĺ���
		 */
		public static Bitmap readImage(String url) {
			// �ж���չ���Ƿ����
			if (!isMounted())
				return null;
			String filename = getFileName(url);
			File file = new File(IMAGE_URL, filename);
			Bitmap bitmap = null;
	        if(file.exists())
	        	bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			return bitmap;
		}

		/**
		 * �����չ�� ����Ŀ¼�е����ݵĹ���
		 */
		public static void clear() {
			// �ж���չ���Ƿ����
			if (!isMounted())
				return;
			File dir = new File(IMAGE_URL);
			if (dir.exists()) {
	              File[] arr = dir.listFiles();
	              for(File f:arr)
	              {
	            	  f.delete();
	              }
			}
		}

		/**
		 * �����ļ�������·����ȡ�ļ���
		 * 
		 * @param url
		 * @return
		 */
		public static String getFileName(String url) {
			
			String a = url.substring(0,url.lastIndexOf("/"));
			
			
			return a.substring(a.lastIndexOf("/") + 1)+".jpg";
		}

}
