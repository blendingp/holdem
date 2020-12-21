package egovframework.example.sample.admin;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import egovframework.example.sample.service.impl.SampleDAO;

public class LoginInterceptor extends HandlerInterceptorAdapter {

	List<String> urls;

	@Resource(name = "sampleDAO")
	private SampleDAO sampleDAO;

	public void setUrls(List urls) {
		this.urls = urls;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		String url = request.getRequestURI();
		String[] urlArr = request.getRequestURI().split("/");
		for (int i = 0; i < urls.size(); i++) { // url이 무시해야될 url이면
			if (urls.get(i).equals(url)) {
				return true; // 바로 그 url로 이동
			}
		}

		// 무시 하지 않아야 할 url들
		if (urlArr[2].equals("admin")) {// 관리자의 경우
			// 로그인 안되어있으면 로그인 페이지로
			if (session.getAttribute("adminId") == null) {
				response.sendRedirect("/holdem/admin/login.do");
				return false;
			}
		}

		return true;
	}

	// 컨트롤러가 수행되고 화면이 보여지기 직전에 수행되는 메서드
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		super.postHandle(request, response, handler, modelAndView);
	}
}
