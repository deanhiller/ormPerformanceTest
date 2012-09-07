package com.alvazan.perftest.db;

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
	@NoSqlQuery(name="findWithJoin", query="PARTITIONS agent(:partition),c(:partition) SELECT c FROM TABLE as c " +
			"INNER JOIN c.agentForThisContact as agent WHERE c.age > :age and agent.yearsExperience > :years")
})
public class Contact {

	@NoSqlId
	private String id;

	@NoSqlIndexed
	private long age;

	@NoSqlPartitionByThisField
	@NoSqlManyToOne
	private Client client;
	
	@NoSqlManyToOne
	@NoSqlIndexed
	private ClientsSalesAgents agentForThisContact;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getAge() {
		return age;
	}

	public void setAge(long age) {
		this.age = age;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client accountNumber) {
		this.client = accountNumber;
	}

	public ClientsSalesAgents getAgentForThisContact() {
		return agentForThisContact;
	}

	public void setAgentForThisContact(ClientsSalesAgents agentForThisContact) {
		this.agentForThisContact = agentForThisContact;
	}

	public static Cursor<KeyValue<ClientsSalesAgents>> findBetween(NoSqlEntityManager mgr, Client partitionId, long age, int yearsExperience) {
		Query<ClientsSalesAgents> query = mgr.createNamedQuery(ClientsSalesAgents.class, "findBetween");
		query.setParameter("partition", partitionId);
		query.setParameter("age", age);
		query.setParameter("years", yearsExperience);
		return query.getResults();
	}
}
