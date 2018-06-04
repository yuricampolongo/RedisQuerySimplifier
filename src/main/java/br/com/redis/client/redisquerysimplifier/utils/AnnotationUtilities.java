package br.com.redis.client.redisquerysimplifier.utils;

import br.com.redis.client.redisquerysimplifier.annotations.RedisObject;
import br.com.redis.client.redisquerysimplifier.exceptions.RedisAnnotationException;

public class AnnotationUtilities {

	/**
	 * Extract the Redis Object name annotated in entity
	 * 
	 * @param entityClass
	 * @return
	 * @throws RedisAnnotationException
	 */
	public static <T> String extractRedisObjectName(Class<T> entityClass) throws RedisAnnotationException {
		RedisObject rediObject = entityClass.getAnnotation(RedisObject.class);
		if (rediObject == null) {
			throw new RedisAnnotationException("Class " + entityClass.getName() + " is not annotated with @RedisObject, please annotate the class with @RedisObject annotation");
		}
		return rediObject.name();
	}
	
	

}
