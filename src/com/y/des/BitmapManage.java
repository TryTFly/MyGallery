package com.y.des;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 图片操作类
 * @author ThinkPad
 *
 */
public class BitmapManage {
	private Context c = null;

	public BitmapManage(Context context) {
		c = context;
	}

	// 载入图片
	public Bitmap LoadBitmap(String path) throws FileNotFoundException {
		//Resources res = c.getResources();
		File file=new File(path);
		FileInputStream f=new FileInputStream(file);
		InputStream is = f;
		return BitmapFactory.decodeStream(is);
	}
	/*public Bitmap LoadBitmap(int resource) {
		Resources res = c.getResources();
		InputStream is = res.openRawResource(resource);
		return BitmapFactory.decodeStream(is);
	}*/

	// Bitmap转Bytes
	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	// Bytes转Bitmap
	public Bitmap Bytes2Bitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

}
