package by.bsu.fpmi.chat;

import org.apache.log4j.Logger;
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
    private static Logger logger = Logger.getLogger(MyServlet.class.getName());
    @Override
    public void init() throws ServletException {
        try {
            loadHistory();
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
           logger.error(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Get request");
        String index = request.getParameter("actionId");
        logger.info("ActionId = "+ index);
        if (Integer.valueOf(index) > MessageStorage.getStorage().size())
        {
            response.setStatus(400);
            logger.error("Bad ActionId");
        }else {
            String messages;
            if (Integer.valueOf(index) < MessageStorage.getStorage().size()) {
                messages = formResponse(Integer.valueOf(index));
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(messages);
                out.flush();
            } else {
                response.setStatus(304);
            }

        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Post Request");
        String data = Functions.getMessageBody(request);
        logger.info(data);
        try {
            JSONObject json = Functions.stringToJson(data);
            Message m = Functions.jsonToMessage(json);
            MessageStorage.addMessageH(m);
            XMLStorage.addData(m);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
           logger.error(e);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Put request");
        String data = Functions.getMessageBody(request);
        logger.info(data);
        try {
            JSONObject json = Functions.stringToJson(data);
            Message m = Functions.jsonToMessage(json);
            MessageStorage.addMessageH(m);
            XMLStorage.addData(m);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error(e);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Delete request");
        String data = Functions.getMessageBody(request);
        logger.info(data);
        try {
            JSONObject json = Functions.stringToJson(data);
            Message m = Functions.jsonToMessage(json);
            MessageStorage.addMessageH(m);
            XMLStorage.addData(m);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error(e);
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