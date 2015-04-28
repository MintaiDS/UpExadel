package by.bsu.fpmi.chat;

import java.util.ArrayList;
import java.util.List;

public class MessageStorage {
    private static List<Message> Messages = new ArrayList<Message>();
    private static List<Message> history = new ArrayList<Message>();
    private static int curId = 0;
    private static int curAction = 0;
    public static void incId()
    {
        curId++;
    }

    public static void incAction()
    {
        curAction++;
    }

    public static List<Message> getStorage() {
        return Messages;
    }

    public static int getCurId() {
        return curId;
    }

    public static int getCurAction() {
        return curAction;
    }

    public static void addMessage(Message m) {
        Messages.add(m);
    }

    public static void addAll(List<Message> m) {
        Messages.addAll(m);
    }

    public static void addMessageH(Message m) {
        history.add(m);
        incAction();
    }

    public static void addAllH(List<Message> m) {
        history.addAll(m);
        curAction += m.size();
    }

    public static List<Message> getSubMessages(int index) {
        return history.subList(index, Messages.size());
    }

    public static void replaceMessage(String id, Message message) {
        for (Message m: Messages) {
            if (m.getId().equals(id)) {
                m = message;
                return ;
            }
        }
        return ;
    }
}
