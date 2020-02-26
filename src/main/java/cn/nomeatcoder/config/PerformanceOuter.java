package cn.nomeatcoder.config;


import cn.nomeatcoder.utils.Profiler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;

@Slf4j
public class PerformanceOuter extends PerformanceInner {
	@Getter
	@Setter
	private int threshold = 1000;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Profiler.Entry start = Profiler.getEntry();
		boolean outer = start == null;
		if (!outer) {
			return super.invoke(invocation);
		}
		String name = getName(invocation);
		Profiler.start(name);
		try {
			return invocation.proceed();
		} finally {
			if (outer) {
				Profiler.release();
				long duration = Profiler.getDuration();
				if (duration > threshold) {
					log.info("{}:\n{}", name, Profiler.dump());
				}
				Profiler.reset();
			}
		}
	}
}
