package com.mengxuegu.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mengxuegu.workflow.entities.Loan;
import com.mengxuegu.workflow.req.LoanREQ;
import org.apache.ibatis.annotations.Param;

public interface LoanMapper extends BaseMapper<Loan> {

    IPage<Loan> getLoanAndStatusList(IPage<Loan> page, @Param("req")LoanREQ req);

}
