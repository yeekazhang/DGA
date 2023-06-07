package com.atguigu.dga.meta.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageTableMetaInfo
{
    private Integer id;
    private String tableName;
    private String schemaName;
    private String tecOwnerUserName;
    private String busiOwnerUserName;
    private String tableComment;
    private long tableSize;
    private long tableTotalSize;
    private Timestamp tableLastAccessTime;
    private Timestamp tableLastModifyTime;
}
