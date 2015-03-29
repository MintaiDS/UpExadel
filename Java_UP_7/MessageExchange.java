import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MessageExchange {

    private JSONParser jsonParser = new JSONParser();

    public String getToken(int index) {
        Integer number = index * 8 + 11;
        return "TN" + number + "EN";
    }

    public int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }

    public String getServerResponse(List<Message> messages) {
        List<JSONObject> messagesReady = new ArrayList<JSONObject>();
        for (Message m: messages){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", m.getID());
            jsonObject.put("name", m.getName());
            jsonObject.put("text", m.getText());
            messagesReady.add(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messages", messagesReady);
        jsonObject.put("token", getToken(messagesReady.size()));
        return jsonObject.toJSONString();
    }

    public String getClientSendMessageRequest(Message message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", message.getID());
        jsonObject.put("name", message.getName());
        jsonObject.put("text", message.getText());
        return jsonObject.toJSONString();
    }

    public Message getClientMessage(InputStream inputStream) throws ParseException {
        JSONObject obj = getJSONObject(inputStreamToString(inputStream));
        Message newMes = new Message(obj);
        return newMes;
    }

    public JSONObject getJSONObject(String json) throws ParseException {
        return (JSONObject) jsonParser.parse(json.trim());
    }

    public String inputStreamToString(InputStream in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        try {
            while ((length = in.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(baos.toByteArray());
    }
}
