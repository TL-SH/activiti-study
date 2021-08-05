package com.mengxuegu.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mengxuegu.workflow.entities.Leave;
import com.mengxuegu.workflow.req.LeaveREQ;
import org.apache.ibatis.annotations.Param;


public interface LeaveMapper extends BaseMapper<Leave> {

    /**
     * 分页条件查询请假申请列表数据
     * @param page 分页对象，mybatis-plus规定一定要作为第1个参数
     * @return
     */
    IPage<Leave> getLeaveAndStatusList(IPage page, @Param("req") LeaveREQ req);

}
