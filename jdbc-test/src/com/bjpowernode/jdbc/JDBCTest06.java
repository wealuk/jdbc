package com.bjpowernode.jdbc;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * 实现功能：
 *      1.需求：
 *          模拟用户登录功能的实现。
 *      2.业务描述：
 *          程序运行的时候，提供一个输入的入口，可以让用户输入用户名和密码
 *          用户输入用户名和密码之后，提供信息，java程序收集到用户信息
 *          Java程序连接数据库验证用户名和密码是否合法
 *          合法：显示登陆成功
 *          不合法：显示登陆失败
 *       3.数据的准备：
 *          在实际开发中，表的设计会使用专业的建模工具，比如：PowerDesigner
 *          使用PD工具来进行数据库表的设计
 *       4.当前程序存在的问题：
 *          用户名：fdsa
 *          密码：fdsa' or '1'='1
 *          登陆成功
 *          这种现象被称为SQL注入(安全隐患)。(黑客接常使用)
 *       5.导致SQL注入的根本原因是什么？
 *            用户输入的信息中含有sql语句的关键字，并且这些关键字参与sql语句的编译过程，
 *            导致sql语句的愿意被扭曲，进而达到sql注入。
 *
 * @author shkstart
 * @create 2021-06-08 20:45
 */
public class JDBCTest06 {
    public static void main(String[] args) {
        //初始化一个界面
        Map<String,String> userLoginInfo = initUI();
        //验证用户名和密码
        boolean loginSuccess = login(userLoginInfo);
        //最后输出结果
        System.out.println(loginSuccess ? "登陆成功" : "登陆失败");
    }

    /**
     * 用户登录
     * @param userLoginInfo 用户登录信息
     * @return false表示失败,true表示成功
     */
    private static boolean login(Map<String, String> userLoginInfo) {
        //打标记的意识
        boolean loginSuccess = false;

        //JDBC代码
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            //1.注册驱动
            Class.forName("com.mysql.jdbc.Driver");
            //2.获取连接
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bjpowernode", "root", "254686");
            //3.获取数据库操作对象
            stmt = conn.createStatement();
            //4.执行sql
//        String sql = "select * from t_user where loginName = 'xx' and loginPwd = 'xx'";
            String sql = "select * from t_user where loginName = '"+ userLoginInfo.get("loginName") +"' and loginPwd = '"+ userLoginInfo.get("loginPwd") +"'";
            //以上正好完成了sql语句的拼接，以下代码的含义是，发送sql语句给DBMS,DBMS进行sql编译
            //正好将用户提供的“非法信息”编译进去。导致原sql语句的含义被扭曲了。
            rs = stmt.executeQuery(sql);
            //5.处理结果集
            if(rs.next()){
                //查不到则没有记录，查到了那么也只有一条。故不像要拿到多个记录，遍历出每一列的数据，用到while(rs.next())
                loginSuccess = true;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //6.释放资源
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stmt != null){
                try {
                    stmt.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(conn != null){
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return loginSuccess;

    }

    /**
     * 初始化用户界面
     * @return 用户输入的用户名和密码等登录基本信息
     */
    private static Map<String, String> initUI() {
        Scanner scan = new Scanner(System.in);
        System.out.print("用户名：");
        String loginName = scan.nextLine();
        System.out.print("密码：");
        String loginPwd = scan.nextLine();
        Map<String,String> userLoginInfo = new HashMap<>();
        userLoginInfo.put("loginName",loginName);
        userLoginInfo.put("loginPwd",loginPwd);
        return userLoginInfo;
    }
}
