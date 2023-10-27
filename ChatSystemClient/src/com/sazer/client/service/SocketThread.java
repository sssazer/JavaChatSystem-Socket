package com.sazer.client.service;

import com.sazer.commonClass.Message;
import com.sazer.commonClass.MessageType;
import com.sazer.commonClass.User;

import java.io.*;
import java.net.Socket;

/**
 * 用于维护与服务器之间建立的socket
 * 使用该类中的方法向相应socket中传递数据
 */
public class SocketThread extends Thread{
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private byte[] buffer = new byte[1024];
    private boolean flag = true;
    private User usr;

    /**
     * 不断监听来自服务器端的消息
     */
    @Override
    public void run() {
        while (flag) {
            try {
                // 接收消息
                Message receivedMsg = (Message) ois.readObject();
                // 收到消息之后，根据消息类型来进行不同的业务处理
                switch (receivedMsg.getMsgType()) {
                    case MSG_RETURN_SHOWONLINEUSER:
                        // 显示在线用户列表
                        UtilFunction.showOnlineUser(receivedMsg);
                        break;
                    case MSG_RETURN_EXIT:
                        // 退出系统
                        flag = false;
                        break;
                    case MSG_SEND_SINGLE_CHAT:
                        // 收到私聊消息
                        UtilFunction.receiveSingleUser(receivedMsg);
                        break;
                    case MSG_ERROR:
                        // 收到系统提示错误信息
                        UtilFunction.printServerMsg(receivedMsg);
                        break;
                    case MSG_SEND_ALL_CHAT:
                        // 收到别的用户发来的群发消息
                        UtilFunction.receiveAllUser(receivedMsg);
                        break;
                    case MSG_SEND_FILE:
                        // 收到发送的文件
                        UtilFunction.receiveFile(receivedMsg);
                        break;


                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 使用对象中封装的socket对象向服务器发送消息
     * @param msg - 要发送的Message对象
     */
    public boolean sendMessage(Message msg) {
        msg.setSenderId(this.usr.getUserId()); // 加入发送方Id(即当前客户端用户的Id)
        try {
            oos.writeObject(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public SocketThread (Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
        this.socket = socket;
        this.ois = ois;
        this.oos = oos;
//        try {
//            this.ois = new ObjectInputStream(socket.getInputStream());
//            this.oos = new ObjectOutputStream(socket.getOutputStream());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     * 退出系统时调用，需要关闭当前线程维护的Socket对象
     */
    public void closeSocket() {
        Message exitMsg = new Message();
        exitMsg.setMsgType(MessageType.MSG_SEND_EXIT);
        sendMessage(exitMsg);
        // 关闭客户端一方Socket发送消息的通道
        try {
            socket.shutdownOutput();
//            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public User getUsr() {
        return usr;
    }

    public void setUsr(User usr) {
        this.usr = usr;
    }
}
