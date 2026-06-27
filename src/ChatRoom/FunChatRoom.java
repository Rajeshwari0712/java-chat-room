package chatRoom;

public class FunChatRoom {
    public static void main(String[] args) {
        ChatRoom room = new ChatRoom();

        new UserThread("User1", room).start();
        new UserThread("User2", room).start();
    }
}