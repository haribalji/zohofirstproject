package com.filter.UserValidation;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
@WebFilter(urlPatterns = {
        "/auth/register",
        "/auth/login"
})
@MultipartConfig
public class SiginandLoginvalid implements Filter {
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$");
    private String trim(String value) {
        return value == null ? null : value.trim();
    }
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        String path = request.getRequestURI().substring(request.getContextPath().length());
            switch (path) {
                case "/auth/register":
                    SiginValid(req,res,chain);
                    break;
                case "/auth/login":
                    LoginValid(req,res,chain);
                    break;
            }

    }
    public void SiginValid(ServletRequest req, ServletResponse res, FilterChain chain)throws IOException, ServletException {
        HttpServletRequest request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setContentType("application/json");
//        it used for triming the string from the front and back space
        Set<String> allowedParams = Set.of("username", "password","email","image");
        Set<String> params = new HashSet<>();
        params.addAll(request.getParameterMap().keySet());
        Part image =null;
        String username = trim(request.getParameter("username"));
        String email    = trim(request.getParameter("email"));
        String password = trim(request.getParameter("password"));
        try {
            image = request.getPart("image");
        }catch(Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Image is expected");
            return;
        }
        if (image == null || image.getSize() == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Image required bro 0 size or missing");
            return;
        }
        String contentType = image.getContentType();
        if (!contentType.startsWith("image/")) {
            response.setStatus(400);
            res.getWriter().print("invalid image type");
            return;
        }
        // username validation
        if (username == null || username.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Username is needed");
            return;
        }
        if (username.length() < 3 || username.length() > 20) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Username must be between 3 and 20 characters");
            return;
        }
        // Email validation
        if (email == null || email.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Email_id is required");
            return;
        }
        //emailcheck
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Enter a valid email");
            return;
        }
        // password check
        if (password == null || password.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Password is required");
            return;
        }
        if(password.length() > 16)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Password  length is too high");
            return;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Password must contain 1 letter, 1 number, 1 special character and be 6+ characters");
            return;
        }
        if (image.getSize() > MAX_SIZE) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("image size exceeds 5mb limit");
            return;
        }
        chain.doFilter(req, res);
    }
    public void LoginValid(ServletRequest req, ServletResponse res,FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username == null || username.trim().isEmpty()) {
            response.setStatus(400);//bad request
            response.getWriter().print("Username is required");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            response.setStatus(400);
            response.getWriter().print("Password is required");
            return;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
////            matcher it will create the object of user entered password and compare with pattern
            response.getWriter().print(  "Password must contain at least 1 letter, 1 number, 1 special character and be 6+ characters long");
           return;
        }
        // normalize values
        request.setAttribute("username", username.trim());
        request.setAttribute("password", password.trim());

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(req, res);
            return;
        }
        chain.doFilter(req, res);
    }

}