package com.y.aes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class MySecret {
	
	private String filePath = Environment.getExternalStorageDirectory().getPath()+ "/test/test.jpg";  
    // AES���ܺ���ļ�  
    private static final String outPath = Environment.getExternalStorageDirectory().getPath()+ "/test/encrypt.jpg";  
    // �����ֽڼ��ܺ��ļ�  
    private static final String bytePath = Environment.getExternalStorageDirectory().getPath()+ "/test/byte.jpg";  
	
	//AES����ʹ�õ���Կ��ע�������Կ�ĳ��ȱ�����16λ  
    private static final String AES_KEY = "MyDifficultPassw";  
    //������ֽ�  
    private static final String BYTE_KEY = "MyByte";
	
	/**  
     * �����ֽڼ���  
     */  
    public  void addByte(String filePath,String bytePath){  
        try {  
            //��ȡͼƬ���ֽ���  
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);  
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
            byte[] bytes = baos.toByteArray();  
            FileOutputStream fops = new FileOutputStream(bytePath);  
            //������ֽ���  
            byte[] bytesAdd = BYTE_KEY.getBytes();  
            fops.write(bytesAdd);  
            fops.write(bytes);  
            fops.flush();  
            fops.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    /**  
     * �Ƴ�������ֽڽ���ͼƬ  
     */  
    public  Bitmap removeByte(String bytePath){  
    	Bitmap bitmap=null;
        try {  
            FileInputStream stream = null;  
            stream = new FileInputStream(new File(bytePath));  
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);  
            byte[] b = new byte[1024];  
            int n;  
            int i=0;  
            while ((n = stream.read(b)) != -1) {  
                if(i==0){  
                    //��һ��д�ļ�����ʱ���Ƴ�����֮ǰ������ֽ�  
                    out.write(b, BYTE_KEY.length(), n-BYTE_KEY.length());  
                }else{  
                    out.write(b, 0, n);  
                }  
                i++;  
            }  
            stream.close();  
            out.close();  
            //��ȡ�ֽ�����ʾͼƬ  
            byte[] bytes= out.toByteArray();  
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);  
            //img.setImageBitmap(bitmap);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
		return bitmap;  
    }  
  
    /**  
     * ʹ��AES���ܱ�׼���м���  
     */  
    public void aesEncrypt(String filePath,String outPath)  {  
        try {  
            FileInputStream fis = null;  
            fis = new FileInputStream(filePath);  
            FileOutputStream fos = new FileOutputStream(outPath);  
            //SecretKeySpec����������һ���ֽ����鹹��һ�� SecretKey  
            SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),  
                    "AES");  
            //Cipher��Ϊ���ܺͽ����ṩ���빦��,��ȡʵ��  
            Cipher cipher = Cipher.getInstance("AES");  
            //��ʼ��  
            cipher.init(Cipher.ENCRYPT_MODE, sks);  
            //CipherOutputStream Ϊ���������  
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);  
            int b;  
            byte[] d = new byte[1024];  
            while ((b = fis.read(d)) != -1) {  
                cos.write(d, 0, b);  
            }  
            cos.flush();  
            cos.close();  
            fis.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
    }  
  
    /**  
     * ʹ��AES��׼����  
     */  
    public void aesDecrypt(String outPath) {  
        try {  
            FileInputStream fis = null;  
            fis = new FileInputStream(outPath);  
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);  
            SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),  
                    "AES");  
            Cipher cipher = Cipher.getInstance("AES");  
            cipher.init(Cipher.DECRYPT_MODE, sks);  
            //CipherInputStream Ϊ����������  
            CipherInputStream cis = new CipherInputStream(fis, cipher);  
            int b;  
            byte[] d = new byte[1024];  
            while ((b = cis.read(d)) != -1) {  
                out.write(d, 0, b);  
            }  
            out.flush();  
            out.close();  
            cis.close();  
            //��ȡ�ֽ�����ʾͼƬ  
            byte[] bytes= out.toByteArray();  
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);  
            //img.setImageBitmap(bitmap);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
    }
}
