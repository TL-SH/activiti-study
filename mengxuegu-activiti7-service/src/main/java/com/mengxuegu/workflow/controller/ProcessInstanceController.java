package com.mengxuegu.workflow.controller;

import com.mengxuegu.workflow.enums.BusinessStatusEnum;
import com.mengxuegu.workflow.req.ProcInstREQ;
import com.mengxuegu.workflow.req.StartREQ;
import com.mengxuegu.workflow.service.IBusinessStatusService;
import com.mengxuegu.workflow.service.IProcessInstanceService;
import com.mengxuegu.workflow.utils.Result;
import com.mengxuegu.workflow.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Api
@Slf4j
@RestController
@RequestMapping("/instance")
public class ProcessInstanceController {

    @Autowired
    private IProcessInstanceService processInstanceService;

    @ApiOperation("提交申请，启动流程实例")
    @PostMapping("/start")
    public Result start(@RequestBody StartREQ req) {
        return processInstanceService.startProcess(req);
    }

    @ApiOperation("返回申请")
    @DeleteMapping("/cancel/apply")
    public Result cancelApply(@RequestParam String businessKey,
                              @RequestParam String procInstId,
                              @RequestParam(defaultValue = "返回成功") String message) {
        return processInstanceService.cancel(businessKey, procInstId, message);
    }

    @ApiOperation("通过流程实例id获取申请表单组件名")
    @GetMapping("/form/name/{procInstId}")
    public Result getFormName(@PathVariable String procInstId) {
        return processInstanceService.getFormNameByProcInstId(procInstId);
    }


    @ApiOperation("通过流程实例id获取任务办理历史记录")
    @GetMapping("/history/list") // ?procInstId=xxx
    public Result getHistoryInfoList(@RequestParam String procInstId) {
        return processInstanceService.getHistoryInfoList(procInstId);
    }

    @ApiOperation("通过流程实例id获取历史流程图")
    @GetMapping("/history/image") // ?procInstId=xxx
    public void getHistoryProcessImage(@RequestParam String procInstId,
                                     HttpServletResponse response) {
        processInstanceService.getHistoryProcessImage(procInstId, response);
    }


    @ApiOperation("查询正在运行中的流程实例")
    @PostMapping("/list/running")
    public Result getProcInstRunning(@RequestBody ProcInstREQ req) {
       return processInstanceService.getProcInstRunning(req);
    }

    @Autowired
    private RuntimeService  runtimeService;


    @ApiOperation("挂起或激活流程实例")
    @PutMapping("/state/{procInstId}")
    public Result updateProcInstState(@PathVariable String procInstId) {
        // 1. 查询指定流程实例的数据
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procInstId)
                .singleResult();

        // 2. 判断当前流程实例的状态
        if(processInstance.isSuspended()) {
            // 如果是已挂起，则更新为激活状态
            runtimeService.activateProcessInstanceById(procInstId);
        }else {
            // 如果是已激活，则更新为挂起状态
            runtimeService.suspendProcessInstanceById(procInstId);
        }

        return Result.ok();
    }

    @Autowired
    IBusinessStatusService businessStatusService;

    @ApiOperation("作废流程实例，不会删除历史记录")
    @DeleteMapping("/{procInstId}")
    public Result deleteProcInst(@PathVariable String procInstId) {
        // 1. 查询流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procInstId).singleResult();

        // 2. 删除流程实例
        runtimeService.deleteProcessInstance(procInstId,
                    UserUtils.getUsername() + "作废了当前流程申请");

        // 3. 更新业务状态
        return businessStatusService.updateState(processInstance.getBusinessKey(),
                BusinessStatusEnum.INVALID);
    }


    @ApiOperation("查询已结束的流程实例")
    @PostMapping("/list/finish")
    public Result getProcInstFinish(@RequestBody ProcInstREQ req) {
        return processInstanceService.getProcInstFinish(req);
    }

    @ApiOperation("删除已结束流程实例和历史记录")
    @DeleteMapping("/history/{procInstId}")
    public Result deleteProcInstAndHistory(@PathVariable String procInstId) {
        return processInstanceService.deleteProcInstAndHistory(procInstId);
    }



}
