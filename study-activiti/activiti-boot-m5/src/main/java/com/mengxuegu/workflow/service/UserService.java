package com.mengxuegu.workflow.service;

import org.springframework.stereotype.Service;

/**
 * 匹配 ${userService.getUsername()}
 */
@Service("userService")
public class UserService {

    /**
     * 返回办理人
     * @return gu
     */
    public String getUsername() {
        return "gu";
    }




}
