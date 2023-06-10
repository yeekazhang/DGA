package com.atguigu.dga.assess.bean;

import com.atguigu.dga.meta.bean.TableMetaInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessParam {

    // 考生的元数据信息
    private TableMetaInfo tableMetaInfo;

    // 考评日期
    private String assessDate;

    // 考评指标（考试题）
    private GovernanceMetric metric;


}
