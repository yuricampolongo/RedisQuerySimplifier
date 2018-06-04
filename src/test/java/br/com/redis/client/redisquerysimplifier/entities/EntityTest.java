package br.com.redis.client.redisquerysimplifier.entities;

import br.com.redis.client.redisquerysimplifier.annotations.RedisObject;

@RedisObject(name = "EntityTest")
public class EntityTest {

	private Long	id;
	private String	name;

	public EntityTest(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
