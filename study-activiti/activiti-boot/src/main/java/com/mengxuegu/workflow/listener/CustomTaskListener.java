package com.mengxuegu.workflow.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 自定义任务监听器 com.mengxuegu.workflow.listener.CustomTaskListener
 */
public class CustomTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("任务ID: " + delegateTask.getId()
                + ",任务名称：" + delegateTask.getName()
                + ",触发事件名：" + delegateTask.getEventName());
        if("总裁审批".equals(delegateTask.getName())
            && "create".equalsIgnoreCase(delegateTask.getEventName())
        ) {
            // 当任务创建后，指定此任务的办理人
            delegateTask.setAssignee("小学");
        }
    }
}
