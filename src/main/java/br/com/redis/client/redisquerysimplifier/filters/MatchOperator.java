package br.com.redis.client.redisquerysimplifier.filters;

/**
 * Class that controls the information about operations to search on index
 * 
 * @author yuri.campolongo
 *
 */
public class MatchOperator {
	private String		fieldName;
	private Object		fieldValue;
	private Operator	operator;

	public MatchOperator() {
		super();
	}

	public MatchOperator(String fieldName, Object fieldValue, Operator operator) {
		super();
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.operator = operator;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}
}
