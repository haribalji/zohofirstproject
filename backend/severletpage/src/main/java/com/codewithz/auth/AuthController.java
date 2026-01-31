package com.codewithz.auth;
import com.codewithz.Log.AppLogger;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.post.db.DataBaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.List;
@MultipartConfig
@WebServlet("/auth/*")
public class AuthController extends HttpServlet {
private static final String CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
private static final String CLIENT_SECRET = System.getenv("GOOGLE_CLIENT_SECRET");
private static final String REDIRECT_URI = System.getenv("GOOGLE_REDIRECT_URI");
private GoogleIdToken.Payload decodeIdToken(String idTokenString) {
        try {
            List<String> audiences = List.of(CLIENT_ID);//more that one id
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(new NetHttpTransport(), new GsonFactory()).setAudience(audiences).build();
            GoogleIdToken idToken = verifier.verify(idTokenString);
            return idToken != null ? idToken.getPayload() : null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        if (path == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        switch (path) {
            case "/login"://done
                handleLogin(request, response);
                break;

            case "/logout"://done
                handleLogout(request, response);
                break;

            case  "/register":
                handleSignin(request,response);
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
            case  "/me"://done
                UserData(request,response);
                break;

            case  "/google_login":
                TriggerGoogleLogin(request,response);
                break;
            case  "/google-callback":
                GoogleCallBack(request,response);
                break;


            default:
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
public void UserData(HttpServletRequest req, HttpServletResponse res)throws IOException {

        res.setContentType("application/json");
        HttpSession session = req.getSession(false);
        int userId = (int) session.getAttribute("userId");
        res.getWriter().print("{\"userId\": " + userId + "}");
    }
public void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//ServletException it is used to handle any servelt related issues
        response.setContentType("application/json");
        //setting http header so receriver(client) understand the format of data
        PrintWriter out = response.getWriter();
        String ct = request.getContentType();
        String   username = request.getParameter("username");
        String password = request.getParameter("password");
        String sql = "SELECT * FROM users WHERE username=? AND password = encode(digest(?, 'sha256'), 'hex')" ;
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
         
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (
                    rs.next()) {
                int userId = rs.getInt("id");
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", userId);
        
                AppLogger.log(
                        "LOGIN",
                        userId,
                        "SUCCESS",
                        "ip=" + request.getRemoteAddr()//gives the IP address of the machine
                        // that directly made the http request to your server.
                );


                out.print("{\"success\": true}");
            }
            else{
                AppLogger.log(
                        "LOGIN",
                        null,
                        "FAIL",
                        "not entered the valid information"
                );
                //is is the situation where the username not found
                out.print("{\"success\": false}");
            }
        } catch (Exception e) {
            AppLogger.log(
                    "LOGIN",
                    null,
                    "FAIL",
                    "Login error"
            );
            throw new ServletException("Login error", e);
        }
    }
public void handleLogout(HttpServletRequest req, HttpServletResponse res)throws IOException {
        HttpSession session = req.getSession(false);
        int userId = (int) session.getAttribute("userId");
        if (session != null) {
            session.invalidate(); //  destroy session
            AppLogger.log(
                    "LOGOUT",
                    userId,
                    "SUCCESS",
                    "session invalidated"
            );
            res.getWriter().print("login ok");

            return;
        }
        res.getWriter().print("login not done");
    }
    public void TriggerGoogleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String oauthUrl =
                "https://accounts.google.com/o/oauth2/v2/auth?" +
                        "client_id=" + CLIENT_ID +
                        "&redirect_uri=" + REDIRECT_URI +
                        "&response_type=code" +//asking for the auth code from google for token exchange
                        "&scope=openid%20email%20profile" +//email,name  and %20 is used for spacing the encoded url
//                        "&access_type=offline" +//getting the refresh token,so our app can access the user’s Google data even when the user
                        // is not  using the app.just it is used to get new refresh token in order to hold the user for long duration”
                        "&prompt=consent";//show the google consent screen. to explicity allow the user
//        offline indicates that even the user in the not use our app we can access there data
//        openid ->it is used to get the information of  the user it is normal jwt token
        response.sendRedirect(oauthUrl);
//it tells the browser to go to this url
    }
    public void GoogleCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
//        getting the code value from the url
        if (code == null) {
            response.sendRedirect("http://localhost:5500/frontend/index.html?authu=false");
            response.getWriter().print("No code received from Google");
            return;
        }
        String tokenEndpoint = "https://oauth2.googleapis.com/token";
        String urlParameters =
                "code=" + code +
                        "&client_id=" + CLIENT_ID +
                        "&client_secret=" + CLIENT_SECRET +
                        "&redirect_uri=" + REDIRECT_URI +
                        "&grant_type=authorization_code";//asking the access token and idtoken
        URL url = new URL(tokenEndpoint);//creating the url object
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);//as HttpURLConnection by default it is in read mode  and we are making
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.getOutputStream().write(urlParameters.getBytes());//here we will write the data in the request body of the connection
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()) );

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
//collecting all the lines from bufferreader adding in string
            result.append(line);
        }
        JSONObject json = new JSONObject(result.toString());//now conerting the string into json

        String idToken = json.getString("id_token");

        // decode ID Token
        GoogleIdToken.Payload payload = decodeIdToken(idToken);
        String email = payload.getEmail();//it has method
        String name = (String) payload.get("name");//it donot have the any method so it wiil be stord as object in hashmap
//        so coverting into string\sout
        String googleimagePath = (String) payload.get("picture");

//then proceed with login
        boolean userExists = false;
        ResultSet rs=null;
        PrintWriter out=response.getWriter();
        HttpSession session = request.getSession(true);
        int userId=-1;
        String authProvider = null;
        try (Connection dbCon = DataBaseConnection.getConnection()) {
            //  check if email exists
            String checkSql = "SELECT id,auth_provider FROM users WHERE email = ?";
            try (PreparedStatement ps = dbCon.prepareStatement(checkSql)) {
                ps.setString(1, email);
                rs = ps.executeQuery();
                userExists = rs.next();
                if (userExists) {//if user exists then getting the value of it
                    userId = rs.getInt("id");
                    authProvider = rs.getString("auth_provider");
                    session.setAttribute("userId", userId);
                }
            }
            // case 1: if existting user is authprovider of local then link to GOOGLE/LOCAL
//            if user exisit then the redirect
            if(userExists){
                AppLogger.log(
                        "Google login",
                        userId,
                        "success",
                        " from "+" ip= "+request.getRemoteAddr()//gives the IP address of the machine
                );
                response.sendRedirect("http://localhost:5500/frontend/index.html?authu=true");
                return;
            }
//            case 2--> it is the case where  new user is created
            if (!userExists) {
                String insertSql = "INSERT INTO users (email, username, password, auth_provider,userimagepath) "+"VALUES (?, ?, ?, ?,?)";
                try (PreparedStatement ps = dbCon.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, email);
                    ps.setString(2, name);
                    ps.setNull(3, Types.VARCHAR);    
                     ps.setString(4, "GOOGLE");
                    ps.setString(5, googleimagePath);

                    int rows= ps.executeUpdate();//it return no of rows sucessfully returned
                    ResultSet keys = ps.getGeneratedKeys();//it returns the automatically generated ids

                    if (keys.next()) {
                        userId = keys.getInt(1);
                        session.setAttribute("userId", userId);
                    }
                }
                AppLogger.log(
                        "Signup",
                        userId,
                        "success",
                        " new user login from google "+" from "+" ip= "+request.getRemoteAddr()//gives the IP address of the machine
                );
                response.sendRedirect("http://localhost:5500/frontend/index.html?authu=true");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            AppLogger.log(
                    "google signup",
                    userId,
                    "failure",
                    "ip="+ "+request.getRemoteAddr()"//gives the IP address of the machine
            );
            out.print("{\"success\": false, \"message\": \"problem \"}");

        }
    }
    public  void handleSignin(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Part image = request.getPart("image");
        String fileName = System.currentTimeMillis() + "." + image.getSubmittedFileName();
        String uploadPath = getServletContext().getRealPath("/uploads");
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();
        image.write(uploadPath + File.separator + fileName);//it is used to save the image in that path
//       File.separator  it is used in  \ on Windows or / on Unix/Linux) in order to differenctialte the file

        String imagePath = "uploads/" + fileName;

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();//out is used to write data in http response
        try (Connection con = DataBaseConnection.getConnection()) {
            // case-1 if there user exists
            String checkSql = "SELECT id,auth_provider FROM users WHERE email = ?";
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String provider = rs.getString("auth_provider");
                    int userid=rs.getInt("id");
//                  case1.1already LOCAL user or local/google
                    if (provider.contains("LOCAL") || provider.contains("GOOGLE")) {
                        AppLogger.log(
                                "Signup",
                                userid,
                                "failure",
                                "user already exists  "+provider+" from "+" ip= "+request.getRemoteAddr()//gives the IP address of the machine
                        );
                        out.print("{\"success\": false, \"message\": \"User already exists \" }");
                        return;
                    }
                }
            }
//case  new user
//encode(digest(?, 'sha256') it is used to convert the raw binary bits to hexadecial 64 characters which a human can read
            String insertSql = "INSERT INTO users (username, email, password, auth_provider,userimagepath) " + "VALUES (?, ?, encode(digest(?, 'sha256'), 'hex'), ?,?)";
            try (PreparedStatement ps = con.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, password);
                ps.setString(4, "LOCAL");
                ps.setString(5, imagePath);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();//it will give the keys correctly
                if (rs.next()) {
                    int userid = rs.getInt(1);
                    AppLogger.log(
                            "Signup",
                            userid,
                            "SUCCESS",
                            "new user from   "+"ip="+request.getRemoteAddr()//gives the IP address of the machine
                    );
                }
                out.print("{\"success\": true, \"message\": \"New user created successfully\"}");

            }
        }
        catch (Exception e) {
            AppLogger.log(
                    "Signup",
                    null,
                    "faliure",
                    "ip=" + request.getRemoteAddr()//gives the IP address of the machine
            );
            e.printStackTrace();
            out.print("{\"success\": false}");

        }
    }
}