package by.bsu.fpmi.chat;

/**
 * Created by NotePad on 30.05.2015.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.Buffer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(LoginServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BufferedReader br = request.getReader();
        String data = br.readLine();
        try {
            JSONObject json = Functions.stringToJson(data);
            String user =json.get("user").toString();
            String password = json.get("password").toString();
            if (DBChanges.checkUser(user, password)) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                PrintWriter out = response.getWriter();
                out.print(request.getContextPath() + "/index.html");
                out.flush();
                out.close();
            } else {
                response.setStatus(404);
                PrintWriter out = response.getWriter();
                out.print("Wrong username or password, or it is already in use");
                out.flush();
                out.close();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
