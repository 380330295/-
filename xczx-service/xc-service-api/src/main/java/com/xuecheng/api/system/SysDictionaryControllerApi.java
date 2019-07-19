package com.xuecheng.api.system;


import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Kiku
 * @date 2019/7/4 8:42
 */
@Api(value = "数据字典接口", description = "提供数据字典查询")
public interface SysDictionaryControllerApi {

    @ApiOperation("查询数据字典")
    @ApiImplicitParam(name = "dType", value = "类型id", required = true, paramType = "path", dataType = "String")
    public SysDictionary getDictionary(String dType);


}
