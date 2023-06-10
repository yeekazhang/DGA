package com.atguigu.dga.ds.mapper;

import com.atguigu.dga.ds.bean.TDsTaskDefinition;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2023-06-09
 */
@Mapper
@DS("ds")
public interface TDsTaskDefinitionMapper extends BaseMapper<TDsTaskDefinition> {

}
