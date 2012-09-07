package com.alvazan.playorm;

import java.util.HashMap;
import java.util.Map;

import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alvazan.orm.api.base.Bootstrap;
import com.alvazan.orm.api.base.DbTypeEnum;
import com.alvazan.orm.api.base.NoSqlEntityManager;
import com.alvazan.orm.api.base.NoSqlEntityManagerFactory;
import com.alvazan.orm.api.z8spi.KeyValue;
import com.alvazan.orm.api.z8spi.iter.Cursor;
import com.alvazan.perftest.WriteData;
import com.alvazan.perftest.db.NoJoinEntity;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.AstyanaxContext.Builder;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ConsistencyLevel;

public class RunPerformanceTest {

	private static final Logger log = LoggerFactory.getLogger(RunPerformanceTest.class);
	
	public static void main(String[] args) throws InterruptedException {
		Map<String, Object> props = new HashMap<String, Object>();
//		if(args.length != 3)
//			throw new IllegalArgumentException("Arguments must be <seeds> <clusterName> <testtype> where " +
//					"testtype is 'writedata' or 'betweenquery' or 'otherquery' or 'innerjoin'");
//		String clusterName = args[0];
//		String seeds = args[1];
//		String testType = args[2];
		String clusterName = "cluster";
		String seeds = "localhost:9160";
		String testType = "otherquery";
		createAndAddBestCassandraConfiguration(props , clusterName, "PlayOrmPerfTest", seeds );
		props.put(Bootstrap.AUTO_CREATE_KEY, "create");
		NoSqlEntityManagerFactory factory = Bootstrap.create(DbTypeEnum.CASSANDRA, props, null, null);
		
		if("writenojoin".equalsIgnoreCase(testType)) {
			WriteData d = new WriteData(new NoSqlListener(factory));
			d.startTest();
		} else if("writejoin".equalsIgnoreCase(testType)) {
			
		} else if("betweenquery".equalsIgnoreCase(testType)) {
			runBetweenQuery(factory);
		} else if("otherquery".equals(testType)) {
			runOtherQuery(factory);
		} else if("innerjoin".equals(testType)) {
			
		}
	}

	private static void createAndAddBestCassandraConfiguration(
			Map<String, Object> props, String clusterName, String keyspace2,
			String seeds2) {
		Builder builder = new AstyanaxContext.Builder()
	    .forCluster(clusterName)
	    .forKeyspace(keyspace2)
	    .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()      
	        .setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
	    )
	    .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("MyConnectionPool")
	        .setMaxConnsPerHost(15)
	        .setInitConnsPerHost(10)
	        .setSeeds(seeds2)
	    )
	    .withConnectionPoolMonitor(new CountingConnectionPoolMonitor());
		
		
		if(!"localhost:9160".equals(seeds2)) {
			if(!seeds2.contains(","))
				throw new IllegalArgumentException("You must specify a comma delimited list of seeds OR 'localhost:9160' as the seed");
			//for a multi-node cluster, we want the test suite using quorum on writes and
			//reads so we have no issues...
			AstyanaxConfigurationImpl config = new AstyanaxConfigurationImpl();
			config.setDefaultWriteConsistencyLevel(ConsistencyLevel.CL_QUORUM);
			config.setDefaultReadConsistencyLevel(ConsistencyLevel.CL_QUORUM);
			builder = builder.withAstyanaxConfiguration(config);
		}		
		props.put(Bootstrap.CASSANDRA_BUILDER, builder);		
	}

	private static void runOtherQuery(NoSqlEntityManagerFactory factory) {
		for(int i = 0; i < 5; i++) {
			runOtherQueryOnce(factory);
		}
	}
	
	private static void runOtherQueryOnce(NoSqlEntityManagerFactory factory) {
		NoSqlEntityManager mgr = factory.createEntityManager();

		int logEveryN = 20;
		
		long start = System.currentTimeMillis();
		Cursor<KeyValue<NoJoinEntity>> cursor = NoJoinEntity.findOther(mgr, null, 30, 40);
		int count = 0;
		long totalShares = 0;
		while(cursor.next()) {
			NoJoinEntity entity = cursor.getCurrent().getValue();
			totalShares += entity.getNumShares();
			count++;
			if(count % logEveryN == 0) 
				Log.info("total count read="+count);
		}
		
		long total = System.currentTimeMillis() - start;
		log.info("total query time="+total+" total shares="+totalShares);		
	}

	private static void runBetweenQuery(NoSqlEntityManagerFactory factory) {
		for(int i = 0; i < 5; i++) {
			runQueryOnce(factory);
		}
	}
	private static void runQueryOnce(NoSqlEntityManagerFactory factory) {
		NoSqlEntityManager mgr = factory.createEntityManager();

		int logEveryN = 50;
		
		long start = System.currentTimeMillis();
		Cursor<KeyValue<NoJoinEntity>> cursor = NoJoinEntity.findBetween(mgr, null, 30, 40);
		int count = 0;
		long totalShares = 0;
		while(cursor.next()) {
			NoJoinEntity entity = cursor.getCurrent().getValue();
			totalShares += entity.getNumShares();
			count++;
			if(count % logEveryN == 0) 
				Log.info("total count read="+count);
		}
		
		long total = System.currentTimeMillis() - start;
		log.info("total query time="+total+" total shares="+totalShares);
	}

}
