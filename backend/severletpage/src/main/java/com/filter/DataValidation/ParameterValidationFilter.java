package com.filter.DataValidation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.Filter;
import jakarta.servlet.http.Part;

@WebFilter(urlPatterns = {
        "/auth/register",
        "/auth/login",
        "/auth/me",
        "/UserPost/edit-comment",
        "/UserPost/comments",
        "/UserPost/create",
        "/UserPost/delete-post",
        "/UserPost/add-comment",
        "/UserPost/edit-post",
        "/UserPost/delete-comment",
        "/UserPost/posts",
        "/auth/logout"
})
public class ParameterValidationFilter implements Filter {
    private static final Map<String, Set<String>> RULES = Map.ofEntries(
            Map.entry("/auth/login", Set.of("username", "password")),
            Map.entry("/auth/register", Set.of("username", "email", "password", "image")),
            Map.entry("/UserPost/comments", Set.of("postId")),
            Map.entry("/UserPost/add-comment", Set.of("postId", "comment")),
            Map.entry("/UserPost/edit-comment", Set.of("commentId", "comment")),
            Map.entry("/UserPost/delete-comment", Set.of("commentId")),
            Map.entry("/UserPost/create", Set.of("image", "caption")),
            Map.entry("/UserPost/delete-post", Set.of("id")),
            Map.entry("/auth/me", Set.of()),
            Map.entry("/UserPost/posts", Set.of()),
            Map.entry("/auth/logout", Set.of()),
            Map.entry("/UserPost/edit-post", Set.of("postId", "caption")),
            Map.entry("/edit-post", Set.of("postId", "caption")));

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI().substring(request.getContextPath().length());
        Set<String> allowedParams = RULES.get(path);
        String ct = request.getContentType();
        Set<String> params = new HashSet<>();
        if (allowedParams == null) {
            // if path it self not exists
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("No parameter rules defined for path: " + path);
            return;
        }
        if (!allowedParams.isEmpty()) {
            // it size will be 0 and isempty is true
          
            params.addAll(request.getParameterMap().keySet());
            if (path.equals("/auth/register")) {
                for (Part part : request.getParts()) {
                    // this loop througth all the fields that were received
                    if (part.getSubmittedFileName() != null) {
                        // if the the field value is file then it get the file name other time it will
                        // null

                        params.add(part.getName());

                    }
                }
            }
            if (path.equals("/UserPost/create")) {
                for (Part part : request.getParts()) {
                    // this loop througth all the fields that were received
                    if (part.getSubmittedFileName() != null) {
                        //
                        // if the the feild is file then it get the file name other time it will null
                        params.add(part.getName());
                        // just collecting the file name
                    }
                }
            }

            // parameter counting checking
            if (params.size() != allowedParams.size()) {
                response.setStatus(400);
                response.getWriter().print("Invalid parameter count from the auth side");
                return;
            }

            // unexpected param came means handling that one
            for (String key : params) {
                if (!allowedParams.contains(key)) {
                    response.setStatus(400);
                    response.getWriter().print("Unexpected parameter: " + key);
                    return;
                }
                if (path.equals("/UserPost/add-comment") || path.equals("/add-comment")) {
                    for (String param : allowedParams) {
                        if (req.getParameter(param).equals("null") || 
                                req.getParameter(param).isBlank()) {
                                
                            response.setStatus(400);
                            res.getWriter().write("Missing parameter: " + param);
                            return;
                        }
                    }
                }
            }
        } else {

            if (!request.getParameterMap().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("No parameters expected");
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
