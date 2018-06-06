package br.com.redis.client.redisquerysimplifier.entities;

import br.com.redis.client.redisquerysimplifier.annotations.RedisFieldIndex;
import br.com.redis.client.redisquerysimplifier.annotations.RedisObject;

@RedisObject(name = "EntityTest")
public class EntityTest {

	private Long	id;
	@RedisFieldIndex
	private String	name;

	private String	address;

	public EntityTest(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public EntityTest(Long id, String name, String address) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
