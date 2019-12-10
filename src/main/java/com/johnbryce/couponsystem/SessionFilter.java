package com.johnbryce.couponsystem;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionFilter implements Filter {

	public SessionFilter() {
		System.out.println("SessionFilter constructor invoked.");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// no op.
	}

//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//
//		HttpServletRequest servletRequest = (HttpServletRequest) request;
//
//		HttpSession session = servletRequest.getSession(false);
//
//		System.out.println("Filter was called.");
//
//		if (session == null) {
//			HttpServletResponse resp = (HttpServletResponse) response;
//			resp.sendRedirect(servletRequest.getContextPath() + "/login");
//		}else {
//			chain.doFilter(servletRequest, response);
//		}
//	}
	
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        System.out.println("Filter was called.");
        HttpSession session = req.getSession(false);
        
        String keyString = req.getRequestURI().split("/")[3] + "_facade";       
        /*
         * If session doesn't exist or if trying to get wrong facade, sends error.
         */
        if (session == null || session.getAttribute(keyString) == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Have to login first!");
        } else {            
            chain.doFilter(request, response);
        }
    }

}
