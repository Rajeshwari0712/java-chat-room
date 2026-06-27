package chatRoom;

import java.util.ArrayList;
import java.util.Date;

class ChatRoom {
    private final java.util.List<UserThread> users = new ArrayList<>();

    public synchronized void addUser(UserThread user) {
        users.add(user);
        broadcast("System: " + user.getUserName() + " joined the chat");
    }

    public synchronized void removeUser(UserThread user) {
        users.remove(user);
        broadcast("System: " + user.getUserName() + " left the chat");
    }

    public synchronized void sendMessage(String user, String message) {
        String time = new java.text.SimpleDateFormat("HH:mm").format(new Date());
        broadcast("[" + time + "] " + user + ": " + message);
    }

    private void broadcast(String msg) {
        for (UserThread u : users) {
            u.receiveMessage(msg);
        }
    }

    public synchronized java.util.List<String> getUserNames() {
        java.util.List<String> list = new ArrayList<>();
        for (UserThread u : users) list.add(u.getUserName());
        return list;
    }
}
