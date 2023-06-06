package com.atguigu.dga.meta.service.impl;

import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.mapper.TableMetaInfoMapper;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 元数据表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-06-03
 */
@Service
public class TableMetaInfoServiceImpl extends ServiceImpl<TableMetaInfoMapper, TableMetaInfo> implements TableMetaInfoService {

    @Override
    public void initMetaInfo(String db, String assessDate) {

        // 先把今天生成的数据删除
        remove(new QueryWrapper<TableMetaInfo>().eq("assess_date", assessDate).eq("schema_name", db));

        // 从hive的元数据中抽取表的描述



        // 使用hdfs客户端统计表目录的元数据信息

        // 把表的元数据信息写入数据库

    }
}
