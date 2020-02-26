package cn.nomeatcoder.config;


import cn.nomeatcoder.utils.Profiler;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class PerformanceInner implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String name = getName(invocation);
		Profiler.enter(name);
		try {
			return invocation.proceed();
		} finally {
			Profiler.release();
		}
	}

	protected String getName(MethodInvocation invocation) {
		return String.format("%s-%s", invocation.getMethod().getDeclaringClass().getSimpleName(), invocation.getMethod().getName());
	}
}
