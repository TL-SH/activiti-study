package com.mengxuegu.workflow.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mengxuegu.workflow.entities.Leave;
import com.mengxuegu.workflow.mapper.LeaveMapper;
import com.mengxuegu.workflow.req.LeaveREQ;
import com.mengxuegu.workflow.service.IBusinessStatusService;
import com.mengxuegu.workflow.service.ILeaveService;
import com.mengxuegu.workflow.utils.Result;
import com.mengxuegu.workflow.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LeaveService extends ServiceImpl<LeaveMapper, Leave> implements ILeaveService {

    @Autowired
    IBusinessStatusService businessStatusService;

    @Override
    public Result add(Leave leave) {
        // 1. 新增请假信息
        // 当前登录用户即为申请人
        leave.setUsername(UserUtils.getUsername());
        int size = baseMapper.insert(leave);
        // 2. 新增请假业务状态：待提交
        if (size == 1) {
            businessStatusService.add(leave.getId());
        }
        return Result.ok();
    }

    @Override
    public Result listPage(LeaveREQ req) {
        if(StringUtils.isEmpty(req.getUsername())) {
            req.setUsername(UserUtils.getUsername());
        }
        IPage<Leave> page = baseMapper.getLeaveAndStatusList(req.getPage(), req);
        return Result.ok(page);
    }

    @Override
    public Result update(Leave leave) {
        if(leave == null || StringUtils.isEmpty(leave.getId())) {
            return Result.error("数据不合法");
        }
        // 查询原数据
        Leave entity = baseMapper.selectById(leave.getId());
        // 拷贝新数据
        BeanUtils.copyProperties(leave, entity);
        entity.setUpdateDate(new Date());
        baseMapper.updateById(entity);
        return Result.ok();
    }

}
