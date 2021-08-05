package com.mengxuegu.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mengxuegu.workflow.entities.ProcessConfig;
import com.mengxuegu.workflow.mapper.ProcessConfigMapper;
import com.mengxuegu.workflow.service.IProcessConfigService;
import com.mengxuegu.workflow.utils.Result;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ProcessConfigService extends ServiceImpl<ProcessConfigMapper, ProcessConfig>
        implements IProcessConfigService {

    @Override
    public ProcessConfig getByProcessKey(String processKey) {
        QueryWrapper<ProcessConfig> query = new QueryWrapper<>();
        query.eq("process_key", processKey);

        return baseMapper.selectOne(query);
    }

    @Override
    public Result deleteByProcessKey(String processKey) {
        QueryWrapper<ProcessConfig> query = new QueryWrapper<>();
        query.eq("process_key", processKey);
        baseMapper.delete(query);
        return Result.ok();
    }

    @Override
    public ProcessConfig getByBusinessRoute(String businessRoute) {
        QueryWrapper<ProcessConfig> query = new QueryWrapper<>();
        query.eq("upper(business_route)", businessRoute.toUpperCase());
        List<ProcessConfig> list = baseMapper.selectList(query);
        if(CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

}
