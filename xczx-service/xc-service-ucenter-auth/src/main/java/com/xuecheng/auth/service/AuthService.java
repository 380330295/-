package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import io.micrometer.core.instrument.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Kiku
 * @date 2019/7/16 20:58
 */
@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Value("${auth.tokenValiditySeconds}")
    private long tokenValiditySeconds;

    /**
     * 登录 :请求spring security申请令牌并保存令牌信息至redis
     * @param username 用户名
     * @param password  密码
     * @param clientId  客户端id
     * @param clientSecret  客户端密码
     * @return
     */
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        /*请求spring security申请令牌*/
        AuthToken authToken = this.applyToken(username, password, clientId, clientSecret);
        if (authToken == null) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        //用户身份令牌
        String access_token = authToken.getAccess_token();
        //AuthToken内容 转换为json格式存储
        String authTokenJsonString = JSON.toJSONString(authToken);
        /*向redis存储令牌信息*/
        boolean result = this.saveToken(access_token, authTokenJsonString, tokenValiditySeconds);
        if (!result) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVE_FAIL);
        }
        return authToken;
    }


    /**
     * 申请令牌
     *
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        //从eureka 中获取认证服务的一个实例地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri(); //此地址 http://ip:port
        //令牌申请的地址  http://ip:port/auth/oauth/token
        String authUrl = uri + "/auth/oauth/token";

        //定义headers
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", this.getHttpBasic(clientId, clientSecret));

        //定义body
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");  //密码模式授权
        body.add("username", username);
        body.add("password", password);
        //请求携带的信息
        HttpEntity<MultiValueMap<String, String>> httpEntity =
                new HttpEntity<>(body, headers);

        //设置restTemplate远程调用时候的错误处理 让其对400 和401 不保存 返回信息
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401)
                    super.handleError(response);
            }
        });

        /* 远程调用: spring security 获取令牌信息
        参数1 : 令牌申请的地址 参数2 : 请求方式  参数3 : 请求携带的信息  参数4 : 响应消息的类型 */
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);
        //申请令牌信息
        Map map = exchange.getBody();

        if (map == null ||
                map.get("jti") == null ||
                map.get("access_token") == null ||
                map.get("refresh_token") == null) {

            if (map!=null&&map.get("error_description")!=null){
                String error_description = (String) map.get("error_description");
                if (error_description.contains("坏的凭证")){
                    ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
                }else if (error_description.contains("UserDetailsService returned null")){
                    ExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
                }
            }

            return null;
        }

        AuthToken authToken = new AuthToken();
        authToken.setAccess_token((String) map.get("jti"));
        authToken.setRefresh_token((String) map.get("refresh_token"));
        authToken.setJwt_token((String) map.get("access_token"));
        return authToken;
    }

    /* 获取httpBasic的字符串*/
    private String getHttpBasic(String clientId, String clientSecret) {
        //客户端id : 客户端密码
        String str = clientId + ":" + clientSecret;
        byte[] encode = Base64Utils.encode(str.getBytes());
        return "Basic " + new String(encode);
    }


    /**  往redis中保存令牌
     * @param access_token 用户身份令牌
     * @param content      AuthToken内容  <json格式>
     * @param ttl          过期时间
     * @return
     */
    private boolean saveToken(String access_token, String content, long ttl) {
        String key = "user_token:" + access_token;
        stringRedisTemplate.boundValueOps(key).set(content, ttl, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key);
        return expire > 0;
    }


    /**  从redis中删除令牌
     * @param access_token 用户身份令牌
     * @return
     */
    public boolean delToken(String access_token) {
        String key = "user_token:" + access_token;
        stringRedisTemplate.delete(key);
        Long expire = stringRedisTemplate.getExpire(key);
        return expire < 0;
    }

    /**从redis中查询令牌
     * @param access_token 用户身份令牌
     * @return
     */
    public AuthToken getUserToken(String access_token) {
        //拼接redis中存储的key
       String key = "user_token:" + access_token;

        /*从redis中查询令牌*/
        String jsonAuthToken = stringRedisTemplate.opsForValue().get(key);

        try {
            //解析json  AuthToken内容  并返回
            return JSON.parseObject(jsonAuthToken, AuthToken.class);
        } catch (Exception e) {
            //如果解析出错返回空
            e.printStackTrace();
            return null;
        }
    }

}
