package com.index.dbHandler;

import org.mariadb.jdbc.MariaDbPoolDataSource;

import java.sql.Connection;

/**
 * AUTHOR
 * MOBIUS
 */
public class dbMain {

    static String url = "jdbc:mariadb://localhost:3306/index_bot?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    static String user = "root";
    static String pass = "";
    static String max_con = "130";

    private static final MariaDbPoolDataSource DATABASE_POOL = new MariaDbPoolDataSource(url + "&user=" + user  + "&password=" + pass + "&maxPoolSize=" + max_con);

    public static void init()
    {
        // Test if connection is valid.
        try
        {
            DATABASE_POOL.getConnection().close();
            System.out.println("Database: Initialized.");
        }
        catch (Exception e)
        {
            System.out.println("Database: Problem on initialize. " + e);
        }
    }

    public static Connection getConnection()
    {
        Connection con = null;
        while (con == null)
        {
            try
            {
                con = DATABASE_POOL.getConnection();
            }
            catch (Exception e)
            {
                System.out.println("DatabaseFactory: Cound not get a connection. " + e);
            }
        }
        return con;
    }

    public static void close()
    {
        try
        {
            DATABASE_POOL.close();
        }
        catch (Exception e)
        {
            System.out.println("DatabaseFactory: There was a problem closing the data source. " + e);
        }
    }
}
