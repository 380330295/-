package com.xuecheng.auth.service;

import com.xuecheng.auth.client.UcenterClient;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ClientDetailsService clientDetailsService;
    @Autowired
    UcenterClient ucenterClient;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if(authentication==null){
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if(clientDetails!=null){
                //密码
                String clientSecret = clientDetails.getClientSecret();
                return new User(username,clientSecret,AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        /*远程调用用户中心服务查询用户信息*/
        XcUserExt userext = ucenterClient.getUserExt(username);
        if (userext==null){
            //如果没有查询到用户信息, 则返回空给spring security 表示用户不存在
            return null;
        }
        /*userext.setUsername("itcast");  暂时使用静态
          userext.setPassword(new BCryptPasswordEncoder().encode("123"));
        //这里暂时使用静态密码
          String password ="123";
        //用户权限，这里暂时使用静态数据，最终会从数据库读取
        // userext.setPermissions(new ArrayList<XcMenu>());
        List<String> user_permission = new ArrayList<>();
        user_permission.add("course_get_baseinfo");
        user_permission.add("course_find_pic"); */
        //取出正确密码（hash值）
        String password = userext.getPassword();
        //取出从数据库获取的权限
        List<XcMenu> permissions = userext.getPermissions();
        if (permissions==null){
            permissions = new ArrayList<>();
        }
        List<String> user_permission = new ArrayList<>(); //用户权限code集合
        //遍历取出所有权限code信息
        permissions.forEach(item-> user_permission.add(item.getCode()));

        //将集合按"," 拼接为字符串 设置到UserJwt中
        String user_permission_string  = StringUtils.join(user_permission.toArray(), ",");
        UserJwt userDetails = new UserJwt(username,
                                            password,
                                             AuthorityUtils.commaSeparatedStringToAuthorityList(user_permission_string));
        userDetails.setId(userext.getId());
        userDetails.setUtype(userext.getUtype());//用户类型
        userDetails.setCompanyId(userext.getCompanyId());//所属企业
        userDetails.setName(userext.getName());//用户名称
        userDetails.setUserpic(userext.getUserpic());//用户头像
        return userDetails;
    }
}
