package com.johnbryce.couponsystem;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import MyExceptions.InvalidLoginException;
import MyExceptions.SystemMalfunctionException;
import facade.AbsFacade;
import facade.LoginType;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	public static final String ADMIN_FACADE = "admin_facade";
	public static final String COMPANY_FACADE = "company_facade";
	public static final String CUSTOMER_FACADE = "customer_facade";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(true);
		/* Get all request parameters */
		String userName = req.getParameter("user");
		String password = req.getParameter("password");
		String loginTypeStr = req.getParameter("loginType");
		/* Convert login type */
		LoginType type = LoginType.valueOf(loginTypeStr);

//        try {
		/* Login to get the facade */
		AbsFacade facade;
		try {
			facade = AbsFacade.login(userName, password, type);

			String key;
			/* Page selection */
			switch (type) {
				case ADMIN:
					key = ADMIN_FACADE;
					break;
				case COMPANY:
					key = COMPANY_FACADE;
					break;
				default:/* CUSTOMER */
					key = CUSTOMER_FACADE;
					break;
			}
			session.setAttribute(key, facade);
			
		} catch (InvalidLoginException | SystemMalfunctionException e) {
			System.out.println(e.getMessage());
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		}
	}
}

//
//import java.io.IOException;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import MyExceptions.InvalidLoginException;
//import MyExceptions.SystemMalfunctionException;
//import facade.AbsFacade;
//import facade.LoginType;
//
//@SuppressWarnings("serial")
//public class LoginServlet extends HttpServlet {
//
//	public static final String KEY_FACADE = "key_facade";
//
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		req.getRequestDispatcher("WEB-INF/login.html").forward(req, resp);
//	}
//
//	@Override
//	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		HttpSession session = req.getSession(true);
//
//		/* Get all request parameters */
//		String userName = req.getParameter("user");
//		String password = req.getParameter("password");
//		String loginTypeStr = req.getParameter("loginType");
//		/* Convert login type */
//		LoginType type = LoginType.valueOf(loginTypeStr);
//
//		try {
//			/* Login to get the facade */
//			AbsFacade facade = AbsFacade.login(userName, password, type);
//			/* Save the facade in session */
//			session.setAttribute(KEY_FACADE, facade);
//
//			String pagePath;
//			/* Page selection */
//			switch (type) {
//				case ADMIN:
//					pagePath = "WEB-INF/admin.html";
//					break;
//				case COMPANY:
//					pagePath = "WEB-INF/company.html";
//					break;
//				default: /* CUSTOMER */
//					pagePath = "WEB-INF/customer.html";
//					break;
//			}
//
//			/* Sends the user to the appropriate page */
//			req.getRequestDispatcher(pagePath).forward(req, resp);
//
//		} catch (InvalidLoginException | SystemMalfunctionException e) {
//			resp.sendRedirect(req.getContextPath() + "/login");
//		}
//	}
//}
