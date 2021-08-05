package com.mengxuegu.workflow.test;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ActivitiTest09Gateway {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;


    // 启动流程实例
    @Test
    public void startProcess() {
        // 流程变量
        Map<String, Object> variables = new HashMap<>();
        // 请假天数，
        variables.put("duration", 2);
        runtimeService.startProcessInstanceByKey("testInclusiveGateway", variables);

        // 启动时不指定 duration 请假天数流程变量，在完成 直接领导审批 时指定流程变量 duration
       // runtimeService.startProcessInstanceByKey("testExclusiveGateway");
    }

    @Test
    public void findWaitTask() {
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee("meng")
                .processDefinitionKey("testExclusiveGateway")
                .list();
        for (Task task : list) {
            System.out.println("任务id:" + task.getId());
            System.out.println("任务名称:"+ task.getName());
            System.out.println("办理人:" + task.getAssignee());
        }
    }

    @Test
    public void complete() {
        String taskId = "5a17a43e-8010-11f6-9aa6-2c337a6d7e1d";
        taskService.complete(taskId);

        // 流程变量
        /*Map<String, Object> variables = new HashMap<>();
        // 请假天数，
        variables.put("duration", 5);
        taskService.complete(taskId, variables);*/
    }

}
