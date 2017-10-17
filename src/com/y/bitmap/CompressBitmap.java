package com.y.bitmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class CompressBitmap {
	
	private static String TAG="Compress";
	
	/**
	 * ͼƬ�����ŷ���
	 *
	 * @param bitmap  ��ԴͼƬ��Դ
	 * @param maxSize ��ͼƬ�������ռ�  ��λ:KB
	 * @return
	 */
	public static Bitmap getZoomImage(Bitmap bitmap, double maxSize) {
	    if (null == bitmap) {
	        return null;
	    }
	    if (bitmap.isRecycled()) {
	        return null;
	    }

	    // ��λ���� Byte ����� KB
	    double currentSize = bitmapToByteArray(bitmap, false).length / 1024;
	    // �ж�bitmapռ�ÿռ��Ƿ�����������ռ�,���������ѹ��,С����ѹ��
	    while (currentSize > maxSize) {
	        // ����bitmap�Ĵ�С��maxSize�Ķ��ٱ�
	        double multiple = currentSize / maxSize;
	        // ��ʼѹ����������͸߶�ѹ������Ӧ��ƽ������
	        // 1.�����µĿ�Ⱥ͸߶ȣ���bitmapԭ���Ŀ�߱���һ��
	        // 2.ѹ����ﵽ������С��Ӧ����bitmap����ʾЧ�����
	        bitmap = getZoomImage(bitmap, bitmap.getWidth() / Math.sqrt(multiple), bitmap.getHeight() / Math.sqrt(multiple));
	        currentSize = bitmapToByteArray(bitmap, false).length / 1024;
	    }
	    return bitmap;
	}

	/**
	 * ͼƬ�����ŷ���
	 *
	 * @param orgBitmap ��ԴͼƬ��Դ
	 * @param newWidth  �����ź���
	 * @param newHeight �����ź�߶�
	 * @return
	 */
	public static Bitmap getZoomImage(Bitmap orgBitmap, double newWidth, double newHeight) {
	    if (null == orgBitmap) {
	        return null;
	    }
	    if (orgBitmap.isRecycled()) {
	        return null;
	    }
	    if (newWidth <= 0 || newHeight <= 0) {
	        return null;
	    }

	    // ��ȡͼƬ�Ŀ�͸�
	    float width = orgBitmap.getWidth();
	    float height = orgBitmap.getHeight();
	    // ��������ͼƬ��matrix����
	    Matrix matrix = new Matrix();
	    // ������������
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // ����ͼƬ����
	    matrix.postScale(scaleWidth, scaleHeight);
	    Bitmap bitmap = Bitmap.createBitmap(orgBitmap, 0, 0, (int) width, (int) height, matrix, true);
	    return bitmap;
	}

	/**
	 * bitmapת����byte����
	 *
	 * @param bitmap
	 * @param needRecycle
	 * @return
	 */
	public static byte[] bitmapToByteArray(Bitmap bitmap, boolean needRecycle) {
	    if (null == bitmap) {
	        return null;
	    }
	    if (bitmap.isRecycled()) {
	        return null;
	    }

	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
	    if (needRecycle) {
	        bitmap.recycle();
	    }

	    byte[] result = output.toByteArray();
	    try {
	        output.close();
	    } catch (Exception e) {
	        Log.e(TAG, e.toString());
	    }
	    return result;
	}
	
	/**
     * �������β���ͼƬ
     */
    public static Bitmap ImageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth(); // �õ�ͼƬ�Ŀ���
        int h = bitmap.getHeight();

        int wh = w > h ? h : w;// ���к���ȡ������������߳�

        int retX = w > h ? (w - h) / 2 : 0;//����ԭͼ��ȡ���������Ͻ�x����
        int retY = w > h ? 0 : (h - w) / 2;

        //��������ǹؼ�
        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }
    
    /**
     * ����scale����һ��ͼƬ
     *
     * @param bitmap
     * @param scale  �ȱ�����ֵ
     * @return
     */
    public static Bitmap bitmapScale(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); // ���Ϳ�Ŵ���С�ı���
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }
    
    private static Bitmap compressImage(Bitmap image) {
        if (image == null) {
            return null;
        }
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();
            ByteArrayInputStream isBm = new ByteArrayInputStream(bytes);
            Bitmap bitmap = BitmapFactory.decodeStream(isBm);
            return bitmap;
        } catch (OutOfMemoryError e) {
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }
    //ѹ��ͼƬ RGB_565ֻռ16λ2�ֽ� ��ARGB_8888��һ��
    public static Bitmap bitmapreadBitMap(byte[] by) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //��ȡ��ԴͼƬ
        InputStream is = new ByteArrayInputStream(by);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
