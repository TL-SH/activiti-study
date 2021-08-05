package com.mengxuegu.workflow.test;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.util.ReflectUtil;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

@SpringBootTest
public class ActivitiTest01 {

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    RepositoryService repositoryService;

    @Test
    public void getProcessEngine() {
        System.out.println("processEngine: " + processEngine);
    }

    /**
     * 通过zip压缩包进行部署流程定义
     */
    @Test
    public void deployByZip() {
        // 3. 部署流程定义
        // 读取zip资源压缩包，转成输入流
        InputStream inputStream = ReflectUtil.getResourceAsStream("processes/leave.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("请假申请流程-压缩包--boot")
                .deploy();

        // 4. 输出部署结果
        System.out.println("部署ID: " + deployment.getId());
        System.out.println("部署名称：" + deployment.getName());
    }


}
