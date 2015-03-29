import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;

import java.sql.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CommonFunctions {
	public static String getUserId(Connection con, String username, 
	                               String myName, PrintStream err) {
		
		try {
		
			Statement statement = con.createStatement();
			
			String sql = "SELECT user_id FROM users WHERE name = '" + 
						 username + "';";
			ResultSet result = statement.executeQuery(sql);

			if (!result.next()) {
				// There is no such username in db
				return "-2";
			}
			if (!result.isLast()) {
				// There is multiple username in db
				return "-3";
			}
			
			return new Integer(result.getInt(1)).toString();
		}
		catch (SQLException se) {
			err.println(myName + ": " + se.getMessage());
			return "-1";
		}
    }
	public static String getUsername(Connection con, String user_id, 
	                               String myName, PrintStream err) {
		
		try {
		
			Statement statement = con.createStatement();
			
			String sql = "SELECT name FROM users WHERE user_id = " + 
						 user_id + ";";
			ResultSet result = statement.executeQuery(sql);

			if (!result.next()) {
				// There is no such username in db
				return "-2";
			}
			if (!result.isLast()) {
				// There is multiple username in db
				return "-3";
			}
			
			return result.getString(1);
		}
		catch (SQLException se) {
			err.println(myName + ": " + se.getMessage());
			return "-1";
		}
    }
}