package com.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class SimpleCorsFilter implements Filter {


    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("hello");
//        HttpServletResponse r = (HttpServletResponse) res;
//        r.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:3000");
//        ((HttpServletResponse) res).setHeader("Access-Control-Allow-Credentials", "true");
//
//        chain.doFilter(req, res);








        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        //  allow orgin
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5500");

        // allows session cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // allows methods
        response.setHeader(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS"
        );

        //  allows headers
        response.setHeader(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );

        //   handle preflight browser sends an option preflight request first
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);







    }








}
