package by.bsu.fpmi.chat;

/**
 * Created by NotePad on 30.05.2015.
 */
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader br = request.getReader();
        String data = br.readLine();
        try {
            JSONObject json = Functions.stringToJson(data);
            String user =json.get("user").toString();
            String password = json.get("password").toString();
            if (DBChanges.addUser(user, password))
            {
                PrintWriter out = response.getWriter();
                out.print("You were successfully registered.");
                out.flush();
                out.close();
            }
            else {
                response.setStatus(302);
                PrintWriter out = response.getWriter();
                out.print("Sorry, we can't register you with such username.");
                out.flush();
                out.close();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
