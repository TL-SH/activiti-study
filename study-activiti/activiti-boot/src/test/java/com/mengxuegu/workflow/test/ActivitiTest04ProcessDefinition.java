package com.mengxuegu.workflow.test;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
public class ActivitiTest04ProcessDefinition {

    @Autowired
    RepositoryService repositoryService;

    /**
     * 分页条件查询流程定义列表数据
     */
    @Test
    public void getProcDefList() {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();

        String name = "请假";
        if(StringUtils.isNotBlank(name)) {
            // 条件查询
            query.processDefinitionNameLike("%"+name+"%");
        }
        // 如果多个相同key, 只查询最新版本的流程定义，
        query.latestVersion();
        // 按key降序
        query.orderByProcessDefinitionKey().desc();

        // 当前页码
        int current = 1;
        // 每页显示多少条
        int size = 5;
        // 当前页第1条数据的下标
        int firstResult = (current-1) * size;
        // 分页查询
        List<ProcessDefinition> processDefinitionList = query.listPage(firstResult, size);

        for (ProcessDefinition pd : processDefinitionList) {
            System.out.print("流程部署id：" + pd.getDeploymentId());
            System.out.print("，流程定义id：" + pd.getId());
            System.out.print("，流程定义key: " + pd.getKey());
            System.out.print("，流程定义名称：" + pd.getName());
            System.out.print("，版本号：" + pd.getVersion());
            System.out.println("，状态：" + ( pd.isSuspended() ? "挂起（暂停）" : "激活（开启）") );
        }

        // 总记录数
        long total = query.count();

        System.out.println("满足条件的流程定义总记录：" + total);

    }

    /**
     * 挂起或激活流程定义 ：对应 act_re_procdef 表中的 SUSPENSION_STATE_ 字段，1是激活，2是挂起
     * - 流程定义被挂起：此流程定义下的所有流程实例不允许继续往后流转了，就被停止了。
     * - 流程定义被激活：此流程定义下的所有流程实例允许继续往后流转。
     * - 为什么会被挂起？
     *   - 可能当前公司的请假流程发现了一些不合理的地方，然后就把此流程定义挂起。
     *   - 流程不合理解决办法：
     *     - 方式一：可以先挂起流程定义，然后更新流程定义，然后激活流程定义。
     *     - 方式二：挂起了就不激活了，重新创建一个新的请假流程定义。
     */
    @Test
    public void updateProcDefState() {
        String processDefinitionId = "leaveProcess:2:f6a379a1-7cdb-11f6-8052-2c337a6d7e1d";
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        // 判断是否挂起，true则挂起，false则激活
        if(processDefinition.isSuspended()) {
            // 将当前为挂起状态更新为激活状态
            // 参数说明：参数1：流程定义id,参数2：是否激活（true是否级联对应流程实例，激活了则对应流程实例都可以审批），参数3：什么时候激活，如果为null则立即激活，如果为具体时间则到达此时间后激活
            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
        }else {
            // 将当前为激活状态更新为挂起状态
            // 参数说明：参数1：流程定义id,参数2：是否挂起（true是否级联对应流程实例，挂起了则对应流程实例都不可以审批），参数3：什么时候挂起，如果为null则立即挂起，如果为具体时间则到达此时间后挂起
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
        }
    }

    /**
     * 导出流程定义文件（xml或png)
     */
    @Test
    public void exportProcDefFile() throws Exception{
        String processDefinitionId = "leaveProcess:2:f6a379a1-7cdb-11f6-8052-2c337a6d7e1d";
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);

        // 获取的是 xml 资源名
        String resourceName = processDefinition.getResourceName();

        // 获取 png 图片资源名
        resourceName = processDefinition.getDiagramResourceName();

        // 查询到相关的资源输入流 （deploymentId, resourceName）
        InputStream input =
                repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);

        // 创建输出流
        File file = new File("D:/" + resourceName);
        FileOutputStream output = new FileOutputStream(file);

        IOUtils.copy(input, output);

        input.close();
        output.close();
        System.out.println("流程定义资源文件导出成功: " + resourceName);
    }

}
