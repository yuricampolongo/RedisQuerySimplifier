package br.com.redis.client.redisquerysimplifier.exceptions;

public class RedisFieldNotIndexedException extends RuntimeException {

	private String message;

	public RedisFieldNotIndexedException(Exception e, String message) {
		super(e);
		this.message = message;
	}

	public RedisFieldNotIndexedException(String message) {
		super();
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
