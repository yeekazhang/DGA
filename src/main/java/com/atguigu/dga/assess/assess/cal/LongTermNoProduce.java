package com.atguigu.dga.assess.assess.cal;

import com.alibaba.fastjson.JSON;
import com.atguigu.dga.assess.assess.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 一张表{days}天内没有产出数据  则给0分，其余给10
 *      通过hdfs的上一次修改时间来判断
 *          最后一次写操作时间
 */
@Component("LONGTERM_NO_PRODUCE")
public class LongTermNoProduce extends AssessorTemplate {

    @Override
    protected void assess(AssessParam param, GovernanceAssessDetail detail) throws Exception {

        // 阈值
        Integer days = JSON.parseObject(param.getMetric().getMetricParamsJson()).getInteger("days");

        // 获取最后一次的修改时间和今天的考评日期做差，判断差值是多少天
        Timestamp tableLastModifyTime = param.getTableMetaInfo().getTableLastModifyTime();

        // 转换修改时间为 日期时间类型
        LocalDateTime lastModifyDateTime= LocalDateTime.ofInstant(Instant.ofEpochMilli(tableLastModifyTime.getTime()), ZoneId.of("Asia/Shanghai"));

        // 把考评日期和阈值计算，求一个极限时间 LocalDate.parse：默认解析 yyyy-MM-dd
        LocalDateTime limitDatetime = LocalDate.parse(param.getAssessDate()).minusDays(days).atStartOfDay();

        if(lastModifyDateTime.isBefore(limitDatetime)){
            assessZeroScore(detail, "上一次访问是" + lastModifyDateTime + ",长时间未被访问,超过了阈值: " + days, param, false);
        }


    }
}













