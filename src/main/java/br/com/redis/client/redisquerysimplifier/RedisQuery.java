package br.com.redis.client.redisquerysimplifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import br.com.redis.client.redisquerysimplifier.filters.MatchOperator;
import br.com.redis.client.redisquerysimplifier.filters.MatchOperatorCombiner;
import br.com.redis.client.redisquerysimplifier.utils.AnnotationUtilities;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;

public class RedisQuery {

	protected static final Gson	GSON	= new Gson();

	/**
	 * May the force be with you
	 */
	static Jedis				mtfbwy;
	private static String		server;
	private static Integer		port;
	private static int			timeout;

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
	public static void init(String server, Integer port, int timeout) {
		mtfbwy = new Jedis(server, port, timeout);
		RedisQuery.server = server;
		RedisQuery.port = port;
		RedisQuery.timeout = timeout;
	}

	private static Jedis getJedis() {
		if (mtfbwy.getClient().isBroken()) {
			init(server, port, timeout);
		}
		return mtfbwy;
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
	public static <T> boolean save(T entity, Long id) {
		String key = generateRedisKey(entity.getClass(), id.toString());
		String set = getJedis().set(key, serializeObject(entity));

		// Do the index process
		Indexer.index(entity, key, id.toString());

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
		Optional<String> firstInCache = getJedis().scan("0", params).getResult().stream().findFirst();
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

	private static <T> Optional<T> findById(Class<T> entityClass, String id) {
		return deserialize(entityClass, id);
	}

	/**
	 * Check if there any occurences of an entity with passed parameters
	 * 
	 * @param params
	 * @return
	 */
	public static <T> boolean exists(Class<T> entityClass, MatchOperatorCombiner matchOperatorCombiner) {
		List<Entry<String, String>> filterByParams = filterByParams(entityClass, matchOperatorCombiner);
		return filterByParams.stream().findFirst().isPresent();
	}

	/**
	 * Remove the entity and all its indexes
	 * 
	 * @param entity
	 */
	public static <T> boolean remove(T entity, Long id) {
		Indexer.removeAllIndexesFromEntity(entity, id.toString()); // Removing indexes
		return getJedis().del(generateRedisKey(entity.getClass(), id.toString())) > 0; // Removing key
	}

	/**
	 * Find the first occurence of an entity based on indexed params
	 * 
	 * @param entityClass
	 * @param params
	 * @return
	 */
	public static <T> Optional<T> findFirstByParams(Class<T> entityClass, MatchOperatorCombiner matchOperatorCombiner) {
		Optional<Entry<String, String>> findFirst = filterByParams(entityClass, matchOperatorCombiner).stream().findFirst();
		Optional<T> toReturn = Optional.empty();

		if (findFirst.isPresent()) {
			toReturn = findById(entityClass, findFirst.get().getValue());
		}

		return toReturn;
	}

	/**
	 * Find all occurrences based on search params
	 * 
	 * @param entityClass
	 * @param params
	 * @return
	 */
	public static <T> List<T> findAllByParams(Class<T> entityClass, MatchOperatorCombiner matchOperatorCombiner) {
		List<Entry<String, String>> collect = filterByParams(entityClass, matchOperatorCombiner).stream().collect(Collectors.toList());
		List<T> toReturn = new ArrayList<>();

		collect.forEach(action -> {
			toReturn.add(findById(entityClass, action.getValue()).get());
		});

		return toReturn;
	}

	/**
	 * Do the search in index
	 * 
	 * @param entityClass
	 * @param params
	 * @return
	 */
	private static <T> List<Entry<String, String>> filterByParams(Class<T> entityClass, MatchOperatorCombiner matchOperatorCombiner) {
		String ro = AnnotationUtilities.extractRedisObjectName(entityClass);

		List<Entry<String, String>> toReturn = new ArrayList<>();

		for (List<MatchOperator> block : matchOperatorCombiner.getOperators()) {
			List<Entry<String, String>> currentBlockResults = new ArrayList<>();
			for (MatchOperator operator : block) {
				ScanParams scanParams = new ScanParams();
				AnnotationUtilities.validateFieldSearchedIndexed(entityClass, operator.getFieldName());
				String filter = operator.getOperator().build(operator.getFieldValue().toString());
				scanParams.match(generateRedisIndexKey(operator.getFieldName(), filter, "*"));

				List<Entry<String, String>> currentResult = getJedis().hscan(ro, "0", scanParams).getResult();

				if (currentResult.isEmpty()) {
					currentBlockResults.clear();
				}

				if (!currentBlockResults.isEmpty()) {
					currentBlockResults.removeIf(p -> !currentResult.stream().anyMatch(p2 -> p2.getValue().equals(p.getValue())));
					currentResult.removeIf(p -> !currentBlockResults.stream().anyMatch(p2 -> p2.getValue().equals(p.getValue())));

					currentResult.forEach(item -> {
						boolean anyMatch = currentBlockResults.stream().anyMatch(p -> !p.getValue().equals(item.getValue()));
						if (anyMatch) {
							currentBlockResults.add(item);
						}
					});
				} else {
					currentBlockResults.addAll(currentResult);
				}

				if (currentBlockResults.isEmpty()) {
					break;
				}
			}
			toReturn.addAll(currentBlockResults);
		}

		return toReturn;
	}

	/**
	 * Find all the occurrences of an entity
	 * 
	 * @param entityClass
	 * @return
	 */
	public static <T> List<T> findAll(Class<T> entityClass) {
		ScanParams params = new ScanParams();
		params.match(generateFilterKey(entityClass));
		List<String> collect = getJedis().scan("0", params).getResult().stream().collect(Collectors.toList());

		List<T> toReturn = new ArrayList<>();

		collect.stream().forEach(key -> {
			toReturn.add(findById(entityClass, key).get());
		});

		return toReturn;
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
		String value = getJedis().get(key);
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
		return getJedis().incr("uniqueId");
	}

	/**
	 * Set uniqueId
	 * 
	 * @return
	 */
	public static void setUniqueId(Long value) {
		getJedis().set("uniqueId", value + "");
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

	static <T> String generateRedisIndexKey(String keyS, String uniqueId, String indexKey) {
		RedisKey key = new RedisKey(keyS, uniqueId, indexKey);
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
