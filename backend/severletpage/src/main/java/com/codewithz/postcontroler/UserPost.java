package com.codewithz.postcontroler;
import com.codewithz.Log.AppLogger;
import com.post.db.DataBaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;

@MultipartConfig
@WebServlet("/UserPost/*")
public class UserPost extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        if (path == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        switch (path) {
            case "/create"://donee
                handlecreatepost(request, response);
                break;
            case  "/add-comment"://done
                handleAddcomment(request,response);
                break;
            case "/edit-post"://done
                handleEditpost(request,response);
                break;
            case "/edit-comment"://done
                handleEditComment(request,response);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

        }
    }
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        if (path == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        switch (path) {
            case "/delete-post"://done
                handleDeletepost(request, response);
                break;

            case "/delete-comment"://done
                handleDeleteComment(request,response);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        if (path == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        switch (path) {
            case "/comments"://done
                handleLoadComments(request, response);
                break;
            case "/posts"://done
                HandleLoadPost(request, response);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public void handlecreatepost(HttpServletRequest req, HttpServletResponse res)throws IOException, ServletException {
        res.setContentType("application/json");
        //  here only we will get the  session
        HttpSession session = req.getSession(false);
//getting the old session without creating newone
        int userId = (int) session.getAttribute("userId");
//get the image and do the validation

        Part image = req.getPart("image");
        String caption = req.getParameter("caption");
        String fileName = System.currentTimeMillis() + "_" + image.getSubmittedFileName();
// returns the current time in milliseconds
//  image.getSubmittedFileName() it is used for getting the file name image variable
        String uploadPath = getServletContext().getRealPath("/uploads");
//C:\Users\Hari\.SmartTomcat\severletpage\severletpage\ uploads  it is used to generate like this path
//generating the path with respect to system
        File dir = new File(uploadPath);//dir is a object that reprsent that path
        if (!dir.exists()) dir.mkdirs();//if the folder does not exists then creates that folder
        image.write(uploadPath + File.separator + fileName);//it is used to save the image in that path
        String imagePath = "uploads/" + fileName;
        String sql = "INSERT INTO posts (user_id, image_path, caption) VALUES (?, ?, ?)";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, imagePath);
            ps.setString(3, caption);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();//it will give the keys correctly
            if(rs.next()) {
                int postId = rs.getInt(1);
                AppLogger.log(
                        "CREATE_POST",
                        userId,
                        "SUCCESS",
                        "postId=" + postId
                );
            }
            res.getWriter().print("{\"success\": true}");

        } catch (Exception e) {
//  sending false it can  error is occured ,like db error
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            AppLogger.log(
                    "CREATE_POST",
                    userId,
                    "Failure",
                    "postId="+"null"
            );


            res.getWriter().print("{\"success\": false}");
//here we are return custom message as response

        }
    }
    public void handleDeletepost(HttpServletRequest req, HttpServletResponse res) throws IOException ,ServletException{
        //check session
        HttpSession session = req.getSession(false);
        int userId = (int) session.getAttribute("userId");
        int postId = Integer.parseInt(req.getParameter("id"));
        String sql = "DELETE FROM posts WHERE id=? AND user_id=?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            int deleted = ps.executeUpdate();//return no of row is deleted
            if (deleted == 0) {
                //  post exists but not belongs to user
                res.setStatus(403); // 403 accepted the request but not
                // able to give authorize or permission
                AppLogger.log(
                        "DELETE_POST",
                        userId,
                        "failure",
                        "not having access"+"postId=" + postId
                );
                res.getWriter().print("no data deleted");
            } else {
                AppLogger.log(
                        "DELETE_POST",
                        userId,
                        "SUCCESS",
                        "postId=" + postId
                );
                res.setStatus(200); // 200 all ok
            }
        } catch (Exception e) {
            AppLogger.log(
                    "DELETE_POST",
                    userId,
                    "failure",
                    "postId=" + postId
            );

            throw new ServletException("Error", e);
        }
    }
    public void handleAddcomment(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
//        gettigng the needed parameter
        Set<String> allowedParams = Set.of("postId","comment");
        int userId = (int) session.getAttribute("userId");
        int postId = Integer.parseInt(req.getParameter("postId"));
        String comment = req.getParameter("comment");
   
        String sql = "INSERT INTO comments (post_id, user_id, comment) VALUES (?, ?, ?)";
        try (Connection con = DataBaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS))
        {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            ps.setString(3, comment);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();//it will give the keys correctly
            if(rs.next()) {

                int commentId = rs.getInt(1);
                AppLogger.log(
                        "comment creation ",
                        userId,
                        "SUCCESS",
                        "commentId=" + commentId
                );
            }
            res.setStatus(HttpServletResponse.SC_OK);//all ok 200
        }
        catch (Exception e) {
//            both are accepted
//due to run time exeception
            AppLogger.log(
                    "comment creation ",
                    userId,
                    "failure",
                    "error"
            );



            e.printStackTrace();//print the error in server logs
//            raise the server error
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // sets the HTTP response status code to 500. that means issues in server side
//            it also not stop the execution of program
        }
    }
    public void handleEditpost(HttpServletRequest req, HttpServletResponse res) throws ServletException{
        HttpSession session = req.getSession(false);//to get seession data without creating
        int userId = (int) session.getAttribute("userId");
        int  postId = Integer.parseInt(req.getParameter("postId"));
        String  caption = req.getParameter("caption");
        String sql = "UPDATE posts SET caption=? WHERE id=? AND user_id=?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, caption);
            ps.setInt(2, postId);
            ps.setInt(3, userId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                AppLogger.log(
                        "EDIT_POST",
                        userId,
                        "failure",
                        "postId=" + postId
                );
                res.setStatus(403); //  not owner or permission
            }

            AppLogger.log(
                    "EDIT_POST",
                    userId,
                    "SUCCESS",
                    "postId=" + postId
            );
        } catch (Exception e) {
            AppLogger.log(
                    "EDIT_POST",
                    userId,
                    "failure",
                    "postId=" + postId
            );
            throw new ServletException(" error", e);//here the error is printed by

        }
    }
    public void handleDeleteComment(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        int userId = (int) session.getAttribute("userId");
        int commentId = Integer.parseInt(req.getParameter("commentId"));
        String sql = "DELETE FROM comments WHERE id=? AND user_id=?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.setInt(2, userId);
            int deleted = ps.executeUpdate();//return no of rows deleted

            if (deleted == 0) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                you do not have permisssion
                AppLogger.log(
                        "DELETE_COMMENT",
                        userId,
                        "failure",
                        "commentId=" + commentId
                );


            } else {
                AppLogger.log(
                        "DELETE_COMMENT",
                        userId,
                        "SUCCESS",
                        "commentId=" + commentId
                );

                res.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppLogger.log(
                    "DELETE_COMMENT",
                    userId,
                    "failure",
                    "commentId=" + commentId
            );

            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    public void handleEditComment(HttpServletRequest req, HttpServletResponse res)throws IOException ,ServletException {
        HttpSession session = req.getSession(false);
        int userId = (int) session.getAttribute("userId");
        int  commentId = Integer.parseInt(req.getParameter("commentId"));
        String comment = req.getParameter("comment");
        String sql = "UPDATE comments SET comment=? WHERE id=? AND user_id=?";
//                id is comment id
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, comment);
            ps.setInt(2, commentId);
            ps.setInt(3, userId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
//                accept the request but not have access to edit the resouces
                AppLogger.log(
                        "EDIT_COMMENT",
                        userId,
                        "failure",
                        "commentId=" + commentId
                );



                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {
//                ok status
                AppLogger.log(
                        "EDIT_COMMENT",
                        userId,
                        "SUCCESS",
                        "commentId=" + commentId
                );

                res.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            AppLogger.log(
                    "EDIT_COMMENT",
                    userId,
                    "failure",
                    "commentId=" + commentId
            );

//            e.printStackTrace();//printing the error in console
            throw new ServletException(" error", e);
        }
    }
    public void handleLoadComments(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        int  postId = Integer.parseInt(req.getParameter("postId"));
        String sql = "SELECT c.id, c.comment, c.user_id, u.username " + "FROM comments c JOIN users u ON c.user_id = u.id " + "WHERE c.post_id=? ORDER BY c.id";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            StringBuilder json = new StringBuilder("[");//as json starts with [
            while (rs.next()) {
                json.append("{")
                        .append("\"id\":").append(rs.getInt("id")).append(",")
                        .append("\"comment\":\"").append(rs.getString("comment")).append("\",")
                        .append("\"user_id\":").append(rs.getInt("user_id")).append(",")
                        .append("\"username\":\"").append(rs.getString("username")).append("\"")
                        .append("},");
            }
// checking last char is ',' if yes delete it
            if (json.charAt(json.length() - 1) == ',')
                json.deleteCharAt(json.length() - 1);

            json.append("]");
            res.getWriter().print(json);
        } catch (Exception e) {
            e.printStackTrace();//it is used to print the logs for developer
        }
    }
    public void HandleLoadPost(HttpServletRequest req, HttpServletResponse res) {
        res.setContentType("application/json");
        HttpSession session = req.getSession(false);
        String sql = "SELECT p.id,p.user_id, p.image_path, p.caption, u.username,u.userimagepath " + "FROM posts p JOIN users u ON p.user_id = u.id " + "ORDER BY p.id DESC";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            StringBuilder json = new StringBuilder("[");
            while (rs.next()) {

                json.append("{")
                        .append("\"id\":").append(rs.getInt("id")).append(",")
                        .append("\"user_id\":").append(rs.getInt("user_id")).append(",")
                        .append("\"image_path\":\"").append(rs.getString("image_path")).append("\",")
                        .append("\"userimagepath\":\"").append(rs.getString("userimagepath")).append("\",")
                        .append("\"caption\":\"").append(rs.getString("caption")).append("\",")
                        .append("\"username\":\"").append(rs.getString("username")).append("\"")
                        .append("},");
            }
            //to remove the waste comma
            if (json.charAt(json.length() - 1) == ',')
                json.deleteCharAt(json.length() - 1);
            json.append("]");//generally the json can't end with comma
            res.getWriter().print(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}