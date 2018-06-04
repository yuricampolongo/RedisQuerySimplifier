package br.com.redis.client.redisquerysimplifier.exceptions;

public class RedisQueryInitializationException extends RuntimeException {

	@Override
	public String getMessage() {
		return "You have to initialize the manager first, please call method init(String server, Integer port)";
	}

}
