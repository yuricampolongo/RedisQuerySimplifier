package br.com.redis.client.redisquerysimplifier.strategies;

import java.util.Collection;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Class to exclude relationships in object from default serialization method. The fields with relationship with another fields will have a separate indexation process.
 * 
 * @author yuri.campolongo
 *
 */
public class RelationshipExclusionStrategy implements ExclusionStrategy {

	@Override
	/**
	 * If the field is a Collection, it must be excluded
	 */
	public boolean shouldSkipField(FieldAttributes f) {
		return Collection.class.isAssignableFrom(f.getDeclaredClass());
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}

}
