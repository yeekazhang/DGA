package com.atguigu.dga.assess.assess.spec;

import com.atguigu.dga.assess.assess.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("TABLE_NAME_STANDARD")
public class CheckTableLegal extends AssessorTemplate {

    /*
        表名是否按照分层的规则命名创建
        一般情况，检测一个字符是否符合规则，都是正则表达式
     */
    @Override
    protected void assess(AssessParam param, GovernanceAssessDetail detail) {

        // 获取表名
        TableMetaInfo tableMetaInfo = param.getTableMetaInfo();
        String tableName = tableMetaInfo.getTableName();

        // 获取库名
        String schemaName = tableMetaInfo.getSchemaName();

        // 获取表的所属层
        String dwLevel = tableMetaInfo.getTableMetaInfoExtra().getDwLevel();

        // 先判断是否设置了层级
        if (MetaConstant.DW_LEVEL_UNSET.equals(dwLevel)) {
            detail.setAssessScore(BigDecimal.ZERO);
            detail.setAssessProblem("未设置层级!");
            detail.setGovernanceUrl(param.getMetric().getGovernanceUrl().replace("{id}", param.getTableMetaInfo().getId().toString()));
        }else if(MetaConstant.DW_LEVEL_OTHER.equals(dwLevel)){
            detail.setAssessScore(BigDecimal.valueOf(5));
            detail.setAssessProblem("其他层级,请规范分层!");
            detail.setGovernanceUrl(param.getMetric().getGovernanceUrl().replace("{id}", param.getTableMetaInfo().getId().toString()));
        }else if("gmall".equals(schemaName)){
            switch (dwLevel) {
                case MetaConstant.DW_LEVEL_ODS: check(tableName, MetaConstant.gmallOdsRegex, param, detail); break;
                case MetaConstant.DW_LEVEL_ADS: check(tableName, MetaConstant.gmallAdsRegex, param, detail); break;
                case MetaConstant.DW_LEVEL_DIM: check(tableName, MetaConstant.gmallDimRegex, param, detail); break;
                case MetaConstant.DW_LEVEL_DWD: check(tableName, MetaConstant.gmallDwdRegex, param, detail); break;
                case MetaConstant.DW_LEVEL_DWS: check(tableName, MetaConstant.gmallDwsRegex, param, detail); break;
                case MetaConstant.DW_LEVEL_DM: check(tableName, MetaConstant.gmallDMRegex , param, detail); break;
            }
        }


    }

    // 校验的方法
    private void check(String tableName, String regex, AssessParam param, GovernanceAssessDetail detail) {

        Pattern pattern = Pattern.compile(regex);

        // 校验
        Matcher matcher = pattern.matcher(tableName);
        if (!matcher.matches()) {
            detail.setAssessScore(BigDecimal.ZERO);
            detail.setAssessProblem("表名不符合当前层的命名规则!");
            detail.setGovernanceUrl(param.getMetric().getGovernanceUrl().replace("{id}", param.getTableMetaInfo().getId().toString()));
        }
    }
}











