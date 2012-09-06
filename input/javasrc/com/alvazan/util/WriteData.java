package com.alvazan.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteData {
	private static final Logger log = LoggerFactory.getLogger(WriteData.class);
	
	private static final int NUM_THREADS = 50;
	private static int numRows = 100000;
	
	private ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);
	private long startTime;
	private WriteListener listener;
	private int numRowsPerThread;
	public WriteData(WriteListener listener) {
		if(numRows % NUM_THREADS != 0)
			throw new IllegalArgumentException("num rows must be a multiple of num threads");
		this.listener = listener;
		numRowsPerThread = numRows/NUM_THREADS;
	}

	public void startTest() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(NUM_THREADS);
		startTime = System.currentTimeMillis();
		for(int i = 0; i < NUM_THREADS; i++) {
			Runnable r = new SlamDataIn(listener, i, latch, numRowsPerThread);
			exec.execute(r);
		}
		
		latch.await();
		long total = System.currentTimeMillis()-startTime;
		log.info("your test took="+total+" ms");
	}

	private static class SlamDataIn implements Runnable {
		private CountDownLatch latch;
		private WriteListener listener;
		private int numRows;

		public SlamDataIn(WriteListener listener, int i, CountDownLatch latch, int numRows) {
			this.latch = latch;
			this.listener = listener;
			this.numRows = numRows;
		}

		@Override
		public void run() {
			listener.writeRows(numRows);
			latch.countDown();
		}
	}
}
