package com.mengxuegu.workflow.test;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ActivitiTest05ProcessInstance {

    @Autowired
    RuntimeService runtimeService;

    /**
     *  涉及到的数据表：
     *  ACT_HI_TASKINST
     *  ACT_HI_PROCINST 流程实例
     *  ACT_HI_ACTINST 节点实例表
     *  ACT_HI_IDENTITYLINK 流程实例相关办理人
     *  ACT_RU_EXECUTION
     *  ACT_RU_TASK
     *  ACT_RU_IDENTITYLINK
     */
    @Test
    public void startProcessInstance() {
        // 流程定义唯一标识key
        String processKey = "leaveProcess";
        // 业务id
        String businessKey = "10000";
        // 启动当前流程实例的用户（会保存到 act_hi_procinst 表中 start_user_id 字段中）
        Authentication.setAuthenticatedUserId("苍老师");

        // 启动流程实例（流程定义唯一标识key, 业务id),采用流程key对应的最新版本的流程定义数据
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(processKey, businessKey);

        // 将流程定义名称 作为 流程实例名称
        runtimeService.setProcessInstanceName(pi.getProcessInstanceId(), pi.getProcessDefinitionName());

        System.out.println("启动流程实例成功：" + pi.getProcessInstanceId());
    }


    /**
     * 查询正在运行中的流程实例
     * 核心表：act_ru_execution ，其中 parent_id 为空，就是正在运行的流程实例
     */
    @Test
    public void getProcInstListRunning() {

        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .processInstanceNameLike("%请假%")
                .list();
        for (ProcessInstance pi : list) {
            System.out.println("流程定义key: " + pi.getProcessDefinitionKey());
            System.out.println("流程定义版本号：" + pi.getProcessDefinitionVersion());
            System.out.println("流程实例Id: " + pi.getProcessInstanceId());
            System.out.println("流程实例名称：" + pi.getName());
            System.out.println("业务key(业务主键id)：" + pi.getBusinessKey());
            System.out.println("发起人：" + pi.getStartUserId());
            System.out.println("流程实例状态：" + ( pi.isSuspended() ? "已挂起（暂停）" : "已激活（启动）" ) );
        }
    }

    /**
     * 激活或挂起流程实例
     * 流程定义：
     *   激活：激活流程定义后，对应所有的流程实例都可以继续向下流转
     *   挂起：挂起流程定义后，对应所有的流程实例都不可以继续向下流转
     * 流程实例：
     *  激活：激活流程实例后，此流程实例可继续向下流转。
     *  挂起：挂起流程实例后，此流程实例不可以向下流转。
     */
    @Test
    public void updateProcInstState() {
        String procInstId = "c1b97f6f-7d0f-11f6-bc11-2c337a6d7e1d";
        // 1. 查询指定流程实例的数据
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procInstId)
                .singleResult();
        // 2. 判断当前流程实例的状态
        if(processInstance.isSuspended()) {
            // 如果是已挂起，则更新为激活状态
            runtimeService.activateProcessInstanceById(procInstId);
            System.out.println("激活流程实例成功");
        }else {
            // 如果是已激活，则更新为挂起状态
            runtimeService.suspendProcessInstanceById(procInstId);
            System.out.println("挂起流程实例成功");
        }


    }


    @Autowired
    HistoryService historyService;
    /**
     * 删除流程实例:
     * 涉及到到的数据表：
     * ACT_RU_IDENTITYLINK
     * ACT_RU_TASK
     * ACT_RU_EXECUTION
     */
    @Test
    public void deleteProcInst() {
        String procInstId = "11d21740-7f7d-11f6-9f0c-2c337a6d7e1d";

        // 1. 查询指定流程实例的数据
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procInstId)
                .singleResult();
        if(processInstance == null) {
            System.out.println("流程实例不存在");
            return;
        }
        // 删除流程实例（流程实例id,删除原因）, 不会删除流程实例相关历史数据
        runtimeService.deleteProcessInstance(procInstId, "xxx作废了当前流程申请");

        // 删除流程实例历史数据
        historyService.deleteHistoricProcessInstance(procInstId);
    }

}
