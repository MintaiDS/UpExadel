package by.bsu.fpmi.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageStorage {
    private static List<Message> Messages = Collections.synchronizedList(new ArrayList<Message>());
    private static  int curId = 0;
    public static void incId()
    {
        curId++;
    }

    public static synchronized List<Message> getStorage() {
        return Messages;
    }

    public static int getCurId() {
        return curId;
    }
    public static void setCurId (int curId) {MessageStorage.curId = curId;}

    public static synchronized void addMessage(Message m) {
        Messages.add(m);
    }

    public static void addAll(List<Message> m) {
        Messages.addAll(m);
    }

    public static List<Message> getSubMessages(int index) {
        return Messages.subList(index, Messages.size());
    }

    public static void replaceMessage(String id, Message message) {
        for (Message m: Messages) {
            if (m.getMessageId().equals(id)) {
                m = message;
                return ;
            }
        }
        return ;
    }
}
