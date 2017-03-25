package core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.maliang.core.arithmetic.node.Parentheses;

public class ParenthesesTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLogicExpression1() {
		Object v = Parentheses.compile("9+8 > 13 & 9 > 12", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.FALSE, v);
		
		v = Parentheses.compile("9+8 > 13 | 9 > 12", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.TRUE, v);
	}
	
	@Test
	public void testLogicExpressionSimple() {
		Object v = Parentheses.compile("1>2", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.FALSE, v);
		
		v = Parentheses.compile("1<2", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.TRUE, v);
		
		v = Parentheses.compile("1=2", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.FALSE, v);
		
		v = Parentheses.compile("1=1", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.TRUE, v);
	}
	
	@Test
	public void testLogicExpressionNotSupport() {
		Object v = Parentheses.compile("1 & 0", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.FALSE, v);
		
		v = Parentheses.compile("1 | 0", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.TRUE, v);
	}
	
	@Test
	public void testLogicExpressionCombination() {
		Object v = Parentheses.compile("2=1+1", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.TRUE, v);
		
		v = Parentheses.compile("2-1=1", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.TRUE, v);
		
		v = Parentheses.compile("2+3=1+4", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.TRUE, v);
		
		v = Parentheses.compile("1+3+1=1+4", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.TRUE, v);
		
		v = Parentheses.compile("1+3-1=4-1", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.TRUE, v);
		
		v = Parentheses.compile("2*3=2+4", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.TRUE, v);
		
		v = Parentheses.compile("4/2=4-2", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.TRUE, v);
		
		v = Parentheses.compile("2*3+2-1=2*3+2/1-1", 0).getValue(null);
		assertEquals(Boolean.class, v.getClass());
		assertEquals(Boolean.TRUE, v);
	}
	
	@Test
	public void testMathExpressionCombination() {
		Object v = Parentheses.compile("1+2*3", 0).getValue(null);
		assertEquals(Integer.class, v.getClass());
		assertEquals(7, v);		
	}
	
	@Test
	public void testBusinessExpression() {
		Map<String,Object> params = new HashMap<String,Object>();
		Map<String,Object> goods = new HashMap<String,Object>();
		Map<String,Object> user = new HashMap<String,Object>();
		Map<String,Object> address = new HashMap<String,Object>();
		goods.put("num",2);
		goods.put("price", 3);
		
		user.put("age",8.5);
		user.put("address", address);
		
		address.put("no",6);
		
		params.put("goods",goods);
		params.put("user", user);
		
		Object v = Parentheses.compile("goods.num+goods.price+user.age+user.address.no", 0).getValue(params);
		assertEquals(Double.class, v.getClass());
		assertEquals(19.5, v);	
	}
	
	@Test
	public void testDataTypeReturnType() {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("interage",1);
		params.put("double",1.0);
		params.put("long",1L);
		params.put("float",1.0F);
		params.put("string","a");
		params.put("date", new Date());
		
		Object v = Parentheses.compile("interage+interage", 0).getValue(params);
		assertEquals(Integer.class, v.getClass());
		
		v = Parentheses.compile("interage+double", 0).getValue(params);
		assertEquals(Double.class, v.getClass());
		
		v = Parentheses.compile("interage+float", 0).getValue(params);
		assertEquals(Double.class, v.getClass());
		
		v = Parentheses.compile("interage+long", 0).getValue(params);
		assertEquals(Double.class, v.getClass());
		
		v = Parentheses.compile("interage+string", 0).getValue(params);
		assertEquals(String.class, v.getClass());
		assertEquals("1a", v);
		
		v = Parentheses.compile("date+'1d'", 0).getValue(params);
		assertEquals(Date.class, v.getClass());
		
		v = Parentheses.compile("D'20160924 00:00:00'+'1d'", 0).getValue(params);
		assertEquals(String.class, v.getClass());
		//System.out.println(v);
		
	}	
	
	@Test
	public void testIfElse() {
		String s = "if(i > 1 & i < 10) { 1 } elseif(i > 10 & i < 20) { 12 } else { 'other' }";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("i",2);
		
		Object v = Parentheses.compile(s, 0).getValue(params);
		assertEquals(Integer.class, v.getClass());
		assertEquals(1, v);
		
		params.put("i", 12);
		v = Parentheses.compile(s, 0).getValue(params);
		assertEquals(Integer.class, v.getClass());
		assertEquals(12, v);
		
		params.put("i", 21);
		v = Parentheses.compile(s, 0).getValue(params);
		assertEquals(String.class, v.getClass());
		assertEquals("other", v);
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testMap() {
		String maplistmap = "{accounts:[{name:'wangfan',age:11},{name:'wangziqing',agen:22},{name:'wmx',age:23}]}";
		Object v = Parentheses.compile(maplistmap, 0).getValue(null);
		assertEquals(HashMap.class, v.getClass());
		Map m = (Map)v;
		assertEquals(ArrayList.class, m.get("accounts").getClass());
		List l = (List) m.get("accounts");
		assertEquals(3, l.size());
		assertEquals("wangfan", ((Map)l.get(0)).get("name"));
		assertEquals(11, ((Map)l.get(0)).get("age"));
	}
	
	@Test
	public void testEach() {
		String maplistmap = "{accounts:[{name:'wangfan',age:11},{name:'wangziqing',agen:22},{name:'wmx',age:23}]}";
		String c = "each(accounts) { this.name  }";
		c = "account.age.sum()";
		Object param = Parentheses.compile(maplistmap, 0).getValue(null);
		System.out.println("param : " + param);
		
		Object v = Parentheses.compile(c, 0).getValue(param);
		assertEquals(ArrayList.class, v);
		assertEquals(3, ((List)v).size());
		assertEquals("wangziqing22", ((List)v).get(1));
	}
}
