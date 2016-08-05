package com.maliang.core.model;

import java.util.List;

import com.maliang.core.arithmetic.AE;

/**
 * 支持单位换算类型
 * **/
public class UCType extends MongodbModel {
	private String name;
	private Integer key;
	private List<String> units = (List<String>)AE.execute("['a','b','c','d','e']");
	private String description;
	private List<Integer> factors = (List<Integer>)AE.execute("[9,8,7,6]"); //换算公式

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getKey() {
		return key;
	}
	public void setKey(Integer key) {
		this.key = key;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getUnits() {
		return units;
	}
	public void setUnits(List<String> units) {
		this.units = units;
	}
	public List<Integer> getFactors() {
		return factors;
	}
	public void setFactors(List<Integer> factors) {
		this.factors = factors;
	}
	
	public String toMaxUnit(int nub){
		StringBuffer sub = new StringBuffer();
		for(int i = this.factors.size()-1; i >= 0 && nub > 0; i--){
			int fa = this.factors.get(i);
			
			int val = nub%fa;
			if(val > 0){
				insert(sub,val,units.get(i+1));
			}
			
			nub = nub/fa;
		}
		
		if(nub > 0){
			insert(sub,nub,units.get(0));
		}
		
		return sub.toString();
	}
	
	private void insert(StringBuffer sbf,int val,String unit){
		if(sbf.length() > 0){
			sbf.insert(0," ");
		}
		sbf.insert(0,unit);
		sbf.insert(0, val);
	}
	
	public Integer toMinUnit(String val){
		if(!this.isValid(val)){
			return null;
		}
		
		int nub = 0;
		if(val.contains(" ")){
			String[] vals = val.split(" ");
			for(String v : vals){
				nub += toSingleMU(v);
			}
		}else {
			nub = toSingleMU(val);
		}
		return nub;
	}
	
	private Integer toSingleMU(String val){
		if(!this.isValid(val)){
			return null;
		}
		
		val = val.trim();
		String[] vals = new String[2];
		
		StringBuffer sbf = new StringBuffer();
		boolean isNumber = true;
		for(char c:val.toCharArray()){
			if((c<48 || c > 57) && isNumber){
				vals[0] = sbf.toString();
				isNumber = false;
				sbf.delete(0,sbf.length());
			}
			sbf.append(c);
		}
		vals[1] = sbf.toString();
		
		int nub = Integer.parseInt(vals[0]);
		int index = units.indexOf(vals[1]);
		while(index < factors.size()){
			nub *= factors.get(index++);
		}
		
		return nub;
	}
	
	public boolean isValid(String s){
		if(s == null || s.isEmpty())return false;
		
		s = s.trim();
		int first = (int)s.charAt(0);
		
		return first >= 49 && first <= 57;
	}
	
	public static void main(String[] args) {
		String s = "11a";
		
		s = "2936a 1b 3c 1d 2e";
		UCType uc = new UCType();
		int nub = uc.toMinUnit(s);
		
		System.out.println("nub : " + nub);
		
		System.out.println(uc.toMaxUnit(8878934));
	}
}
