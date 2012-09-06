package com.alvazan.util;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alvazan.orm.api.base.NoSqlEntityManagerFactory;

public class WriteData {

	private static final int NUM_THREADS = 50;
	private static final int NUM_ROWS = 100000;
	private static final int LOG_EVERY_N = 300;
	
	private ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);
	private long startTime;
	private WriteListener listener;
	
	public WriteData(WriteListener listener) {
		if(NUM_ROWS % NUM_THREADS != 0)
			throw new IllegalArgumentException("num rows must be a multiple of num threads");
		this.listener = listener;
	}

	public void startTest() {

		startTime = System.currentTimeMillis();
		for(int i = 0; i < NUM_THREADS; i++) {
			Runnable r = new SlamDataIn(listener, i);
			exec.execute(r);
		}
		
	}

	private static class SlamDataIn implements Runnable {

		public SlamDataIn(WriteListener listener, int i) {
		}

		@Override
		public void run() {
			
		}
		
	}
}
