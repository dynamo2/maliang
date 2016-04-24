package com.maliang.core.model;

import java.util.Date;

import com.maliang.core.arithmetic.calculator.DateCalculator;

public class TianmaDate extends Date {
	public TianmaDate(Date d){
//		Timestamp t = new Timestamp(d);
//		
//		super(d);
	}
	
	public String toString(){
		return DateCalculator.dateFormat.format(this);
	}
}
