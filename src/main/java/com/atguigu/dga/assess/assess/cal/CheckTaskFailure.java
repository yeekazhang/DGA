package com.atguigu.dga.assess.assess.cal;

import com.atguigu.dga.assess.assess.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.ds.bean.TDsTaskInstance;
import com.atguigu.dga.ds.service.TDsTaskInstanceService;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("TASK_FAILURE")
public class CheckTaskFailure extends AssessorTemplate {

    @Autowired
    private TDsTaskInstanceService instanceService;
    /*
        检查DS 有报错 则给0分，其余给10分
            在ds的t_ds_task_instance表中
                一个task_instance，如果state是6，就是失败
                                  如果state是7，就是成功

        DS是一天一调度
            考评日期和调度日期是同一天
     */
    @Override
    protected void assess(AssessParam param, GovernanceAssessDetail detail) throws Exception {

        // 获取当前表的名字
        TableMetaInfo tableMetaInfo = param.getTableMetaInfo();
        String name = tableMetaInfo.getSchemaName() + "." + tableMetaInfo.getTableName();
        // 查询t_ds_task_instance表中，当前表计算的任务state是不是6
        QueryWrapper<TDsTaskInstance> queryWrapper = new QueryWrapper<TDsTaskInstance>()
                .eq("name", name)
                .eq("date(start_time)", param.getAssessDate())
                .eq("state", MetaConstant.TASK_STATE_FAILED);

        // 由于有重试机制，因此可能一个任务，会有多次失败的情况
        List<TDsTaskInstance> failInstances = instanceService.list(queryWrapper);

        // 没查到，说明当前表在调度执行时，没报错
        if (failInstances.size() > 0){
            String msg = failInstances.stream().map(i -> i.getId() + "在 " + i.getEndTime() + "失败.").collect(Collectors.joining(","));
            assessZeroScore(detail, msg, param, false);
        }

    }
}
