package com.alvazan.playorm;

import java.util.HashMap;
import java.util.Map;

import com.alvazan.orm.api.base.Bootstrap;
import com.alvazan.orm.api.base.DbTypeEnum;
import com.alvazan.orm.api.base.NoSqlEntityManagerFactory;
import com.alvazan.util.WriteData;

public class RunPerformanceTest {

	public static void main(String[] args) throws InterruptedException {
		if(args.length != 3)
			throw new IllegalArgumentException("Arguments must be <seeds> <clusterName> <testtype> where " +
					"testtype is 'writedata' or 'betweenquery' or 'otherquery' or 'innerjoin'");
		Map<String, Object> props = new HashMap<String, Object>();
		String clusterName = args[0];
		String seeds = args[1];
		String testType = args[2];
		Bootstrap.createAndAddBestCassandraConfiguration(props , clusterName, "PlayOrmPerfTest", seeds );
		NoSqlEntityManagerFactory factory = Bootstrap.create(DbTypeEnum.CASSANDRA, props, null, null);
		
		if("writedata".equalsIgnoreCase(testType)) {
			WriteData d = new WriteData(new NoSqlListener(factory));
			d.startTest();
		} else if("betweenquery".equalsIgnoreCase(testType)) {
			
		} else if("otherquery".equals(testType)) {
			
		} else if("innerjoin".equals(testType)) {
			
		}
	}

}
