package com.sazer.commonClass;

public enum MessageType {

    /************登陆验证*************/
    LOGINSUCCEED, // 登录验证成功
    LOGINFAILED, // 登录验证失败

    /*************显示在线用户******************/
    MSG_SEND_SHOWONLINEUSER, // 显示所有在线用户的请求消息
    MSG_RETURN_SHOWONLINEUSER, // 服务器对于显示所有在线用户请求的返回结果

    /****************退出系统*************************/
    MSG_SEND_EXIT, // 客户端发出的退出系统的请求
    MSG_RETURN_EXIT, // 服务器端对客户端退出系统的响应消息

    /*******************发送消息***********************/
    MSG_SEND_SINGLE_CHAT, // 发送给对方的私聊消息
    MSG_SEND_ALL_CHAT, // 群发消息

    /***********************错误消息***********************/
    MSG_ERROR, // 错误信息

    /**********************发送文件**********************/
    MSG_SEND_FILE, // 发送文件(只能发送给单个用户)

}
