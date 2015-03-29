import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;

import java.sql.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class FirstServlet extends HttpServlet {

    final PrintStream err = System.err;
    final String myName = "FirstServlet";

    static Connection connection;
    
    public void init(ServletConfig config) {
        err.println(myName + ": init");
        
        String username = config.getInitParameter("username");
        String password = config.getInitParameter("password");
        String database = config.getInitParameter("database");
        
        ServletContext context = config.getServletContext();
        context.setAttribute("username", username);
        context.setAttribute("password", password);
        context.setAttribute("database", database);
        
        try {
            err.println(myName + ": try to load org.postgresql.Driver");
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException cfe) {
            err.println(myName + ": So sad");
            err.println(cfe.getMessage());
            
            cfe.printStackTrace();
            System.exit(1);
        }
        
        try {
			err.println(myName + ": try to get connection");
            connection = DriverManager.getConnection(database, username,
                                                     password);
        }
        catch (SQLException se) {
            err.println(myName + ": So sad 2");
            err.println();
            se.printStackTrace();
            //System.out.println(se.getMessage());
            System.exit(2);
        }
    }
	
	public void doGet(HttpServletRequest request, 
                      HttpServletResponse response)
            throws ServletException, IOException {
	}

    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        //response.setHeader("Access-Control-Allow-Origin", 
        //                   "*");

        String username = request.getParameter("username");
		
		String userId;
        
        userId = CommonFunctions.getUserId(connection, username, 
		                                   myName, err);
        out.println(userId);
        
        err.println(myName + " - userId: " + userId);
        
        err.println(myName + ": after all");
    }
}