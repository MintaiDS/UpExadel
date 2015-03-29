import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;

import java.sql.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetServlet extends HttpServlet {

    final PrintStream err = System.err;
    final String myName = "GetServlet";

    static Connection connection;
    
    
    public void init(ServletConfig config) {
        err.println(myName + ": init");
        
        ServletContext context = config.getServletContext();
        String username = (String)context.getAttribute("username");
        String password = (String)context.getAttribute("password");
        String database = (String)context.getAttribute("database");
        
        try {
            err.println(myName + ": try to load class");
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException cfe) {
            err.println(myName + ": So sad");
            err.println(cfe.getMessage());
            
            cfe.printStackTrace();
            System.exit(1);
        }
        
        try {
            connection = DriverManager.getConnection(database, username,
                                                     password);
        
            /*connection = 
       DriverManager.getConnection("jdbc:postgresql://localhost:5432/Test", "IFLED", "4321");*/
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
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        //response.setHeader("Access-Control-Allow-Origin", 
        //                   "*");

        String action_id  = request.getParameter("action_id");
        String session_id = request.getParameter("session_id");
        String username = request.getParameter("username");
		
		String result = getMessages(session_id, action_id, username);
		
		out.println(result);
		
        err.println(myName + ": after all");
    }
	
	public String getMessages(String session_id, String action_id, 
							 String username) {
		String user_id = CommonFunctions.getUserId(connection, 
												   username, myName, err);
		if (user_id.charAt(0) == '-') {
			err.println(myName + ": there are some errors");
			return "-3";
		}
		
		try {
			Statement statement = connection.createStatement();
			
			if (user_id.charAt(0) == '-') {
				err.println("there are some errors");
				return "-3";
			}
			
			String sql = "SELECT * FROM messages WHERE room_id in " +
						 "(SELECT room_id FROM links WHERE user_id = " +
						 user_id + ") and action_id > " + action_id +
						 " ORDER BY message_id;";
			//err.println(sql);
			JSONObject answer = new JSONObject();
			JSONArray messages = new JSONArray();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				int message_id = result.getInt(1);
				int user_id_now = result.getInt(2);
				String user = CommonFunctions.getUsername(connection, 
									String.valueOf(user_id_now), myName, err);
				int room_id = result.getInt(3);
				int action_id_now = result.getInt(4);
				String text = result.getString(5);
				int status = result.getInt(6);
				String time = result.getString(7);
				
				//err.println(message_id + " " + user + " " + room_id + " " + 
				//		    action_id_now + " " + text + " " + status + " " + time);
				
				JSONObject msg = getJsonMessage(message_id, user, room_id,
												action_id_now, text, status, time);
				messages.add(msg);
			}
			
			answer.put("messages", messages);
			
			return answer.toJSONString();
			
		}
		catch (SQLException se) {
			//out.println(se.getMessage());
			err.println(myName + ": " + se.getMessage());
			return "-2";
		}
	}
	
	JSONObject getJsonMessage(int message_id, String username, int room_id,
	                          int action_id, String text, int status, String time) {
		JSONObject obj = new JSONObject();
		obj.put("message_id", message_id);
		obj.put("username", username);
		obj.put("room_id", room_id);
		obj.put("action_id", action_id);
		obj.put("text", text);
		obj.put("status", status);
		obj.put("time", time);
		
		return obj;
	}
}