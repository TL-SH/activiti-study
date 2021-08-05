package com.mengxuegu.workflow.test;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ActivitiTest11Mail {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    /**
     * 测试发送邮件任务
     * 1. 部署流程定义
     * 2. 启动流程实例
     * 3. 完成领导审批任务，指定相关邮件流程变量值
     * 4. 查看对应邮件收件人是否收到相关邮件
     */
    @Test
    public void testMail() {
        // 1. 启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testMail");

        // 2. 完成领导审批任务，指定相关邮件流程变量值
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getProcessInstanceId())
                .taskAssignee("meng")
                .singleResult();
        Map<String, Object> variables = new HashMap<>();
        variables.put("currName", task.getAssignee());
        variables.put("username", "梦老师");
        taskService.complete(task.getId(), variables);

        // 3. 完成任务后，根据流程定义配置会自动发送邮件。
        // 去查看对应邮件收件人是否收到相关邮件

    }


}
