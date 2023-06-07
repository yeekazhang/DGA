package com.atguigu.dga.config;

import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 容器配置类，配置容器中要创建的对象
 */
//@Configuration
public class DgaConfig {

    @Value("${hive.metastore.uris}")
    private String metaStoreUrl;

    @Bean
    public HiveMetaStoreClient createHiveMetaStoreClient() {

        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        // 配置客户端要连接的HiveMetaStore服务的地址
        conf.setStrings("hive.metastore.uris", metaStoreUrl);

        HiveMetaStoreClient hiveMetaStoreClient = null;
        try {
            hiveMetaStoreClient = new HiveMetaStoreClient(conf);
        } catch (MetaException e) {
            throw new RuntimeException(e);
        }

        return hiveMetaStoreClient;
    }
}
