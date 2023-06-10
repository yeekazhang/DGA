package com.atguigu.dga.assess.assess.spec;

import com.alibaba.fastjson.JSON;
import com.atguigu.dga.assess.assess.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.Field;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  字段有备注信息     有备注字段/所有字段 *10分
 */
@Component("HAVE_FIELD_COMMENT")
public class CheckFieldComment extends AssessorTemplate {
    @Override
    protected void assess(AssessParam param, GovernanceAssessDetail detail) {

        // 获取所有的字段[]
        String colNameJson = param.getTableMetaInfo().getColNameJson();
        List<Field> fields = JSON.parseArray(colNameJson, Field.class);

        // 判断没有Field是否有comment
        List<String> noComment = fields
                .stream()
                .filter(f -> StringUtils.isBlank(f.getComment()))
                .map(f -> f.getName())
                .collect(Collectors.toList());

        // 判断并打分
        if(noComment.size() > 0){
            // 有字段缺失注释
            BigDecimal score = BigDecimal.valueOf(fields.size())
                    .subtract(BigDecimal.valueOf(noComment.size()))
                    .divide(BigDecimal.valueOf(fields.size()), 2, RoundingMode.HALF_UP)
                    .movePointRight(1);

            detail.setAssessScore(score);
            detail.setAssessProblem("以下:" + noComment + "字段缺少注释!");

        }

    }
}
