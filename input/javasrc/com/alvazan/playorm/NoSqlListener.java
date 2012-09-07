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
		return new NoSqlWriteListener(factory.createEntityManager());
	}

	private static class NoSqlWriteListener implements WriteListener {
		private NoSqlEntityManager mgr;
		public NoSqlWriteListener(NoSqlEntityManager mgr) {
			this.mgr = mgr;
		}
		@Override
		public void flush() {
			mgr.flush();
		}
		@Override
		public void saveEntity(Object entity) {
			mgr.put(entity);
		}
	}
}
