package by.bsu.fpmi.chat;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Override;

@WebServlet ("/chat")
public class MyServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        try {
            loadHistory();
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
           System.err.println(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //try{
            //response.setStatus(500);
            //getServletContext().getRequestDispatcher("/500Error.jsp").forward(request, response);
            String index = request.getParameter("actionId");
        if (Integer.valueOf(index) > MessageStorage.getStorage().size())
        {
            response.setContentType("text/html");
            response.setStatus(500);
            getServletContext().getRequestDispatcher("/500Error.jsp").forward(request, response);
        }else {
            String messages;
            if (index != null) {
                messages = formResponse(Integer.valueOf(index));
            } else {
                messages = formResponse(0);
            }
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(messages);
            out.flush();
       /* catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
            System.out.println(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }*/
            //System.out.println(messages);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //System.out.println("some");
        String data = Functions.getMessageBody(request);
        try {
            //System.out.println(data);
            JSONObject json = Functions.stringToJson(data);
           // System.out.println("how");
            Message m = Functions.jsonToMessage(json);
           // System.out.println(m.toString());
           // System.out.println("how2");
            //MessageStorage.addMessage(m);
            MessageStorage.addMessageH(m);
            XMLStorage.addData(m);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
            response.setContentType("text/html");
            response.setStatus(400);
            getServletContext().getRequestDispatcher("/400Error.jsp").forward(request, response);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = Functions.getMessageBody(request);
        try {
            //System.out.println(data);
            JSONObject json = Functions.stringToJson(data);
            // System.out.println("how");
            Message m = Functions.jsonToMessage(json);
            // System.out.println(m.toString());
            // System.out.println("how2");
            //MessageStorage.addMessage(m);
            MessageStorage.addMessageH(m);
            XMLStorage.addData(m);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
            response.setContentType("text/html");
            response.setStatus(400);
            getServletContext().getRequestDispatcher("/400Error.jsp").forward(request, response);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = Functions.getMessageBody(request);
        try {
            //System.out.println(data);
            JSONObject json = Functions.stringToJson(data);
            // System.out.println("how");
            Message m = Functions.jsonToMessage(json);
            // System.out.println(m.toString());
            // System.out.println("how2");
            //MessageStorage.addMessage(m);
            MessageStorage.addMessageH(m);
            XMLStorage.addData(m);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
            response.setContentType("text/html");
            response.setStatus(400);
            getServletContext().getRequestDispatcher("/400Error.jsp").forward(request, response);
        }
    }

    private String formResponse(int index) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Functions.MESSAGES, MessageStorage.getSubMessages(index));
        jsonObject.put("actionId", MessageStorage.getCurAction());

        return jsonObject.toJSONString();
    }

    private void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
        if (XMLStorage.doesStorageExist()) {
            MessageStorage.addAll(XMLStorage.getMessages());
            MessageStorage.addAllH(XMLStorage.getMessages());
        } else {
            XMLStorage.createStorage();
        }
    }
}