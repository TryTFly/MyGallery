package com.y.des;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileIO {
	
	/**
	 * ���ֽ�����Ϊ�ļ�
	 * @param bytes
	 * @param fileName �ļ���ַ���ļ���������"/sdcard/a.txt��
	 */

	public static void saveFileByBytes(byte[] bytes,String fileName) {
		try {
			File saveFile = new File(fileName);
			FileOutputStream outStream = new FileOutputStream(saveFile);
			outStream.write(bytes);
			outStream.close();
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}
	
	/**
	 * ���ֽ�����ʽ��ȡ�ļ�
	 * @param fileName �ļ���ַ���ļ���������"/sdcard/a.txt��
	 * @return
	 */
	public static byte[] loadFile2Bytes(String fileName)
	{
	    try {

	        File file = new File(fileName); 
            FileInputStream inStream = new FileInputStream(file); 
	        ByteArrayOutputStream stream=new ByteArrayOutputStream();
	        long l = file.length();//���ֵ2147483647
	        byte[] buffer=new byte[(int) l];
	        int length=-1;
	        while((length=inStream.read(buffer))!=-1)   {
	            stream.write(buffer,0,length);
	        }
	        stream.close();
	        inStream.close();
	        return buffer;
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	        return null;
	    }
	    catch (IOException e){
	        e.printStackTrace();
	        return null;
	    }
	}
}
