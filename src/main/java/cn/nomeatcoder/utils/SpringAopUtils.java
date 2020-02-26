package cn.nomeatcoder.utils;

import org.springframework.aop.aspectj.*;
import org.springframework.aop.config.MethodLocatingFactoryBean;
import org.springframework.aop.config.SimpleBeanFactoryAwareAspectInstanceFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.Map;

/**
 * Spring AOP 工具类
 *
 * @author Harold Luo
 * @date 2018-08-16
 */
public class SpringAopUtils {

	private static final String EXPRESSION = "expression";
	private static final String ASPECT_NAME_PROPERTY = "aspectName";
	private static final String ASPECT_BEAN_NAME_PROPERTY = "aspectBeanName";
	private static final String ORDER_PROPERTY = "order";
	private static final String DECLARATION_ORDER_PROPERTY = "declarationOrder";
	private static final String RETURNING_PROPERTY = "returningName";
	private static final String THROWING_PROPERTY = "throwingName";
	private static final String ARG_NAMES_PROPERTY = "argumentNames";
	private static final String TARGET_BEAN_NAME_PROPERTY = "targetBeanName";
	private static final String METHOD_NAME_PROPERTY = "methodName";

	/**
	 * 根据 Pointcut 表达式创建 Around
	 *
	 * @param method
	 * @param targetAroundBeanName
	 * @param order
	 * @param expression
	 * @param registry
	 * @return void
	 **/
	public static final void registerAroundByExpression(String method, String targetAroundBeanName, Integer order, String expression, BeanDefinitionRegistry registry) {
		BeanReference pointcutReference = registerPointcutWithExpression(expression, registry);

		BeanDefinition methodDefinition = createMethodDefinition(method, targetAroundBeanName);

		BeanDefinition aspectInstanceFactoryDefinition = createAspectInstanceFactoryDefinition(targetAroundBeanName);

		RootBeanDefinition adviceDefinition = createAroundAdviceDefinition(targetAroundBeanName, order, null, methodDefinition, pointcutReference, aspectInstanceFactoryDefinition);

		RootBeanDefinition advisorDefinition = createAspectJAdvisor(adviceDefinition, order);

		BeanDefinitionReaderUtils.registerWithGeneratedName(advisorDefinition, registry);
	}

	/**
	 * 根据 Pointcut 引用创建 Around
	 *
	 * @param method
	 * @param targetAroundBeanName
	 * @param order
	 * @param pointcutRef
	 * @param registry
	 * @return void
	 **/
	public static final void registerAround(String method, String targetAroundBeanName, Integer order, String pointcutRef, BeanDefinitionRegistry registry) {
		BeanReference pointcutReference = createBeanReference(pointcutRef);

		BeanDefinition methodDefinition = createMethodDefinition(method, targetAroundBeanName);

		BeanDefinition aspectInstanceFactoryDefinition = createAspectInstanceFactoryDefinition(targetAroundBeanName);

		RootBeanDefinition adviceDefinition = createAroundAdviceDefinition(targetAroundBeanName, order, null, methodDefinition, pointcutReference, aspectInstanceFactoryDefinition);

		RootBeanDefinition advisorDefinition = createAspectJAdvisor(adviceDefinition, order);

		BeanDefinitionReaderUtils.registerWithGeneratedName(advisorDefinition, registry);
	}

	/**
	 * 批量新增 property
	 *
	 * @param beanDefinition
	 * @param properties
	 * @return org.springframework.beans.MutablePropertyValues
	 **/
	public static final MutablePropertyValues addProperties(BeanDefinition beanDefinition, Map<String, Object> properties) {
		return beanDefinition.getPropertyValues().addPropertyValues(properties);
	}

	/**
	 * 新增 property
	 *
	 * @param beanDefinition
	 * @param key
	 * @param value
	 * @return org.springframework.beans.MutablePropertyValues
	 **/
	public static final MutablePropertyValues addProperty(BeanDefinition beanDefinition, String key, Object value) {
		return beanDefinition.getPropertyValues().add(key, value);
	}

	/**
	 * 构造函数初始化
	 *
	 * @param beanDefinition
	 * @param args
	 * @return void
	 **/
	public static final void constructor(BeanDefinition beanDefinition, Object... args) {
		if (beanDefinition == null || args == null || args.length <= 0) {
			return;
		}
		ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null) {
				continue;
			}
			constructorArgumentValues.addIndexedArgumentValue(i, args[i]);

		}
	}

	/**
	 * 根据表达式创建 Pointcut 并注册到 Spring 容器
	 *
	 * @param expression
	 * @param registry
	 * @return org.springframework.beans.factory.config.RuntimeBeanReference
	 **/
	public static final RuntimeBeanReference registerPointcutWithExpression(String expression, BeanDefinitionRegistry registry) {
		AbstractBeanDefinition pointcutDefinition = createAspectJPointcutDefinition(expression);
		String pointcutBeanName = BeanDefinitionReaderUtils.registerWithGeneratedName(pointcutDefinition, registry);
		return createBeanReference(pointcutBeanName);
	}

	/**
	 * 创建 Spring Bean 的引用
	 *
	 * @param ref
	 * @return org.springframework.beans.factory.config.RuntimeBeanReference
	 **/
	public static final RuntimeBeanReference createBeanReference(String ref) {
		return new RuntimeBeanReference(ref);
	}

	/**
	 * 创建 Advisor 的定义
	 *
	 * @param advice
	 * @param order
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAspectJAdvisor(Object advice, Integer order) {
		RootBeanDefinition advisorDefinition = new RootBeanDefinition(AspectJPointcutAdvisor.class);
		constructor(advisorDefinition, advice);
		if (order != null) {
			addProperty(advisorDefinition, ORDER_PROPERTY, order);
		}
		return advisorDefinition;
	}

	/**
	 * 创建 Advice 的定义
	 *
	 * @param beanName
	 * @param order
	 * @param clazz
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	private static final <T extends AbstractAspectJAdvice> RootBeanDefinition createAspectJAdviceDefinition(String beanName, Integer order, String argument, Class<T> clazz, Object method, Object pointcut, Object aspectInstanceFactory) {
		RootBeanDefinition aspectJAdviceDefinition = new RootBeanDefinition(clazz);
		constructor(aspectJAdviceDefinition, method, pointcut, aspectInstanceFactory);
		addProperty(aspectJAdviceDefinition, ASPECT_NAME_PROPERTY, beanName);
		if (order != null) {
			addProperty(aspectJAdviceDefinition, DECLARATION_ORDER_PROPERTY, order);
		}
		if (argument != null && !argument.trim().isEmpty()) {
			addProperty(aspectJAdviceDefinition, ARG_NAMES_PROPERTY, argument);
		}
		return aspectJAdviceDefinition;
	}

	/**
	 * 创建 AfterThrowing Advice
	 *
	 * @param beanName
	 * @param order
	 * @param argument
	 * @param throwing
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAfterThrowingAdviceDefinition(String beanName, Integer order, String argument, String throwing, Object method, Object pointcut, Object aspectInstanceFactory) {
		RootBeanDefinition afterThrowingAdviceDefinition = createAspectJAdviceDefinition(beanName, order, argument, AspectJAfterThrowingAdvice.class, method, pointcut, aspectInstanceFactory);
		if (throwing != null && !throwing.trim().isEmpty()) {
			addProperty(afterThrowingAdviceDefinition, THROWING_PROPERTY, throwing);
		}
		return afterThrowingAdviceDefinition;
	}

	/**
	 * 创建 AfterThrowing Advice
	 *
	 * @param beanName
	 * @param argument
	 * @param throwing
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAfterThrowingAdviceDefinition(String beanName, String argument, String throwing, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAfterThrowingAdviceDefinition(beanName, null, argument, throwing, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 AfterThrowing Advice
	 *
	 * @param beanReference
	 * @param order
	 * @param argument
	 * @param throwing
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAfterThrowingAdviceDefinition(BeanReference beanReference, Integer order, String argument, String throwing, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAfterThrowingAdviceDefinition(beanReference.getBeanName(), order, argument, throwing, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 AfterThrowing Advice
	 *
	 * @param beanReference
	 * @param argument
	 * @param throwing
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAfterThrowingAdviceDefinition(BeanReference beanReference, String argument, String throwing, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAfterThrowingAdviceDefinition(beanReference.getBeanName(), argument, throwing, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 AfterReturning Advice
	 *
	 * @param beanName
	 * @param order
	 * @param argument
	 * @param returning
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAfterReturningAdviceDefinition(String beanName, Integer order, String argument, String returning, Object method, Object pointcut, Object aspectInstanceFactory) {
		RootBeanDefinition afterReturningAdviceDefinition = createAspectJAdviceDefinition(beanName, order, argument, AspectJAfterReturningAdvice.class, method, pointcut, aspectInstanceFactory);
		if (returning != null && !returning.trim().isEmpty()) {
			addProperty(afterReturningAdviceDefinition, RETURNING_PROPERTY, returning);
		}
		return afterReturningAdviceDefinition;
	}

	/**
	 * 创建 AfterReturning Advice
	 *
	 * @param beanName
	 * @param argument
	 * @param returning
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAfterReturningAdviceDefinition(String beanName, String argument, String returning, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAfterReturningAdviceDefinition(beanName, null, argument, returning, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 AfterReturning Advice
	 *
	 * @param beanReference
	 * @param order
	 * @param argument
	 * @param returning
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAfterReturningAdviceDefinition(BeanReference beanReference, Integer order, String argument, String returning, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAfterReturningAdviceDefinition(beanReference.getBeanName(), order, argument, returning, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 AfterReturning Advice
	 *
	 * @param beanReference
	 * @param argument
	 * @param returning
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAfterReturningAdviceDefinition(BeanReference beanReference, String argument, String returning, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAfterReturningAdviceDefinition(beanReference.getBeanName(), argument, returning, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 After Advice
	 *
	 * @param beanName
	 * @param order
	 * @param argument
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAfterAdviceDefinition(String beanName, Integer order, String argument, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAspectJAdviceDefinition(beanName, order, argument, AspectJAfterAdvice.class, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 After Advice
	 *
	 * @param beanName
	 * @param argument
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAfterAdviceDefinition(String beanName, String argument, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAfterAdviceDefinition(beanName, null, argument, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 After Advice
	 *
	 * @param beanReference
	 * @param order
	 * @param argument
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAfterAdviceDefinition(BeanReference beanReference, String argument, Integer order, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAfterAdviceDefinition(beanReference.getBeanName(), order, argument, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 After Advice
	 *
	 * @param beanReference
	 * @param argument
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAfterAdviceDefinition(BeanReference beanReference, String argument, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAfterAdviceDefinition(beanReference.getBeanName(), argument, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 Before Advice
	 *
	 * @param beanName
	 * @param order
	 * @param argument
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createBeforeAdviceDefinition(String beanName, Integer order, String argument, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAspectJAdviceDefinition(beanName, order, argument, AspectJMethodBeforeAdvice.class, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 Before Advice
	 *
	 * @param beanName
	 * @param argument
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createBeforeAdviceDefinition(String beanName, String argument, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createBeforeAdviceDefinition(beanName, null, argument, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 Before Advice
	 *
	 * @param beanReference
	 * @param order
	 * @param argument
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createBeforeAdviceDefinition(BeanReference beanReference, Integer order, String argument, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createBeforeAdviceDefinition(beanReference.getBeanName(), order, argument, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 Before Advice
	 *
	 * @param beanReference
	 * @param argument
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createBeforeAdviceDefinition(BeanReference beanReference, String argument, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createBeforeAdviceDefinition(beanReference.getBeanName(), argument, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 Around Advice
	 *
	 * @param beanName
	 * @param order
	 * @param argument
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAroundAdviceDefinition(String beanName, Integer order, String argument, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAspectJAdviceDefinition(beanName, order, argument, AspectJAroundAdvice.class, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 Around Advice
	 *
	 * @param beanName
	 * @param argument
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAroundAdviceDefinition(String beanName, String argument, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAroundAdviceDefinition(beanName, null, argument, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 Around Advice
	 *
	 * @param beanReference
	 * @param order
	 * @param argument
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAroundAdviceDefinition(BeanReference beanReference, String argument, Integer order, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAroundAdviceDefinition(beanReference.getBeanName(), order, argument, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 创建 Around Advice
	 *
	 * @param beanReference
	 * @param argument
	 * @param method
	 * @param pointcut
	 * @param aspectInstanceFactory
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAroundAdviceDefinition(BeanReference beanReference, String argument, Object method, Object pointcut, Object aspectInstanceFactory) {
		return createAroundAdviceDefinition(beanReference.getBeanName(), argument, method, pointcut, aspectInstanceFactory);
	}

	/**
	 * 根据 BeanName 创建获取某个 method 的定义
	 *
	 * @param method
	 * @param beanName
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createMethodDefinition(String method, String beanName) {
		RootBeanDefinition methodDefinition = new RootBeanDefinition(MethodLocatingFactoryBean.class);
		addProperty(methodDefinition, TARGET_BEAN_NAME_PROPERTY, beanName).add(METHOD_NAME_PROPERTY, method);
		methodDefinition.setSynthetic(true);
		return methodDefinition;
	}

	/**
	 * 根据 BeanReference 创建获取某个 method 的定义
	 *
	 * @param method
	 * @param beanReference
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createMethodDefinition(String method, BeanReference beanReference) {
		return createMethodDefinition(method, beanReference.getBeanName());
	}

	/**
	 * 根据 BeanName 获取 AspectInstanceFactory 的定义
	 *
	 * @param beanName
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAspectInstanceFactoryDefinition(String beanName) {
		RootBeanDefinition aspectInstanceFactoryDefinition = new RootBeanDefinition(SimpleBeanFactoryAwareAspectInstanceFactory.class);
		addProperty(aspectInstanceFactoryDefinition, ASPECT_BEAN_NAME_PROPERTY, beanName);
		aspectInstanceFactoryDefinition.setSynthetic(true);
		return aspectInstanceFactoryDefinition;
	}

	/**
	 * 根据 BeanReference 获取 AspectInstanceFactory 的定义
	 *
	 * @param beanReference
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAspectInstanceFactoryDefinition(BeanReference beanReference) {
		return createAspectInstanceFactoryDefinition(beanReference.getBeanName());
	}

	/**
	 * 创建 Pointcut 的 bean 定义
	 *
	 * @param expression
	 * @return org.springframework.beans.factory.support.RootBeanDefinition
	 **/
	public static final RootBeanDefinition createAspectJPointcutDefinition(String expression) {
		RootBeanDefinition pointcutDefinition = new RootBeanDefinition(AspectJExpressionPointcut.class);
		pointcutDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		pointcutDefinition.setSynthetic(true);
		addProperty(pointcutDefinition, EXPRESSION, expression);
		return pointcutDefinition;
	}
}
