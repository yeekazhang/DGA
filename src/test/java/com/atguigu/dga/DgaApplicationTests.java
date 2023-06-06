package com.atguigu.dga;

import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DgaApplicationTests {

	@Autowired
	private HiveMetaStoreClient client;

	@Test
	void contextLoads() {
	}

	@Test
	void testHiveMetaStoreClient() throws Exception {

		// 获取库下所有的表
		System.out.println(client.getAllTables("gmall"));

		// 获取单个表的所有元数据信息
		Table table = client.getTable("gmall", "ods_log_inc");
	}

}
