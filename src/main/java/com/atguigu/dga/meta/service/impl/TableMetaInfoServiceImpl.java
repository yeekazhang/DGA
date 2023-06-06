package com.atguigu.dga.meta.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.mapper.TableMetaInfoMapper;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private HiveMetaStoreClient client;

    @Value("${hdfs.uri}")
    private String hdfsUri;

    @Value("${hdfs.admin}")
    private String hdfsAdmin;

    @Override
    public void initMetaInfo(String db, String assessDate) {

        // 先把今天生成的数据删除
        remove(new QueryWrapper<TableMetaInfo>().eq("assess_date", assessDate).eq("schema_name", db));

        // 从hive的元数据中抽取表的描述
        List<TableMetaInfo> tableMetaInfos;
        try {
            tableMetaInfos = extractMetaInfoFromHive(db, assessDate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 使用hdfs客户端统计表目录的元数据信息
        extractMetaInfoFromHdfs(tableMetaInfos);


        // 把表的元数据信息写入数据库

    }

    private void extractMetaInfoFromHdfs(List<TableMetaInfo> tableMetaInfos) throws Exception {

        /*
            准备一个hdfs的客户端对象

            查看数仓中所有表在hdfs的元数据信息，这个用户必须有权限才可以
            直接用hdfs管理员
         */
        FileSystem hdfs = FileSystem.get(new URI(hdfsUri), new Configuration(), hdfsAdmin);

        for (TableMetaInfo tableMetaInfo : tableMetaInfos) {



        }
    }

    public List<TableMetaInfo> extractMetaInfoFromHive(String db, String assessDate) throws Exception{

        // 创建最终返回的结果
        List<TableMetaInfo> result = new ArrayList<>();

        // 获取库下所有的表
        List<String> allTables = client.getAllTables(db);

        // 每一张表都需要获取元数据信息
        for (String table : allTables) {

            Table tableMeta = client.getTable(db, table);

            // 执行封装逻辑
            TableMetaInfo tableMetaInfo = createTableMetaInfo(tableMeta);
            result.add(tableMetaInfo);

        }

        return result;
    }

    /*
        方法用来封装 Table tableMeta 中的元数据到 TableMetaInfo
     */
    private TableMetaInfo createTableMetaInfo(Table tableMeta){

        TableMetaInfo tableMetaInfo = new TableMetaInfo();

        tableMetaInfo.setTableName(tableMeta.getTableName());
        tableMetaInfo.setSchemaName(tableMeta.getDbName());

        // 很多信息放在sd里面
        StorageDescriptor sd = tableMeta.getSd();
        // 过滤出列的元数据中指定的字段信息
        SimplePropertyPreFilter simplePropertyPreFilter = new SimplePropertyPreFilter("comment", "name", "type");

        // 获取所有的字段信息，制作为json
        tableMetaInfo.setColNameJson(JSON.toJSONString(sd.getCols(), simplePropertyPreFilter));
        tableMetaInfo.setPartitionColNameJson(JSON.toJSONString(tableMeta.getPartitionKeys(), simplePropertyPreFilter));
        tableMetaInfo.setTableFsOwner(tableMeta.getOwner());
        // 不确定
        tableMetaInfo.setTableParametersJson(JSON.toJSONString(tableMeta.getParameters()));
        tableMetaInfo.setTableComment(tableMeta.getParameters().get("comment"));
        tableMetaInfo.setTableFsPath(sd.getLocation());
        tableMetaInfo.setTableInputFormat(sd.getInputFormat());
        tableMetaInfo.setTableOutputFormat(sd.getOutputFormat());
        tableMetaInfo.setTableRowFormatSerde(JSON.toJSONString(sd.getSerdeInfo()));
        tableMetaInfo.setTableCreateTime(tableMeta.getCreateTime()+"");
        tableMetaInfo.setTableType(tableMeta.getTableType());
        tableMetaInfo.setTableBucketColsJson(JSON.toJSONString(sd.getBucketCols()));
        tableMetaInfo.setTableBucketNum(Long.parseLong(sd.getNumBuckets()+""));
        tableMetaInfo.setTableSortColsJson(JSON.toJSONString(sd.getSortCols()));



        return tableMetaInfo;
    }


}









