package com.sazer.client.service;

import com.sazer.commonClass.Message;
import com.sazer.commonClass.MessageType;
import com.sazer.commonClass.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 负责建立和服务器端之间的连接
 */
public class LoginService {
    private User usr;
    private boolean loginStatus = false;


    /**
     * 将用户输入的用户名和密码发送至服务器端进行检验
     * @return 如果用户名和密码存在且匹配，能成功登录返回true，否则返回false
     */
    public boolean checkUser(String usrName, String pwd) {
        User loginUser = new User(usrName, pwd);
        try {
            // 连接服务器端
            Socket socket = new Socket(InetAddress.getByName("58.87.96.200"), 9999);
            // 给服务器端发送User对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(loginUser);
            // 接收服务器端返回的User对象的验证结果
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message receiveMessage = (Message)ois.readObject();
            // 登录成功
            if (receiveMessage.getMsgType() == MessageType.LOGINSUCCEED) {
                this.usr = loginUser;
                // 创建一个线程保持该连接，即保持与服务器端的通信
                SocketThread socketThread = new SocketThread(socket, ois, oos);
                socketThread.setUsr(usr);
                socketThread.start();
                SocketThreadCollector.setCommunicationThread(socketThread);
                loginStatus = true;
            }
            // 登录失败
            else {
                oos.close();
                ois.close();
                socket.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return loginStatus;
    }

    public LoginService() {
    }

    public LoginService(User usr) {
        this.usr = usr;
    }

    public User getUsr() {
        return usr;
    }

    public void setUsr(User usr) {
        this.usr = usr;
    }
}
