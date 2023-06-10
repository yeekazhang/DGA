package com.atguigu.dga.assess.assess.storage;

import com.alibaba.fastjson.JSON;
import com.atguigu.dga.assess.assess.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *  未设定周期类型的 给 0分
 * 周期类型为永久、拉链表 则给10分
 * 周期类型为日分区 :
 *             无分区信息的给0分
 *             没设生命周期给0分
 *             周期长度超过建议周期天数{days}，则给 （建议周期天数  / 设置周期天数 ）*10
 */
@Component("LIFECYCLE_ELIGIBLE")
public class CheckLifecycle extends AssessorTemplate {
    @Override
    protected void assess(AssessParam param, GovernanceAssessDetail detail) {

        // 获取当前表的生命周期的各种功能参数，包括days阈值
        Integer days = JSON.parseObject(param.getMetric().getMetricParamsJson()).getInteger("days");

        TableMetaInfoExtra tableMetaInfoExtra = param.getTableMetaInfo().getTableMetaInfoExtra();
        Long lifecycleDays = tableMetaInfoExtra.getLifecycleDays();
        String lifecycleType = tableMetaInfoExtra.getLifecycleType();

        // 未设置生命周期，生命周期属于额外手动录入的元数据，需要提供整改链接
        if (MetaConstant.LIFECYCLE_TYPE_UNSET.equals(lifecycleType)){
            assessZeroScore(detail, "未设置生命周期", param, true);
        }else if (MetaConstant.LIFECYCLE_TYPE_DAY.equals(lifecycleType)){

            // 没有分区字段
            if("[]".equals(param.getTableMetaInfo().getPartitionColNameJson())){
                assessZeroScore(detail, "未设置分区字段", param,false);
            }

            // 未设置生命周期的天数
            if (lifecycleDays == null){
                assessZeroScore(detail, "未设置生命周期天数", param, true);
            }else if (lifecycleDays > days){

                BigDecimal score = BigDecimal.valueOf(days)
                        .divide(BigDecimal.valueOf(lifecycleDays), 2, RoundingMode.HALF_UP)
                        .movePointRight(1);
                detail.setAssessScore(score);
                detail.setAssessProblem("设置的生命周期超过阈值:" + days);
                detail.setGovernanceUrl(param.getMetric().getGovernanceUrl().replace("{id}", param.getTableMetaInfo().getId().toString()));
            }
        }

    }
}

















