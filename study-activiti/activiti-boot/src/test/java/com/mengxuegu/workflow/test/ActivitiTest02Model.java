package com.mengxuegu.workflow.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@SpringBootTest
public class ActivitiTest02Model {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 查询所有流程定义模型
     */
    @Test
    public void modelList() {
        // 1. 模型查询对象
        ModelQuery query = repositoryService.createModelQuery();
        List<Model> list = query.orderByCreateTime()
                .desc()
                .list();
        for (Model model : list) {
            System.out.print("模型id:" + model.getId());
            System.out.print(", 模型名称：" + model.getName());
            System.out.print("，模型描述： " + model.getMetaInfo());
            System.out.print("，模型标识key:" + model.getKey());
            System.out.println("，模型版本号：" + model.getVersion());
        }
    }

    /**
     * 删除流程定义模型
     * ACT_RE_MODE
     * ACT_GE_BYTEARRAY
     */
    @Test
    public void deleteModel() {
        String modelId = "0a499790-7c1d-11f6-9001-2c337a6d7e1d";
        repositoryService.deleteModel(modelId);
        System.out.println("删除成功");
    }

    /**
     * 导出流程定义模型的资源的zip压缩包
     */
    @Test
    public void exportZip() throws IOException {
        // 1. 查询模型基本信息
        String modelId = "da0fbf5c-7c1d-11f6-949f-2c337a6d7e1d";
        Model model = repositoryService.getModel(modelId);
        if(model != null) {
            // 2. 查询流程定义模型的json字节码
            byte[] bpmnJsonBytes = repositoryService.getModelEditorSource(modelId);
            // 2.1 将json字节码转换为xml字节码
            byte[] xmlBytes = bpmnJsonXmlBytes(bpmnJsonBytes);
            if(xmlBytes == null) {
                System.out.println("模型数据为空-请先设计流程定义模型，再导出");
            }else {
                // 压缩包文件名
                String zipName = model.getName() + "." + model.getKey() + ".zip";
                File file = new File("D:/" + zipName);
                FileOutputStream outputStream = new FileOutputStream(file);
                // 实例化zip输出流
                ZipOutputStream zipos = new ZipOutputStream(outputStream);

                // 将xml添加到压缩包中(指定xml文件名：请假流程.bpmn20.xml ）
                zipos.putNextEntry(new ZipEntry(model.getName() + ".bpmn20.xml"));
                zipos.write(xmlBytes);

                // 3. 查询流程定义模型的图片字节码
                byte[] pngBytes = repositoryService.getModelEditorSourceExtra(modelId);
                if(pngBytes != null) {
                    // 图片文件名（请假流程.leaveProcess.png)
                    zipos.putNextEntry(new ZipEntry(model.getName() + "." + model.getKey() + ".png"));
                    zipos.write(pngBytes);
                }
                // 4. 以压缩包的方式导出流程定义模型文件
                zipos.closeEntry();
                zipos.close();
                System.out.println("导出流程定义模型zip成功");
            }
        }else {
            System.out.println("模型不存在");
        }

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


    @Test
    public void exportXml() throws IOException {
        // 1. 查询模型基本信息
        String modelId = "da0fbf5c-7c1d-11f6-949f-2c337a6d7e1d";
        // 查询json字节码
        byte[] bytes = repositoryService.getModelEditorSource(modelId);

        String filename = null;
        ByteArrayInputStream in = null;
        if(bytes != null) {
            // 1. json字节码转成 BpmnModel 对象
            JsonNode jsonNode = objectMapper.readTree(bytes);
            BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(jsonNode);
            if(bpmnModel.getProcesses().size() > 0) {
                // 2. BpmnModel 对象转为xml字节码
                byte[] xmlBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
                in = new ByteArrayInputStream(xmlBytes);
                filename = StringUtils.isBlank(bpmnModel.getMainProcess().getName())
                        ? bpmnModel.getMainProcess().getId() : bpmnModel.getMainProcess().getName();
            }
        }

        if(filename == null) {
            filename = "模型数据为空，请先设计流程，再导出";
            in = new ByteArrayInputStream(filename.getBytes("utf-8"));
        }

        // 文件输出流
        FileOutputStream out = new FileOutputStream(new File("D:/" + filename + ".bpmn20.xml"));

        IOUtils.copy(in, out);

        out.close();
        in.close();
        System.out.println("导出xml成功");
    }

    /**
     * 通过流程定义模型数据部署流程定义
     * ACT_RE_PROCDEF
     * ACT_RE_DEPLOYMENT
     * ACT_GE_BYTEARRAY
     * ACT_RE_MODEL 更新流程部署id，将模型与部署的流程定义绑定
     * @throws Exception
     */
    @Test
    public void deploy() throws Exception {
        // 1. 查询流程定义模型json字节码
        String modelId = "b8efa921-da43-11eb-8aa7-2c337a6d7e1d";
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

}
