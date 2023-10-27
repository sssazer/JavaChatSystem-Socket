package com.sazer.client.Menu;

import com.sazer.client.service.SocketThreadCollector;
import com.sazer.client.service.UtilFunction;
import com.sazer.commonClass.User;
import com.sazer.client.service.LoginService;
import jdk.jshell.execution.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Scanner;
import java.util.Stack;

/**
 * 系统主菜单及入口
 */
public class MainMenu extends JFrame {
    public static void main(String[] args) {
//        new MainMenu().loginPage();
        MainMenu mainMenu = new MainMenu();
        UtilFunction.mainMenu = mainMenu;
    }

    public LoginPanel loginPanel = null;
    public ChatPanel chatPanel = null;

    public MainMenu() {
        super("聊天系统登陆界面");
        this.setSize(400,200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        setResizable(false);

        loginPanel = new LoginPanel();
        this.add(loginPanel);
//        this.setSize(800, 600);
//        this.add(new ChatPanel());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeSockets();
                System.exit(0);
            }
        });

        this.setVisible(true);
    }

    private void changePanel () {
        loginPanel.setVisible(false);

        this.setSize(800,600);
        this.setTitle(user.getUserId());
        chatPanel = new ChatPanel();
        this.add(chatPanel);
        chatPanel.setVisible(true);
    }

    public class ChatPanel extends JPanel {

        JTextArea chatTextArea = null;
        JTextField inputTextField = null;

        public ChatPanel () {
            // 设置布局模式
            this.setLayout(new BorderLayout());

            /********设置聊天记录显示框***********/
            JScrollPane scrollPane = new JScrollPane(); // 滚动条
            // 设置滚动条的出现规则
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            // 设置文本显示区域
            chatTextArea = new JTextArea(30, 100);
            chatTextArea.setFocusable(false); // 隐藏光标
            scrollPane.setViewportView(chatTextArea); // 将文本显示区域放入滚动条区域中
            this.add(scrollPane, BorderLayout.CENTER); // 将滚动条区域加入Panel

            /*************设置文本输入框*******************/
            JPanel inputPanel = new JPanel();
            // 输入框
            inputTextField = new JTextField(30);
            inputTextField.requestFocus();
            inputTextField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        String text = inputTextField.getText();
                        if (sendText(text)) {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                            String date = dtf.format(LocalDateTime.now());
                            addText(dtf.format(LocalDateTime.now()) + "  我 ： " + text);
                        }
                    }
                }
            });
            inputPanel.add(inputTextField);
            // 发送按钮
            JButton sendBtn = new JButton("发送");
            sendBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String text = inputTextField.getText();
                    if (sendText(text)) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        String date = dtf.format(LocalDateTime.now());
                        addText(dtf.format(LocalDateTime.now()) + "  我 ： " + text);
                    }
                }
            });
            inputPanel.add(sendBtn);

            this.add(inputPanel, BorderLayout.SOUTH);
        }

        /**
         * 向聊天记录框中加入消息
         * @param text - 要添加的文本信息
         */
        public void addText(String text) {
            chatTextArea.append(" " + text + "\n");
        }

        /**
         * 点击发送按钮之后发送消息
         */
        public boolean sendText(String text) {
            if ("".equals(text)) {
                JOptionPane.showMessageDialog(MainMenu.this, "发送内容不能为空");
                return false;
            }
            UtilFunction.sendAllUser(text);
            inputTextField.setText("");
            inputTextField.requestFocus();
            return true;
        }
    }

    public class LoginPanel extends JPanel{

        public LoginPanel() {
            this.setLayout(null);

            // 提示文本：用户名
            JLabel nameLabel = new JLabel("用户名：");
            nameLabel.setBounds(30, 20, 80,25);
            this.add(nameLabel);

            // 用户名输入框
            JTextField nameTextField = new JTextField(20);
            nameTextField.setBounds(100, 20, 165, 25);
            this.add(nameTextField);

            // 提示文本：密码
            JLabel pwdLabel = new JLabel("密码：");
            pwdLabel.setBounds(30, 50, 80, 25);
            this.add(pwdLabel);

            // 密码输入框
            JPasswordField pwdPasswordField = new JPasswordField(20);
            pwdPasswordField.setBounds(100, 50, 165, 25);
            this.add(pwdPasswordField);

            // 提示信息
            JLabel warnNoneUsername = new JLabel(); // 用户名为空
            warnNoneUsername.setForeground(Color.red);
            warnNoneUsername.setBounds(275, 20, 80, 25);
            this.add(warnNoneUsername);
            JLabel warnNonePwd = new JLabel(); // 密码为空
            warnNonePwd.setForeground(Color.red);
            warnNonePwd.setBounds(275, 50, 80, 25);
            this.add(warnNonePwd);
            JLabel warnLoginFault = new JLabel(); // 登录失败
            warnLoginFault.setForeground(Color.red);
            warnLoginFault.setBounds(100, 70, 160, 30);
            this.add(warnLoginFault);

            // 登录按钮
            JButton loginButton = new JButton("登录");
            loginButton.setBounds(130, 100, 80, 25);
            this.add(loginButton);
            // 给按钮添加点击事件
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 获取用户输入的用户名和密码并保证非空
                    String usrName = nameTextField.getText();
                    // 用户名为空时显示提示信息
                    if ("".equals(usrName)) {
                        warnNoneUsername.setText("用户名为空");
                        return;
                    } else {
                        warnNoneUsername.setText("");
                    }
                    String pwd = new String(pwdPasswordField.getPassword());
                    // 密码为空时显示提示信息
                    if ("".equals(pwd)) {
                        warnNonePwd.setText("密码为空");
                        return;
                    } else {
                        warnNonePwd.setText("");
                    }

                    // 调用登录服务
                    if (loginService.checkUser(usrName, pwd)) {
                        user = new User(usrName, pwd);
                        changePanel();
                    } else {
                        warnLoginFault.setText("用户名不存在或密码错误");
                    }
                }
            });
        }
    }

    private static Scanner scanner = new Scanner(System.in);

    private boolean loginLoop = true; // 控制登录界面退出
    private boolean mainLoop = true; // 控制主界面退出
    private User user;
    LoginService loginService = new LoginService();
    /**
     * 登录界面
     */
//    public void loginPage () {
//        while (loginLoop) {
//            System.out.println("==============网络通信系统登录界面=================");
//            System.out.println("\t\t1 登录");
//            System.out.println("\t\t9 退出");
//            System.out.print("请输入您的选择:");
//            String choice;
//            choice = scanner.next();
//
//            switch (choice) {
//                case "1":
//                    System.out.print("请输入您的UserId: ");
//                    String userId = scanner.next();
//                    System.out.print("请输入您的密码: ");
//                    String userPwd = scanner.next();
//                    // 将用户id和密码打包为User对象并发往服务器端验证
//                    this.user = new User(userId, userPwd);
//                    loginService = new LoginService(user);
//                    if (loginService.checkUser()) { // 登录成功
//                        // 进入系统主页面
//                        System.out.println("登录成功，欢迎 " + userId);
//                        mainLoop = true;
//                        mainPage();
//                    } else { // 登录失败
//                        System.out.println("用户名或密码有误，请重试");
//                    }
//                    break;
//                case "9":
//                    loginLoop = false;
//                    System.out.println("退出成功，再见");
//                    break;
//                default:
//                    System.out.println("您的输入有误，请重新输入");
//                    break;
//            }
//        }
//    } // loginPage
//
//    /**
//     * 系统主页面
//     */
//    private void mainPage () {
//        while (mainLoop) {
//            System.out.println("===============网络通信系统（用户" + user.getUserId() + ") ====================");
//            System.out.println("\t\t\t1 显示在线用户列表");
//            System.out.println("\t\t\t2 群发消息");
//            System.out.println("\t\t\t3 私   聊");
//            System.out.println("\t\t\t4 发送文件");
//            System.out.println("\t\t\t8 退出登录");
//            System.out.println("\t\t\t9 退出系统");
//            System.out.print("请输入您的选择: ");
//            String choice = scanner.next();
//
//            switch (choice) {
//                case "1":
//                    // 显示在线用户列表
//                    UtilFunction.showOnlineUser();
//                    break;
//                case "2":
//                    // 群发消息
//                    System.out.print("请输入要发送的消息：");
//                    String allContent = scanner.next();
//                    UtilFunction.sendAllUser(allContent);
//                    break;
//                case "3":
//                    // 私聊
//                    System.out.print("请输入要私聊的用户Id：");
//                    String receiverId = scanner.next();
//                    System.out.print("请输入要发送的内容：");
//                    String singleContent = scanner.next();
//                    UtilFunction.sendSingleUser(singleContent,receiverId);
//                    break;
//                case "4":
//                    // 发送文件
//                    System.out.print("请输入要发送的用户Id：");
//                    String fileReceiverId = scanner.next();
//                    System.out.println("请输入要发送的文件路径");
//                    String filePath = scanner.next();
//                    UtilFunction.sendFile(fileReceiverId, filePath);
//                    break;
//                case "8":
//                    System.out.println("退出登录成功");
//                    closeSockets();
//                    mainLoop = false;
//                    break;
//                case "9":
//                    System.out.println("退出成功，再见");
//                    closeSockets();
//                    mainLoop = false;
//                    loginLoop = false;
//                    break;
//                default:
//                    System.out.println("输入有误，请重新输入");
//                    break;
//            }
//        }
//    } // mainPage

    /**
     * 用于正常退出系统
     * 包括退出关闭所有Socket对象，退出所有线程
     */
    private void closeSockets() {
        if (SocketThreadCollector.getCommunicationThread() != null) {
            SocketThreadCollector.getCommunicationThread().closeSocket();
        }
    }
}
