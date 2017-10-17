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
	     * 密钥算法
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
	     * 加密
	     *
	     * @param file  待加密数据
	     * @param key   二进制密钥
	     * @return byte[]   加密数据
	     * @throws Exception
	     */
	    public static OutputStream encrypt(File file, byte[] key) throws Exception{
	        return encrypt(file, key, DEFAULT_CIPHER_ALGORITHM);
	    }

	    /**
	     * 解密
	     *
	     * @param file  待解密数据
	     * @param key   二进制密钥
	     * @return byte[]   解密数据
	     * @throws Exception
	     */
	    public static InputStream decrypt(File file, byte[] key) throws Exception{
	        return decrypt(file, key,DEFAULT_CIPHER_ALGORITHM);
	    }


	    /**
	     * 转换密钥
	     *
	     * @param key   二进制密钥
	     * @return 密钥
	     */
	    private static Key toKey(byte[] key){
	        //生成密钥
	        return new SecretKeySpec(key, KEY_ALGORITHM);
	    }

	    /**
	     * 加密
	     *
	     * @param file  待加密数据
	     * @param key   二进制密钥
	     * @param cipherAlgorithm   加密算法/工作模式/填充方式
	     * @return byte[]   加密数据
	     * @throws Exception
	     */
	    private static OutputStream encrypt(File file, byte[] key, String cipherAlgorithm) throws Exception{
	        //还原密钥
	        Key k = toKey(key);
	        return encrypt(file, k, cipherAlgorithm);
	    }

	    /**
	     * 加密
	     *
	     * @param file  待加密数据
	     * @param key   密钥
	     * @param cipherAlgorithm   加密算法/工作模式/填充方式
	     * @return byte[]   加密数据
	     * @throws Exception
	     */
	    private static OutputStream encrypt(File file, Key key, String cipherAlgorithm) throws Exception{
	        //实例化
	        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
	        //使用密钥初始化，设置为加密模式
	        cipher.init(Cipher.ENCRYPT_MODE, key);
	        //执行操作
	        CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(file), cipher);
	        return cos;
	    }

	    /**
	     * 解密
	     *
	     * @param file  待解密数据
	     * @param key   二进制密钥
	     * @param cipherAlgorithm   加密算法/工作模式/填充方式
	     * @return byte[]   解密数据
	     * @throws Exception
	     */
	    private static InputStream decrypt(File file, byte[] key,String cipherAlgorithm) throws Exception{
	        //还原密钥
	        Key k = toKey(key);
	        return decrypt(file, k, cipherAlgorithm);
	    }

	    /**
	     * 解密
	     *
	     * @param file  待解密文件
	     * @param key   密钥
	     * @param cipherAlgorithm   加密算法/工作模式/填充方式
	     * @return byte[]   解密数据
	     * @throws Exception
	     */
	    private static InputStream decrypt(File file, Key key, String cipherAlgorithm) throws Exception{
	        //实例化
	        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
	        //使用密钥初始化，设置为解密模式
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        //执行操作
	        CipherInputStream cis = new CipherInputStream(new FileInputStream(file), cipher);
	        return cis;
	    }
}