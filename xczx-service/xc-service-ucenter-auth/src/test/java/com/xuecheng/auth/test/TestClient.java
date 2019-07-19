package com.xuecheng.auth.test;

import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Map;

/**
 * @author Kiku
 * @date 2019/7/16 19:46
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {

    @Autowired
    LoadBalancerClient loadBalancerClient;  //客户端负载均衡器

    @Autowired
    RestTemplate restTemplate;

    //测试远程调用spring security
    @Test
    public void testClient() {
        //从eureka 中获取认证服务的一个实例地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri(); //此地址 http://ip:port
        //令牌申请的地址  http://ip:port/auth/oauth/token
        String authUrl = uri + "/auth/oauth/token";

        //定义headers
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", getHttpBasic("XcWebApp","XcWebApp"));

        //定义body
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");  //密码模式授权
        body.add("username","itcast");
        body.add("password","123");
        //请求携带的信息
        HttpEntity<MultiValueMap<String, String>> httpEntity =
                new HttpEntity<>(body, headers);

        //设置restTemplate远程调用时候的错误处理 让其对400 和401 不保存 返回信息
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode()!=400&&response.getRawStatusCode()!=401)
                super.handleError(response);
            }
        });


        /*
          参数1 : 令牌申请的地址
          参数2 : 请求方式
          参数3 : 请求携带的信息
          参数4 : 响应消息的类型
         */
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);

        //申请令牌信息
        Map map = exchange.getBody();
        System.out.println(map);
    }

    //获取httpBasic的字符串
    private String getHttpBasic(String clientId, String clientSecret) {
        //客户端id : 客户端密码
        String str = clientId + ":" + clientSecret;
        byte[] encode = Base64Utils.encode(str.getBytes());
        return "Basic " + new String(encode);

    }

}
