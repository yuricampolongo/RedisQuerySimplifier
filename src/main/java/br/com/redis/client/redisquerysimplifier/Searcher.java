package br.com.redis.client.redisquerysimplifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.com.redis.client.redisquerysimplifier.exceptions.RedisFieldNotIndexedException;
import br.com.redis.client.redisquerysimplifier.strategies.RedisSerialization;

public class Searcher {

	protected static final Gson GSON = new Gson();

	/**
	 * Fill the object with available joins
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static <T> void fillObjectWithRelationships(Object source, String key, Class<T> entityClass) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String, String> keys = RedisSerialization.serializeRelationships(source, key);
		for (String k : keys.keySet()) {
			String[] split = k.split("_");
			String setMethodName = split[split.length - 1].replace("get", "set");
			String getMethodName = split[split.length - 1];

			Method setMethod = null;
			Method getMethod = null;

			for (Method method : Arrays.asList(entityClass.getMethods())) {
				if (method.getName().equals(setMethodName)) {
					setMethod = method;
				} else if (method.getName().equals(getMethodName)) {
					getMethod = method;
				}
			}

			String json = RedisQuery.getJedis().get(k);

			Type returnType = getMethod.getGenericReturnType();
			if (returnType instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) returnType;
				for (Type t : pt.getActualTypeArguments()) {
					Type type = TypeToken.getParameterized(getMethod.getReturnType(), t).getType();
					setMethod.invoke(source, (Collection<?>) GSON.fromJson(json, type));
				}
			}
		}
	}
}
