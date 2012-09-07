package com.alvazan.perftest.db;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.Index;

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

/**
 * They call it annotation HELL ;), when we have this object use hibernate AND playOrm annotations, it looks hilarious as
 * there is more annotations than there is code.
 * @author dhiller2
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name="findBetween", query="SELECT n FROM NoJoinEntity as n WHERE n.numShares >= :low and n.numShares < :high"),
	@NamedQuery(name="findOther", query="SELECT n FROM NoJoinEntity as n WHERE n.numShares >= :low and n.pricePerShare >= :price")
})
@NoSqlEntity
@NoSqlQueries({
	@NoSqlQuery(name="findBetween", query="PARTITIONS n(:partition) SELECT n FROM TABLE as n WHERE n.numShares >= :low and n.numShares < :high"),
	@NoSqlQuery(name="findOther", query="PARTITIONS n(:partition) SELECT n FROM TABLE as n WHERE n.numShares >= :low and n.pricePerShare >= :price")
})
public class NoJoinEntity {

	@Id
	@Access(AccessType.PROPERTY)
	@GeneratedValue(strategy=GenerationType.AUTO)
	@NoSqlId
	private String id;
	
	@Index(name="numShares")
	@NoSqlIndexed
	private long numShares;

	@Index(name="pricePerShare")
	@NoSqlIndexed
	private int pricePerShare;
	
	//This is not needed by hibernate, they don't partition.
	//We are comparing ONE partition in noSQL to ONE table in DBMS with the same amount of
	//rows in both the partition as the table.
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
		query.setBatchSize(500);
		query.setParameter("partition", partitionId);
		query.setParameter("low", low);
		query.setParameter("high", high);
		return query.getResults();
	}

	public static Cursor<KeyValue<NoJoinEntity>> findOther(NoSqlEntityManager mgr, String partitionId, long low, int price) {
		Query<NoJoinEntity> query = mgr.createNamedQuery(NoJoinEntity.class, "findOther");
		query.setParameter("partition", partitionId);
		query.setParameter("low", low);
		query.setParameter("price", price);
		return query.getResults();
	}
	
	@SuppressWarnings("unchecked")
	public static List<NoJoinEntity> findBetween(EntityManager mgr, long low, long high) {
		javax.persistence.Query query = mgr.createNamedQuery("findBetween");
		query.setParameter("low", low);
		query.setParameter("high", high);
		return query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public static List<NoJoinEntity> findOther(EntityManager mgr, long low, int price) {
		javax.persistence.Query query = mgr.createNamedQuery("findOther");
		query.setParameter("low", low);
		query.setParameter("price", price);
		return query.getResultList();
	}
}
