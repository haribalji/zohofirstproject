package com.filter.AuthValidation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import jakarta.servlet.Filter;
@WebFilter(urlPatterns = {
        "/UserPost/createpost",
        "/UserPost/delete-post",
        "/UserPost/add-comment",
        "/UserPost/edit-comment",
        "/UserPost/edit-post",
        "/UserPost/delete-comment",
        "/auth/me",
        "/UserPost/posts",
        "/auth/logout",
        "/UserPost/comments",
})
public class AuthFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(req, res);
            return;
        }
        String path = request.getServletPath();
        HttpSession session = request.getSession(false);
if(session==null){
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    response.getWriter().print("not logined in");
    return;
}
        chain.doFilter(req, res);
    }
}