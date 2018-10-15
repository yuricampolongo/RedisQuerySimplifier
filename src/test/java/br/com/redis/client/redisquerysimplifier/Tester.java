package br.com.redis.client.redisquerysimplifier;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.redis.client.redisquerysimplifier.entities.EntityNotExistTest;
import br.com.redis.client.redisquerysimplifier.entities.EntityTest;		
import br.com.redis.client.redisquerysimplifier.exceptions.RedisFieldNotIndexedException;
import br.com.redis.client.redisquerysimplifier.filters.MatchOperator;
import br.com.redis.client.redisquerysimplifier.filters.MatchOperatorCombiner;
import br.com.redis.client.redisquerysimplifier.filters.Operator;

public class Tester {

	private static String	server			= "localhost";
	private static Integer	port			= 6379;
	private static Long		id;

	@BeforeClass
	public static void configure() {
		RedisQuery.init(server, port, 5000);

		id = RedisQuery.nextUniqueId();
		EntityTest et = new EntityTest(id, "EntityTest");
		RedisQuery.save(et, id);
	}

	@Test
	public void findFirst() {
		Optional<EntityTest> findFirst = RedisQuery.findFirst(EntityTest.class);
		if (!findFirst.isPresent()) {
			throw new AssertionError("EntityTest not find");
		}
	}

	@Test
	public void mustNotFind() {
		Optional<EntityNotExistTest> findFirst = RedisQuery.findFirst(EntityNotExistTest.class);
		if (findFirst.isPresent()) {
			throw new AssertionError("EntityNotExistTest must not exist");
		}
	}

	@Test
	public void findById() {
		Optional<EntityTest> findById = RedisQuery.findById(EntityTest.class, id);
		if (!findById.isPresent()) {
			throw new AssertionError("EntityTest with ID 10 must be found");
		}
	}

	@Test
	public void mustNotFindById() {
		Optional<EntityTest> findById = RedisQuery.findById(EntityTest.class, id + 1);
		if (findById.isPresent()) {
			throw new AssertionError("EntityTest with ID 11 must not be found");
		}
	}

	@Test
	public void exists() {
		MatchOperatorCombiner where = new MatchOperatorCombiner().where(new MatchOperator("name", "EntityTest", Operator.EQUAL));

		if (!RedisQuery.exists(EntityTest.class, where)) {
			throw new AssertionError("EntityTest with name = 'EntityTest' must exist");
		}

	}

	@Test
	public void mustNotExists() {
		MatchOperatorCombiner where = new MatchOperatorCombiner().where(new MatchOperator("name", "EntityTestNotExist", Operator.EQUAL));

		if (RedisQuery.exists(EntityTest.class, where)) {
			throw new AssertionError("EntityTest with name = 'EntityTestNotExist' must not exist");
		}

	}

	@Test
	public void testRemoval() {
		// Create an entity to remove
		Long id = RedisQuery.nextUniqueId();
		EntityTest et = new EntityTest(id, "EntityTestToRemove");
		if (RedisQuery.save(et, id)) {
			// Remove the entity
			RedisQuery.remove(et, id);
		} else {
			throw new AssertionError("Unable to create entity to remove");
		}
	}

	@Test
	public void findFirstByParams() {
		MatchOperatorCombiner where = new MatchOperatorCombiner().where(new MatchOperator("name", "EntityTest", Operator.EQUAL));

		if (!RedisQuery.findFirstByParams(EntityTest.class, where).isPresent()) {
			throw new AssertionError("EntityTest with name = 'EntityTest' must exist");
		}
	}

	@Test
	public void mustNotFindFirstByParams() {
		MatchOperatorCombiner where = new MatchOperatorCombiner().where(new MatchOperator("name", "EntityTestNotExist", Operator.EQUAL));

		if (RedisQuery.findFirstByParams(EntityTest.class, where).isPresent()) {
			throw new AssertionError("EntityTest with name = 'EntityTestNotExist' must not exist");
		}
	}

	@Test
	public void findAll() {
		Long id1 = RedisQuery.nextUniqueId();
		EntityTest et = new EntityTest(id1, "EntityTestId15");
		RedisQuery.save(et, id1);

		Long id2 = RedisQuery.nextUniqueId();
		EntityTest et2 = new EntityTest(id2, "EntityTestId16");
		RedisQuery.save(et2, id2);

		boolean findAll = RedisQuery.findAll(EntityTest.class).size() == 3;

		RedisQuery.remove(et, et.getId());
		RedisQuery.remove(et2, et2.getId());
		
		assertTrue(findAll);
	}

	@Test
	public void testFieldNotIndexedError() {
		Long id = RedisQuery.nextUniqueId();
		EntityTest et = new EntityTest(id, "EntityTestId" + id, "valueAddress");
		RedisQuery.save(et, et.getId());

		MatchOperatorCombiner where = new MatchOperatorCombiner().where(new MatchOperator("address", "valueAddress", Operator.EQUAL));

		boolean errorOccurred = false;

		try {
			RedisQuery.findFirstByParams(EntityTest.class, where);
		} catch (RedisFieldNotIndexedException e) {
			errorOccurred = true;
			// This exception is supposed to happen in this case because 'address' field is not marked with @RedisIndexField
		}

		RedisQuery.remove(et, et.getId());

		assertTrue(errorOccurred);
	}

	@Test
	public void findAllByParams() {
		Long id1 = RedisQuery.nextUniqueId();
		EntityTest et = new EntityTest(id1, "EntityToSearch");
		RedisQuery.save(et, id1);

		Long id2 = RedisQuery.nextUniqueId();
		EntityTest et2 = new EntityTest(id2, "EntityToSearch");
		RedisQuery.save(et2, id2);

		MatchOperatorCombiner where = new MatchOperatorCombiner().where(new MatchOperator("name", "EntityToSearch", Operator.EQUAL));

		assertTrue(RedisQuery.findAllByParams(EntityTest.class, where).size() == 2);

		RedisQuery.remove(et, et.getId());
		RedisQuery.remove(et2, et2.getId());
	}

	@Test
	public void filterMultipleParamsAnd() {
		Long id = RedisQuery.nextUniqueId();
		EntityTest et = new EntityTest(id, "EntityToSearch", 123426l);
		RedisQuery.save(et, id);

		MatchOperatorCombiner where = new MatchOperatorCombiner().where(new MatchOperator("name", "EntityToSearch", Operator.EQUAL)).and(new MatchOperator("docNumber", 123426l, Operator.EQUAL));

		assertTrue(RedisQuery.findAllByParams(EntityTest.class, where).size() == 1);

		RedisQuery.remove(et, et.getId());
	}

	@Test
	public void filterMultipleParamsEmpty() {
		Long id = RedisQuery.nextUniqueId();
		EntityTest et = new EntityTest(id, "EntityToSearch", 123426l);
		RedisQuery.save(et, id);

		Long id2 = RedisQuery.nextUniqueId();
		EntityTest et2 = new EntityTest(id2, "EntityToSearch2", 999999l);
		RedisQuery.save(et2, id2);

		MatchOperatorCombiner where = new MatchOperatorCombiner().where(new MatchOperator("name", "EntityToSearch", Operator.EQUAL)).and(new MatchOperator("docNumber", 999999l, Operator.EQUAL));

		assertTrue(RedisQuery.findAllByParams(EntityTest.class, where).size() == 0);

		RedisQuery.remove(et, et.getId());
		RedisQuery.remove(et2, et2.getId());
	}

	@Test
	public void filterMultipleParamsOr() {
		Long id = RedisQuery.nextUniqueId();
		EntityTest et = new EntityTest(id, "EntityToSearch", 123426l);
		RedisQuery.save(et, id);

		Long id2 = RedisQuery.nextUniqueId();
		EntityTest et2 = new EntityTest(id2, "EntityToSearch2", 999999l);
		RedisQuery.save(et2, id2);

		MatchOperatorCombiner where = new MatchOperatorCombiner().where(new MatchOperator("name", "EntityToSearch", Operator.EQUAL)).or(new MatchOperator("docNumber", 999999l, Operator.EQUAL));

		assertTrue(RedisQuery.findAllByParams(EntityTest.class, where).size() == 2);

		RedisQuery.remove(et, et.getId());
		RedisQuery.remove(et2, et2.getId());
	}

	@Test
	public void filterMultipleParamsOrEmpty() {
		Long id = RedisQuery.nextUniqueId();
		EntityTest et = new EntityTest(id, "EntityToSearch", 123426l);
		RedisQuery.save(et, id);

		Long id2 = RedisQuery.nextUniqueId();
		EntityTest et2 = new EntityTest(id2, "EntityToSearch2", 999999l);
		RedisQuery.save(et2, id2);

		MatchOperatorCombiner where = new MatchOperatorCombiner().where(new MatchOperator("docNumber", "EntityToSearch", Operator.EQUAL)).or(new MatchOperator("name", 999999l, Operator.EQUAL));

		assertTrue(RedisQuery.findAllByParams(EntityTest.class, where).size() == 0);

		RedisQuery.remove(et, et.getId());
		RedisQuery.remove(et2, et2.getId());
	}

	@Test
	public void testContains() {
		Long id = RedisQuery.nextUniqueId();
		EntityTest et = new EntityTest(id, "EntityToSearch", 123426l);
		RedisQuery.save(et, id);

		MatchOperatorCombiner where = new MatchOperatorCombiner().where(new MatchOperator("name", "ToSea", Operator.CONTAINS));

		assertTrue(RedisQuery.findAllByParams(EntityTest.class, where).size() == 1);

		RedisQuery.remove(et, et.getId());
	}

	@Test
	public void testStartsWith() {
		Long id = RedisQuery.nextUniqueId();
		EntityTest et = new EntityTest(id, "EntityToSearch", 123426l);
		RedisQuery.save(et, id);

		MatchOperatorCombiner where = new MatchOperatorCombiner().where(new MatchOperator("name", "EntityTo", Operator.STARTS_WITH));

		assertTrue(RedisQuery.findAllByParams(EntityTest.class, where).size() == 1);

		RedisQuery.remove(et, et.getId());
	}

	@Test
	public void testEndsWith() {
		Long id = RedisQuery.nextUniqueId();
		EntityTest et = new EntityTest(id, "EntityToSearch", 123426l);
		RedisQuery.save(et, id);

		MatchOperatorCombiner where = new MatchOperatorCombiner().where(new MatchOperator("name", "Search", Operator.ENDS_WITH));

		assertTrue(RedisQuery.findAllByParams(EntityTest.class, where).size() == 1);

		RedisQuery.remove(et, et.getId());
	}

	/**
	 * Clean the Redis after the tests
	 */
	@AfterClass
	public static void cleanIndex() {
		EntityTest et = new EntityTest(id, "EntityTest");
		RedisQuery.remove(et, id);
	}
}
