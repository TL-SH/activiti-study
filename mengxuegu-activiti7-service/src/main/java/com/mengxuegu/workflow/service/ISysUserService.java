package com.mengxuegu.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mengxuegu.workflow.entities.SysUser;

public interface ISysUserService extends IService<SysUser> {

    SysUser findByUsername(String username);

}
