package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Kiku
 * @date 2019/7/17 13:33
 */
@Service
public class UcenterService {

    @Autowired
    private XcUserRepository xcUserRepository;
    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;
    @Autowired
    private XcMenuMapper xcMenuMapper;

    /**
     * 根据用户名获取用户Ext信息
     *
     * @param username
     * @return
     */
    public XcUserExt getUserExt(String username) {
        /*查询用户基本信息*/
        XcUser xcUser = this.getUserByUsername(username);
        if (xcUser == null) {
            return null;
        }
        String userId = xcUser.getId();

        /*查询用户所属公司*/
        XcCompanyUser xcCompanyUser = this.getXcCompanyUserByUserId(userId);
        String companyId = null;
        if (xcCompanyUser != null) {
            companyId = xcCompanyUser.getCompanyId();
        }
        /*查询用户权限信息*/
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(userId);

        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt); //基本信息
        xcUserExt.setPermissions(xcMenus); //权限信息
        xcUserExt.setCompanyId(companyId); //企业信息
        return xcUserExt;
    }

    /**
     * 根据用户id查询用户所属公司
     *
     * @param userId
     * @return
     */
    public XcCompanyUser getXcCompanyUserByUserId(String userId) {
        return xcCompanyUserRepository.findByUserId(userId);
    }


    /**
     * 根据用户名查询用户基本信息
     *
     * @param username
     * @return
     */
    public XcUser getUserByUsername(String username) {
        return xcUserRepository.findByUsername(username);
    }
}
