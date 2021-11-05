package com.bjpowernode.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * JDBC事务的机制：
 *      JDBC中的事务是自动提交的，什么是自动提交？
 *         只要执行任意一条DML语句，则自动提交一次。这是JDBC默认的事务行为。
 *         但是在实际的业务中，通常都是N条DML语句共同联合才能完成的，必须
 *         保证他们这些DML语句在同一个实务中同时成功或者同时失败。
 *
 * @author shkstart
 * @create 2021-06-10 21:56
 */
public class JDBCTest10 {
    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1.注册驱动
            Class.forName("com.mysql.jdbc.Driver");
            //2.获取连接
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bjpowernode", "root", "254686");
            //3.获取预编译的数据库操作对象
            //第一次给占位符传值
            String sql = "update dept set dname = ? where deptno = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,"x部门");
            ps.setInt(2,30);
            int count = ps.executeUpdate();//执行第一条update语句.执行完就自动提交了，也就是磁盘数据修改了
            System.out.println(count);

            //重新给占位符传值
            ps.setString(1,"y部门");
            ps.setInt(2,20);
            count = ps.executeUpdate();//执行第二条update语句
            System.out.println(count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //6.释放资源
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }
    }
}
