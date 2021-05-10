package com.mpproject.delivery_together;

public class ChatRoomItem {

    String name;
    String lastMsg;
    String time;
    int count;

    public ChatRoomItem(String name, String lastMsg, String time, int count) {
        this.name = name;
        this.lastMsg = lastMsg;
        this.time = time;
        this.count = count;

    }

//    public ChatRoomItem(String name, String message) {
//        this.name = name;
//        this.message = message;
//        this.time="0:0";
//        this.read=0;
//    }

    //firebase DB에 객체로 값을 읽어올 때
    //파라미터가 비어있는 생성자가 필요함.
    public ChatRoomItem() {

    }

    //Getter & Setter
    public String getName() {
        return name;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public String getTime() {
        return time;
    }

    public int getCount(){ return count; }
}
