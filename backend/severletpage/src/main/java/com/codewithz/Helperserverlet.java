package com.codewithz;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.PrintWriter;
import java.io.IOException;

@WebServlet("/help")

public class Helperserverlet  extends HttpServlet   {


    public void doGet(HttpServletRequest req, HttpServletResponse res)  throws ServletException , IOException
    {
//because by default it will send daat in string
//        int num = Integer.parseInt(req.getParameter("num"));

//        PrintWriter out=res.getWriter();
///        inorder to print  data in the screen  it will send it
//        out.println(num*num);
//        System.out.println((num*num));
//        as from the helloserverlet we are setting the value
//        so from here we are getting the value
//        System.out.println(req.getParameter("value"));




//        httpsession workout
//        HttpSession session=req.getSession();
//        int value=(int)session.getAttribute("value");
//        System.out.println(value+"nvp");
//        if need to remove the value from the current session
//        session.removeAttribute("value");



//  another way of getting the data from client using cookie


//
        int value=0;
        Cookie cookies[]= req.getCookies();//we will get all the cookie
//        from there we need to collect our cookie

for(Cookie c:cookies){
    if(c.getName().equals("value")){
        value=Integer.parseInt(c.getValue());
    }
}
        System.out.println(value+"nan");

    }



}
