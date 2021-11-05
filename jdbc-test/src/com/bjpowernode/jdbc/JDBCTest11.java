package com.bjpowernode.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * sql脚本：
 *  drop table if exists t_act;
 *  create table t_act(
 *      actno bigint,
 *      balance double(7,2)  //注意：7表示有效数字，2表示小数位的个数
 *  );
 *  insert into t_act(actno,balance) values(111,20000);
 *  insert into t_act(actno,balance) values(222,0);
 *  commit;
 *  select * from t_act;
 *  注：alt + shift + insert可以复制不包含*的部分
 *
 *  重点三行代码：
 *          conn.setAutoCommit(false);--在获取连接对象后，设置是否为自动提交
 *          conn.commit();--在所有的DML语句后，进行提交
 *          conn.rollback();--出现异常处理就可能让事务不能同时成功，故应该在catch()中进行回滚事务，在出现异常后回滚回去。
 *
 * @author shkstart
 * @create 2021-06-10 22:18
 */
public class JDBCTest11 {
    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1.注册驱动
            Class.forName("com.mysql.jdbc.Driver");
            //2.获取连接
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bjpowernode", "root", "254686");
            //将自动提交机制修改为手动调节
            conn.setAutoCommit(false);//开启事务

            //3.获取预编译的数据库操作对象
            String sql = "update t_act set balance = ? where actno = ?";
            ps = conn.prepareStatement(sql);

            //给?传值
            ps.setDouble(1,10000);
            ps.setDouble(2,111);
            int count = ps.executeUpdate();

            String str = null;
            str.toString();//如果不开启事务，此时会出现空指针异常，前面的传值完成了，但是后面的传值操作因为异常没有运行
                            //因为是运行一次就提交一次，所以丢失10000块。所以我们应该将两个操作捆绑成一个事务

            //给?传值
            ps.setDouble(1,10000);
            ps.setInt(2,222);
            count += ps.executeUpdate();

            System.out.println(count == 2 ? "转账成功！" : "转账失败！");

            //程序能够走到这里说明以上程序没有异常，事务结束，手动提交数据
            conn.commit();//提交事务

        } catch (Exception e) {
            //回滚事务
            if(conn != null){
                try {
                    conn.rollback();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
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
