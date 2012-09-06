package com.alvazan.playorm;

import com.alvazan.orm.api.base.NoSqlEntityManager;
import com.alvazan.orm.api.base.NoSqlEntityManagerFactory;
import com.alvazan.util.WriteListener;

public class NoSqlListener implements WriteListener {

	private NoSqlEntityManagerFactory factory;

	public NoSqlListener(NoSqlEntityManagerFactory factory) {
		this.factory = factory;
	}

	@Override
	public void writeRows(int numRows) {
		NoSqlEntityManager mgr = factory.createEntityManager();
		
		//NOW, create lots of stuff!!!!
		
	}

}
