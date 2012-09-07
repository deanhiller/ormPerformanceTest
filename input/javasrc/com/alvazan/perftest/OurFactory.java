package com.alvazan.perftest;

import java.util.concurrent.ThreadFactory;

public class OurFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setName("playOrmWriteThread");
		return t;
	}

}
