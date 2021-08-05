package com.mengxuegu.workflow.controller;


import com.mengxuegu.workflow.req.ModelAddREQ;
import com.mengxuegu.workflow.req.ModelREQ;
import com.mengxuegu.workflow.service.IModelService;
import com.mengxuegu.workflow.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Api("流程定义模型管理")
@Slf4j
@RestController
@RequestMapping("/model")
public class ModelController {

    @Autowired
    IModelService modelService;

    @ApiOperation("新增流程定义模型数据")
    @PostMapping // post 请求 /model
    public Result add(@RequestBody ModelAddREQ req) {
        try {
            return modelService.add(req);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建模型失败：" + e.getMessage());
            return Result.error("创建模型失败");
        }
    }

    @ApiOperation("条件分页查询流程定义模型数据")
    @PostMapping("/list")
    public Result modelList(@RequestBody ModelREQ req) {
        try {
            return modelService.getModelList(req);
        }catch (Exception e) {
            e.printStackTrace();
            log.error("条件分页查询流程定义模型数据:" + e.getMessage());
            return Result.error("查询列表数据失败");
        }

    }

    @ApiOperation("通过流程定义模型id部署流程定义")
    @PostMapping("/deploy/{modelId}")
    public Result deploy(@PathVariable("modelId") String modelId) {
        try {
            return modelService.deploy(modelId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("部署流程定义失败：" + e.getMessage());
            return Result.error("部署流程定义失败");
        }
    }

    @ApiOperation("导出流程定义模型zip压缩包")
    @GetMapping("/export/zip/{modelId}")
    public void exportZip(@PathVariable("modelId") String modelId,
                          HttpServletResponse response) {
        modelService.exportZip(modelId, response);
    }


    @Autowired
    RepositoryService repositoryService;

    @ApiOperation("删除流程定义模型")
    @DeleteMapping("{modelId}")
    public Result deleteModel(@PathVariable("modelId") String modelId) {
        repositoryService.deleteModel(modelId);
        return Result.ok();
    }



}



