package com.zqf.util;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

public class MyCacheKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object arg0, Method arg1, Object... arg2) {
		String projectName = PropertiesUtil.getConfig("projectName");
		// shiro管理的session
		// Subject currentUser = SecurityUtils.getSubject();
		// Session session = currentUser.getSession();
		// User user = (User) session.getAttribute(Const.SESSION_USER);
		// Integer user_id = user.getUser_id();
		DefaultKey dfk = new DefaultKey(arg0, arg1, arg2);
		int hashCode = dfk.hashCode();
		String name = arg1.getName();
		StringBuffer sb = new StringBuffer(projectName);
		sb.append("_").append(name).append("_").append(hashCode);
		return sb.toString();
	}

}
