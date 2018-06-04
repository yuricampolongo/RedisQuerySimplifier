package br.com.redis.client.redisquerysimplifier;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.com.redis.client.redisquerysimplifier.entities.EntityNotExistTest;
import br.com.redis.client.redisquerysimplifier.entities.EntityTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Tester {

	private static String	server	= "localhost";
	private static Integer	port	= 6379;

	@BeforeClass
	public static void configure() {
		RedisQuery.init(server, port);
	}

	@Test
	public void test1_save() {
		Long id = 10l;
		EntityTest et = new EntityTest(id, "EntityTest");
		RedisQuery.save(et, id);
	}

	@Test
	public void test2_findFirst() {
		Optional<EntityTest> findFirst = RedisQuery.findFirst(EntityTest.class);
		if (!findFirst.isPresent()) {
			throw new AssertionError("EntityTest not find");
		}
	}
	
	@Test
	public void test3_mustNotFind() {
		Optional<EntityNotExistTest> findFirst = RedisQuery.findFirst(EntityNotExistTest.class);
		if (findFirst.isPresent()) {
			throw new AssertionError("EntityNotExistTest must not exist");
		}
	}
	
	@Test
	public void test4_findById() {
		Optional<EntityTest> findById = RedisQuery.findById(EntityTest.class, 10l);
		if (!findById.isPresent()) {
			throw new AssertionError("EntityTest with ID 10 must be found");
		}
	}
	
	@Test
	public void test5_mustNotFindById() {
		Optional<EntityTest> findById = RedisQuery.findById(EntityTest.class, 11l);
		if (findById.isPresent()) {
			throw new AssertionError("EntityTest with ID 11 must not be found");
		}
	}

}
