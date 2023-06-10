package com.atguigu.dga.config;

import com.atguigu.dga.meta.bean.TableMetaInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MetaDataUtils {

    // 存储所有表的元数据信息
    public Map<String, TableMetaInfo> tableMetaInfoMap;
}
