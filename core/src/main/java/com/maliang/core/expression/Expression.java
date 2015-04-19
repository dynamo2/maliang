package com.maliang.core.expression;

import com.mongodb.BasicDBObject;

public interface Expression {
	public BasicDBObject generateQuery();
}
