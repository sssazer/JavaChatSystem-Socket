package com.sazer.server.service;

import com.sazer.commonClass.Message;
import com.sazer.commonClass.MessageType;
import com.sazer.commonClass.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketThread extends Thread{

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private User user;
    private boolean flag = true;

    public SocketThread(Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
        this.socket = socket;
//        try {
//            ois = new ObjectInputStream(socket.getInputStream());
//            oos = new ObjectOutputStream(socket.getOutputStream());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        this.ois = ois;
        this.oos = oos;
    }

    /**
     * 不断接收来自客户端的消息，并根据消息类型进行处理
     */
    @Override
    public void run() {
        try {
            while (flag) {
                Message msg = (Message)ois.readObject();
                // 根据消息类型进行处理
                switch (msg.getMsgType()) {
                    // 显示在线列表
                    case MSG_SEND_SHOWONLINEUSER:
                        System.out.println("收到来自用户 " + this.user.getUserId() + " 显示在线用户列表的请求");
                        // 调用方法获取在线用户列表
                        Message showOnelineUserMsg = UtilFunctions.showOnlineUser(user.getUserId());
                        // 向服务器发送在线用户列表
                        this.sendMessage(showOnelineUserMsg);
                        break;
                    // 退出系统
                    case MSG_SEND_EXIT:
                        System.out.println("收到来自用户 " + this.user.getUserId() + " 退出系统的请求");
                        closeSocket();
                        break;
                    // 收到发给其他用户的私聊消息
                    case MSG_SEND_SINGLE_CHAT:
                        UtilFunctions.sendToSingleUser(msg);
                        break;
                    // 收到用户的群发消息
                    case MSG_SEND_ALL_CHAT:
                        UtilFunctions.sendToAllUser(msg);
                        break;
                    // 收到用户发送文件的请求
                    case MSG_SEND_FILE:
                        UtilFunctions.sendFile(msg);
                        break;


                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送消息给客户端
     * @param msg - 要发送的消息
     */
    public void sendMessage(Message msg) {
        try {
            System.out.println("向用户 " + this.user.getUserId() + " 发送消息:" + msg.getContent());
            oos.writeObject(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 收到该线程对应用户的下线申请后调用该函数
     */
    public void closeSocket() {
        // 向客户端发送确认退出系统的消息
        Message returnExitMsg = new Message();
        returnExitMsg.setMsgType(MessageType.MSG_RETURN_EXIT);
        sendMessage(returnExitMsg);
        // 从SocketCollection中删除该Thread对象
        SocketThreadCollection.map.remove(this.user.getUserId());
        // 关闭Socket
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 关闭线程
        flag = false;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

