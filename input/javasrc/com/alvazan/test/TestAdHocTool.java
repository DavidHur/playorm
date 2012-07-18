package com.alvazan.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.alvazan.orm.api.base.DbTypeEnum;
import com.alvazan.orm.api.spi.db.Column;
import com.alvazan.orm.api.spi.db.Row;
import com.alvazan.orm.api.spi.layer2.DboColumnMeta;
import com.alvazan.orm.api.spi.layer2.DboDatabaseMeta;
import com.alvazan.orm.api.spi.layer2.DboTableMeta;
import com.alvazan.orm.api.spi.layer2.NoSqlSession;
import com.alvazan.orm.api.spi.layer2.NoSqlSessionFactory;
import com.alvazan.orm.impl.bindings.Bootstrap;

public class TestAdHocTool {

	@Test
	public void testBasic() {
		DboDatabaseMeta metaDb = new DboDatabaseMeta();
		addMetaClassDbo(metaDb, "MyEntity", "cat", "mouse", "dog");
		addMetaClassDbo(metaDb, "OtherEntity", "id", "dean", "declan", "pet", "house");

		NoSqlSessionFactory factory = Bootstrap.createRawInstance(DbTypeEnum.IN_MEMORY, metaDb);
		String sql = "ON /someindex select * FROM MyEntity e WHERE e.cat = \"deano\"";

		NoSqlSession session = factory.createSession();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cat", "deano");
		session.addToIndex("/someindex", "myId", map);
		byte[] myId = "myId".getBytes();
		List<Column> columns = new ArrayList<Column>();
		session.persist("MyEntity", myId, columns);
		
		session.flush();
		
		List<Row> rows = factory.runQuery(sql);
		Assert.assertEquals(1, rows.size());
	}

	private void addMetaClassDbo(DboDatabaseMeta map, String entityName, String ... fields) {
		DboTableMeta meta = new DboTableMeta();
		meta.setColumnFamily(entityName);
		map.addMetaClassDbo(meta);
		
		for(String field : fields) {
			DboColumnMeta fieldDbo = new DboColumnMeta();
			fieldDbo.setup(field, null, String.class, false);
			meta.addField(fieldDbo);
		}
	}
}
