package com.atguigu.dga.assess.assess.quality;

import com.alibaba.fastjson.JSON;
import com.atguigu.dga.assess.assess.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.bean.GovernanceMetric;
import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.ds.bean.TDsTaskInstance;
import com.atguigu.dga.ds.service.TDsTaskInstanceService;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 前一天产出时效，超过前{days}天产出时效平均值n%
 * 则给0分，其余10分
 *
 *      1 计算Task当天运行的时间(endtime - starttime)
 *      2 计算Task前days天的平均运行时间 avg(endtime - starttime)
 *      3 计算比例
 */
@Component("TABLE_PRODUCE_EFFICIENCY")
public class TableProduceEfficient extends AssessorTemplate {

    @Autowired
    private TDsTaskInstanceService instanceService;
    @Override
    protected void assess(AssessParam param, GovernanceAssessDetail detail) throws Exception {

        // 获取相关的参数
        GovernanceMetric metric = param.getMetric();
        Integer days = JSON.parseObject(metric.getMetricParamsJson()).getInteger("days");
        Integer n = JSON.parseObject(metric.getMetricParamsJson()).getInteger("n");
        String assessDate = param.getAssessDate();

        TableMetaInfo tableMetaInfo = param.getTableMetaInfo();

        String name = tableMetaInfo.getSchemaName() + "." + tableMetaInfo.getTableName();
        QueryWrapper<TDsTaskInstance> queryWrapper1 = new QueryWrapper<TDsTaskInstance>()
                .eq("name", name)
                .eq("date(start_time)", param.getAssessDate())
                .eq("state", MetaConstant.TASK_STATE_SUCCESS)
                .select("timestampdiff(second, start_time, end_time) sec");

        // 当前查询的结果是一行一列,封装为Bean，必须要求这一列的列名在bean中有对应属性名
        // getMap():会把查询到的列，按照列名作为key，列值作为value，封装到一个Map集合中,用于单行查询
        Integer sec = (Integer) instanceService.getMap(queryWrapper1).get("sec");


        // 前days天的平均运行时间
        // avg(endtime - starttime) where date(start_time) >= date_sub(assess_time, days) and date(start_time) < assess_date
        QueryWrapper<TDsTaskInstance> queryWrapper2 = new QueryWrapper<TDsTaskInstance>()
                .eq("name", name)
                .last("date(start_time) >= DATE_SUB(" + assessDate + " , INTERVAL " + days + " DAY) AND date(start_time) < " + assessDate)
                .eq("state", MetaConstant.TASK_STATE_SUCCESS)
                .select("avg(TIMESTAMPDIFF(second , start_time, end_time)) avgSec ");

        BigDecimal avgSec = (BigDecimal)instanceService.getMap(queryWrapper2).get("avgSec");

        // 运算比较
        // 极限值
        BigDecimal upperTime = avgSec.multiply(BigDecimal.valueOf(100).add(BigDecimal.valueOf(n)).movePointLeft(2));

        //今天运行的失效低
        if (BigDecimal.valueOf(sec).compareTo(upperTime) == 1){
            assessZeroScore(detail,"当前运行时间:"+sec+"秒，超过了过去 "+days+" 天的平均值 "+avgSec+" 的"+n +"%",param,false);
        }


    }
}














