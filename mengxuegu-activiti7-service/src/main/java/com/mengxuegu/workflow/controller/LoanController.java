package com.mengxuegu.workflow.controller;

import com.mengxuegu.workflow.entities.Leave;
import com.mengxuegu.workflow.entities.Loan;
import com.mengxuegu.workflow.req.LeaveREQ;
import com.mengxuegu.workflow.req.LoanREQ;
import com.mengxuegu.workflow.service.ILoanService;
import com.mengxuegu.workflow.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("借款申请控制层")
@Slf4j
@RestController
@RequestMapping("/loan")
public class LoanController {

    @Autowired
    ILoanService loanService;

    @ApiOperation("新增借款申请")
    @PostMapping
    public Result add(@RequestBody Loan loan) {
        return loanService.add(loan);
    }

    @ApiOperation("条件分页查询借款申请列表数据")
    @PostMapping("/list")
    public Result listPage(@RequestBody LoanREQ req) {
        return loanService.listPage(req);
    }

    @ApiOperation("查询借款详情信息")
    @GetMapping("/{id}")
    public Result view(@PathVariable String id) {
        Loan loan = loanService.getById(id);
        return Result.ok(loan);
    }


    @ApiOperation("更新借款详情信息")
    @PutMapping
    public Result view(@RequestBody Loan loan) {
        return loanService.update(loan);
    }

}
