package com.mengxuegu.workflow.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mengxuegu.workflow.entities.Loan;
import com.mengxuegu.workflow.mapper.LoanMapper;
import com.mengxuegu.workflow.req.LoanREQ;
import com.mengxuegu.workflow.service.IBusinessStatusService;
import com.mengxuegu.workflow.service.ILoanService;
import com.mengxuegu.workflow.utils.Result;
import com.mengxuegu.workflow.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoanService extends ServiceImpl<LoanMapper, Loan> implements ILoanService {

    @Autowired
    IBusinessStatusService businessStatusService;

    @Override
    public Result add(Loan loan) {
        // 1. 新增借款信息
        // 当前登录用户即为申请人
        loan.setUserId(UserUtils.getUsername());
        int size = baseMapper.insert(loan);
        // 2. 新增借款业务状态：待提交
        if (size == 1) {
            businessStatusService.add(loan.getId());
        }
        return Result.ok();
    }

    @Override
    public Result listPage(LoanREQ req) {
        if(StringUtils.isEmpty(req.getUsername())) {
            req.setUsername(UserUtils.getUsername());
        }
        IPage<Loan> page = baseMapper.getLoanAndStatusList(req.getPage(), req);
        return Result.ok(page);
    }

    @Override
    public Result update(Loan loan) {
        if(loan == null || StringUtils.isEmpty(loan.getId())) {
            return Result.error("数据不合法");
        }
        // 查询原数据
        Loan entity = baseMapper.selectById(loan.getId());
        // 拷贝新数据
        BeanUtils.copyProperties(loan, entity);
        entity.setUpdateDate(new Date());
        baseMapper.updateById(entity);
        return Result.ok();
    }

}
