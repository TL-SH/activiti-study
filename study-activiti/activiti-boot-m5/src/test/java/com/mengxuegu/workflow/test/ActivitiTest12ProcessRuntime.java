package com.mengxuegu.workflow.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mengxuegu.workflow.config.SecurityUtil;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class ActivitiTest12ProcessRuntime {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 通过流程定义模型数据部署流程定义
     * @throws Exception
     */
    @Test
    public void deploy() throws Exception {
        // 1. 查询流程定义模型json字节码
        String modelId = "d58ab2c0-f75b-11f5-8438-2c337a6d7e1d";
        byte[] jsonBytes = repositoryService.getModelEditorSource(modelId);
        if(jsonBytes == null) {
            System.out.println("模型数据为空，请先设计流程定义模型，再进行部署");
            return;
        }
        // 将json字节码转为 xml 字节码，因为bpmn2.0规范中关于流程模型的描述是xml格式的，而activiti遵守了这个规范
        byte[] xmlBytes = bpmnJsonXmlBytes(jsonBytes);
        if(xmlBytes == null) {
            System.out.println("数据模型不符合要求，请至少设计一条主线流程");
            return;
        }
        // 2. 查询流程定义模型的图片
        byte[] pngBytes = repositoryService.getModelEditorSourceExtra(modelId);

        // 查询模型的基本信息
        Model model = repositoryService.getModel(modelId);

        // xml资源的名称 ，对应act_ge_bytearray表中的name_字段
        String processName = model.getName() + ".bpmn20.xml";
        // 图片资源名称，对应act_ge_bytearray表中的name_字段
        String pngName = model.getName() + "." + model.getKey() + ".png";

        // 3. 调用部署相关的api方法进行部署流程定义
        Deployment deployment = repositoryService.createDeployment()
                .name(model.getName()) // 部署名称
                .addString(processName, new String(xmlBytes, "UTF-8")) // bpmn20.xml资源
                .addBytes(pngName, pngBytes) // png资源
                .deploy();

        // 更新 部署id 到流程定义模型数据表中
        model.setDeploymentId(deployment.getId());
        repositoryService.saveModel(model);

        System.out.println("部署成功");
    }


    private byte[] bpmnJsonXmlBytes(byte[] jsonBytes) throws IOException {
        if(jsonBytes == null) {
            return null;
        }

        // 1. json字节码转成 BpmnModel 对象
        JsonNode jsonNode = objectMapper.readTree(jsonBytes);
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(jsonNode);

        if(bpmnModel.getProcesses().size() == 0) {
            return null;
        }
        // 2. BpmnModel 对象转为xml字节码
        byte[] xmlBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
        return xmlBytes;
    }


    @Autowired
    ProcessRuntime processRuntime;

    @Autowired
    SecurityUtil securityUtil;

    /**
     * 在使用了7版本API，不管使用是的M几都是一样
     * 报错1：An Authentication object was not found in the SecurityContext
     * 原因：就需要使用Security进行登录，并且登录用户要有ACTIVITI_USER角色
     * 解决：securityUtil.logInAs("meng");
     * 报错2： access.AccessDeniedException: 不允许访问
     * 解决：登录用户要有ACTIVITI_USER角色 ,securityUtil.logInAs("meng");
     *
     * 7.1.0.M6 会报错：
     *  1. Query return 22 results instead of max 1
     *     原因底层：select distinct RES.* from ACT_RE_DEPLOYMENT RES order by RES.ID_ asc
     *     查询出来22条，而代码中要求返回一条，所以报错。
     *
     */
    @Test
    public void getProcessDefinitionList () {
        securityUtil.logInAs("meng");
        // M6报错：
        Page<ProcessDefinition> page =
                processRuntime.processDefinitions(Pageable.of(0, 10));

        int total = page.getTotalItems();
        System.out.println("部署的流程定义总记录数：" + total);

        List<ProcessDefinition> content = page.getContent();
        for (ProcessDefinition pd : content) {
            System.out.println("流程定义名称：" + pd.getName());
            System.out.println("流程定义KEY：" + pd.getKey());
        }
    }

    /**
     * 启动实例
     */
    @Test
    public void startProcess() {
        securityUtil.logInAs("meng");

        String key = "loanProcess";
        // 通过流程定义key启动流程实例，采用的版本号是1版本
        ProcessInstance processInstance = processRuntime.start(
                ProcessPayloadBuilder.start()
                        .withProcessDefinitionId("loanProcess:2:7d670684-f765-11f5-b5b3-2c337a6d7e1d")
                       //.withProcessDefinitionKey(key)
                        .withName("流程实例名")
                        .withBusinessKey("666")
                        .withVariable("user1", "xue")
                        .build() );
        System.out.println("启动流程实例成功：" + processInstance.getId());
    }

    @Test
    public void getPoreInstList() {
        securityUtil.logInAs("meng");
        Page<ProcessInstance> page = processRuntime.processInstances(
                Pageable.of(0, 10),
                ProcessPayloadBuilder.processInstances()
                        .withProcessDefinitionKey("loanProcess")
                        .build()
        );

        int totalItems = page.getTotalItems();
        System.out.println("流程实例总记录数：" + totalItems);
        for (ProcessInstance processInstance : page.getContent()) {
            System.out.println("流程实例名称：" + processInstance.getName());
            System.out.println("流程定义key: " + processInstance.getProcessDefinitionKey());
            System.out.println("业务key:" + processInstance.getBusinessKey());
        }
    }

    /**
     * 挂起流程实例
     */
    @Test
    public void suspend() {
        securityUtil.logInAs("meng");

        processRuntime.suspend(
                ProcessPayloadBuilder.suspend()
                .withProcessInstanceId("31e8ac80-f768-11f5-bb3b-2c337a6d7e1d")
                .build()
        );
    }

    /**
     * 激活流程实例
     */
    @Test
    public void resume() {
        securityUtil.logInAs("meng");
        String procInstId = "31e8ac80-f768-11f5-bb3b-2c337a6d7e1d";
        processRuntime.resume( ProcessPayloadBuilder.resume(procInstId));
    }

    /**
     * 删除流程实例
     */
    @Test
    public void delete() {
        securityUtil.logInAs("meng");
        String procInstId = "31e8ac80-f768-11f5-bb3b-2c337a6d7e1d";
        processRuntime.delete( ProcessPayloadBuilder.delete(procInstId) );
    }



}
