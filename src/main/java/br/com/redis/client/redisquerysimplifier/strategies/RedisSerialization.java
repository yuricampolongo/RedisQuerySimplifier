package br.com.redis.client.redisquerysimplifier.strategies;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.redis.client.redisquerysimplifier.RedisQuery;
import br.com.redis.client.redisquerysimplifier.exceptions.RedisFieldNotIndexedException;

/**
 * Class to serialize the object in json to be indexed in Redis
 * 
 * @author yuri.campolongo
 *
 */
public class RedisSerialization {

	protected static final Gson GSON = new GsonBuilder().setExclusionStrategies(new RelationshipExclusionStrategy()).create();

	/**
	 * Serializes the source and all its relationships
	 * 
	 * @param source
	 *            the object to be serialized
	 * @param key
	 *            the object key in Redis
	 * @return Map<String,String> with the keys and values to be stored
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Map<String, String> serialize(Object source, String key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String, String> toStore = new HashMap<>();
		toStore.put(key, GSON.toJson(source));
		toStore.putAll(serializeRelationships(source, key));
		return toStore;
	}

	/**
	 * Serialize all collections in json inside the object
	 * 
	 * @param source
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Map<String, String> serializeRelationships(Object source, String key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<Method> methods = Arrays.asList(source.getClass().getMethods());
		Map<String, String> serialized = new HashMap<>();
		for (Method method : methods) {
			if (Collection.class.isAssignableFrom(method.getReturnType())) {
				serialized.put(key + "_" + method.getName(), GSON.toJson(method.invoke(source)));
			}
		}
		return serialized;
	}

}
