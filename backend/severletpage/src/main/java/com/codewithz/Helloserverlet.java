package com.codewithz;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

@WebServlet("/send")
public class Helloserverlet extends HttpServlet{
    public void doGet(HttpServletRequest req, HttpServletResponse res)
 throws ServletException ,IOException
//instructing the tomcat if anything bad happen handle it

    {

//        it is the 2 object that created by the tomcat that is used to
//        used to get the request  from the client and  send the response to the client

            String username=req.getParameter("username");
            String pass=req.getParameter("password");
            System.out.println(username);
            System.out.println(pass);
//            request_dispatcher
//            calling  from one serverlet  to another  serverlet
//        creating the instance for the requestdispacther
//        RequestDispatcher rd=req.getRequestDispatcher("help");
//        along with we need to send the data also if needed

//        req.setAttribute("value",100);
// here we are forwarding to nextpages
//        rd.forward(req,res);


//    another way of commnication from one serverlet to another serverlet is sendredirect
//res send to client(saying to go to 2 servelet) then from their browser sent the data to serverlet2
//      res.sendRedirect("help?value="+100);

//as the 2 second serverlet expecting for value we attach
//  with the url itself to get from getparameter


//       maintaining the httpsession





//        HttpSession session=req.getSession();
//        session.setAttribute("value",100);
//        res.sendRedirect("help");


//     or we can also send the data using cookie
         Cookie cookie=new Cookie("value",10+"");
         res.addCookie(cookie);
         res.sendRedirect("help");




    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res){
//        it is the 2 object that created by the tomcat that is used to
//        used to get the request  from the client and  send the response to the client
        String username=req.getParameter("username");
        String pass=req.getParameter("password");
        System.out.println(username);
        System.out.println(pass);

    }


}
