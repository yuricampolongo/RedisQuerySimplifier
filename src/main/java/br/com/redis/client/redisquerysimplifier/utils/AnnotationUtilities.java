package br.com.redis.client.redisquerysimplifier.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.redis.client.redisquerysimplifier.annotations.RedisFieldIndex;
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

	/**
	 * Extract the fields marked with @RedisFieldIndex
	 * 
	 * @param entityClass
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	public static <T> Map<String, String> extractFieldsToIndex(T entity) {
		List<Field> fields = Arrays.asList(entity.getClass().getDeclaredFields());
		Map<String, String> index = new HashMap<>();

		try {
			for (Field field : fields) {
				if (field.isAnnotationPresent(RedisFieldIndex.class)) {
					Method method = entity.getClass().getMethod("get" + capitalize(field.getName()));
					Object object = method.invoke(entity);
					index.put(field.getName(), object.toString());
				}
			}
		} catch (Exception e) {
			throw new RedisAnnotationException(e.getMessage());
		}

		return index;
	}

	private static String capitalize(final String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

}
