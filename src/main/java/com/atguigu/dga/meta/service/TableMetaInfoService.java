package com.atguigu.dga.meta.service;

import com.atguigu.dga.meta.bean.PageTableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 元数据表 服务类
 *      每天为每张表都生成一条元数据考评信息
 * </p>
 *
 * @author atguigu
 * @since 2023-06-03
 */
public interface TableMetaInfoService extends IService<TableMetaInfo> {

    // 初始化元数据
    void initMetaInfo(String db, String assessDate) throws Exception;

    // 查询元数据列表
    List<PageTableMetaInfo> queryTableMetaInfoList(Integer from, Integer pageSize, String tableName, String schemaName, String dwLevel);

    // 查询元数据列表总数
    int statsTotalNum(String tableName, String schemaName, String dwLevel);

    // 查询今天要考评的所有表元数据
    List<TableMetaInfo> queryAllTableMeta();


}
