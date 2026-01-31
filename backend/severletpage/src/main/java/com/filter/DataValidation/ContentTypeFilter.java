package com.filter.DataValidation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jakarta.servlet.Filter;
@WebFilter(
        urlPatterns = {
        "/auth/register",
        "/auth/login",
         "/UserPost/edit-comment",
         "/UserPost/posts",
         "/UserPost/add-comment",
         "/UserPost/edit-post",
          "/UserPost/create"
        }
)
public class ContentTypeFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String ct = request.getContentType();//what type of data that
// The client has sent
        if (path.equals("/auth/login")|| path.equals("/UserPost/edit-post")||
                path.equals("/UserPost/edit-comment")||path.equals("/UserPost/add-comment")) {
            if (ct == null || !ct.contains("application/x-www-form-urlencoded")) {
                response.setStatus(400);
                response.getWriter().print("Invalid Content-Type");
                return;
            }
        }
        if(path.equals("/UserPost/posts")) {
            if (ct != null && ct.startsWith("multipart/form-data")) {
//any fields it will come here like files
                    response.setStatus(400);
                    res.getWriter().write("No file upload expected");
                    return;
                }
            }
        if(path.equals("/UserPost/create")||path.equals("/auth/register")) {
            if (ct != null && !ct.startsWith("multipart/form-data")) {
//any fields it will come here like files
                response.setStatus(400);
                res.getWriter().write("Invalid content type");
                return;
            }
        }


        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(req, res);
            return;
        }
        chain.doFilter(req, res);
    }
}
