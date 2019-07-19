package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Kiku
 * @date 2019/7/17 13:35
 */
public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser,String> {

    public XcCompanyUser findByUserId(String userId);
}
