//package com.wrkr.tickety.global.config.security.filter;
//
//import com.wrkr.tickety.global.config.security.xss.XssRequestWrapper;
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import org.springframework.stereotype.Component;
//
//
//@Component
//public class XssFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        if (request instanceof HttpServletRequest) {
//            chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
//        } else {
//            chain.doFilter(request, response);
//        }
//    }
//}
