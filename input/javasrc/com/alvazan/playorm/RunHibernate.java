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
		Map<String, Object> props = new HashMap<String, Object>();
//		if(args.length != 3)
//			throw new IllegalArgumentException("Arguments must be <seeds> <clusterName> <testtype> where " +
//					"testtype is 'writedata' or 'betweenquery' or 'otherquery' or 'innerjoin'");
//		String clusterName = args[0];
//		String seeds = args[1];
//		String testType = args[2];
		String seeds = "localhost:9160";
		String testType = "writenojoin";
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("inmemory"); 
		
		if("writenojoin".equalsIgnoreCase(testType)) {
			WriteData d = new WriteData(new HibernateNoSqlListener(factory));
			d.startTest();
		} else if("writejoin".equalsIgnoreCase(testType)) {
			
		} else if("betweenquery".equalsIgnoreCase(testType)) {
			runBetweenQuery(factory);
		} else if("otherquery".equals(testType)) {
			runOtherQuery(factory);
		} else if("innerjoin".equals(testType)) {
			
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
