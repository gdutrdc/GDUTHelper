package com.rdc.gduthelper.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * <p/>
 * Created by seasonyuu on 16-4-18.
 */
public class DESUtils {
	public static String encrypt(String text) {
		String result = null;
		try {
			result = HexUtils.encodeHexStr(encrypt(Key.DES_KEY.getBytes(), text.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String decrypt(String text) {
		String result = null;
		try {
			result = new String(decrypt(HexUtils.decodeHex(text.toCharArray()),
					Key.DES_KEY.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 加密
	 *
	 * @param rawKeyData 秘钥
	 * @param data       要加密的明文
	 * @return
	 * @throws Exception
	 */
	private static byte[] encrypt(byte rawKeyData[], byte[] data)
			throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(rawKeyData);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, key, sr);
		// 现在，获取数据并加密
		// 正式执行加密操作
		byte[] encryptedData = cipher.doFinal(data);
		return encryptedData;
	}

	/**
	 * 解密
	 *
	 * @param src      byte[] 要解密的密文
	 * @param password String 秘钥
	 * @return byte[]
	 * @throws Exception
	 */
	private static byte[] decrypt(byte[] src, byte[] password) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password);
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 真正开始解密操作
		return cipher.doFinal(src);
	}


}
