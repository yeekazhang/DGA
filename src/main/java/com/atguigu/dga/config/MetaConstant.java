package com.atguigu.dga.config;

/*
    常量一般用接口封装
        没有实例化的风险
 */
public interface MetaConstant
{
    // 存储gmall数仓分层的校验规则
    String gmallOdsRegex = "^ods_\\w+_(inc|full)$";
    String gmallDwdRegex = "^dwd_(trade|interaction|traffic|user|tool)_\\w+_(inc|full|acc)$";
    String gmallDwsRegex = "^dws_(trade|interaction|traffic|user|tool)_\\w+_(\\d+d|nd|td)$";
    String gmallDimRegex = "^dim_\\w+_(zip|full)$";
    String gmallAdsRegex = "^ads_(trade|interaction|traffic|user|tool)_\\w$";
    // Data Market: 模棱两可的，可以放入DM层
    String gmallDMRegex = "^dm_\\w$";


    String schema_name="";
    //存储周期
    String LIFECYCLE_TYPE_PERM="PERM";  //永久
    String LIFECYCLE_TYPE_ZIP="ZIP";   //拉链
    String LIFECYCLE_TYPE_DAY="DAY";  //日分区
    String LIFECYCLE_TYPE_OTHER="OTHER";  //其他
    String LIFECYCLE_TYPE_UNSET="UNSET";  //未设置

    //安全级别
    String SECURITY_LEVEL_UNSET="UNSET";  //未设置
    String SECURITY_LEVEL_PUBLIC="PUBLIC";  //公开
    String SECURITY_LEVEL_INTERNAL="INTERNAL";  //内部
    String SECURITY_LEVEL_SECRET="SECRET";  //保密
    String SECURITY_LEVEL_HIGH="HIGH";  //高度机密

    //层级 对应页面上的下拉框中的选项
    String DW_LEVEL_UNSET = "UNSET";
    String DW_LEVEL_ODS = "ODS";
    String DW_LEVEL_DWD = "DWD";
    String DW_LEVEL_DWS = "DWS";
    String DW_LEVEL_DIM = "DIM";
    // DM：四不像放入DM
    String DW_LEVEL_DM = "DM";
    String DW_LEVEL_ADS = "ADS";
    String DW_LEVEL_OTHER = "OTHER";

    //DS状态码
    Integer TASK_STATE_SUCCESS = 7;
    Integer TASK_STATE_FAILED = 6;
}
