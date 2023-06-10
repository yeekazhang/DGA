package com.atguigu.dga.assess.assess.storage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.assess.assess.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.Field;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.config.MetaDataUtils;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *  同层次两个表字段重复超过{percent}%，则给0分，其余给10分
 */
@Component("TABLE_SIMILAR")
public class CheckTableFieldSimilar extends AssessorTemplate {

    @Autowired
    private MetaDataUtils metaDataUtils;

    @Override
    protected void assess(AssessParam param, GovernanceAssessDetail detail) {

        // 先获取到percent
        Integer percent = JSON.parseObject(param.getMetric().getMetricParamsJson()).getInteger("percent");

        // 获取当前表的字段结构
        TableMetaInfo tableMetaInfo = param.getTableMetaInfo();
        Set<String> currentTableFields = parseTableFields(tableMetaInfo.getColNameJson());
        String tableName = tableMetaInfo.getSchemaName() + "." + tableMetaInfo.getTableName();
        String dwLevel = tableMetaInfo.getTableMetaInfoExtra().getDwLevel();

        /*
            使用当前表和这个表同一层的其他表字段结构进行对比

            查询所有同一层的其他表的元数据信息
                如果使用jdbc的方式，有几张表就需要发送几次请求
                类似这类大家都需要使用的公共信息，可以在项目初始化时，只查询一次，之后将查询到的结果，缓存到内存中，
                这样在使用时，不发送请求到Mysql
         */
        Map<String, TableMetaInfo> tableMetaInfoMap = metaDataUtils.tableMetaInfoMap;

        // 先准备一个问题结果
        List<JSONObject> result = new ArrayList<>();

        for (Map.Entry<String, TableMetaInfo> entry : tableMetaInfoMap.entrySet()) {

            // 判断是否是同一层，不能和自己比
            if (!entry.getKey().equals(tableName) && entry.getValue().getTableMetaInfoExtra().getDwLevel().equals(dwLevel)){
                // 计算要对比的表的字段结构
                Set<String> targetTableFields = parseTableFields(entry.getValue().getColNameJson());
                /*
                    对比
                    算两个Set的交集
                 */
                // 交集运算,结果保存在targetTableFields里面
                targetTableFields.retainAll(currentTableFields);

                // 计算相似比例
                BigDecimal currentPercent = BigDecimal.valueOf(targetTableFields.size())
                        .divide(BigDecimal.valueOf(currentTableFields.size()), 2, RoundingMode.HALF_UP)
                        .movePointRight(2);

                // BigDecimal.compareTo(BigDecimal) 返回1代表前者大于后者
                if(currentPercent.compareTo(BigDecimal.valueOf(percent)) == 1){
                    detail.setAssessScore(BigDecimal.ZERO);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("table", entry.getKey());
                    jsonObject.put("percent",currentPercent);
                    jsonObject.put("fields",targetTableFields);
                    result.add(jsonObject);
                }

            }

        }

        if (result.size() > 0){
            detail.setAssessProblem(JSON.toJSONString(result));
        }

    }

    /*
        获取某张表的字段结构
            从表的col_name_json中获取
        如果判断表的字段是相似的或一致的
            使用 name + comment 对比，如果一致，认为是一样的
     */
    private Set<String> parseTableFields(String fieldJson){

        List<Field> fields = JSON.parseArray(fieldJson, Field.class);

        // 获取每一个字段 name_comment
        Set<String> result = fields.stream().map(f -> f.getName() + "_" + f.getComment()).collect(Collectors.toSet());

        return result;

    }
}











