package br.com.redis.client.redisquerysimplifier.exceptions;

public class RedisAnnotationException extends RuntimeException {

	public RedisAnnotationException(String message) {
		super(message);
	}

	public RedisAnnotationException(Exception cause) {
		super(cause);
	}

}
