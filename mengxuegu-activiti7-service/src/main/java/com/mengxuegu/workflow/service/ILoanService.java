package com.mengxuegu.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mengxuegu.workflow.entities.Leave;
import com.mengxuegu.workflow.entities.Loan;
import com.mengxuegu.workflow.req.LeaveREQ;
import com.mengxuegu.workflow.req.LoanREQ;
import com.mengxuegu.workflow.utils.Result;

public interface ILoanService extends IService<Loan> {

    Result add(Loan loan);

    /**
     * 条件分页查询借款列表
     * @param req
     * @return
     */
    Result listPage(LoanREQ req);

    Result update(Loan loan);
}
