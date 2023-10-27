package com.sazer.server.service;

import com.sazer.commonClass.Message;
import com.sazer.commonClass.MessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具类,实现系统中各功能
 */
public class UtilFunctions {

    // 已注册的用户列表
    private static Map<String, String> userList = new HashMap<>();

    static {
        userList.put("123", "123456");
        userList.put("nanbao", "nannan");
        userList.put("sazer", "sazer");
        userList.put("yuebao", "yuebao");
        userList.put("zebao", "zebao");
    }

    /**
     * 验证用户名和密码是否正确
     *
     * @return - 验证成功返回true
     */
    public static boolean checkLogin(String usrId, String usrPwd) {
        if (userList.getOrDefault(usrId, null) == null) return false;
        if (!userList.get(usrId).equals(usrPwd)) return false;
        return true;
    }

    /**
     * 将当前在线用户列表封装进Message对象中并返回
     *
     * @param userId - 记录这个列表要返还给哪个客户端
     */
    public static Message showOnlineUser(String userId) {
        // 获取用户列表
        String users = "";
        for (String user : SocketThreadCollection.map.keySet()) {
            users = users + user + " ";
        }

        // 包装消息对象
        Message msg = new Message();
        msg.setContent(users);
        msg.setMsgType(MessageType.MSG_RETURN_SHOWONLINEUSER);

        return msg;
    }

    /**
     * 将收到的私聊消息转发给目标用户
     * @param msg - 收到的私聊消息
     */
    public static void sendToSingleUser(Message msg) {
        String sendId = msg.getSenderId();
        String receiverId = msg.getReceiverId();
        // 如果用户不存在或者不在线，返回给发送方一个错误信息
        if (!SocketThreadCollection.map.containsKey(receiverId)) {
            Message errMsg = new Message();
            errMsg.setMsgType(MessageType.MSG_ERROR);
            errMsg.setContent("用户" + receiverId + "不存在或未上线");
            SocketThreadCollection.map.get(sendId).sendMessage(errMsg);
            return;
        }
        // 如果用户存在且在线，则将该消息对象转发
        SocketThreadCollection.map.get(receiverId).sendMessage(msg);
    }

    /**
     * 将收到的群发消息转发给所有用户
     * @param msg - 要群发的消息对象
     */
    public static void sendToAllUser(Message msg) {
        for (var entry : SocketThreadCollection.map.entrySet()) {
            if (msg.getSenderId() != null && entry.getKey().equals(msg.getSenderId())) continue;
            entry.getValue().sendMessage(msg);
        }
    }

    public static void sendFile(Message msg) {
        String sendId = msg.getSenderId();
        String receiverId = msg.getReceiverId();
        // 如果用户不存在或者不在线，返回给发送方一个错误信息
        if (!SocketThreadCollection.map.containsKey(receiverId)) {
            Message errMsg = new Message();
            errMsg.setMsgType(MessageType.MSG_ERROR);
            errMsg.setContent("用户" + receiverId + "不存在或未上线");
            SocketThreadCollection.map.get(sendId).sendMessage(errMsg);
            return;
        }
        // 如果用户存在且在线，则将该消息对象转发
        SocketThreadCollection.map.get(receiverId).sendMessage(msg);
    }
}
