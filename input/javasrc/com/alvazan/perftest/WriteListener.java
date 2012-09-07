package com.alvazan.perftest;


public interface WriteListener {

	void flush();

	void saveEntity(Object entity);
}
