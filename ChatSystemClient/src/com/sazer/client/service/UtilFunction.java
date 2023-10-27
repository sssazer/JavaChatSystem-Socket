package com.sazer.client.service;

import com.sazer.client.Menu.MainMenu;
import com.sazer.commonClass.Message;
import com.sazer.commonClass.MessageType;

import java.io.*;
import java.sql.DataTruncation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilFunction {
    public volatile static MainMenu mainMenu = null; // 主线程修改该变量，子线程读取该变量，因此需要解决多线程可见性问题

    /**
     * 显示在线用户列表
     * 这个是给主界面调用的，用于给服务器端发送显示在线用户的请求
     */
    public static void showOnlineUser() {
        Message msg = new Message();
        msg.setMsgType(MessageType.MSG_SEND_SHOWONLINEUSER);
        SocketThreadCollector.getCommunicationThread().sendMessage(msg);
    }

    /**
     * 显示在线用户列表
     * 这个是给接收消息的Socket线程用的，当收到服务器返回的消息之后调用该函数打印在线用户
     * @param msg - 服务器返回的包含在线用户列表的Message对象
     */
    public static void showOnlineUser(Message msg) {
        String[] onlineUsers = msg.getContent().split(" ");
        for (String user : onlineUsers) {
            System.out.println("用户 " + user);
        }
    }

    /**
     * 向指定用户发送私聊消息
     * @param content - 要发送的消息的内容
     * @param receiverId - 消息接收用户id，即该消息要发给谁
     */
    public static void sendSingleUser(String content, String receiverId) {
        // 封装消息对象
        Message sendMsg = new Message();
        sendMsg.setMsgType(MessageType.MSG_SEND_SINGLE_CHAT);
        sendMsg.setContent(content);
        sendMsg.setReceiverId(receiverId); // 发送者Id(即自己的Id)发送时再封装

        SocketThreadCollector.getCommunicationThread().sendMessage(sendMsg);
    }

    /**
     * 当收到服务器发来的私聊消息时显示在屏幕上
     * @param msg - 接收到的私聊消息
     */
    public static void receiveSingleUser(Message msg) {
        System.out.println(msg.getSenderId() + "(私聊) : " + msg.getContent());
    }

    /**
     * 打印系统通知
     * @param msg - 系统发来的通知消息
     */
    public static void printServerMsg(Message msg) {
        System.out.println("系统： " + msg.getContent());
    }

    /**
     * 群发消息
     * @param content - 要群发的消息内容
     */
    public static void sendAllUser(String content) {
        Message allMsg = new Message();
        allMsg.setMsgType(MessageType.MSG_SEND_ALL_CHAT);
        allMsg.setContent(content);
        SocketThreadCollector.getCommunicationThread().sendMessage(allMsg);
    }

    /**
     * 打印接收到的群发消息
     * @param msg - 接收到的群发消息对象
     */
    public static void receiveAllUser(Message msg) {
//        System.out.println(msg.getSenderId() + " : " + msg.getContent());
        while (UtilFunction.mainMenu.chatPanel == null) {} // 防止主线程还没赋值子线程就提前调用该函数
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String date = dtf.format(LocalDateTime.now());
        mainMenu.chatPanel.addText(date + " " + msg.getSenderId() + " : " + msg.getContent());
    }

    /**
     * 将指定目录的文件发送给指定用户
     * @param receiverId - 文件要发送给的用户
     * @param filePath - 要发送文件的目录
     */
    public static void sendFile(String receiverId, String filePath) {
        // 先将文件读入客户端中，再封装入Message对象，再发给服务器
        File readFile = new File(filePath);
        if (readFile == null) {
            System.out.println("文件路径有误或不存在，请重新检查");
            return;
        }
        // 创建Message对象
        Message msg = new Message();
        msg.setMsgType(MessageType.MSG_SEND_FILE);
        msg.setReceiverId(receiverId);
        msg.setContent(readFile.getName());

        BufferedInputStream bis = null;
        try {
            // 读取文件内容
            byte[] buffer = new byte[(int)readFile.length()];
            bis = new BufferedInputStream(new FileInputStream(filePath));
            bis.read(buffer);
            // 将文件内容封装入Message对象并发送
            msg.setFileBuffer(buffer);
            SocketThreadCollector.getCommunicationThread().sendMessage(msg);
        } catch (FileNotFoundException e) {
            System.out.println("文件路径有误或不存在，请重新检查");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 将接收到文件存储到本地
     * @param msg - 接收到的文件对象
     */
    public static void receiveFile(Message msg) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(".\\src\\" + msg.getContent()));
            bos.write(msg.getFileBuffer());
            bos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
//            if (bos != null) {
//                try {
//                    bos.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
        }
    } // receiveFile
}
