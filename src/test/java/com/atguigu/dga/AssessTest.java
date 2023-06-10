package com.atguigu.dga;

import com.atguigu.dga.assess.bean.GovernanceAssessDetail;
import com.atguigu.dga.assess.service.GovernanceAssessDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AssessTest {

    @Autowired
    private GovernanceAssessDetailService governanceAssessDetailService;

    @Test
    public void testAssess(){

        governanceAssessDetailService.assess("2023-06-09");

    }

    /*
        正则表达式
            ^: 开头
            $: 结尾
            |: 或
            \w: 匹配任意一个 a-z,A-Z._ 字符
            \d: 匹配任意一个数字
            .: 匹配任意一个字符
            +: 匹配多次
     */
    @Test
    public void regexTest(){

        // 定义正则表达式对象

        // 使用对象验证字符串是否符合规则

    }


}
