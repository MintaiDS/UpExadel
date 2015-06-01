package by.bsu.fpmi.chat;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Functions {
    public static final String MESSAGES = "messages";
    public static final String USERNAME = "username";
    public static final String USER = "user";
    public static final String MESSAGETEXT = "messageText";
    public static final String MESSAGEID = "messageId";
    public static final String STATUS = "status";
    public static final String DB = "messages";
    public static final String DBCHANGES = "changes";
    public static final String DBUSERS = "users";
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
        Object username = json.get(USERNAME);
        Object user = json.get(USER);
        if (id != null && username != null && user != null && text != null) {
            if (isNew) {
                MessageStorage.incId();
            }
            return new Message( id.toString(), username.toString(), user.toString(), text.toString(), status.toString());
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
    public static Integer addKey() {
        keyNumber++;
        return keyNumber;
    }
    static String createSalt(String str) {
        int mn = Math.min(str.length(), 10);
        int key = 0;
        for (int i = 0; i < mn; ++i){
            key += str.charAt(i);
        }
        int len = str.length();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < str.length(); i++)
        {
            if (i % 2 == 0)
                buf.append(str.charAt(i));
            else
            buf.append((char)(33+(key+len)%31));
        }
        return buf.toString();
    }
    static String hashAndSalt(String salt, String password) throws NoSuchAlgorithmException {
        MessageDigest mDigest = null;
        mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest((salt+password).getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}