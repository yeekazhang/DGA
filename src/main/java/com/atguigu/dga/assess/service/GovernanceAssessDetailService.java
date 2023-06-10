package com.atguigu.dga.assess.service;

import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 治理考评结果明细 服务类
 *
 *      考评的主要业务模型
 * </p>
 *
 * @author atguigu
 * @since 2023-06-08
 */
public interface GovernanceAssessDetailService extends IService<GovernanceAssessDetail> {

    // 最终只需要把考评的成绩写入到数据库
    void assess(String assessDate);

}
