package com.bjpowernode.jdbc;

import java.sql.*;
import java.util.Scanner;

/**
 * @author shkstart
 * @create 2021-06-10 18:39
 */
public class JDBCTest08 {
    public static void main(String[] args) {
        /*
        只能使用Statement，才能用到sql注入。而像PreparedStatement，先编译完只能赋值了无法sql注入
         */
        //用户在控制台输入desc就是降序，输入asc就是升序
        Scanner scan = new Scanner(System.in);
        System.out.println("输入desc或asc，desc表示降序，asc表示升序");
        System.out.println("请输入：");
        String keyWords = scan.nextLine();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            //执行SQL
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bjpowernode", "root", "254686");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select ename from emp order by ename " + keyWords);
            while(rs.next()){
                System.out.println(rs.getString("ename"));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
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
                scan.close();
            }
        }

    }
}
