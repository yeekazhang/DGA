package com.atguigu.dga.meta.mapper;

import com.atguigu.dga.meta.bean.PageTableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 元数据表 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2023-06-03
 */
@Mapper
public interface TableMetaInfoMapper extends BaseMapper<TableMetaInfo> {

    // 查询指定页面的列表
    List<PageTableMetaInfo> queryTableMetaInfoList(@Param("from") Integer from,
                                                   @Param("size") Integer pageSize,
                                                   @Param("db") String db,
                                                   @Param("table") String table,
                                                   @Param("dwLevel") String dwLevel);

    // 查询符合条件的列表总数
    int statsTotalNum(@Param("db") String db,
                      @Param("table") String table,
                      @Param("dwLevel") String dwLevel);



}
