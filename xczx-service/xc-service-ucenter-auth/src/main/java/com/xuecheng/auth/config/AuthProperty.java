package com.xuecheng.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Kiku
 * @date 2019/7/16 20:53
 */

@Data
@ConfigurationProperties(prefix = "auth")
public class AuthProperty {
    private String tokenValiditySeconds;
    private String clientId;
    private String clientSecret;
    private String cookieDomain;
    private int cookieMaxAge;
}
