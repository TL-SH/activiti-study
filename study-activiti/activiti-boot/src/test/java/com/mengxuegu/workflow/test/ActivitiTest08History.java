package com.mengxuegu.workflow.test;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ActivitiTest08History {

    @Autowired
    HistoryService historyService;

    /**
     * 查询指定用户的已经办理任务
     * select distinct RES.* from ACT_HI_TASKINST RES
     * WHERE RES.ASSIGNEE_ = ? and RES.END_TIME_ is not null
     * order by RES.START_TIME_ desc
     *
     * // 查询了流程变量信息
     * select distinct RES.*, VAR.ID_ as VAR_ID_, VAR.NAME_ as VAR_NAME_, VAR.VAR_TYPE_ as VAR_TYPE_, VAR.REV_ as VAR_REV_, VAR.PROC_INST_ID_ as VAR_PROC_INST_ID_,
     * VAR.EXECUTION_ID_ as VAR_EXECUTION_ID_, VAR.TASK_ID_ as VAR_TASK_ID_,
     * VAR.BYTEARRAY_ID_ as VAR_BYTEARRAY_ID_, VAR.DOUBLE_ as VAR_DOUBLE_,
     * VAR.TEXT_ as VAR_TEXT_, VAR.TEXT2_ as VAR_TEXT2_,
     * VAR.LAST_UPDATED_TIME_ as VAR_LAST_UPDATED_TIME_, VAR.LONG_ as VAR_LONG_
     * from ACT_HI_TASKINST RES
     * left outer join ACT_HI_VARINST VAR
     * ON RES.PROC_INST_ID_ = VAR.EXECUTION_ID_
     * and VAR.TASK_ID_ is null
     * WHERE RES.ASSIGNEE_ = ? and RES.END_TIME_ is not null order by RES.START_TIME_ desc, VAR.LAST_UPDATED_TIME_ asc LIMIT ? OFFSET ?
     */
    @Test
    public void findCompleteTask() {
        // 查询
        List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee("meng") // 办理人
                .includeProcessVariables()
                .orderByTaskCreateTime()
                .desc() // 任务创建时间降序排列
                .finished()
                .list();
        for (HistoricTaskInstance task : taskList) {
            System.out.print(" 任务ID: " + task.getId());
            System.out.print(" ,任务名称: " + task.getName());
            System.out.print(" ,任务开始时间: " + task.getStartTime());
            System.out.print(" ,任务结束时间: " + task.getEndTime());
            System.out.print(" ,办理人: " + task.getAssignee());
            System.out.print(" ,流程定义id: " + task.getProcessDefinitionId());
            System.out.print(" ,流程实例id: " + task.getProcessInstanceId());
            System.out.print(" ,业务id: " + task.getBusinessKey());
            System.out.println("，流程变量：" + task.getProcessVariables());
        }

    }

    /**
     * 查询流程实例的办理历史节点信息
     */
    @Test
    public void historyInfo() {
        // 1. 获取查询对象
        HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery();

        // 2. 开始查询
        List<HistoricActivityInstance> list = query.processInstanceId("6a9fed2b-7e6d-11f6-946b-2c337a6d7e1d")
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();
        for (HistoricActivityInstance hi : list) {
            System.out.print("流程定义id：" + hi.getProcessDefinitionId());
            System.out.print("，流程实例Id: " + hi.getProcessInstanceId());
            System.out.print("，节点id: " + hi.getActivityId());
            System.out.print("，节点名称：" + hi.getActivityName());
            System.out.println("，任务办理人：" + hi.getAssignee());
        }
    }

    /**
     * 查询已经结束的流程实例
     */
    @Test
    public void getProcInstListFinish() {

        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery()
                .orderByProcessInstanceEndTime()
                .desc()
                .finished()
                .list();

        for (HistoricProcessInstance hpi : list) {
            System.out.print("流程实例id: " + hpi.getId());
            System.out.print("，流程实例名称：" + hpi.getName());
            System.out.print(", 流程标识key:" + hpi.getProcessDefinitionKey());
            System.out.print(", 流程定义版本号:" + hpi.getProcessDefinitionVersion());
            System.out.print(", 业务id:" + hpi.getBusinessKey());
            System.out.print(", 流程发起人:" + hpi.getStartUserId());
            System.out.print(", 开始时间:" + hpi.getStartTime());
            System.out.print(", 结束时间:" + hpi.getEndTime());
            System.out.println(", 删除原因详情:" + hpi.getDeleteReason());
        }
    }

    /**
     * 删除已结束的流程实例
     * ACT_HI_DETAIL
     * ACT_HI_VARINST
     * ACT_HI_TASKINST
     * ACT_HI_PROCINST
     * ACT_HI_ACTINST
     * ACT_HI_IDENTITYLINK
     * ACT_HI_COMMENT
     */
    @Test
    public void deleteFinishProcInst() {
        String procInstId = "b355a0f9-7d23-11f6-9986-2c337a6d7e1d";
        // 1. 查询流程实例是否已结束
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(procInstId)
                .finished()
                .singleResult();
        if(instance == null) {
            System.out.println("流程实例未结束或不存在");
            return;
        }
        // 2. 删除已结束流程实例(如果实例未结束，会抛出异常，如果需要删除，参考com.mengxuegu.workflow.test.ActivitiTest05ProcessInstance.deleteProcInst）
        historyService.deleteHistoricProcessInstance(procInstId);
    }

}
