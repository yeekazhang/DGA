package com.atguigu.dga.meta.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.mapper.TableMetaInfoMapper;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    public void initMetaInfo(String db, String assessDate) throws Exception {

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
        saveBatch(tableMetaInfos);

    }

    private void extractMetaInfoFromHdfs(List<TableMetaInfo> tableMetaInfos) throws Exception {

        /*
            准备一个hdfs的客户端对象

            查看数仓中所有表在hdfs的元数据信息，这个用户必须有权限才可以
            直接用hdfs管理员
         */
        FileSystem hdfs = FileSystem.get(new URI(hdfsUri), new Configuration(), hdfsAdmin);

        for (TableMetaInfo tableMetaInfo : tableMetaInfos) {

            // 获取当前表在hdfs存储的路径 表目录
            String tableFsPath = tableMetaInfo.getTableFsPath();
            // 这三个指标无需遍历目录
            FileStatus[] fileStatuses = hdfs.listStatus(new Path(tableFsPath));
            // 统计表中文件的大小
            statsTableSize(fileStatuses, tableMetaInfo, hdfs);
            // 继续封装三个参数
            FsStatus status = hdfs.getStatus();
            tableMetaInfo.setFsCapcitySize(status.getCapacity());
            tableMetaInfo.setFsUsedSize(status.getUsed());
            tableMetaInfo.setFsRemainSize(status.getRemaining());
        }
    }

    /*
        遍历表目录，统计目录中所有文件大小之和
     */
    private void statsTableSize(FileStatus[] fileStatuses, TableMetaInfo tableMetaInfo, FileSystem hdfs) throws IOException {

        for (FileStatus fileStatus : fileStatuses) {

            // 判断表目录下的这个文件是目录还是直接就是文件
            if (fileStatus.isFile()) {
                /*
                    直接统计 运算时，要避免双方为null，可以给bean的属性初始化
                    为下面计算的四个bean的属性初始化默认值
                 */
                tableMetaInfo.setTableSize(tableMetaInfo.getTableSize() + fileStatus.getLen());
                tableMetaInfo.setTableSize(tableMetaInfo.getTableTotalSize() + fileStatus.getLen() * fileStatus.getReplication());
                // 取当前表目录中的某个文件的最大访问时间，作为表目录的最大访问时间
                tableMetaInfo.getTableLastModifyTime().setTime(
                        Math.max(tableMetaInfo.getTableLastModifyTime().getTime(), fileStatus.getModificationTime())
                );
                tableMetaInfo.getTableLastAccessTime().setTime(
                        Math.max(tableMetaInfo.getTableLastAccessTime().getTime(), fileStatus.getAccessTime())
                );
            } else {

                // 当前不是文件，是一个目录。 继续遍历
                // 列出目录下所有的文件
                FileStatus[] subFileStatus = hdfs.listStatus(fileStatus.getPath());
                // 递归 所有涉及到文件系统的遍历，都是递归
                statsTableSize(subFileStatus, tableMetaInfo, hdfs);

            }

        }

    }

    public List<TableMetaInfo> extractMetaInfoFromHive(String db, String assessDate) throws Exception {

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
    private TableMetaInfo createTableMetaInfo(Table tableMeta) {

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
        tableMetaInfo.setTableCreateTime(tableMeta.getCreateTime() + "");
        tableMetaInfo.setTableType(tableMeta.getTableType());
        tableMetaInfo.setTableBucketColsJson(JSON.toJSONString(sd.getBucketCols()));
        tableMetaInfo.setTableBucketNum(Long.parseLong(sd.getNumBuckets() + ""));
        tableMetaInfo.setTableSortColsJson(JSON.toJSONString(sd.getSortCols()));


        return tableMetaInfo;
    }


}









