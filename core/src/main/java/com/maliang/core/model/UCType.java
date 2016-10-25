package com.maliang.core.model;

import java.util.List;

import com.maliang.core.arithmetic.AE;

/**
 * 支持单位换算类型
 * **/
public class UCType extends MongodbModel {
	private String name;
	private Integer key;
	
	@Mapped(type=String.class)
	private List<String> units = (List<String>)AE.execute("['a','b','c','d','e']");
	private String description;
	
	@Mapped(type=Integer.class)
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
		boolean negative = false;
		if(nub < 0){
			nub = -nub;
			negative = true;
		}
		
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
		
		if(negative){
			sub.insert(0,"-");
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
		
		val = val.trim();
		boolean negative = false;
		if(val.charAt(0) == '-'){
			negative = true;
			val = val.substring(1).trim();
		}
		
		int nub = 0;
		if(val.contains(" ")){
			String[] vals = val.split(" ");
			for(String v : vals){
				if(v.isEmpty())continue;
				
				nub += toSingleMU(v);
			}
		}else {
			nub = toSingleMU(val);
		}
		
		if(negative){
			nub = -nub;
		}
		return nub;
	}
	
	private Integer toSingleMU(String val){
		val = val.trim();
		String[] vals = separateUnit(val);
		
		int nub = Integer.parseInt(vals[0]);
		int index = units.indexOf(vals[1]);
		while(index < factors.size()){
			nub *= factors.get(index++);
		}
		
		return nub;
	}
	
	public boolean isValid(String s){
		if(s == null || s.trim().isEmpty())return false;
		
		/**
		 * 字符串是否以数字开头（忽略空格）
		 */
		s = s.trim();
		int first = (int)s.charAt(0);
		if(first == '-'){
			s = s.substring(1).trim();
			first = (int)s.charAt(0);
		}
		if(first < 49 && first > 57)return false;
		
		String[] vals = null;
		if(s.contains(" ")){
			vals = s.split(" ");
		}else {
			vals = new String[]{s};
		}
		
		/**
		 * 遍历并检查每个单元：
		 * 1. 是否以数字开头
		 * 2. 单位是否定义
		 * 3. 单位顺序是否正确
		 * **/
		int lastIndex = -1;
		for(String v : vals){
			v = v.trim();
			if(v.isEmpty()) continue;
			
			String[] val = separateUnit(v);
			try {
				Integer.parseInt(val[0]);
			}catch(Exception e){
				return false;
			}
			
			//Check unit
			int index = units.indexOf(val[1]);
			if(index <= lastIndex)return false;
			
			lastIndex = index;
		}
		
		return true;
	}
	
	private String[] separateUnit(String s){
		String[] val = new String[2];
		StringBuffer sbf = new StringBuffer();
		boolean isNumber = true;
		for(char c:s.toCharArray()){
			if((c<48 || c > 57) && isNumber){
				val[0] = sbf.toString();
				isNumber = false;
				sbf.delete(0,sbf.length());
			}
			sbf.append(c);
		}
		val[1] = sbf.toString();
		return val;
	}
	
	public static void main(String[] args) {
		String s = "11a";
		
		s = "   -  2936a    1b    3c 1d 2e";
		UCType uc = new UCType();
		
		int v = 76576876; 
		System.out.println(uc.toMaxUnit(v));
		System.out.println(uc.toMaxUnit(uc.toMinUnit(s)));
		
		//first = (int)s.charAt(0);
		
		//int nub = uc.toMinUnit(s);
		
		//System.out.println("nub : " + nub);
		
		//System.out.println(uc.toMaxUnit(8878934));
	}
}
