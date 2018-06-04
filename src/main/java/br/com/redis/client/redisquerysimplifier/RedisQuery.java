package br.com.redis.client.redisquerysimplifier;

import java.util.Optional;

import com.google.gson.Gson;

import br.com.redis.client.redisquerysimplifier.annotations.RedisObject;
import br.com.redis.client.redisquerysimplifier.exceptions.RedisObjectNotIdentifiedException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;

public class RedisQuery {

	protected static final Gson	GSON	= new Gson();

	/**
	 * May the force be with you
	 */
	private static Jedis		mtfbwy;

	private RedisQuery() {

	}

	/**
	 * Initialize the redis manager
	 * 
	 * @param server
	 *            Server name
	 * @param port
	 *            Redis port
	 */
	public static void init(String server, Integer port) {
		mtfbwy = new Jedis(server, port);
	}

	/**
	 * Save the entity in Redis or update.
	 * 
	 * @param entity
	 * @param id
	 * @return
	 */
	public static <T> boolean save(T entity, Long id) {
		String set = mtfbwy.set(generateRedisKey(entity.getClass(), id.toString()), serializeObject(entity));
		return set == null ? false : set.equals("OK");
	}

	/**
	 * Search for the first occurrence of the object in Redis
	 * 
	 * @param entityClass
	 *            the entity to be searched for
	 * @return an Optional of the value
	 */
	public static <T> Optional<T> findFirst(Class<T> entityClass) {
		ScanParams params = new ScanParams();
		params.match(generateFilterKey(entityClass));
		Optional<String> firstInCache = mtfbwy.scan("0", params).getResult().stream().findFirst();
		Optional<T> deserialize = Optional.empty();
		if (firstInCache.isPresent()) {
			deserialize = deserialize(entityClass, firstInCache.get());
		}
		return deserialize;
	}

	/**
	 * Find the occurrence by ID
	 * 
	 * @param entityClass
	 * @param id
	 * @return
	 */
	public static <T> Optional<T> findById(Class<T> entityClass, Long id) {
		return deserialize(entityClass, RedisQuery.generateRedisKey(entityClass, id.toString()));
	}

	/**
	 * Serialize an object to a json string
	 * 
	 * @return
	 */
	private static <T> String serializeObject(T entity) {
		return GSON.toJson(entity);
	}

	/**
	 * Deserialize an object to a specified class passed as param
	 * 
	 * @return
	 */
	private static <T> Optional<T> deserialize(Class<T> entityClass, String key) {
		String value = mtfbwy.get(key);
		if (value == null) {
			return Optional.empty();
		}
		return Optional.of(GSON.fromJson(value, entityClass));
	}

	/**
	 * Get next atomic ID
	 * 
	 * @return
	 */
	public static Long nextUniqueId() {
		return mtfbwy.incr("uniqueId");
	}

	/**
	 * Generate a Redis key with specified uniqueId and class identifier
	 * 
	 * @param entityClass
	 * @param uniqueId
	 * @return
	 */
	private static <T> String generateRedisKey(Class<T> entityClass, String uniqueId) {
		RedisObject rediObject = entityClass.getAnnotation(RedisObject.class);
		if (rediObject == null) {
			throw new RedisObjectNotIdentifiedException("Class " + entityClass.getName() + " is not annotated with @RedisObject, please annotate the class with @RedisObject annotation");
		}
		String rediObjectName = rediObject.name();
		RedisKey key = new RedisKey(rediObjectName, uniqueId);
		return GSON.toJson(key).replaceAll(" ", "").replaceAll("\"", "\\\"");
	}

	/**
	 * Generate a key to make filters
	 * 
	 * @param entityClass
	 * @return
	 */
	private static <T> String generateFilterKey(Class<T> entityClass) {
		RedisObject rediObject = entityClass.getAnnotation(RedisObject.class);
		if (rediObject == null) {
			throw new RedisObjectNotIdentifiedException("Class " + entityClass.getName() + " is not annotated with @RedisObject, please annotate the class with @RedisObject annotation");
		}
		String rediObjectName = rediObject.name();
		RedisKey key = new RedisKey(rediObjectName, "*");
		return GSON.toJson(key).replaceAll(" ", "").replaceAll("\"", "\\\"");
	}

}
