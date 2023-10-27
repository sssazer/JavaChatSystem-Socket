package com.sazer.server.service;

import com.sazer.commonClass.Message;
import com.sazer.commonClass.MessageType;
import com.sazer.commonClass.User;
import jdk.jshell.execution.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectServer {

    public static void main(String[] args) {
        new ConnectServer();
    }

    private ServerSocket ss;

    public ConnectServer () {
        try {
            ss = new ServerSocket(9999);
            System.out.println("服务器端开始监听。。。");
            while (true) {
                Socket socket = ss.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                // 接收客户端发来的Usr对象，并验证用户名和密码
                User usr = (User)ois.readObject();
                Message returnMsg = new Message();
                // 验证成功
                if (UtilFunctions.checkLogin(usr.getUserId(), usr.getUserpwd())) {
                    // 给客户端发送登录成功的消息
                    returnMsg.setMsgType(MessageType.LOGINSUCCEED);
                    oos.writeObject(returnMsg);
                    // 建立一个线程来维护与该客户端的Socket连接
                    SocketThread thread = new SocketThread(socket, ois, oos);
                    thread.setUser(usr);
                    SocketThreadCollection.add(usr.getUserId(), thread);
                    thread.start();
                    // 发送系统公告通知所有用户有人上线
                    Message noticeMsg = new Message();
                    noticeMsg.setMsgType(MessageType.MSG_SEND_ALL_CHAT);
                    noticeMsg.setContent("------用户" + usr.getUserId() + "上线---------");
                    noticeMsg.setSenderId("系统通知");
                    UtilFunctions.sendToAllUser(noticeMsg);
                }
                // 验证失败
                else {
                    // 给客户端发送登录失败的消息
                    returnMsg.setMsgType(MessageType.LOGINFAILED);
                    oos.writeObject(returnMsg);
                    socket.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 关闭ServerSocket对象
            try {
                ss.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
