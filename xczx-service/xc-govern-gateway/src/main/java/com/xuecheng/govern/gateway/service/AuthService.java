package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Kiku
 * @date 2019/7/17 19:49
 */
@Service
public class AuthService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     * 获取cookie中的用户身份令牌
     *
     * @param request
     * @return token
     */
    public String getTokenFromCookie(HttpServletRequest request) {
        Map<String, String> cookieMap = CookieUtil.readCookie(request, "uid");
        String token = cookieMap.get("uid");
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return token;
    }

    /**
     * 从HttpHeader中获取jwt令牌
     *
     * @param request
     * @return jwt
     */
    public String getJwtFromHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            return null;
        }
        if (!authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring(7);
    }

    /**
     * 查询redis 查询用户身份令牌有效期
     *
     * @param access_token 用户身份令牌
     * @return 有效期时间
     */
    public Long getJwtExpireFromRedis(String access_token) {
        String key = "user_token:" + access_token;
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
