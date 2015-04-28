package by.bsu.fpmi.chat;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Functions {
    public static final String MESSAGES = "messages";
    public static final String AUTHOR = "username";
    public static final String TEXT = "text";
    public static final String ID = "message_id";
    public static final String STATUS = "status";

    public static String getMessageBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static JSONObject stringToJson(String data) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(data.trim());
    }

    public static Message jsonToMessage(JSONObject json) {
        Object status = json.get(STATUS);
        Boolean isNew = false;
        Object id;
        if ("new".equals(status.toString())) {
             id = MessageStorage.getCurId();
            isNew = true;
        }
        else {
            id = json.get(ID);
        }
        Object author = json.get(AUTHOR);
        Object text = json.get(TEXT);
        if (id != null && author != null && text != null) {
            if (isNew) {
                MessageStorage.incId();
            }
            return new Message((String) id, (String) author, (String) text);
        }
        return null;
    }
}