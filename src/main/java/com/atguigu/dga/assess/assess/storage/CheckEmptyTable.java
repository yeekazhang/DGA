package com.atguigu.dga.assess.assess.storage;

import com.atguigu.dga.assess.assess.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

/**
 *  是否为空表
 */
@Component("IS_EMPTY_TABLE")
public class CheckEmptyTable extends AssessorTemplate {

    /*
        获取表的totalsize属性
           totalsize: 统计表在hdfs的目录中所有文件的总大小
     */
    @Override
    protected void assess(AssessParam param, GovernanceAssessDetail detail) {
        Long tableSize = param.getTableMetaInfo().getTableSize();

        // 判断是否空表
        if (tableSize == 0){
            assessZeroScore(detail, "当前表是空表!", param, false);
        }
    }
}
