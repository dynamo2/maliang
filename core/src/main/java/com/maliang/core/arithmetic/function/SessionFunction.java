package com.maliang.core.arithmetic.function;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.maliang.core.arithmetic.ArithmeticExpression;

public class SessionFunction {
	public static final String HTTP_REQUEST_KEY = "httpRequest";
	
	public static Object execute(Function function ,Map<String,Object> params){
		Object obj = function.executeExpression(params);
		if(obj != null){
			HttpSession session = getSession();
			if(obj instanceof Map){
				Map<String,Object> mv = (Map<String,Object>)obj;
				if(mv.size() > 0){
					for(String k :mv.keySet()){
						session.setAttribute(k, mv.get(k));
					}
				}
				
				return mv;
			}else {
				return session.getAttribute(obj.toString());
			}
		}
		
		return null;
	}
	
	private static HttpSession getSession(){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest(); 
		if(request != null){
			return request.getSession();
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		String s = "addToParams({uname:'wmx',password:'123456',"
				+ "user:db.User.get({user:uname}),"
				//+ "c1:check([notNull(user),'用户名不存在']),"
				//+ "c2:check([user.password=password,'密码错误']),"
				+ "userSession:session({user22:user})"
				+ "})";
		
		//s = "{user:db.User.get({user:uname}),dd:session({user:u})}";
		Map mp = new HashMap();
		ArithmeticExpression.execute(s,mp);
		System.out.println(mp);
	}
}
