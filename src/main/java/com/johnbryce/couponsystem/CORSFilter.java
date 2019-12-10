//package com.johnbryce.couponsystem;
//
//import com.sun.jersey.spi.container.ContainerRequest;
//import com.sun.jersey.spi.container.ContainerRequestFilter;
//import com.sun.jersey.spi.container.ContainerResponse;
//import com.sun.jersey.spi.container.ContainerResponseFilter;
//import com.sun.jersey.spi.container.ResourceFilter;
//
//public class CORSFilter implements ResourceFilter, ContainerRequestFilter, ContainerResponseFilter {
//
//	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
//		response.getHttpHeaders().add("Access-Control-Allow-Origin", "http://localhost:4200");
//		response.getHttpHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");
//		response.getHttpHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with");
//		return response;
//	}
//
//	public ContainerRequest filter(ContainerRequest request) {
//		return request;
//	}
//
//	public ContainerRequestFilter getRequestFilter() {
//		return this;
//	}
//
//	public ContainerResponseFilter getResponseFilter() {
//		return this;
//	}
//
//}


//		<init-param>
//			<param-name>com.sun.jersey.spi.container.ContainerResponseFilters</param-name>
//			<param-value>com.johnbryce.couponsystem.CORSFilter.java</param-value>
//		</init-param>

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
public class CORSFilter implements Filter {
    public CORSFilter() {
        System.out.println("CORS Filter invoked");
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub      
    }
    
    @Override
    public void destroy() {
    
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        
        System.out.println("CORS filter run!");
        HttpServletResponse resp = (HttpServletResponse) response;
        
        resp.addHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        resp.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, PUT, POST, DELETE");
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        resp.addHeader("Access-Control-Allow-Headers", "content-type");
        
        if (req.getMethod().equals("OPTIONS")) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        
        chain.doFilter(request, resp);
    }
}
