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
	 * 图片的缩放方法
	 *
	 * @param bitmap  ：源图片资源
	 * @param maxSize ：图片允许最大空间  单位:KB
	 * @return
	 */
	public static Bitmap getZoomImage(Bitmap bitmap, double maxSize) {
	    if (null == bitmap) {
	        return null;
	    }
	    if (bitmap.isRecycled()) {
	        return null;
	    }

	    // 单位：从 Byte 换算成 KB
	    double currentSize = bitmapToByteArray(bitmap, false).length / 1024;
	    // 判断bitmap占用空间是否大于允许最大空间,如果大于则压缩,小于则不压缩
	    while (currentSize > maxSize) {
	        // 计算bitmap的大小是maxSize的多少倍
	        double multiple = currentSize / maxSize;
	        // 开始压缩：将宽带和高度压缩掉对应的平方根倍
	        // 1.保持新的宽度和高度，与bitmap原来的宽高比率一致
	        // 2.压缩后达到了最大大小对应的新bitmap，显示效果最好
	        bitmap = getZoomImage(bitmap, bitmap.getWidth() / Math.sqrt(multiple), bitmap.getHeight() / Math.sqrt(multiple));
	        currentSize = bitmapToByteArray(bitmap, false).length / 1024;
	    }
	    return bitmap;
	}

	/**
	 * 图片的缩放方法
	 *
	 * @param orgBitmap ：源图片资源
	 * @param newWidth  ：缩放后宽度
	 * @param newHeight ：缩放后高度
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

	    // 获取图片的宽和高
	    float width = orgBitmap.getWidth();
	    float height = orgBitmap.getHeight();
	    // 创建操作图片的matrix对象
	    Matrix matrix = new Matrix();
	    // 计算宽高缩放率
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // 缩放图片动作
	    matrix.postScale(scaleWidth, scaleHeight);
	    Bitmap bitmap = Bitmap.createBitmap(orgBitmap, 0, 0, (int) width, (int) height, matrix, true);
	    return bitmap;
	}

	/**
	 * bitmap转换成byte数组
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
     * 按正方形裁切图片
     */
    public static Bitmap ImageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

        int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;

        //下面这句是关键
        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }
    
    /**
     * 根据scale生成一张图片
     *
     * @param bitmap
     * @param scale  等比缩放值
     * @return
     */
    public static Bitmap bitmapScale(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); // 长和宽放大缩小的比例
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
    //压缩图片 RGB_565只占16位2字节 是ARGB_8888的一半
    public static Bitmap bitmapreadBitMap(byte[] by) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = new ByteArrayInputStream(by);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
