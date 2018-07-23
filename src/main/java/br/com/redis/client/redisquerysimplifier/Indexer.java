package br.com.redis.client.redisquerysimplifier;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.redis.client.redisquerysimplifier.utils.AnnotationUtilities;
import redis.clients.jedis.ScanParams;

public class Indexer {

	/**
	 * Do the index process on entity attributes marked with @RedisFieldIndex annotation
	 * 
	 * @param entity
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static <T> void index(T entity, String key, String indexKey) {
		Map<String, String> toIndex = AnnotationUtilities.extractFieldsToIndex(entity);
		String redisObjectName = AnnotationUtilities.extractRedisObjectName(entity.getClass());

		removeAllIndexesFromEntity(entity, indexKey);

		toIndex.forEach((k, v) -> {
			RedisQuery.mtfbwy.hset(redisObjectName, RedisQuery.generateRedisIndexKey(k, v, indexKey), key);
		});
	}

	/**
	 * Remove all indexes for entity
	 * 
	 * @param entity
	 */
	public static <T> void removeAllIndexesFromEntity(T entity, String indexKey) {
		String redisObjectName = AnnotationUtilities.extractRedisObjectName(entity.getClass());

		ScanParams scanParams = new ScanParams();
		scanParams.match(RedisQuery.generateRedisIndexKey("*", "*", indexKey));
		List<Entry<String, String>> currentResult = RedisQuery.mtfbwy.hscan(redisObjectName, "0", scanParams).getResult();

		for (Entry<String, String> entry : currentResult) {
			RedisQuery.mtfbwy.hdel(redisObjectName, entry.getKey());
		}
	}
}