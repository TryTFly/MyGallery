package com.y.aes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MyYH {
	
	public static final int XOR_CONST = 0X99; // 异或密钥
	
	/** 
     * 文件流异或加密 
     * @param src 
     * @param dest 
     * @throws Exception 
     */  
    public  void encryptImg(File load, File result) throws Exception {  
        FileInputStream fis = new FileInputStream(load);  
        FileOutputStream fos = new FileOutputStream(result);  
  
        int read;  
        while ((read = fis.read()) > -1) {  
            fos.write(read ^ XOR_CONST);    //进行异或运算并写入结果  
        }  
        fos.flush();  
        fos.close();  
        fis.close();  
    }
	
	/** 
     * 异或解密图片方法 
     * @param context 
     * @param fileName 
     * @return 
     */  
    public  Bitmap readBitmap(String file) {  
        Bitmap bitmap = null;  
        List<Byte> list = new ArrayList<Byte>();  
        try {  
        	File f=new File(file); 
        	InputStream is=new FileInputStream(f);
            int read;  
            while ((read = is.read()) > -1) {  
                read = read ^ 0X99;     // 密钥  
                list.add((byte) read);  
            }  
  
            byte[] arr = new byte[list.size()];  
            for (int i = 0; i < arr.length; i++) {  
                arr[i] = (Byte) list.get(i);    //list转成byte[]  
            }  
            //通过byte数组获取二进制图片流  
            bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);  
            System.out.println(bitmap);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return bitmap;  
    }
}
