package com.atguigu.dga.meta.service.impl;

import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import com.atguigu.dga.meta.mapper.TableMetaInfoExtraMapper;
import com.atguigu.dga.meta.service.TableMetaInfoExtraService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
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

    //@Autowired
    private HiveMetaStoreClient client;

    @Override
    public void initExtraMetaInfo(String db) throws Exception {

        // 查询库下所有的表名
        List<String> allTables = client.getAllTables(db);

        // 创建一个集合用于保存要存入的结果
        List<TableMetaInfoExtra> result = new ArrayList<>();

        for (String table : allTables) {
            // 判断当前表的辅助信息是否已经保存过了，未初始化，再初始化
            TableMetaInfoExtra infoExtra = getOne(
                    new QueryWrapper<TableMetaInfoExtra>()
                            .eq("schema_name", db)
                            .eq("table_name", table)
            );

            // 未初始化的就初始化
            if (infoExtra == null) {
                infoExtra = new TableMetaInfoExtra();
                infoExtra.setTableName(table);
                infoExtra.setSchemaName(db);
                infoExtra.setLifecycleType(MetaConstant.LIFECYCLE_TYPE_UNSET);
                infoExtra.setLifecycleDays(-1L);
                infoExtra.setSecurityLevel(MetaConstant.SECURITY_LEVEL_UNSET);
                infoExtra.setDwLevel(getDwLevelByTableName(table));
                infoExtra.setCreateTime(new Timestamp(System.currentTimeMillis()));
                result.add(infoExtra);
            }
        }

        // 保存到数据库
        saveBatch(result);

    }

    /*
        根据表名的命名规范来判断表是哪一层的
     */
    private String getDwLevelByTableName(String tableName) {

        // 统一转换大小写
        String upperTableName = tableName.toUpperCase();

        // 层级都是在前缀体现
        String prefix = upperTableName.substring(0, 5);

        if (prefix.contains(MetaConstant.DW_LEVEL_ODS)) {
            return MetaConstant.DW_LEVEL_ODS;
        } else if (prefix.contains(MetaConstant.DW_LEVEL_DIM)) {
            return MetaConstant.DW_LEVEL_DIM;
        } else if (prefix.contains(MetaConstant.DW_LEVEL_DWD)) {
            return MetaConstant.DW_LEVEL_DWD;
        } else if (prefix.contains(MetaConstant.DW_LEVEL_DWS)) {
            return MetaConstant.DW_LEVEL_DWS;
        } else if (prefix.contains(MetaConstant.DW_LEVEL_ADS)) {
            return MetaConstant.DW_LEVEL_ADS;
        } else if (prefix.contains(MetaConstant.DW_LEVEL_DM)) {
            return MetaConstant.DW_LEVEL_DM;
        } else if (prefix.contains(MetaConstant.DW_LEVEL_OTHER)) {
            return MetaConstant.DW_LEVEL_OTHER;
        } else {
            return MetaConstant.DW_LEVEL_UNSET;
        }
    }
}























