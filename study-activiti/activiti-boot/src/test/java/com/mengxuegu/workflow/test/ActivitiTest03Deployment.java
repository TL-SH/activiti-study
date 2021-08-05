package com.mengxuegu.workflow.test;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.zip.ZipInputStream;

@SpringBootTest
public class ActivitiTest03Deployment {

    @Autowired
    RepositoryService repositoryService;

    /**
     * 通过zip压缩包进行部署流程定义
     */
    @Test
    public void deployByZip() throws Exception {

        File file = new File("D:/请假流程模型xx.leaveProcess.zip");
        // 创建输入流
        FileInputStream inputStream = new FileInputStream(file);

        // 读取zip资源压缩包，转成输入流
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("请假申请流程222-压缩包")
                .deploy();

        // 4. 输出部署结果
        System.out.println("部署ID: " + deployment.getId());
        System.out.println("部署名称：" + deployment.getName());
    }

    @Test
    public void deployByFile() throws FileNotFoundException {
        File file = new File("D:/请假申请流程.bpmn20.xml");
        // 文件输入流
        FileInputStream inputStream = new FileInputStream(file);
        // 资源名称
        String filename = file.getName();

        // 调用相关api方法进行部署
        Deployment deployment = repositoryService.createDeployment()
                .name("请假申请流程")
                .addInputStream(filename, inputStream)
                .deploy();
        // 输出部署结果
        System.out.println("部署ID: " + deployment.getId());
        System.out.println("部署名称：" + deployment.getName());
    }

    /**
     * 根据部署ID来删除部署的流程定义数据
     * ACT_GE_BYTEARRAY
     * ACT_RE_DEPLOYMENT
     * ACT_RE_PROCDEF
     * ACT_RU_EVENT_SUBSCR
     * ACT_RU_IDENTITYLINK
     */
    @Test
    public void delete() {
        try {
            String deploymentId = "f88ccd79-db17-11eb-a1e0-2c337a6d7e1d";
            // 默认不是级联删除操作，如果有正在执行的流程实例，则删除时会抛出异常，并且不会删除历史表数据
            //repositoryService.deleteDeployment(deploymentId);

            // 如果为true则是级联删除操作，如果流程定义启动了对应的流程实例，也可以进行删除，并且会删除历史数据
            repositoryService.deleteDeployment(deploymentId, true);
            System.out.println("删除流程定义数据成功");
        }catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().indexOf("a foreign key constraint fails") > 0) {
                System.out.println("有正在执行的流程实例，不允许删除");
            }else {
                System.out.println("删除失败，原因： " + e.getMessage());
            }
        }

    }

    @Autowired
    RuntimeService runtimeService;

    @Test
    public void startProcessInstance() {
        // 启动流程实例(流程定义key processDefinitionKey)
        // 通过流程定义key启动的流程实例 ，找的是最新版本的流程定义数据
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leaveProcess");

        System.out.println("流程定义id: " + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id: " + processInstance.getId());
    }


}
