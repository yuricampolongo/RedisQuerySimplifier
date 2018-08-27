package br.com.redis.client.redisquerysimplifier.entities;

import java.util.List;

import br.com.redis.client.redisquerysimplifier.annotations.RedisObject;

@RedisObject(name = "Grade")
public class Grade {

	private Long			value;
	private List<Student>	students;

	public Grade(Long value) {
		super();
		this.value = value;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

}
