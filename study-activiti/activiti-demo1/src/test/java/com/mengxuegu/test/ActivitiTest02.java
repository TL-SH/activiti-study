package com.mengxuegu.test;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.util.ReflectUtil;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

public class ActivitiTest02 {


    /**
     * 部署流程定义:
     * ACT_RE_DEPLOYMENT 生成流程部署表
     * ACT_RE_PROCDEF 生成流程定义信息
     *  ID值组成：流程定义唯一标识key:版本号:随机标识值（如 leaveProcess:1:4 ）
     *  每次部署，针对相同的流程定义key，对应的version会自增1
     * ACT_GE_BYTEARRAY 流程资源表（.bpmn和.png资源）
     */
    @Test
    public void deployByFile() {
        // 1. 获取流程引擎实例
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        // 2. 获取部署流程定义的相关 service
        RepositoryService repositoryService = processEngine.getRepositoryService();

        // 3. 调用相关api方法进行部署
        Deployment deployment = repositoryService.createDeployment()
                .name("请假申请流程")
                .addClasspathResource("processes/leave.bpmn")
                .addClasspathResource("processes/leave.png")
                .deploy();
        // 4. 输出部署结果
        System.out.println("部署ID: " + deployment.getId());
        System.out.println("部署名称：" + deployment.getName());
    }

    /**
     * 通过zip压缩包进行部署流程定义
     */
    @Test
    public void deployByZip() {
        // 1. 获取流程引擎实例
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        // 2. 获取部署流程定义的相关 service
        RepositoryService repositoryService = processEngine.getRepositoryService();

        // 3. 部署流程定义
        // 读取zip资源压缩包，转成输入流
        InputStream inputStream = ReflectUtil.getResourceAsStream("processes/leave.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("请假申请流程-压缩包")
                .deploy();

        // 4. 输出部署结果
        System.out.println("部署ID: " + deployment.getId());
        System.out.println("部署名称：" + deployment.getName());
    }

    /**
     * 查询部署好的流程定义数据
     */
    @Test
    public void getProcessDefinitionList() {
        // 1. 获取流程引擎实例
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        // 2. 获取部署流程定义的相关 service
        RepositoryService repositoryService = processEngine.getRepositoryService();

        // 3. 获取流程定义查询对象
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> processDefinitions = query.processDefinitionKey("leaveProcess")
                .orderByProcessDefinitionVersion()
                .desc()
                .list();

        for (ProcessDefinition pd : processDefinitions) {
            System.out.print("流程部署id：" + pd.getDeploymentId());
            System.out.print("，流程定义id：" + pd.getId());
            System.out.print("，流程定义key: " + pd.getKey());
            System.out.print("，流程定义名称：" + pd.getName());
            System.out.println("，版本号：" + pd.getVersion());
        }

    }

    /**
     * 启动流程实例（提交申请），会涉及以下7张表：
    * ACT_HI_TASKINST 流程实例的历史任务信息
    * ACT_HI_PROCINST 流程实例历史数据
    * ACT_HI_ACTINST 流程实例执行的节点历史信息
    * ACT_HI_IDENTITYLINK 流程实例的参与者历史信息
    * ACT_RU_EXECUTION 流程实例运行中的执行信息
    * ACT_RU_TASK 流程实例运行中的（节点）任务信息
    * ACT_RU_IDENTITYLINK 流程实例运行中的参与者信息
     */
    @Test
    public void startProcessInstance() {
        // 1. 获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        // 2. 获取 RuntimeService 服务实例
        RuntimeService runtimeService = processEngine.getRuntimeService();

        // 3. 启动流程实例(流程定义key processDefinitionKey)
        // 通过流程定义key启动的流程实例 ，找的是最新版本的流程定义数据
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leaveProcess");

        System.out.println("流程定义id: " + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id: " + processInstance.getId());
    }


    /**
     * 查询办理人的待办任务
     */
    @Test
    public void taskListByAssignee() {
        // 1. 流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        // 2. 获取 taskService
        TaskService taskService = processEngine.getTaskService();

        // 3. 查询待办任务
        List<Task> taskList = taskService.createTaskQuery()
                //.processDefinitionKey("leaveProcess")
                .taskAssignee("meng")
                .list();
        for (Task task : taskList) {
            System.out.println("流程实例ID: " + task.getProcessInstanceId());
            System.out.println("任务id: " + task.getId());
            System.out.println("任务名称：" + task.getName());
            System.out.println("任务办理人：" + task.getAssignee());
        }
    }

    /**
     * 办理人来办理任务
     */
    @Test
    public void completeTask() {
        // 1. 流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        // 2. 获取 taskService
        TaskService taskService = processEngine.getTaskService();

        // 3. 查询待办任务 ：当前meng只有一条，则仅返回一条
        Task task = taskService.createTaskQuery()
                .taskAssignee("meng")
                .singleResult();

        // 4. 完成任务
        if(task != null) {
            taskService.complete(task.getId());
            System.out.println("完成任务");
        }else {
            System.out.println("未查询到待办任务");
        }

    }

    /**
     * 查询流程实例的办理历史节点信息
     */
    @Test
    public void historyInfo() {
        // 1. 流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        // 2. 历史相关的service
        HistoryService historyService = processEngine.getHistoryService();

        // 3. 获取查询对象
        HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery();

        // 4. 开始查询
        List<HistoricActivityInstance> list = query.processInstanceId("15001")
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


}
