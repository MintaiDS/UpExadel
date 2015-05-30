package by.bsu.fpmi.chat;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by NotePad on 30.05.2015.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(LogoutServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        logger.info("Invalidating Session: " + session);
        if (session != null) {
            DBChanges.freeUser(session.getAttribute("user").toString());
            session.invalidate();
            PrintWriter out = response.getWriter();
            out.print(request.getContextPath() + "/login.html");
            out.flush();
            out.close();
        }
    }
}