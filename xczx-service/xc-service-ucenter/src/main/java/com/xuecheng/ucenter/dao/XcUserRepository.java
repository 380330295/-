package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Kiku
 * @date 2019/7/17 13:35
 */
public interface XcUserRepository extends JpaRepository<XcUser,String> {

    public XcUser findByUsername(String username);

}
