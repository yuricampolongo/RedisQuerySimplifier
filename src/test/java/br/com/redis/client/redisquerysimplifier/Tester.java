package br.com.redis.client.redisquerysimplifier;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.com.redis.client.redisquerysimplifier.entities.EntityNotExistTest;
import br.com.redis.client.redisquerysimplifier.entities.EntityTest;
import br.com.redis.client.redisquerysimplifier.exceptions.RedisFieldNotIndexedException;

public class Tester {

	private static String server = "WVOX-000963";
	private static Integer port = 6379;

	@BeforeClass
	public static void configure() {
		RedisQuery.init(server, port);
		
		Long id = 10l;
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
		Optional<EntityTest> findById = RedisQuery.findById(EntityTest.class, 10l);
		if (!findById.isPresent()) {
			throw new AssertionError("EntityTest with ID 10 must be found");
		}
	}

	@Test
	public void mustNotFindById() {
		Optional<EntityTest> findById = RedisQuery.findById(EntityTest.class, 11l);
		if (findById.isPresent()) {
			throw new AssertionError("EntityTest with ID 11 must not be found");
		}
	}

	@Test
	public void exists() {
		Map<String, String> params = new HashMap<>();
		params.put("name", "EntityTest");
		if (!RedisQuery.exists(EntityTest.class, params)) {
			throw new AssertionError("EntityTest with name = 'EntityTest' must exist");
		}

	}

	@Test
	public void mustNotExists() {
		Map<String, String> params = new HashMap<>();
		params.put("name", "EntityTestNotExist");
		if (RedisQuery.exists(EntityTest.class, params)) {
			throw new AssertionError("EntityTest with name = 'EntityTestNotExist' must not exist");
		}

	}

	@Test
	public void testRemoval() {
		// Create an entity to remove
		Long id = 11l;
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
		Map<String, String> params = new HashMap<>();
		params.put("name", "EntityTest");
		if(!RedisQuery.findFirstByParams(EntityTest.class, params).isPresent()) {
			throw new AssertionError("EntityTest with name = 'EntityTest' must exist");
		}
	}
	
	@Test
	public void mustNotFindFirstByParams() {
		Map<String, String> params = new HashMap<>();
		params.put("name", "EntityTestNotExist");
		if(RedisQuery.findFirstByParams(EntityTest.class, params).isPresent()) {
			throw new AssertionError("EntityTest with name = 'EntityTestNotExist' must not exist");
		}
	}
	
	@Test
	public void findAll() {
		EntityTest et = new EntityTest(15l, "EntityTestId15");
		RedisQuery.save(et, 15l);
		
		EntityTest et2 = new EntityTest(16l, "EntityTestId16");
		RedisQuery.save(et2, 16l);
		
		assertTrue(RedisQuery.findAll(EntityTest.class).size() == 3);
		
		RedisQuery.remove(et, et.getId());
		RedisQuery.remove(et2, et2.getId());
	}
	
	@Test
	public void testFieldNotIndexedError() {
		EntityTest et = new EntityTest(17l, "EntityTestId17","valueAddress");
		RedisQuery.save(et, et.getId());
		
		Map<String, String> params = new HashMap<>();
		params.put("address", "valueAddress");
		
		boolean errorOccurred = false;
		
		try {
			RedisQuery.findFirstByParams(EntityTest.class, params);
		}catch (RedisFieldNotIndexedException e) {
			errorOccurred = true;
			// This exception is supposed to happen in this case because 'address' field is not marked with @RedisIndexField
		}
		
		RedisQuery.remove(et, et.getId());
		
		assertTrue(errorOccurred);
	}
	
	/**
	 * Clean the Redis after the tests
	 */
	@AfterClass
	public static void cleanIndex() {
		Long id = 10l;
		EntityTest et = new EntityTest(id, "EntityTest");
		RedisQuery.remove(et, id);
	}
}
