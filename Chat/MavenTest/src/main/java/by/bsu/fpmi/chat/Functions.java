package by.bsu.fpmi.chat;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

public class Functions {
    public static final String MESSAGES = "messages";
    public static final String USERNAME = "username";
    public static final String MESSAGETEXT = "messageText";
    public static final String MESSAGEID = "messageId";
    public static final String STATUS = "status";
    public static final String DB = "messages";
    public static final String DBCHANGES = "changes";
    private static HashMap<Integer, AsyncContext> asyncMap = new HashMap<Integer, AsyncContext>();
    private static Integer keyNumber = 0;
    public static String getMessageBody(HttpServletRequest request) throws IOException {
        return request.getParameter("message");
    }

    public static JSONObject stringToJson(String data) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(data.trim());
    }

    public static Message jsonToMessage(JSONObject json) {
        Object status = json.get(STATUS);
        Boolean isNew = false;
        Object id;
        Object text;
        if ("new".equals(status.toString())) {
             id = MessageStorage.getCurId();
            text = json.get(MESSAGETEXT);
            isNew = true;
        }
        else {
            if ("delete".equals(status.toString()))
            {
                text = "";
            }
            else {
                text = json.get(MESSAGETEXT);
            }
            id = json.get(MESSAGEID);
        }
        Object author = json.get(USERNAME);
        if (id != null && author != null && text != null) {
            if (isNew) {
                MessageStorage.incId();
            }
            return new Message( id.toString(), author.toString(),  text.toString(), status.toString());
        }
        return null;
    }

    public static String formResponse(List<Message> m) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Functions.MESSAGES, m);
        return jsonObject.toJSONString();
    }



    public static void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
        if (!XMLStorage.doesStorageExist()) {
            XMLStorage.createStorage();
        }
    }

    public static void startResponse () throws IOException {
        Set<Integer> keys = asyncMap.keySet();
        for (Integer key: keys){
            AsyncContext ac = asyncMap.get(key);
            ServletResponse res = ac.getResponse();
            String messages = Functions.formResponse(DBChanges.selectAll(DBCHANGES));
            PrintWriter out = res.getWriter();
            out.print(messages);
            out.flush();
            out.close();
            ac.complete();
        }
        asyncMap.clear();
        keyNumber = 0;
    }
    public static HashMap<Integer, AsyncContext> getRequests() {
        return asyncMap;
    }
    public static void deleteRequest(AsyncContext ac){
        for (Integer i : asyncMap.keySet()){
            if (asyncMap.get(i).equals(ac)){
                asyncMap.remove(i);
                break;
            }
        }
    }
    public static Integer addKey()
    {
        keyNumber++;
        return keyNumber;
    }
}