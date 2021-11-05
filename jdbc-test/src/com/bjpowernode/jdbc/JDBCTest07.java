package com.bjpowernode.jdbc;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 1.解决SQL注入问题
 *      只要用户提供的信息不参与SQL语句的编译过程，问题就解决了。
 *      即使用户提供的信息含有SQL语句的关键字，但是没有参与编译，不起作用。
 *      要想用户信息不参与SQL语句的编译，那么必须使用java.sql.PreparedStatement
 *      PreparedStatement接口继承了java.sql.Statement
 *      PreparedStatement是属于预编译的数据库操作对象
 *      PreparedStatement的原理是：预先对SQL语句的框架进行编译，然后再给SQL语句传“值”
 * 2.测试结果：用户名：fdsa
 *            密码：fdsa' or '1'='1
 *            登陆失败
 * 3.解决SQL注入的关键是什么？
 *      用户提供的信息中即使含有sql语句的关键字，但是这些关键字并没有参与编译。不起作用。
 * 4.对比下Statement 和 preparedStatement?
 *      - Statement存在SQL注入问题，PreparedStatement解决了SQL注入问题。
 *      - Statement是编译一次执行一次，PreparedStatement是编译一次，可执行N次。PreparedStatement效率更高一点
 *      //      notes：数据库中如果执行完一条语句以后(需要编译后运行)，再执行与这条语句相同的语句，那么就不需要再次编译可以直接运行
 *      //          Statement每次的loginName = 'xx'都是不同的，故每次都需要编译；而PreparedStatement每次编译的
 *      //          都是框架，都是一样的，故第一次以后都不需要再编译了。(赋值，是在每次编译以后)
 *      - PreparedStatement会在编译阶段做类型的安全检查。---比如loginName = 100。在Statement不会出现问题，而PS的setString()会报错
 * 5.什么情况下必须使用Statement呢？
 *      业务方面要求必须支持SQL注入
 *      Statement支持SQL注入，凡是业务方面要求是需要进行sql语句的拼接的，必须使用Statement
 *
 * @author shkstart
 * @create 2021-06-10 14:14
 */
public class JDBCTest07 {
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

        String loginName = userLoginInfo.get("loginName");
        String loginPwd = userLoginInfo.get("loginPwd");

        //JDBC代码
        Connection conn = null;
        PreparedStatement ps = null;//这里使用PreparedStatement(预编译的数据库操作对象)
        ResultSet rs = null;

        try {
            //1.注册驱动
            Class.forName("com.mysql.jdbc.Driver");
            //2.获取连接
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bjpowernode", "root", "254686");
            //3.获取预编译的数据库操作对象
            //sql语句的框架。其中一个?，表示一个占位符，一个?将来接收一个“值”，注意：占位符不能使用单引号括起来。
            String sql = "select * from t_user where loginName = ? and loginPwd = ?";
            //程序执行到此处，会发送sql语句框架给DBMS,然后DBMS进行sql语句的预先编译
            ps = conn.prepareStatement(sql);
            //给占位符?传值(第一个问号下标是1，第二个问号下标是2，JDBC中所有下标从1开始。包括前面的结果集rs.getString(1)也是代表取得结果中的第一列)
            ps.setString(1,loginName);//字符串加进去自带''，故可以参与编译
            ps.setString(2,loginPwd);
            //4.执行sql
            rs = ps.executeQuery(); //此时无需在里面赋String sql了，因为创建ps对象时，赋sql时，就已经预先对其sql语句的框架进行了编译。然后就赋值了。
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
            if(ps != null){
                try {
                    ps.close();
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
