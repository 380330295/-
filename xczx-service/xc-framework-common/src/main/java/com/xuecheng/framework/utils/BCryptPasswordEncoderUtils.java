package com.xuecheng.framework.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Kiku
 * @date 2019/6/10 15:43
 */
public class BCryptPasswordEncoderUtils {
    private static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    public static String passwordEncoder(String password){
        return bCryptPasswordEncoder.encode(password);
    }

    public static void main(String[] args) {
         System.out.println(passwordEncoder("sunwei250"));
         //$2a$10$DcI0IfPft6Yji4VxsE3elua1ixKZtUEodrpsJNEY82Gn.wfeIwVvG
    }
}
