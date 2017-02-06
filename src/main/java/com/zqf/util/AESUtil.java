package com.zqf.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * @author wchun
 * 
 *         AES128 算法，加密模式为ECB，填充模式为 pkcs7（实际就是pkcs5）
 * 
 * 
 */
public class AESUtil {

	static final String algorithmStr = "AES/ECB/PKCS5Padding";
	static private KeyGenerator keyGen;
	static private Cipher cipher;
	static boolean isInited = false;

	// 初始化
	static private void init() {
		isInited = true;
		// 初始化keyGen
		try {
			keyGen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			isInited = false;
		}
		keyGen.init(128);
		// 初始化cipher
		try {
			cipher = Cipher.getInstance(algorithmStr);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			isInited = false;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			isInited = false;
		}
	}

	public static byte[] GenKey() {
		// 如果没有初始化过,则初始化
		if (!isInited) {
			init();
		}
		return keyGen.generateKey().getEncoded();
	}

	public static byte[] Encrypt(byte[] content, byte[] keyBytes) {
		byte[] encryptedText = null;
		// 未初始化
		if (!isInited) {
			init();
		}
		Key key = new SecretKeySpec(keyBytes, "AES");
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		try {
			encryptedText = cipher.doFinal(content);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return encryptedText;
	}

	// 解密为byte[]
	public static byte[] DecryptToBytes(byte[] content, byte[] keyBytes) {
		byte[] originBytes = null;
		if (!isInited) {
			init();
		}
		Key key = new SecretKeySpec(keyBytes, "AES");
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		// 解密
		try {
			originBytes = cipher.doFinal(content);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return originBytes;
	}
}