package com.y.adapter;

import java.util.ArrayList;

import com.y.aes.MySecret;
import com.y.aes.MyYH;
import com.y.bitmap.CompressBitmap;
import com.y.des.BitmapManage;
import com.y.des.DES_Algorithm;
import com.y.des.FileIO;
import com.y.mygallery.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class SecretAdapter extends BaseAdapter{

	private ArrayList<String> arrayList;
	private LayoutInflater inflater;
	private String name;
	public  Bitmap bitmap=null;
	private MySecret mysecret;
	private MyYH myYH;
	private Context context;
	final String KEY = "QCLZ-KEY";
	private BitmapManage manage;
	public static SecretAdapter instance;
	
	public SecretAdapter(Context context,ArrayList<String> arg1) {
		arrayList=arg1;
		inflater=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO �Զ����ɵķ������
		return arrayList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO �Զ����ɵķ������
		return arrayList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO �Զ����ɵķ������
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO �Զ����ɵķ������
	
		ImageView imageView = null;
		name=arrayList.get(arg0);

		arg1=inflater.inflate(R.layout.secret_model, null);
		imageView=(ImageView)arg1.findViewById(R.id.secretimv);
		Log.d("SecretAdapter", name);
		//bitmap=getImageThumbnail(name, 96, 96);
		
		manage=new BitmapManage(context);
		//���ؼ��ܺ���ֽ����ļ�
		byte[] b=FileIO.loadFile2Bytes(name);
		//����
		try {
			b=DES_Algorithm.decrypt(b, KEY);
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		//���ֽ���ת��Ϊλͼ����ʾ����
		try{
		bitmap=manage.Bytes2Bitmap(b);
		//bitmap=CompressBitmap.getZoomImage(bitmap, 100);
		bitmap=CompressBitmap.bitmapScale(bitmap, 0.05f);
		imageView.setImageBitmap(bitmap);
		}catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return arg1;
	}
	
	//��ϵͳ��ȡ����ͼ
	 public static Bitmap getImageThumbnail(String imagePath, int width, int height)
	 {
	 Bitmap bitmap = null;
	 BitmapFactory.Options options = new BitmapFactory.Options();
	 options.inJustDecodeBounds = true;
	 // ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull
	 bitmap = BitmapFactory.decodeFile(imagePath, options);
	 options.inJustDecodeBounds = false; // ��Ϊ false
	 // �������ű�
	 int h = options.outHeight;
	 int w = options.outWidth;
	 int beWidth = w / width;
	 int beHeight = h / height;
	 int be = 1;
	 if (beWidth < beHeight)
	 {
	  be = beWidth;
	 }
	 else
	 {
	  be = beHeight;
	 }
	 if (be <= 0)
	 {
	  be = 1;
	 }
	 options.inSampleSize = be;
	 // ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false
	 bitmap = BitmapFactory.decodeFile(imagePath, options);
	 // ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����
	 bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
	  ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	 return bitmap;
	 }

}
