package com.mengxuegu.workflow.test;

import com.mengxuegu.workflow.config.SecurityUtil;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 当前测试采用的Activti版本号：7.1.0.M6
 */
@SpringBootTest
public class ActivitiTest12ProcessRuntime {

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


}
