package com.atguigu.dga.assess.service.impl;

import com.atguigu.dga.assess.assess.AssessorOld;
import com.atguigu.dga.assess.assess.AssessorTemplate;
import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.bean.GovernanceMetric;
import com.atguigu.dga.assess.mapper.GovernanceAssessDetailMapper;
import com.atguigu.dga.assess.service.GovernanceAssessDetailService;
import com.atguigu.dga.assess.service.GovernanceMetricService;
import com.atguigu.dga.config.MetaDataUtils;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 治理考评结果明细 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-06-08
 */
@Service
public class GovernanceAssessDetailServiceImpl extends ServiceImpl<GovernanceAssessDetailMapper, GovernanceAssessDetail> implements GovernanceAssessDetailService {

    /*
        1 考生进场
                考生： 库下的所有表
        2 出题考试
                题： 考评指标
                    governance_metric下，一共18个
        3 收卷，密封，押运
                把考评的结果GovernanceAssessDetail收集起来
                    List<GovernanceAssessDetail>
                    保存到数据库
     */
    @Autowired
    private TableMetaInfoService tableMetaInfoService;

    @Autowired
    private GovernanceMetricService metricService;

    //@Autowired
    //private AssessorOld assessorold;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MetaDataUtils metaDataUtils;

    @Override
    public void assess(String assessDate) {

        // 先删除当前已经考评过的结果
        remove(new QueryWrapper<GovernanceAssessDetail>().eq("assess_date", assessDate));

        List<GovernanceAssessDetail> result = new ArrayList<>();

        /*
            1 查询所有表的元数据信息
                从数据库的 table_meta_info_extra 和 table_meta_info 查询信息，
                封装为TableMetaInfo(内置了属性TableMetaInfoExtra)

                方式一：参考 TableMetaInfoController.queryTableMetaInfo
                            先查询table_meta_info,再根据查出的table_name,schema_name查询table_meta_info_extra
                            最后set到 TableMetaInfo对象中

                            编码成本低,请求次数多,71次

                方式二：一次性查询，一条sql，必须join，只能自己实现，不能用MybatisPlus
                            编码成本高,请求次数低，1次 选择这种方式
         */
        List<TableMetaInfo> tableMetaInfos = tableMetaInfoService.queryAllTableMeta();

        // 1.5 处理，把查询到的元数据信息，转换为Map : key是表名，value是TableMetaInfo
        if (metaDataUtils.tableMetaInfoMap == null){
            metaDataUtils.tableMetaInfoMap = new HashMap<>();
            for (TableMetaInfo tableMetaInfo : tableMetaInfos) {
                String key = tableMetaInfo.getSchemaName() + "." + tableMetaInfo.getTableName();
                metaDataUtils.tableMetaInfoMap.put(key, tableMetaInfo);
            }
        }

        /*
            2 出题考试
                题： 考评指标
                    governance_metric 下，一共18个
         */
        // 查询所有的考试题
        List<GovernanceMetric> metrics = metricService.list(new QueryWrapper<GovernanceMetric>()
                .eq("is_disabled", '否'));

        /*
            考试
                遍历所有考生（表），让每个考生遍历所有的题
                for( for() )
         */
        for (TableMetaInfo tableMetaInfo : tableMetaInfos) {

            for (GovernanceMetric metric : metrics) {

                /*
                    判断当前考评的指标是什么，是什么就调用什么方法来处理
                    能解决，但是不利于维护
                        违反了一个原则： 开闭原则

                        开闭原则：增加或减少新功能时，不去修改现有的功能
                        低耦合，高内聚

                    模版模式：
                        把做事的一般步骤，抽象为一个模版，定义在一个抽象类中
                        具体做什么事情，由子类实现抽象类
                        符合开闭原则

                 */
//
//                if("HAVE_TEC_OWNER".equals(metric.getMetricCode())){
//                    assessorold.assessHadTecOwner();
//                }else if("HAVE_BUS_OWNER".equals(metric.getMetricCode())) {
//                    assessorold.assessHadBusOwner();
//                }
                // ... 18层

                // 获取指标对应的考评对象
                try {
                   // 封装考评参数
                    AssessParam assessParam = new AssessParam(tableMetaInfo, assessDate, metric);
                    AssessorTemplate template = context.getBean(metric.getMetricCode(), AssessorTemplate.class);
                    GovernanceAssessDetail detail = template.doAssess(assessParam);
                    result.add(detail);
                }catch (Exception e){

                }

            }
        }

        // 3 收卷，密封，押运
        saveBatch(result);


    }
}



















