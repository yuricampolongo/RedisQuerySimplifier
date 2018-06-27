package br.com.redis.client.redisquerysimplifier.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.redis.client.redisquerysimplifier.exceptions.WhereAlreadyPresentException;
import br.com.redis.client.redisquerysimplifier.exceptions.WhereNotPresentException;

public class MatchOperatorCombiner {

	private List<List<MatchOperator>>	operators;
	private boolean						whereInserted	= false;
	private int							currentBlock	= 0;

	public MatchOperatorCombiner() {
		super();
		this.operators = new ArrayList<>();
	}

	public MatchOperatorCombiner where(MatchOperator operator) {
		if (whereInserted) {
			throw new WhereAlreadyPresentException("Where already called, use 'and' or 'or' methods");
		}
		whereInserted = true;
		add(operator);
		return this;
	}

	public MatchOperatorCombiner where(String fieldName, Object fieldValue, Operator operator) {
		if (whereInserted) {
			throw new WhereAlreadyPresentException("Where already called, use 'and' or 'or' methods");
		}
		whereInserted = true;
		add(new MatchOperator(fieldName, fieldValue, operator));
		return this;
	}

	public MatchOperatorCombiner and(MatchOperator operator) {
		if (!whereInserted) {
			throw new WhereNotPresentException("You must first call 'where' method and then 'and'");
		}
		add(operator);
		return this;
	}

	public MatchOperatorCombiner or(MatchOperator operator) {
		currentBlock++;
		if (!whereInserted) {
			throw new WhereNotPresentException("You must first call 'where' method and then 'or'");
		}
		add(operator);
		return this;
	}

	private void add(MatchOperator operator) {
		if (operators.size() <= currentBlock) {
			operators.add(new ArrayList<>());
		}
		operators.get(currentBlock).add(operator);
	}

	public List<List<MatchOperator>> getOperators() {
		return Collections.unmodifiableList(operators);
	}

}
