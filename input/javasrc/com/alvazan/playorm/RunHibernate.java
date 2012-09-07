package com.alvazan.playorm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alvazan.perftest.WriteData;
import com.alvazan.perftest.db.NoJoinEntity;

public class RunHibernate {

	private static final Logger log = LoggerFactory.getLogger(RunHibernate.class);
	
	public static void main(String[] args) throws InterruptedException {
		Map<String, String> props = new HashMap<String, String>();
		if(args.length != 4)
			throw new IllegalArgumentException("Arguments must be <jdbcUrl> <username> <password> <testtype> where " +
					"testtype is 'writenojoin' or 'betweenquery' or 'otherquery' or 'innerjoin'");
		String jdbcUrl = args[0];
		String username = args[1];
		String password = args[2];
		String testType = args[3];
//		String jdbcUrl = "localhost:9160";
//		String username = "sa";
//		String password = "";
//		String testType = "writenojoin";

		props.put("hibernate.connection.username", username);
		props.put("hibernate.connection.password", password);
		props.put("hibernate.connection.url", jdbcUrl);
		//props.put("hibernate.connection.url", "jdbc:h2:mem:test_mem;MVCC=true");
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("postgres", props); 
		
		if("writenojoin".equalsIgnoreCase(testType)) {
			WriteData d = new WriteData(new HibernateNoSqlListener(factory));
			d.startTest();
		} else if("writejoin".equalsIgnoreCase(testType)) {
			
		} else if("betweenquery".equalsIgnoreCase(testType)) {
			runBetweenQuery(factory);
		} else if("otherquery".equals(testType)) {
			runOtherQuery(factory);
		} else if("innerjoin".equals(testType)) {
			
		} else {
			throw new IllegalArgumentException("test type="+testType+" not supported. check the code to see what is supported.");
		}
	}

	private static void runOtherQuery(EntityManagerFactory factory) {
		for(int i = 0; i < 5; i++) {
			runOtherQueryOnce(factory);
		}
	}
	
	private static void runOtherQueryOnce(EntityManagerFactory factory) {
		EntityManager mgr = factory.createEntityManager();

		int logEveryN = 20;
		
		long start = System.currentTimeMillis();
		List<NoJoinEntity> cursor = NoJoinEntity.findOther(mgr, 30, 40);
		int count = 0;
		long totalShares = 0;
		for(NoJoinEntity entity : cursor) {
			totalShares += entity.getNumShares();
			count++;
			if(count % logEveryN == 0) 
				Log.info("total count read="+count);
		}
		
		long total = System.currentTimeMillis() - start;
		log.info("total query time="+total+" total shares="+totalShares);		
	}

	private static void runBetweenQuery(EntityManagerFactory factory) {
		for(int i = 0; i < 5; i++) {
			runQueryOnce(factory);
		}
	}
	private static void runQueryOnce(EntityManagerFactory factory) {
		EntityManager mgr = factory.createEntityManager();

		int logEveryN = 50;
		
		long start = System.currentTimeMillis();
		List<NoJoinEntity> cursor = NoJoinEntity.findBetween(mgr, 30, 40);
		int count = 0;
		long totalShares = 0;
		for(NoJoinEntity entity : cursor) {
			totalShares += entity.getNumShares();
			count++;
			if(count % logEveryN == 0) 
				Log.info("total count read="+count);
		}
		
		long total = System.currentTimeMillis() - start;
		log.info("total query time="+total+" total shares="+totalShares);
	}

}
