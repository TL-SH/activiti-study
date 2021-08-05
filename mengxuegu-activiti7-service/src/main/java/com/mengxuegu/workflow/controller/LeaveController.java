package com.mengxuegu.workflow.controller;

import com.mengxuegu.workflow.entities.Leave;
import com.mengxuegu.workflow.req.LeaveREQ;
import com.mengxuegu.workflow.service.ILeaveService;
import com.mengxuegu.workflow.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("请假申请控制层")
@Slf4j
@RestController
@RequestMapping("/leave")
public class LeaveController {

    @Autowired
    ILeaveService leaveService;

    @ApiOperation("新增请假申请")
    @PostMapping
    public Result add(@RequestBody Leave leave) {
        return leaveService.add(leave);
    }

    @ApiOperation("条件分页查询请假申请列表数据")
    @PostMapping("/list")
    public Result listPage(@RequestBody LeaveREQ req) {
        return leaveService.listPage(req);
    }

    @ApiOperation("查询请假详情信息")
    @GetMapping("/{id}")
    public Result view(@PathVariable String id) {
        Leave leave = leaveService.getById(id);
        return Result.ok(leave);
    }


    @ApiOperation("更新请假详情信息")
    @PutMapping
    public Result view(@RequestBody Leave leave) {
        return leaveService.update(leave);
    }

}
