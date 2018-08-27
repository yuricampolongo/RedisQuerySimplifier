package br.com.redis.client.redisquerysimplifier.entities;

import java.util.List;

import br.com.redis.client.redisquerysimplifier.annotations.RedisObject;

@RedisObject(name = "Student")
public class Student {

	private String		studentName;
	private List<Grade>	grades;

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public List<Grade> getGrades() {
		return grades;
	}

	public void setGrades(List<Grade> grades) {
		this.grades = grades;
	}

}
