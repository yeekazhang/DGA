package com.atguigu.dga.meta.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.meta.bean.PageTableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import com.atguigu.dga.meta.service.TableMetaInfoExtraService;
import com.atguigu.dga.meta.service.TableMetaInfoService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 元数据表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-06-03
 */
@RestController
@RequestMapping("/tableMetaInfo")
public class TableMetaInfoController {

    @Autowired
    private TableMetaInfoService metaInfoService;

    @Autowired
    private TableMetaInfoExtraService metaInfoExtraService;

    /*
        /tableMetaInfo/init-tables/a/2022-02-05
     */
    @PostMapping("/init-tables/{db}/{assessDate}")
    public Object manualInitMetaInfoByDbName(@PathVariable("db") String db, @PathVariable("assessDate") String date) throws Exception {

        // 先对这个库下所有的表生成今天考评日期的元数据信息
        metaInfoService.initMetaInfo(db, date);

        // 再对这个库下的所有表生成辅助信息
        metaInfoExtraService.initExtraMetaInfo(db);

        // 调用业务模型处理请求
        return "ok";
    }

    /*
        /tableMetaInfo/table-list

            参数：pageNo, pageSize, tableName, schemaName, dwlevel

            前端要求返回数据格式的最外层的是：
                {  } java处理方法return的应该是一个Bean 或 Map 或 JSONObject
                [  ] java处理方法return的应该是一个List 或 JSONObject

            分页的规律:  pageSize = 20
                       pageNo = 1, 从第 1 条数据开始返回，返回之后的20条
                       pageNo = 2, 从第 21 条数据开始返回，返回之后的20条

                       from = (pageNo - 1) * 20 + 1

     */
    @GetMapping("/table-list")
    public Object queryMetaInfoList(Integer pageNo, Integer pageSize, String tableName, String schemaName, String dwLevel){

        JSONObject result = new JSONObject();

        // 计算返回数据的其实行数
        int from = (pageNo - 1) * pageSize;

        // 调用service查询列表数据
        List<PageTableMetaInfo> metaInfos = metaInfoService.queryTableMetaInfoList(from, pageSize, tableName, schemaName, dwLevel);

        // 查询符合条件的总数
        int num = metaInfoService.statsTotalNum(tableName, schemaName, dwLevel);

        // 按照要求，封装返回的数据
        result.put("total", num);
        result.put("list", metaInfos);

        return result;
    }


    @GetMapping("/table/{id}")
    public TableMetaInfo queryTableMetaInfo(@PathVariable("id") Long id){

        // 根据id查询TableMetaInfo对象
        TableMetaInfo result = metaInfoService.getById(id);

        // 按照库名和表名查询额外的元数据信息
        TableMetaInfoExtra metaInfoExtra = metaInfoExtraService.getOne(new QueryWrapper<TableMetaInfoExtra>()
                .eq("table_name", result.getTableName())
                .eq("schema_name", result.getSchemaName())
        );

        result.setTableMetaInfoExtra(metaInfoExtra);

        return result;
    }
}





















