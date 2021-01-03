package com.example;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES128 {
	public static Key getAESKey() throws Exception {
	    Key keySpec;
	
	    String key = "1234567890123456";
	    byte[] keyBytes = new byte[16];
	    byte[] b = key.getBytes("UTF-8");
	
	    int len = b.length;
	    if (len > keyBytes.length) {
	       len = keyBytes.length;
	    }
	
	    System.arraycopy(b, 0, keyBytes, 0, len);
	    keySpec = new SecretKeySpec(keyBytes, "AES");
	
	    return keySpec;
	}
	
	// 암호화
	public static String encAES(String str){
		try {
			Key keySpec = getAESKey();
	        String iv = "0987654321654321";
	        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));
	        byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
	        String enStr = new String(Base64.getEncoder().encode(encrypted));


		    return enStr;
		} catch (Exception e) {
			return null;
		}
	}
	
	// 복호화
	public static String decAES(String enStr){
		try {
	        Key keySpec = getAESKey();
	        String iv = "0987654321654321";
	        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));
	        byte[] byteStr = Base64.getDecoder().decode(enStr.getBytes("UTF-8"));
	        String decStr = new String(c.doFinal(byteStr), "UTF-8");

		
		    return decStr;
		} catch (Exception e) {
			return null;
		}
	}
}

