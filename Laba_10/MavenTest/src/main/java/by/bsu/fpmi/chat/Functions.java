package by.bsu.fpmi.chat;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Functions {
    public static final String MESSAGES = "messages";
    public static final String USERNAME = "username";
    public static final String MESSAGETEXT = "messageText";
    public static final String MESSAGEID = "messageId";
    public static final String STATUS = "status";

    public static String getMessageBody(HttpServletRequest request) throws IOException {
        //StringBuilder sb = new StringBuilder();
        return request.getParameter("message");
        /*BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();*/
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
}