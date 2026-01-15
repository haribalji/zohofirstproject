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

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
//  which header is allowed to send request to server
// it used for  allow orgin

//why it send in response ,because the browser trust  what server says
//        frontend (browser) sends a request
//        backend processes the request
//        backend sends a response
//        browser checks the response headers
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

        //   handle preflight request that browser sends an option preflight request first
// if we not handle prefight then browser blocks the request
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//      it checks whether it  options  request
            response.setStatus(HttpServletResponse.SC_OK);
//
            return;
        }
//the options  is a http method used by browser asks the server:
//am i allowed to do this request with your server
        chain.doFilter(req, res);


    }

}
