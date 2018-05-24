package com.maliang.core.exception;

public class ThrowException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final RuntimeException exception;
	
	public ThrowException(RuntimeException e){
		super("");
		this.exception = e;
	}
	
	public void doThrow()throws RuntimeException {
		throw this.exception;
	}
}
