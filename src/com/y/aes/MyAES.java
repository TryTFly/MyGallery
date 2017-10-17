package com.y.aes;

import android.content.Context;

	import javax.crypto.Cipher;
	import javax.crypto.CipherInputStream;
	import javax.crypto.CipherOutputStream;
	import javax.crypto.spec.SecretKeySpec;
	import java.io.*;
	import java.security.Key;
	
public class MyAES {

	    /**
	     * ��Կ�㷨
	     */
	    private static final String KEY_ALGORITHM = "AES";

	    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	    public static byte[] get(Context context){
	        try {
	            InputStream is = context.getAssets().open("aes.key");
	            byte[] k = new byte[16];
	            is.read(k);
	            return k;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    /**
	     * ����
	     *
	     * @param file  ����������
	     * @param key   ��������Կ
	     * @return byte[]   ��������
	     * @throws Exception
	     */
	    public static OutputStream encrypt(File file, byte[] key) throws Exception{
	        return encrypt(file, key, DEFAULT_CIPHER_ALGORITHM);
	    }

	    /**
	     * ����
	     *
	     * @param file  ����������
	     * @param key   ��������Կ
	     * @return byte[]   ��������
	     * @throws Exception
	     */
	    public static InputStream decrypt(File file, byte[] key) throws Exception{
	        return decrypt(file, key,DEFAULT_CIPHER_ALGORITHM);
	    }


	    /**
	     * ת����Կ
	     *
	     * @param key   ��������Կ
	     * @return ��Կ
	     */
	    private static Key toKey(byte[] key){
	        //������Կ
	        return new SecretKeySpec(key, KEY_ALGORITHM);
	    }

	    /**
	     * ����
	     *
	     * @param file  ����������
	     * @param key   ��������Կ
	     * @param cipherAlgorithm   �����㷨/����ģʽ/��䷽ʽ
	     * @return byte[]   ��������
	     * @throws Exception
	     */
	    private static OutputStream encrypt(File file, byte[] key, String cipherAlgorithm) throws Exception{
	        //��ԭ��Կ
	        Key k = toKey(key);
	        return encrypt(file, k, cipherAlgorithm);
	    }

	    /**
	     * ����
	     *
	     * @param file  ����������
	     * @param key   ��Կ
	     * @param cipherAlgorithm   �����㷨/����ģʽ/��䷽ʽ
	     * @return byte[]   ��������
	     * @throws Exception
	     */
	    private static OutputStream encrypt(File file, Key key, String cipherAlgorithm) throws Exception{
	        //ʵ����
	        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
	        //ʹ����Կ��ʼ��������Ϊ����ģʽ
	        cipher.init(Cipher.ENCRYPT_MODE, key);
	        //ִ�в���
	        CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(file), cipher);
	        return cos;
	    }

	    /**
	     * ����
	     *
	     * @param file  ����������
	     * @param key   ��������Կ
	     * @param cipherAlgorithm   �����㷨/����ģʽ/��䷽ʽ
	     * @return byte[]   ��������
	     * @throws Exception
	     */
	    private static InputStream decrypt(File file, byte[] key,String cipherAlgorithm) throws Exception{
	        //��ԭ��Կ
	        Key k = toKey(key);
	        return decrypt(file, k, cipherAlgorithm);
	    }

	    /**
	     * ����
	     *
	     * @param file  �������ļ�
	     * @param key   ��Կ
	     * @param cipherAlgorithm   �����㷨/����ģʽ/��䷽ʽ
	     * @return byte[]   ��������
	     * @throws Exception
	     */
	    private static InputStream decrypt(File file, Key key, String cipherAlgorithm) throws Exception{
	        //ʵ����
	        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
	        //ʹ����Կ��ʼ��������Ϊ����ģʽ
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        //ִ�в���
	        CipherInputStream cis = new CipherInputStream(new FileInputStream(file), cipher);
	        return cis;
	    }
}