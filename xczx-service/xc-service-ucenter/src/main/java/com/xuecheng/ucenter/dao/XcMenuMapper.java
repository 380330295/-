package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Kiku
 * @date 2019/7/18 12:08
 */
@Mapper
public interface XcMenuMapper {
    public List<XcMenu> selectPermissionByUserId(String userId);
}
