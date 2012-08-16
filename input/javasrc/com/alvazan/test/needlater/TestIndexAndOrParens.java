package com.alvazan.test.needlater;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alvazan.orm.api.base.NoSqlEntityManager;
import com.alvazan.orm.api.base.NoSqlEntityManagerFactory;
import com.alvazan.test.FactorySingleton;
import com.alvazan.test.db.Account;
import com.alvazan.test.db.Activity;

public class TestIndexAndOrParens {

	private static NoSqlEntityManagerFactory factory;
	private NoSqlEntityManager mgr;

	@BeforeClass
	public static void setup() {
		factory = FactorySingleton.createFactoryOnce();
	}
	
	@Before
	public void createEntityManager() {
		mgr = factory.createEntityManager();
		setupRecords();
	}
	@After
	public void clearDatabase() {
		NoSqlEntityManager other = factory.createEntityManager();
		other.clearDatabase();
	}
	
	@Test
	public void empty() {}
	
	//@Test
	public void testSimpleAnd() {
		List<Activity> findByName = Activity.findWithAnd(mgr, "hello", 5);
		Assert.assertEquals(1, findByName.size());
		
		List<Activity> list = Activity.findWithAnd(mgr, "hello", 6);
		Assert.assertEquals(1, list.size());
	}

	//@Test
	public void testBooleanWithAndClause() {
		Account acc = new Account();
		acc.setName("abc");
		acc.setIsActive(true);
		mgr.put(acc);
		Account acc2 = new Account();
		acc2.setName("dean");
		acc2.setIsActive(false);
		mgr.put(acc2);
		Account acc3 = new Account();
		acc3.setName("dean");
		acc3.setIsActive(true);
		mgr.put(acc3);
		Account acc4 = new Account();
		acc4.setName("dean");
		acc4.setIsActive(true);
		mgr.put(acc4);		
		Account acc5 = new Account();
		acc5.setName("dean");
		acc5.setIsActive(null);
		mgr.put(acc5);
		
		mgr.flush();
		
		List<Account> activeList = Account.findAnd(mgr, "dean", true);
		Assert.assertEquals(2, activeList.size());
		
		List<Account> nullList = Account.findAnd(mgr, "dean", null);
		Assert.assertEquals(1, nullList.size());
		
		List<Account> orList = Account.findOr(mgr, "dean", true);
		Assert.assertEquals(5, orList.size());
	}
		
	//@Test
	public void testSimpleOr() {
		List<Activity> findByName = Activity.findWithOr(mgr, "hello", 6);
		Assert.assertEquals(3, findByName.size());
		
		List<Activity> list = Activity.findWithOr(mgr, "nothaveThe5OrHellohere", 20);
		Assert.assertEquals(1, list.size());
	}
	
	//@Test
	public void testParensVsNoParens() {
		//@NoSqlQuery(name="findWithParens", query="select * FROM TABLE e WHERE" +
		//		" e.name=:name and (e.numTimes = :numTimes or e.isCool = :isCool)"),	
		//@NoSqlQuery(name="findWithoutParens", query="select * FROM TABLE e WHERE" +
		//		" e.name=:name and e.numTimes = :numTimes or e.isCool = :isCool"),
		//We have a truth table of this where the result of A named query and B named query are on right side
		//We need to test #2 (A=F, B=T) and the opposite (A=T, B=F)
		//1 F, F, F : A=F, B=F
		//2 F, F, T : A=F, B=T
		//3 F, T, F : A=F, B=F
		//4 F, T, T : A=F, B=T
		//5 T, F, F : A=F, B=F
		//6 T, F, T : 
		//7 T, T, F : 
		//8 T, T, T :
		
		//First query should not be found....(A)
		List<Activity> withParens = Activity.findWithParens(mgr, "notfound", 99, 5.55f);
		Assert.assertEquals(0, withParens.size());
		
		//Second query should be found....(B)
		List<Activity> list = Activity.findWithoutParens(mgr, "notfound", 99, 5.55f);
		Assert.assertEquals(1, list.size());		
	}
	
	private void setupRecords() {
		
		Activity act1 = new Activity();
		act1.setName("hello");
		act1.setMyFloat(5.65f);
		act1.setUniqueColumn("notunique");
		act1.setNumTimes(5);
		act1.setIsCool(true);
		mgr.put(act1);
		
		Activity act2 = new Activity();
		act2.setName("notelloBUTHas5ForNumTimes");
		act2.setMyFloat(5.65f);
		act2.setUniqueColumn("notunique");
		act2.setNumTimes(5);
		act2.setIsCool(true);
		mgr.put(act2);
		
		Activity act4 = new Activity();
		act4.setName("hello");
		act4.setMyFloat(5.65f);
		act4.setUniqueColumn("notunique");
		act4.setNumTimes(6);
		act4.setIsCool(true);
		mgr.put(act4);
		
		Activity act5 = new Activity();
		act5.setName("nothaveThe5OrHellohere");
		act5.setMyFloat(5.65f);
		act5.setUniqueColumn("notunique");
		act5.setNumTimes(6);
		act5.setIsCool(true);
		mgr.put(act5);		

		Activity act6 = new Activity();
		act6.setName("somethingtttt");
		act6.setMyFloat(5.55f);
		act6.setUniqueColumn("notunique");
		act6.setNumTimes(9);
		act6.setIsCool(true);
		mgr.put(act6);
		
		mgr.flush();
	}

}