package br.com.redis.client.redisquerysimplifier;

public class RedisKey {

	private String	name;
	private String	uniqueId;

	public RedisKey(String name, String uniqueId) {
		super();
		this.name = name;
		this.uniqueId = uniqueId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

}
