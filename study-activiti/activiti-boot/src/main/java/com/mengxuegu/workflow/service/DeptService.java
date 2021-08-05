package com.mengxuegu.workflow.service;

import org.springframework.stereotype.Service;

/**
 * 对应UEL表达式： ${deptService.findManagerByUserId(userId)}
 */
@Service("deptService")
public class DeptService {

    /**
     * 通过用户id查询上级领导人作为办理人
     * @param userId
     * @return
     */
    public String findManagerByUserId(String userId) {
        System.out.println("DeptService.findManagerByUserId 查询userId=" + userId + "的上级领导作为办理人");
        return "小梦";
    }

}
