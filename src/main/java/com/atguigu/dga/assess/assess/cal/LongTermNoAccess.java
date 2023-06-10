package com.atguigu.dga.assess.assess.cal;

import com.alibaba.fastjson.JSON;
import com.atguigu.dga.assess.assess.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 一张表{days}天内没有访问 则给0分 ， 其余给10
 */
@Component("LONGTERM_NO_ACCESS")
public class LongTermNoAccess extends AssessorTemplate {

    @Override
    protected void assess(AssessParam param, GovernanceAssessDetail detail) throws Exception {

        // 阈值
        Integer days = JSON.parseObject(param.getMetric().getMetricParamsJson()).getInteger("days");

        // 获取最后一次的访问时间和今天的考评日期做差，判断差值是多少天
        Timestamp tableLastAccessTime = param.getTableMetaInfo().getTableLastAccessTime();

        // 需要把 字符串格式的考评日期 2023-06-09 转换为 ts
        Date date = DateUtils.parseDate(param.getAssessDate(), "yyyy-MM-dd");

        // 做差，得到结果是毫秒差值
        long dif = Math.abs(tableLastAccessTime.getTime() - date.getTime());


        /*
            把毫秒换算为天
            convert(long sourceDuration, TimeUnit sourceUnit)
                    sourceDuration: 要换算的数值
                    sourceUnit: 要换算的时间单位
         */
        long difDays = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS);

        if (difDays > days){
            assessZeroScore(detail, "已经 " + difDays + " 天未被访问,超过了阈值: " + days, param, false);
        }

    }
}













