package com.mengxuegu.workflow.test;

import com.mengxuegu.workflow.entities.User;
import org.activiti.bpmn.model.*;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.util.CollectionUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ActivitiTest06Task {

    @Autowired
    TaskService taskService;

    @Autowired
    RuntimeService runtimeService;

    /**
     * 查询指定办理人或候选人的待办任务
     * select distinct RES.* from ACT_RU_TASK RES left join ACT_RU_IDENTITYLINK I on I.TASK_ID_ = RES.ID_ WHERE (RES.ASSIGNEE_ = ? or (RES.ASSIGNEE_ is null and I.TYPE_ = 'candidate' and (I.USER_ID_ = ? or I.GROUP_ID_ IN ( ? ) ))) order by RES.CREATE_TIME_ desc LIMIT ? OFFSET ?
     */
    @Test
    public void findWaitTask() {
        // 办理人
        String assignee = "meng";
        // 查询个人待办任务
        List<Task> list = taskService.createTaskQuery()
                //.taskAssignee(assignee) // 具体办理人
                .taskCandidateOrAssigned(assignee) // 作为候选人或者是办理人
                .orderByTaskCreateTime()
                .desc()
                .list();
        for (Task task : list) {
            System.out.println("流程实例ID: " + task.getProcessInstanceId());
            System.out.println("任务id: " + task.getId());
            System.out.println("任务名称：" + task.getName());
            System.out.println("任务办理人：" + task.getAssignee());
        }
    }



    /**
     * 启动流程实例：分配流程定义中的流程变量值（节点任务就可以获取到办理人）
     */
    @Test
    public void startProcInstSetAssigneeUEL() {
        // 流程变量
        Map<String, Object> variables = new HashMap<>();
        // 流程变量值：基本数据类型，JavaBean, Map, List，。。。等
        variables.put("assigness1", "meng");

        // User user = new User("xue");
        // variables.put("user", user);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", "xue");
        variables.put("user", userMap);

        // 使用在 DeptService.findManagerByUserId(userId) 作为参数值
        variables.put("userId", "123456");

        // 启动流程实例（流程定义key, 业务id, 流程变量）
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leaveProcess", "8888", variables);
        //runtimeService.startProcessInstanceByKey("leaveProcess",  variables);

        System.out.println("启动流程实例成功：" + processInstance.getProcessInstanceId());
    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask() {
        String taskId = "cdbdce28-7e6e-11f6-9939-2c337a6d7e1d";
        taskService.complete(taskId);
    }

    @Autowired
    RepositoryService repositoryService;

    /**
     * 1. 查询下一个节点是什么类型的节点：如果是人工任务节点，则返回，返回后前端显示一个指定一下节办理人输入框，
     * 如果下一节点是其他类型节点，则不弹出指定下一节点办理人
     *
     */
    @Test
    public void getNextNodeInfo() {
        // 1. 获取当前任务信息
        String taskId = "3c78aefb-7e73-11f6-a84b-2c337a6d7e1d";
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 2. 从当前任务信息中获取此流程定义id，
        String processDefinitionId = task.getProcessDefinitionId();
        // 3. 拿到流程定义id后可获取此bpmnModel对象
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        // 4. 通过任务节点id，来获取当前节点信息
        FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());

        // 获取当前节点的连线信息
        List<SequenceFlow> outgoingFlows = ((FlowNode) flowElement).getOutgoingFlows();

        // 当前节点的所有下一节点出口
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            // 下一节点的目标元素
            FlowElement nextFlowElement = outgoingFlow.getTargetFlowElement();
            if(nextFlowElement instanceof UserTask) {
                System.out.println("节点id: " + nextFlowElement.getId() );
                System.out.println("节点名称：" + nextFlowElement.getName() );
            }else if(nextFlowElement instanceof EndEvent) {
                break;
            }
        }
    }

    /**
     * 2. 完成当前节点时，设置下一节点任务办理人（上面输入框指定的那个办理人）
     */
    @Test
    public void completeTaskSetNextAssignee() {
        // 1. 查询当前任务
        String taskId = "3c78aefb-7e73-11f6-a84b-2c337a6d7e1d";
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        // 2. 完成当前任务
        taskService.complete(taskId);

        // 3. 查询下一任务
        List<Task> taskList = taskService.createTaskQuery()
                .processInstanceId(task.getProcessInstanceId())
                .list();

        // 4. 设置下一任务办理人
        if(CollectionUtil.isNotEmpty(taskList)) {
            for (Task t : taskList) {
               // 分配任务办理人
                taskService.setAssignee(t.getId(), "小谷");
            }
        }

    }

}
