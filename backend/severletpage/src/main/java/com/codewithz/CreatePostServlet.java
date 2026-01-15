package com.codewithz;
import com.post.db.dbconnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@MultipartConfig//is used  when your servlet needs to receive files
// large form data (like images, PDFs, videos) without this servelt cannot read the data
@WebServlet("/createpost")
public class CreatePostServlet extends HttpServlet {
public void doPost(HttpServletRequest req, HttpServletResponse res)throws IOException, ServletException {

    // ServletException  any issues in db
//IOException issues in:
//reading request data
// writing response data
        res.setContentType("application/json");

        //  here only we will get the  session
        HttpSession session = req.getSession(false);
//getting the old session without creating newone
        System.out.println("create session: " + (session != null ? session.getId() : "null"));
        if (session == null || session.getAttribute("userId") == null) {
//            sending the response to client or browser
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().print("{\"success\": false, \"msg\":\"Not logged in\"}");
            return;
        }
        int userId = (int) session.getAttribute("userId");
        System.out.println(userId+"this  name");
        Part image = req.getPart("image");
        String caption = req.getParameter("caption");
        String fileName = System.currentTimeMillis() + "_" + image.getSubmittedFileName();
// returns the current time in milliseconds
//  image.getSubmittedFileName() it is used for getting the file name image variable
        String uploadPath = getServletContext().getRealPath("/uploads");

//        //C:\Users\Hari\.SmartTomcat\severletpage\severletpage\ uploads  it is used to generate like this path
//generating the path with respect to system
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();
        image.write(uploadPath + File.separator + fileName);
   //it used for file in folder
        String imagePath = "uploads/" + fileName;

        String sql = "INSERT INTO posts (user_id, image_path, caption) VALUES (?, ?, ?)";

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
//PreparedStatement here the query is complied once and reused again
// it is used to prevent from sql injection as it seprate the query and input data and send , then
// db combine  it and execute ,but statement does not seperate them, then sql injection can happen
// it means sending the query with some sql code  in place user input and accessing the db
//eg SELECT * FROM users
//WHERE username = 'admin' AND password = '' OR '1'='1'-->it is always true
            ps.setInt(1, userId);
            ps.setString(2, imagePath);
            ps.setString(3, caption);

            ps.executeUpdate();

            res.getWriter().print("{\"success\": true}");

        } catch (Exception e) {
//  sending false it can  error is occured ,like db error
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().print("{\"success\": false}");
//here we are return custom message as response

        }
    }
}
