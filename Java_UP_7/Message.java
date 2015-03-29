import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class Message{
    private String id;
    private String name;
    private String text;
    Message(){}
    Message(String id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }
    Message(JSONObject json){
        this.id = (String)json.get("id");
        this.name = (String)json.get("name");
        this.text = (String)json.get("text");
    }
    void setID (String id){
        this.id = id;
    }
    void setName (String name){
        this.name = name;
    }
    void setText (String text){
        this.text = text;
    }
    String getID (){
        return id;
    }
    String getName (){
        return name;
    }
    String getText (){
        return text;
    }
}