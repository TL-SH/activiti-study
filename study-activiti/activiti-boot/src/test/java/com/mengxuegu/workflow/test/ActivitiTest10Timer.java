package com.mengxuegu.workflow.test;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ActivitiTest10Timer {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;


    // 启动流程实例,测试边界定时器事件
    @Test
    public void testTimer2() throws InterruptedException {
        // 1. 启动流程实例
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("testTimer2");
        System.out.println("流程实例id:" + processInstance.getId());

        List<Task> list = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();
        for (Task task : list) {
            System.out.println("启动流程实例后的任务信息：");
            System.out.println("任务id:" + task.getId());
            System.out.println("任务名称：" + task.getName());
        }

        // 2. 等待20秒
        Thread.sleep(25 * 1000);

        list = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();
        for (Task task : list) {
            System.out.println("20秒后的任务信息：");
            System.out.println("任务id:" + task.getId());
            System.out.println("任务名称：" + task.getName());
        }
    }


    /**
     * 测试中间定时器事件
     * 1. 启动流程实例
     * 2. 完成任务一，
     * 3. 马上查询 act_ru_task表中是否有任务二，如果10秒内是没有任务二，超过10秒才有任务二
     */
    @Test
    public void testTimer3() {
        // 1. 启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testTimer3");
        System.out.println("启动实例ID:" + processInstance.getProcessInstanceId());
    }

    @Test
    public void complete() {
        // 完成任务1
        taskService.complete("04184214-8040-11f6-b401-2c337a6d7e1d");
    }


}
