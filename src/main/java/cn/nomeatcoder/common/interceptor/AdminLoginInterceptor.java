package cn.nomeatcoder.common.interceptor;

import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.IgnoreLogin;
import cn.nomeatcoder.common.ResponseCode;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.User;
import cn.nomeatcoder.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class AdminLoginInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (hasPermission(request.getSession(), handler)) {
			return true;
		}
		String json = GsonUtils.toJson(ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员"));
		returnJson(response, json);
		return false;
	}

	private boolean hasPermission(HttpSession session, Object handler) {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			String methodName = handlerMethod.getMethod().getName();

			log.info("拦截请求, className:{}, methodName:{}", handlerMethod.getBean().getClass().getSimpleName(), methodName);
			IgnoreLogin ignoreLogin = handlerMethod.getMethodAnnotation(IgnoreLogin.class);
			if (ignoreLogin == null) {
				User user = (User) session.getAttribute(Const.CURRENT_USER);
				if (user == null || user.getRole() != Const.Role.ROLE_ADMIN) {
					log.info("需要登录但未登录, 拦截");
					return false;
				}
				user.setPassword(null);
				log.info("需要登录, 是管理员登录, 放行, user:{}", user);
				return true;
			}
			log.info("不需要前置登录校验, 放行");
		}
		return true;
	}

	private void returnJson(HttpServletResponse response, String json) {
		PrintWriter writer = null;
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		try {
			writer = response.getWriter();
			writer.print(json);

		} catch (IOException e) {
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
