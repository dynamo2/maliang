package com.model.controller;

import org.bson.types.ObjectId;

import net.sf.ezmorph.object.AbstractObjectMorpher;

public class ObjectIdMorpher extends AbstractObjectMorpher {
	public Class morphsTo() {
		return ObjectId.class;
	}

	public boolean supports(Class c) {
		return true;
	}

	public Object morph(Object v) {
		if(v == null || v instanceof ObjectId){
			return v;
		}
		
		try {
			return new ObjectId(v.toString());
		}catch(Exception e){
			return null;
		}
	}
}
