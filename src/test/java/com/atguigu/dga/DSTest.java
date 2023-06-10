package com.atguigu.dga;

import com.atguigu.dga.config.MetaConstant;
import com.atguigu.dga.ds.bean.TDsTaskInstance;
import com.atguigu.dga.ds.service.TDsTaskInstanceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DSTest {

    @Autowired
    private TDsTaskInstanceService instanceService;

    @Test
    public void testGetMap(){

    }

}
