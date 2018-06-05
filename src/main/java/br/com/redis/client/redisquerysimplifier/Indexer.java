package br.com.redis.client.redisquerysimplifier;

import java.util.Map;

import br.com.redis.client.redisquerysimplifier.utils.AnnotationUtilities;

public class Indexer {

	/**
	 * Do the index process on entity attributes marked with @RedisFieldIndex annotation
	 * 
	 * @param entity
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static <T> void index(T entity, String key){
		Map<String, String> toIndex = AnnotationUtilities.extractFieldsToIndex(entity);
		String redisObjectName = AnnotationUtilities.extractRedisObjectName(entity.getClass());
		toIndex.forEach((k,v) -> {
			RedisQuery.mtfbwy.hset(redisObjectName, RedisQuery.generateRedisKey(k, v), key);
		});
	}
	
	/**
	 * Remove all indexes for entity
	 * @param entity
	 */
	public static <T> void removeAllIndexesFromEntity(T entity){
		Map<String, String> toRemove = AnnotationUtilities.extractFieldsToIndex(entity);
		String redisObjectName = AnnotationUtilities.extractRedisObjectName(entity.getClass());
		toRemove.forEach((k,v) -> {
			RedisQuery.mtfbwy.hdel(redisObjectName, RedisQuery.generateRedisKey(k, v));
		});
	}
}