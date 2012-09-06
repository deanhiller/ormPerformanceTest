package com.alvazan.playorm.db;

import com.alvazan.orm.api.base.anno.NoSqlEntity;
import com.alvazan.orm.api.base.anno.NoSqlId;
import com.alvazan.orm.api.base.anno.NoSqlIndexed;
import com.alvazan.orm.api.base.anno.NoSqlManyToOne;
import com.alvazan.orm.api.base.anno.NoSqlPartitionByThisField;
import com.alvazan.orm.api.base.anno.NoSqlQueries;
import com.alvazan.orm.api.base.anno.NoSqlQuery;

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
	private Client client;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getNumSalesTotal() {
		return numSalesTotal;
	}

	public void setNumSalesTotal(long numSalesTotal) {
		this.numSalesTotal = numSalesTotal;
	}

	public int getYearsExperience() {
		return yearsExperience;
	}

	public void setYearsExperience(int yearsExperience) {
		this.yearsExperience = yearsExperience;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
}
