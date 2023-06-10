package com.atguigu.dga.assess.assess.quality;

import com.alibaba.fastjson.JSON;
import com.atguigu.dga.assess.assess.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.bean.GovernanceMetric;
import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 *
 * 必须日分区表
 *
 * 前一天产出的数据量，超过前x天平均产出量{upper_limit}% ，或低于{lower_limit}%  ，则给0分，其余10分
 */
@Component("TABLE_PRODUCT_VOLUME_MONITOR")
public class TableDataVolumeCheck extends AssessorTemplate {

    @Value("${hdfs.uri}")
    private String hdfsUri;

    @Value("${hdfs.admin}")
    private String hdfsAdmin;

    @Override
    protected void assess(AssessParam param, GovernanceAssessDetail detail) throws Exception {


        // 先取参数
        GovernanceMetric metric = param.getMetric();
        Integer days = JSON.parseObject(metric.getMetricParamsJson()).getInteger("days");
        Integer upperLimit = JSON.parseObject(metric.getMetricParamsJson()).getInteger("upper_limit");
        Integer lowerLimit = JSON.parseObject(metric.getMetricParamsJson()).getInteger("lower_limit");

        // 先判断是否是日分区表
        TableMetaInfo tableMetaInfo = param.getTableMetaInfo();
        String lifecycleType = tableMetaInfo.getTableMetaInfoExtra().getLifecycleType();

        // 准备客户端
        FileSystem.get(new URI(hdfsUri))

        if(MetaConstant.LIFECYCLE_TYPE_DAY.equals(lifecycleType)) {

            // 统计当天的数据产量

        }

    }

    // 统计某个表下某一天分区的数据总量
    private void statPartitionDataSize(FileSystem hdfs, Long size, int offset ){

    }
}















