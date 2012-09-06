package com.alvazan.playorm.db;

import com.alvazan.orm.api.base.NoSqlEntityManager;
import com.alvazan.orm.api.base.Query;
import com.alvazan.orm.api.base.anno.NoSqlEntity;
import com.alvazan.orm.api.base.anno.NoSqlId;
import com.alvazan.orm.api.base.anno.NoSqlIndexed;
import com.alvazan.orm.api.base.anno.NoSqlManyToOne;
import com.alvazan.orm.api.base.anno.NoSqlPartitionByThisField;
import com.alvazan.orm.api.base.anno.NoSqlQueries;
import com.alvazan.orm.api.base.anno.NoSqlQuery;
import com.alvazan.orm.api.z8spi.KeyValue;
import com.alvazan.orm.api.z8spi.iter.Cursor;

@NoSqlEntity
@NoSqlQueries({
	@NoSqlQuery(name="findBetween", query="PARTITIONS n(:partition) SELECT n FROM TABLE as n WHERE n.numShares > :low and n.numShares <= :high")
})
public class ClientsSalesAgents {

	@NoSqlId
	private String id;

	@NoSqlIndexed
	private long numSalesTotal;

	@NoSqlIndexed
	private int yearsExperience;

	@NoSqlPartitionByThisField
	@NoSqlManyToOne
	private Client accountNumber;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static Cursor<KeyValue<ClientsSalesAgents>> findBetween(NoSqlEntityManager mgr, String partitionId, long low, long high) {
		Query<ClientsSalesAgents> query = mgr.createNamedQuery(ClientsSalesAgents.class, "findBetween");
		query.setParameter("partition", partitionId);
		query.setParameter("low", low);
		query.setParameter("high", high);
		return query.getResults();
	}

	public static Cursor<KeyValue<ClientsSalesAgents>> findOther(NoSqlEntityManager mgr, String partitionId, long low, long price) {
		Query<ClientsSalesAgents> query = mgr.createNamedQuery(ClientsSalesAgents.class, "findOther");
		query.setParameter("partition", partitionId);
		query.setParameter("low", low);
		query.setParameter("price", price);
		return query.getResults();
	}
}
