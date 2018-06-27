package br.com.redis.client.redisquerysimplifier.exceptions;

public class WhereAlreadyPresentException extends RuntimeException {

	public WhereAlreadyPresentException(String message) {
		super(message);
	}

}
