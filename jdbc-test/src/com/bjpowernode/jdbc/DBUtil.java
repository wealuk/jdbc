package com.bjpowernode.jdbc;

import java.sql.*;

/**
 * JDBC工具类，简化JDBC编程
 *
 * @author shkstart
 * @create 2021-06-10 23:20
 */
public class DBUtil {
    /*
    工具类中的构造方法都是私有的。
    因为工具类当中的方法都是静态的，不需要new对象，直接采用类名调用。
     */

    private DBUtil() {
    }

    //静态代码块在类中加载时执行，并且只执行一次
    static {
        try {
            //注册驱动只需要一次，如果放在方法里面的话可能重复注册。故需要放在静态代码块中，只在加载时执行一次。
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接对象
     *
     * @return 连接对象
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/bjpowernode", "root", "254686");
    }

    /**
     * 关闭资源
     *
     * @param conn 连接对象
     * @param ps   数据库操作对象
     * @param rs   结果集
     */
    public static void close(Connection conn, Statement ps, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
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