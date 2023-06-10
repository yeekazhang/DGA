package com.atguigu.dga.assess.assess.spec;

import com.atguigu.dga.assess.assess.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component("HAVE_TABLE_COMMENT")
public class CheckTableComment extends AssessorTemplate {
    @Override
    protected void assess(AssessParam param, GovernanceAssessDetail detail) {

        String tableComment = param.getTableMetaInfo().getTableComment();

        if (StringUtils.isBlank(tableComment)){

            assessZeroScore(detail,"表缺少备注!", param,false);
        }
    }
}
