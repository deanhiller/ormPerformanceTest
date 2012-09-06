package com.alvazan.playorm.db;

import com.alvazan.orm.api.base.NoSqlEntityManager;
import com.alvazan.orm.api.base.Query;
import com.alvazan.orm.api.base.anno.NoSqlEntity;
import com.alvazan.orm.api.base.anno.NoSqlId;
import com.alvazan.orm.api.base.anno.NoSqlIndexed;
import com.alvazan.orm.api.base.anno.NoSqlPartitionByThisField;
import com.alvazan.orm.api.base.anno.NoSqlQueries;
import com.alvazan.orm.api.base.anno.NoSqlQuery;
import com.alvazan.orm.api.z8spi.KeyValue;
import com.alvazan.orm.api.z8spi.iter.Cursor;

@NoSqlEntity
@NoSqlQueries({
	@NoSqlQuery(name="findBetween", query="PARTITIONS n(:partition) SELECT n FROM TABLE as n WHERE n.numShares > :low and n.numShares <= :high"),
	@NoSqlQuery(name="findOther", query="PARTITIONS n(:partition) SELECT n FROM TABLE as n WHERE n.numShares > :low and n.pricePerShares < :price")
})
public class NoJoinEntity {

	@NoSqlId
	private String id;
	
	@NoSqlIndexed
	private long numShares;
	
	@NoSqlIndexed
	private int pricePerShare;
	
	@NoSqlPartitionByThisField
	private String accountNumber;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getNumShares() {
		return numShares;
	}

	public void setNumShares(long numShares) {
		this.numShares = numShares;
	}

	public int getPricePerShare() {
		return pricePerShare;
	}

	public void setPricePerShare(int pricePerShare) {
		this.pricePerShare = pricePerShare;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public static Cursor<KeyValue<NoJoinEntity>> findBetween(NoSqlEntityManager mgr, String partitionId, long low, long high) {
		Query<NoJoinEntity> query = mgr.createNamedQuery(NoJoinEntity.class, "findBetween");
		query.setParameter("partition", partitionId);
		query.setParameter("low", low);
		query.setParameter("high", high);
		return query.getResults();
	}

	public static Cursor<KeyValue<NoJoinEntity>> findOther(NoSqlEntityManager mgr, String partitionId, long low, long price) {
		Query<NoJoinEntity> query = mgr.createNamedQuery(NoJoinEntity.class, "findOther");
		query.setParameter("partition", partitionId);
		query.setParameter("low", low);
		query.setParameter("price", price);
		return query.getResults();
	}
}
