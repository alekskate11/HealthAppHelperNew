package com.example.healthapphelper.chats;

public class Chat {
    private String chat_id,chat_name,userId1,userId2;

    public Chat(String chat, String chat_name, String userId1, String userId2) {
        this.chat_id = chat;
        this.chat_name = chat_name;
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    public String getChat() {
        return chat_id;
    }

    public void setChat(String chat) {
        this.chat_id = chat;
    }

    public String getChat_name() {
        return chat_name;
    }

    public void setChat_name(String chat_name) {
        this.chat_name = chat_name;
    }

    public String getUserId1() {
        return userId1;
    }

    public void setUserId1(String userId1) {
        this.userId1 = userId1;
    }

    public String getUserId2() {
        return userId2;
    }

    public void setUserId2(String userId2) {
        this.userId2 = userId2;
    }
}
