package com.atguigu.dga.meta.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 元数据表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-06-03
 */
@RestController
//@RequestMapping("/meta/tableMetaInfo")
public class TableMetaInfoController {

    /*
        /tableMetaInfo/init-tables/a/2022-02-05
     */
    @PostMapping("/tableMetaInfo/init-tables/{db}/{assessDate}")
    public Object manualInitMetaInfoByDbName(@PathVariable("db") String db, @PathVariable("assessDate") String date){

        // 调用业务模型处理请求
        return "ok";

    }
}
