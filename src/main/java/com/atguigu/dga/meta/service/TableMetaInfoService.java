package com.atguigu.dga.meta.service;

import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 元数据表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-06-03
 */
public interface TableMetaInfoService extends IService<TableMetaInfo> {

    // 初始化元数据
    void initMetaInfo(String db, String assessDate);

}
