package br.com.redis.client.redisquerysimplifier;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.log4j.jmx.Agent;

import com.google.gson.Gson;

import br.com.redis.client.redisquerysimplifier.utils.AnnotationUtilities;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;

public class RedisQuery {

	protected static final Gson GSON = new Gson();

	/**
	 * May the force be with you
	 */
	static Jedis mtfbwy;

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
	 * Save the entity in Redis or update. The fields annotated with @RedisFieldIndex will be indexed for search
	 * 
	 * @param entity
	 * @param id
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static <T> boolean save(T entity, Long id){
		String key = generateRedisKey(entity.getClass(), id.toString());
		String set = mtfbwy.set(key, serializeObject(entity));

		// Do the index process
		Indexer.index(entity, key);

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
	 * Check if there any occurences of an entity with passed parameters
	 * 
	 * @param params
	 * @return
	 */
	public static <T> boolean exists(Class<T> entityClass, Map<String, String> params) {
		String ro = AnnotationUtilities.extractRedisObjectName(entityClass);

		ScanParams scanParams = new ScanParams();
		params.forEach((k, v) -> {
			scanParams.match(generateRedisKey(k, v));
		});

		Optional<Entry<String, String>> exist = mtfbwy.hscan(ro, "0", scanParams).getResult().stream().findFirst();
		return exist.isPresent();
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
	static <T> String generateRedisKey(Class<T> entityClass, String uniqueId) {
		String ro = AnnotationUtilities.extractRedisObjectName(entityClass);
		return generateRedisKey(ro, uniqueId);
	}

	static <T> String generateRedisKey(String keyS, String uniqueId) {
		RedisKey key = new RedisKey(keyS, uniqueId);
		return GSON.toJson(key).replaceAll(" ", "").replaceAll("\"", "\\\"");
	}

	/**
	 * Generate a key to make filters
	 * 
	 * @param entityClass
	 * @return
	 */
	private static <T> String generateFilterKey(Class<T> entityClass) {
		String ro = AnnotationUtilities.extractRedisObjectName(entityClass);
		RedisKey key = new RedisKey(ro, "*");
		return GSON.toJson(key).replaceAll(" ", "").replaceAll("\"", "\\\"");
	}

}
