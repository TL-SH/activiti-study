package com.mengxuegu.workflow.test;

import com.mengxuegu.workflow.config.SecurityUtil;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * activiti7 新特性的：任务管理API
 */
@SpringBootTest
public class ActivitiTest13TaskRuntime {

    @Autowired
    TaskRuntime taskRuntime;

    @Autowired
    SecurityUtil securityUtil;

    /**
     * 查询当前用户的待办任务
     */
    @Test
    public void getWaitTaskList() {
        securityUtil.logInAs("xue");
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 1));
        System.out.println("当前用户总的待办任务数：" + tasks.getTotalItems());
        for (Task task : tasks.getContent()) {
            System.out.println("任务名称： " + task.getName());
            System.out.println("办理人：" + task.getAssignee());
        }
    }

    /**
     * 完成自己的任务
     */
    @Test
    public void completeTask() {
        securityUtil.logInAs("xue");

        String taskId = "af4e392f-f767-11f5-9712-2c337a6d7e1d";
        taskRuntime.complete(
                TaskPayloadBuilder.complete()
                .withTaskId(taskId)
                .build()
        );
    }

    /**
     * 拾取与归还任务：任务组（多个候选人或候选组）
     */
    @Test
    public void taskGroup() {
        securityUtil.logInAs("meng");
        String taskId = "d6e5f856-f76d-11f5-87cf-2c337a6d7e1d";
        // 1. 拾取任务
        /*taskRuntime.claim(
                TaskPayloadBuilder.claim()
                .withTaskId(taskId)
                .build()
        );*/
        // 2. 归还任务到任务组中
        taskRuntime.release(
                TaskPayloadBuilder.release()
                        .withTaskId(taskId)
                        .build()
        );

    }

    /**
     * 完成候选组任务
     */
    @Test
    public void completeTaskGroup() {
        securityUtil.logInAs("gu");
        //1. 查询任务
        String taskId = "d6e5f856-f76d-11f5-87cf-2c337a6d7e1d";
        Task task = taskRuntime.task(taskId);
        if(task == null) {
            System.out.println("不是你的任务");
            return;
        }
        // 2. 判断是否有办理人，没有办理人，则拾取任务
        if(StringUtils.isEmpty(task.getAssignee() )) {
            // 拾取任务
            taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(taskId).build());
        }
        // 3. 完成任务
        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskId).build());
    }

}
