package com.alvazan.perftest;


public interface WriteListener {

	void saveEntity(Object entity);

	void startTransaction();

	void commitTransation();
}
