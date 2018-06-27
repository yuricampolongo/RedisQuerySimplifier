package br.com.redis.client.redisquerysimplifier.filters;

/**
 * SQL Operators to define which where clause use. <ul> <li>{@link #EQUAL}</li> <li>{@link #STARTS_WITH}</li> <li>{@link #ENDS_WITH}</li> <li>{@link #CONTAINS}</li> </ul>
 * 
 * @author victor.bello
 *
 */
public enum Operator {
	/**
	 * Indicates that where clause should check for equality. EG: SELECT * FROM Table WHERE id = fieldValue
	 */
	EQUAL("__TEXT__"),
	/**
	 * Indicates that where clause should check whether the field starts with the field value. EG: SELECT * FROM Table WHERE name LIKE 'fieldValue%'
	 */
	STARTS_WITH("__TEXT__*"),
	/**
	 * Indicates that where clause should check whether the field ends with the field value. EG: SELECT * FROM Table WHERE name LIKE '%fieldValue'
	 */
	ENDS_WITH("*__TEXT__"),
	/**
	 * Indicates that where clause should check whether the field contains the field value. EG: SELECT * FROM Table WHERE name LIKE '%fieldValue%'
	 */
	CONTAINS("*__TEXT__*");

	private String filter;

	private Operator(String filter) {
		this.filter = filter;
	}

	public String build(String fieldValue) {
		return this.filter.replace("__TEXT__", fieldValue);
	}
}
