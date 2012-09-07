package com.alvazan.perftest;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alvazan.perftest.db.NoJoinEntity;

public class WriteData {
	private static final Logger log = LoggerFactory.getLogger(WriteData.class);
	
	private static final int NUM_THREADS = 10;
	private static int numRows = 1000000;
	private static final int BATCH_SIZE = 50;
	private static final int LOG_EVERY_N = 1000;
	
	private ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS, new OurFactory());
	private long startTime;
	private WriteListenerCreator listener;
	private int numRowsPerThread;
	private int totalRowsWritten = 0;
	
	public synchronized void addRows() {
		totalRowsWritten++;
		if(totalRowsWritten % LOG_EVERY_N == 0) {
			log.info("Wrote numRows total="+totalRowsWritten);
		}
	}
	
	public WriteData(WriteListenerCreator listener) {
		if(numRows % NUM_THREADS != 0)
			throw new IllegalArgumentException("num rows must be a multiple of num threads");
		this.listener = listener;
		numRowsPerThread = numRows/NUM_THREADS;
	}

	public void startTest() throws InterruptedException {
		log.info("starting test now");
		CountDownLatch latch = new CountDownLatch(NUM_THREADS);
		startTime = System.currentTimeMillis();
		for(int i = 0; i < NUM_THREADS; i++) {
			log.info("spin up thread="+i);
			Runnable r = new SlamDataIn(listener, i, latch, numRowsPerThread);
			exec.execute(r);
		}
		
		latch.await();
		long total = System.currentTimeMillis()-startTime;
		log.info("your test took="+total+" ms");
	}

	private class SlamDataIn implements Runnable {
		private CountDownLatch latch;
		private WriteListener listener;
		private int numRows;
		private Random r = new Random(System.currentTimeMillis());
		private int threadId;
		
		public SlamDataIn(WriteListenerCreator listener, int i, CountDownLatch latch, int numRows) {
			this.threadId = i;
			this.latch = latch;
			this.listener = listener.createListener();
			this.numRows = numRows;
		}

		@Override
		public void run() {
			log.info("Thread id="+threadId+" starting");
			boolean wroteRow = false;
			int rowCount = 0;
			while(rowCount < numRows) {
				rowCount = processBatch(rowCount);
				if(!wroteRow) {
					log.info("threadid="+threadId+" wrote first row");
					wroteRow = true;
				}
			}
			
			latch.countDown();
		}

		private int processBatch(int rowCount) {
			listener.startTransaction();
			for(int i = 0; i < BATCH_SIZE && rowCount < numRows; i++) {
				createAndSaveRow(rowCount);
				addRows();
				rowCount++;
			}
			listener.commitTransation();
			return rowCount;
		}

		private void createAndSaveRow(int rowCount) {
			NoJoinEntity entity = new NoJoinEntity();
			
			//okay, we want 50% of numShares between 10 and 20
			//we want 25% of prices to be below 10 so....
			int num = r.nextInt(10);
			int price = r.nextInt(10);
			if(rowCount % 2 == 0) {
				entity.setNumShares(num+10);
			} else {
				entity.setNumShares(num);
			}
			
			if(rowCount % 4 == 0) {
				entity.setPricePerShare(price);
			} else {
				entity.setPricePerShare(price+20);
			}

			//special case to test small query resultset
			if(rowCount < 10) {
				entity.setNumShares(30+num);
				entity.setPricePerShare(price+60);
			}
			
			listener.saveEntity(entity);
		}
	}
}
