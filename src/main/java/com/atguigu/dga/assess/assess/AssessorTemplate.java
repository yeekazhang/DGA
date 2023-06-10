package com.atguigu.dga.assess.assess;

import com.atguigu.dga.assess.bean.AssessParam;
import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.bean.GovernanceMetric;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;

/**
 * 拥有抽象方法的类，成为抽象类
 * 抽象类中可以有具体方法
 */
public abstract class AssessorTemplate {

    /*
        声明考评的一般流程
            1 获取考生的信息   TableMetaInfo
            2 考生做题        根据不同的题，调用不同的具体实现，由子类提供
            3 考生做题的结果
            4 卷子返回
     */
    public GovernanceAssessDetail doAssess(AssessParam param) {
        GovernanceAssessDetail detail = new GovernanceAssessDetail();
        // 1 获取考生信息
        TableMetaInfo tableMetaInfo = param.getTableMetaInfo();
        TableMetaInfoExtra tableMetaInfoExtra = tableMetaInfo.getTableMetaInfoExtra();
        GovernanceMetric metric = param.getMetric();

        detail.setAssessDate(param.getAssessDate());
        detail.setTableName(tableMetaInfo.getTableName());
        detail.setSchemaName(tableMetaInfo.getSchemaName());
        detail.setMetricId(metric.getId().toString());
        detail.setMetricName(metric.getMetricName());
        detail.setGovernanceType(metric.getGovernanceType());
        detail.setTecOwner(tableMetaInfoExtra.getTecOwnerUserName());
        // 默认给满分，如果出了问题再修改
        detail.setAssessScore(BigDecimal.TEN);
        detail.setCreateTime(new Timestamp(System.currentTimeMillis()));


        // 2 让考生做题
        try {
            assess(param, detail);
        } catch (Exception e) {
           /*
                不能抛出异常 一旦抛出，整个考评过程终止
            */
            detail.setIsAssessException("1");

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            // 把堆栈异常输出到stringWriter中
            e.printStackTrace(printWriter);
            // 表中的字段最多只存2000个字符
            detail.setAssessExceptionMsg(stringWriter.toString().substring(0, 2000));
        }

        // 3 收回卷子
        return detail;
    }

    protected void assessZeroScore(GovernanceAssessDetail detail, String problem, AssessParam param, boolean replaceUrl) {

        // 打分
        detail.setAssessScore(BigDecimal.ZERO);
        detail.setAssessProblem(problem);

        // 跳转到对应的页面进行整改
        if (replaceUrl) {
            detail.setGovernanceUrl(param.getMetric().getGovernanceUrl().replace("{id}", param.getTableMetaInfo().getId().toString()));
        }
    }


    // 核心考评
    // 调用处理逻辑计算，计算后为 assess_score,assess_problem,assess_comment,governance_url 赋值
    protected abstract void assess(AssessParam param, GovernanceAssessDetail detail) throws Exception;

}
