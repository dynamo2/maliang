package com.maliang.core.exception;

import java.util.Map;

public class TurnToPage extends RuntimeException{
	private static final long serialVersionUID = 1L;

	private final Object result;
	
	public TurnToPage(Object ret){
		super();
		
		this.result = ret;
	}
	
	public Object getResult(){
		return result;
	}
}
