package com.alvazan.playorm;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.alvazan.perftest.WriteListener;
import com.alvazan.perftest.WriteListenerCreator;

public class HibernateNoSqlListener implements WriteListenerCreator {

	private EntityManagerFactory factory;

	public HibernateNoSqlListener(EntityManagerFactory factory) {
		this.factory = factory;
	}

	@Override
	public WriteListener createListener() {
		return new HibernateNoSqlWriter(factory);
	}
	
	private static class HibernateNoSqlWriter implements WriteListener {

		private EntityManagerFactory factory;
		private EntityManager mgr;

		public HibernateNoSqlWriter(EntityManagerFactory factory) {
			this.factory = factory;
		}

		@Override
		public void saveEntity(Object entity) {
			mgr.persist(entity);
		}

		@Override
		public void startTransaction() {
			mgr = factory.createEntityManager();
			mgr.getTransaction().begin();
		}

		@Override
		public void commitTransation() {
			mgr.flush();
			mgr.getTransaction().commit();
			mgr.close();
		}
	}

}
