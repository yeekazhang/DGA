package com.atguigu.dga.meta.service.impl;

import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import com.atguigu.dga.meta.mapper.TableMetaInfoExtraMapper;
import com.atguigu.dga.meta.service.TableMetaInfoExtraService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 元数据表附加信息 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-06-06
 */
@Service
public class TableMetaInfoExtraServiceImpl extends ServiceImpl<TableMetaInfoExtraMapper, TableMetaInfoExtra> implements TableMetaInfoExtraService {

    @Autowired
    private HiveMetaStoreClient client;

    @Override
    public void initExtraMetaInfo(String db) throws Exception {

        List<String> allTables = client.getAllTables(db);

    }
}
