package com.mengxuegu.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mengxuegu.workflow.entities.ProcessConfig;
import com.mengxuegu.workflow.utils.Result;

public interface IProcessConfigService extends IService<ProcessConfig> {

    /**
     * 查询流程配置数据
     * @param processKey 流程定义key
     * @return
     */
    ProcessConfig getByProcessKey(String processKey);

    /**
     * 删除流程配置数据
     * @param processKey 流程定义key
     * @return
     */
    Result deleteByProcessKey(String processKey);

    /**
     * 通过业务路由名查询流程定义配置信息（目的查询获取流程定义key)
     * @param businessRoute
     * @return
     */
    ProcessConfig getByBusinessRoute(String businessRoute);

}
