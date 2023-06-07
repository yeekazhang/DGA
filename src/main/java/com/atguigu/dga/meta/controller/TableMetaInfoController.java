package com.atguigu.dga.meta.controller;

import com.atguigu.dga.meta.service.TableMetaInfoExtraService;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

                       from = (pageNo

     */
    @GetMapping("/table-list")
    public Object queryMetaInfoList(Integer pageNo, Integer pageSize, String tableName, String schemaName, String dwlevel){

        JSONObject result = new JSONObject();

        return result;
    }
}





















