package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.config.AuthProperty;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Kiku
 * @date 2019/7/16 20:56
 */
@EnableConfigurationProperties(AuthProperty.class)
@RestController
@RequestMapping("/")
public class AuthController implements AuthControllerApi {

    @Autowired
    private AuthProperty authProperty;
    @Autowired
    private AuthService authService;

    /**用户登录
     * @param loginRequest
     * @return
     */
    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {
        //校验用户名
        if (StringUtils.isEmpty(loginRequest.getUsername())) {
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }
        //校验密码
        if (StringUtils.isEmpty(loginRequest.getPassword())) {
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        //用户名
        String username = loginRequest.getUsername();
        //密码
        String password = loginRequest.getPassword();

        /*申请令牌*/
        AuthToken authToken = authService.login(username, password, authProperty.getClientId(), authProperty.getClientSecret());
        //用户身份令牌
        String access_token = authToken.getAccess_token();
        /*将用户身份令牌存储到cookie*/
        this.saveCookie(access_token);

        return new LoginResult(CommonCode.SUCCESS, access_token);
    }

    /**
     * 用户退出
     * @return
     */
    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {
        //取出身份令牌
        String access_token = this.getTokenFormCookie();
        //删除redis中令牌的缓存
        authService.delToken(access_token);

        //清除cookie
        this.cleanCookie(access_token);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 获取用户jwt令牌
     * @return
     */
    @Override
    @GetMapping("/userjwt")
    public JwtResult queryUserJwt() {

        /*获取cookie中的令牌*/
        String access_token = this.getTokenFormCookie();
        if (StringUtils.isEmpty(access_token)) {
            return new JwtResult(CommonCode.FAIL, null);
        }
        /*从redis中查询*/
        AuthToken authToken = authService.getUserToken(access_token);
        return new JwtResult(CommonCode.SUCCESS, authToken.getJwt_token());
    }


    /*将用户身份令牌存储到cookie*/
    private void saveCookie(String token) {
        //获取response对象
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //使用工具类存储cookie
        CookieUtil.addCookie(response, authProperty.getCookieDomain(),
                "/", "uid", token,
                authProperty.getCookieMaxAge(), false);
    }

    /*获取cookie中的用户身份令牌*/
    private String getTokenFormCookie() {
        //获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
       //工具类获取cookie
        Map<String, String> cookieMap = CookieUtil.readCookie(request, "uid");
        return cookieMap.get("uid");
    }

    /*清除cookie中的用户身份令牌*/
    private void cleanCookie(String token) {
        //获取response对象
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //使用工具类存储cookie
        CookieUtil.addCookie(response, authProperty.getCookieDomain(),
                "/", "uid", token,
                0, false);
    }


}
