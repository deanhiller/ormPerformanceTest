package com.alvazan.playorm;

import com.alvazan.orm.api.base.NoSqlEntityManager;
import com.alvazan.orm.api.base.NoSqlEntityManagerFactory;
import com.alvazan.perftest.WriteListener;
import com.alvazan.perftest.WriteListenerCreator;

public class NoSqlListener implements WriteListenerCreator {

	private NoSqlEntityManagerFactory factory;

	public NoSqlListener(NoSqlEntityManagerFactory factory) {
		this.factory = factory;
	}

	@Override
	public WriteListener createListener() {
		return new NoSqlWriteListener(factory);
	}

	private static class NoSqlWriteListener implements WriteListener {
		private NoSqlEntityManager mgr;
		private NoSqlEntityManagerFactory factory;
		public NoSqlWriteListener(NoSqlEntityManagerFactory factory) {
			this.factory = factory;
		}
		@Override
		public void saveEntity(Object entity) {
			mgr.put(entity);
		}
		@Override
		public void startTransaction() {
			mgr = factory.createEntityManager();
		}
		@Override
		public void commitTransation() {
			mgr.flush();
		}
	}
}
