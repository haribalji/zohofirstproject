package com.filter.PostValidation;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
@MultipartConfig
@WebFilter(urlPatterns = {
"/UserPost/create",
"/UserPost/edit-post",
"/UserPost/edit-comment",
"/UserPost/add-comment"
}
)
public class PostValid implements Filter {
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().print("{\"success\": false, \"message\": \"" + message + "\"}");
    }
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI().substring(request.getContextPath().length());
        switch (path) {
            case "/UserPost/create":
                PostVerification(req,res,chain);
                break;
            case "/UserPost/edit-post":
                PostEditVerification(req,res,chain);
                break;
            case "/UserPost/edit-comment":
                PostCommentEditVerification(req,res,chain);
                break;
            case "/UserPost/add-comment":
                PostAddCommentVerification(req,res,chain);
                break;
        }
    }
    public void PostVerification(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setContentType("application/json");
        Part image=null;

        try {
            image = request.getPart("image");

        }
        catch (Exception e) {
            sendError(response, "Image is error");
            return;
        }
        if (image == null || image.getSize() == 0) {
            sendError(response, "Image is required,not even submitted");
            return;
        }
        //  image type validation
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            sendError(response, "only image files are allowed");
            return;
        }

        // image size validation
        if (image.getSize() > MAX_SIZE) {
            sendError(response, "Image size exceeds 5MB limit");
            return;
        }
        //  caption validation
        String caption = request.getParameter("caption");
        if (caption == null ||
                caption.isBlank() ||
                caption.equalsIgnoreCase("null")) {

            sendError(response, "Caption is required");
            return;
        }
        // 5 caption length
        if (caption.length() > 500) {
            sendError(response, "Caption is too long");
            return;
        }
        chain.doFilter(req, res);
    }
    public void PostEditVerification(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        response.setContentType("application/json");

        //  caption validation
        String caption = request.getParameter("caption");

        if (caption == null || caption.isBlank() || caption.equalsIgnoreCase("null")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"Caption is blank\"}"
            );
            return;
        }

        if (caption.length() > 500) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"Caption is too long\"}"
            );
            return;
        }

        //  postId validation
        String value = request.getParameter("postId");

        if (value == null || value.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"postId is required\"}"
            );
            return;
        }

        int postId;
        try {
            postId = Integer.parseInt(value);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"Invalid postId\"}"
            );
            return;
        }

        if (postId <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"postId must be positive\"}"
            );
            return;
        }

        chain.doFilter(req, res);
    }
    public void PostCommentEditVerification(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setContentType("application/json");
        // comment validation
        String comment = request.getParameter("comment");
        if (comment == null || comment.isBlank() || comment.equalsIgnoreCase("null")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"Comment is required\"}"
            );
            return;
        }
        if (comment.length() > 500) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"Comment is too long\"}"
            );
            return;
        }
        //  commentId validation
        String value = request.getParameter("commentId");

        if (value == null || value.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"commentId is required\"}"
            );
            return;
        }
        int commentId;
        try {
            commentId = Integer.parseInt(value);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"Invalid commentId\"}"
            );
            return;
        }

        if (commentId <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"commentId must be positive\"}"
            );
            return;
        }


        chain.doFilter(req, res);
    }
    public void PostAddCommentVerification(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setContentType("application/json");
        // comment validation
        String comment = request.getParameter("comment");
        if (comment == null || comment.isBlank() || comment.equalsIgnoreCase("null")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"Comment is required\"}"
            );
            return;
        }
        if (comment.length() > 500) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"Comment is too long\"}"
            );
            return;
        }
        //  postId validation
        String value = request.getParameter("postId");

        if (value == null || value.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"postId is required\"}"
            );
            return;
        }
        int postId;
        try {
            postId = Integer.parseInt(value);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"Invalid postId\"}"
            );
            return;
        }

        if (postId <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(
                    "{\"success\": false, \"message\": \"postId must be positive\"}"
            );
            return;
        }


        chain.doFilter(req, res);
    }
}
