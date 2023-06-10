package com.atguigu.dga.assess.assess.spec;

import com.atguigu.dga.assess.assess.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component("HAVE_BUS_OWNER")
public class CheckBusOwner extends AssessorTemplate {
    @Override
    protected void assess(AssessParam param, GovernanceAssessDetail detail) {

        TableMetaInfoExtra tableMetaInfoExtra = param.getTableMetaInfo().getTableMetaInfoExtra();

        if(StringUtils.isBlank(tableMetaInfoExtra.getBusiOwnerUserName())){
            assessZeroScore(detail, "缺少业务负责人!", param, true);

        }

    }
}
