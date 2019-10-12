package com.cochan.blog.common;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author cochan
 */
@Component
public class HashUtil {
    /**
     * SHA-265加密
     */
    public static String getHashPass(long uid, String username, String password, String salt, long created) {
        if (uid == 0 || username == null || password == null || salt == null || created == 0) {
            return null;
        }
        String str = "uid=" + uid + "username=" + username + "password=" + password + "salt=" + salt + "created=" + created;
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodeStr;


    }

    /**
     * 将byte转16进制
     */
    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
