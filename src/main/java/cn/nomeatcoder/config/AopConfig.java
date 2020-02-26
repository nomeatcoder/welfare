package cn.nomeatcoder.config;

import cn.nomeatcoder.utils.SpringAopUtils;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * AOP配置类
 *
 * @author Chenzhe Mao
 * @date 2020-02-26
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopConfig implements BeanDefinitionRegistryPostProcessor {


	/**
	 * 性能监控入口的 AOP
	 *
	 * @return com.shinemo.client.aop.performance.PerformanceOuter
	 **/
	@Bean
	public PerformanceOuter performanceOuter(@Value("${log.threshold:0}") int threshold) {
		PerformanceOuter performanceOuter = new PerformanceOuter();
		performanceOuter.setThreshold(threshold);
		return performanceOuter;
	}

	/**
	 * 性能监控内部方法调用的 AOP
	 *
	 * @return com.shinemo.client.aop.performance.PerformanceOuter
	 **/
	@Bean
	public PerformanceInner performanceInner() {
		return new PerformanceInner();
	}

	/**
	 * 打印出入参
	 *
	 * @return cn.nomeatcoder.config.PrintParamResult
	 */
	@Bean
	public PrintParamResult printParamResult() {
		return new PrintParamResult();
	}

	/**
	 * 定义 Pointcut
	 *
	 * @return org.springframework.aop.Pointcut
	 **/
	@Bean
	public Pointcut applicationFacadePointcut() {
		AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
		aspectJExpressionPointcut.setExpression("execution(* (cn.nomeatcoder.controller..*Controller).*(..))");
		return aspectJExpressionPointcut;
	}

	/**
	 * 定义 Advisor
	 *
	 * @param performanceInner
	 * @return org.springframework.aop.Advisor
	 **/
	@Bean
	@DependsOn({"performanceInner"})
	public Advisor performanceInnerAdvisor(@Qualifier("performanceInner") PerformanceInner performanceInner) {
		AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
		advisor.setExpression("execution(* cn.nomeatcoder.service.*Service+.*(..)) or execution(* cn.nomeatcoder.dal.mapper.*Mapper.*(..)) or execution(* cn.nomeatcoder.common.MyCache.*(..))");
		advisor.setAdvice(performanceInner);
		// advisor.setOrder(1000);
		return advisor;
	}

	/**
	 * 定义 Advisor
	 *
	 * @param performanceOuter
	 * @param pointcut
	 * @return org.springframework.aop.Advisor
	 **/
	@Bean
	@DependsOn({"performanceOuter", "applicationFacadePointcut"})
	public Advisor performanceOuterAdvisor(@Qualifier("performanceOuter") PerformanceOuter performanceOuter,
	                                       @Qualifier("applicationFacadePointcut") Pointcut pointcut) {
		DefaultBeanFactoryPointcutAdvisor advisor = new DefaultBeanFactoryPointcutAdvisor();
		advisor.setPointcut(pointcut);
		advisor.setAdvice(performanceOuter);
		// advisor.setOrder(1000);
		return advisor;
	}

	/**
	 * 自定义 Bean
	 *
	 * @param registry
	 * @return void
	 **/
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		SpringAopUtils.registerAround(
			"around",
			"printParamResult",
			Ordered.HIGHEST_PRECEDENCE,
			"applicationFacadePointcut",
			registry);
	}

	/**
	 * 自定义 BeanFactory
	 *
	 * @param beanFactory
	 * @return void
	 **/
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	@Bean
	public TaskScheduler scheduledExecutorService() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(8);
		scheduler.setThreadNamePrefix("scheduled-thread-");
		return scheduler;
	}

}
