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
            System.out.println("some2");
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
           System.err.println(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String index = request.getParameter("action_id");
        System.out.println("some1");
        String messages;
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        if (index != null) {
            messages = formResponse(Integer.valueOf(index));

        }
        else
        {
            messages = formResponse(0);
        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(messages);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("some");
        String data = Functions.getMessageBody(request);
        try {
            JSONObject json = Functions.stringToJson(data);
            Message m = Functions.jsonToMessage(json);
            System.out.println(m.toString());
            MessageStorage.addMessage(m);
            MessageStorage.addMessageH(m);
            XMLStorage.addData(m);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = Functions.getMessageBody(request);
        try {
            JSONObject json = Functions.stringToJson(data);
            Message m = Functions.jsonToMessage(json);
            MessageStorage.addMessageH(m);
            XMLStorage.addData(m);
            MessageStorage.replaceMessage(m.getId(), m);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = Functions.getMessageBody(request);
    }

    private String formResponse(int index) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Functions.MESSAGES, MessageStorage.getSubMessages(index));
        jsonObject.put("action_id", MessageStorage.getCurAction());
        return jsonObject.toJSONString();
    }

    private void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
        if (XMLStorage.doesStorageExist()) {
            MessageStorage.addAll(XMLStorage.getMessages());
            MessageStorage.addAllH(MessageStorage.getStorage());
        } else {
            XMLStorage.createStorage();
        }
    }
}