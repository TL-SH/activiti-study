package com.mengxuegu.workflow.test;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ActivitiTest07GroupTask {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Test
    public void startProcessInstance() {
        runtimeService.startProcessInstanceByKey("testGroupTask");
    }

    /**
     * 查询候选任务
     */
    @Test
    public void getGroupTaskList() {
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("testGroupTask")
                .taskCandidateUser("meng") // 候选人
                .list();
        for (Task task : list) {
            System.out.println("任务id:" + task.getId());
            System.out.println("任务名称：" + task.getName());
            System.out.println("办理人：" + task.getAssignee());
        }
    }

    /**
     * 拾取候选任务
     */
    @Test
    public void claimTask() {
        String taskId = "6903fa10-7e79-11f6-8210-2c337a6d7e1d";
        String userId = "meng";
        // 注意：即使拾取任务的办理人，不在候选人中，也可以进行拾取成功。
        // 所以最好在拾取之前判断是否为当前任务的候选人
        /*List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(taskId);
        for (IdentityLink identityLink : identityLinkList) {
            System.out.println(identityLink.getUserId());
        }*/

        // 拾取候选任务，将 meng 作为办理人
        taskService.claim(taskId, userId);
    }

    /**
     * 任务办理人归还组任务中
     */
    @Test
    public void assigneeToGroupTask() {
        String taskId = "6903fa10-7e79-11f6-8210-2c337a6d7e1d";
        String assignee = "meng3333";  //办理人

        // 1. 查询办理人任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("testGroupTask")
                .taskAssignee(assignee)
                .singleResult();

        // 2. 归还组任务中
        if(task != null) {
            // 直接将办理人设置为null，即归还到了组任务中
            taskService.setAssignee(task.getId(), null);
        }

    }

    /**
     * 转办任务
     */
    @Test
    public void turnTask() {
        String taskId = "6903fa10-7e79-11f6-8210-2c337a6d7e1d";
        String assignee = "meng";  //办理人

        String candidateuser = "xue"; // 转办的目标办理人

        // 1. 查询办理人任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("testGroupTask")
                .taskAssignee(assignee)
                .taskId(taskId)
                .singleResult();

        // 将任务转办给 xue 用户
        if(task != null) {
            taskService.setAssignee(task.getId(), candidateuser);
        }
    }

    @Test
    public void completeTask() {
        taskService.complete("6903fa10-7e79-11f6-8210-2c337a6d7e1d");
    }

}
