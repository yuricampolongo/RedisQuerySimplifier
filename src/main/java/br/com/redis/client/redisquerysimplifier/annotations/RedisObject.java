package br.com.redis.client.redisquerysimplifier.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface RedisObject {

	/**
	 * (Optional) The name of the object to be used in search. <p> Defaults to the entity name.
	 */
	String name() default "";

}
