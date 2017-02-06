package com.zqf.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.zqf.common.BaseEntity;

/**
 * @author ZhuQiFeng
 * @addDate 2016年12月13日下午5:13:51
 * @description Spring cache key 生成策略 类名+方法名+参数信息
 *              如果key的hashCode与equals一致,认为是同一个Key
 *              如果传入对象是BaseEntity,那么遍历它所有的一级属性,如果所有一级属性的hashCode一致, 则认为Key相同
 */
public class DefaultKey implements Serializable {

	private static final long serialVersionUID = 1930236297081366076L;

	/** 调用目标对象全类名 */
	private String targetClassName;
	/** 调用目标方法名称 */
	private String methodName;
	/** 调用目标参数 */
	private Object[] params;

	private final int hashCode;

	public DefaultKey(Object target, Method method, Object[] elements) {
		this.targetClassName = target.getClass().getName();
		this.methodName = generatorMethodName(method);
		if (elements != null && elements.length > 0) {
			this.params = new Object[elements.length];
			for (int i = 0; i < elements.length; i++) {
				Object ele = elements[i];
				if (ele instanceof BaseEntity) {
					this.params[i] = ReflectUtil.beanToMap(ele);
				} else {
					this.params[i] = ele;
				}
			}
		}
		this.hashCode = generatorHashCode();
	}

	private String generatorMethodName(Method method) {
		StringBuilder builder = new StringBuilder(method.getName());
		Class<?>[] types = method.getParameterTypes();
		if (types != null && types.length > 0) {
			builder.append("(");
			for (Class<?> type : types) {
				String name = type.getName();
				builder.append(name + ",");
			}
			builder.append(")");
		}
		return builder.toString();
	}

	// 生成hashCode
	private int generatorHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hashCode;
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + Arrays.hashCode(params);
		result = prime * result + ((targetClassName == null) ? 0 : targetClassName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultKey other = (DefaultKey) obj;
		if (hashCode != other.hashCode)
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (!Arrays.equals(params, other.params))
			return false;
		if (targetClassName == null) {
			if (other.targetClassName != null)
				return false;
		} else if (!targetClassName.equals(other.targetClassName))
			return false;
		return true;
	}

	@Override
	public final int hashCode() {
		return hashCode;
	}

}
