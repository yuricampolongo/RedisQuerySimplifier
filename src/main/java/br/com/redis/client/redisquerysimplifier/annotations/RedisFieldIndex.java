package br.com.redis.client.redisquerysimplifier.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Fields marked with this annotation will be indexed for further searches.
 * Because of that, this field value must be unique for the object
 * @author yuri.campolongo
 *
 */
@Target({FIELD}) 
@Retention(RUNTIME)
public @interface RedisFieldIndex {

}
