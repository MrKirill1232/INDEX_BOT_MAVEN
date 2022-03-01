package com.index.dbHandler.handlers;

import com.index.IndexMain;
import com.index.dbHandler.dbMain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class dbGIFHandler {

    IndexMain im = new IndexMain();

    private static final String GET_USER_ID_FROM_TABLE = "SELECT user_id FROM user_params WHERE chat_id=?";

    /**
     * Проверка, имеется ли переменная юзера для чата;
     *
     * @param chat_id чат, для которого запрашуют ;
     * @param name имя пользователя, который выполнил команду;
     * @return <li>{@code true} стикер присутствует в списке игнорируемых стикеров;<li>{@code false} отсутствует в списке игнорируемых стикеров;
     */
    public boolean getUserFromUserParams(long chat_id, long user_id, String name)
    {
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_USER_ID_FROM_TABLE))
        {
            statement.setString(1, String.valueOf(chat_id));
            try (ResultSet rset = statement.executeQuery())
            {
                while (rset.next())
                {
                    final String dbuser_id = rset.getString("user_id");
                    if (dbuser_id.equals(String.valueOf(user_id))){

                        return true;
                    }
                }
            }
            im.SendAnswer(chat_id, name, "При попытке выполнить getUserFromUserParams() и получить user_id с БД произошла ошибка;");
            return false;
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить getUserFromUserParams() и получить user_id с БД;");
            System.out.println(e);
            return false;
        }
    }

    private static final String ADD_USER_TO_TABLE = "INSERT INTO user_params (chat_id,user_id,last_message,restriction_time_message,sticker_count,gif_count,restriction_sticker_count,last_message_id) VALUES (?,?,?,?,?,?,?,?)";

    /**
     * Попытка добавить юзера в БД
     *
     * @param chat_id чат, для которого запрашуют ;
     * @param name имя пользователя, который выполнил команду;
     * @return <li>{@code true} стикер присутствует в списке игнорируемых стикеров;<li>{@code false} отсутствует в списке игнорируемых стикеров;
     */
    public boolean addUserToTable(long chat_id, long user_id, String name, String restriction_time)
    {
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(ADD_USER_TO_TABLE))
        {
            statement.setString(1, String.valueOf(chat_id));
            statement.setString(2, String.valueOf(user_id));
            statement.setString(3, String.valueOf("0"));
            statement.setString(4, String.valueOf(restriction_time));
            statement.setString(5, String.valueOf("0"));
            statement.setString(6, String.valueOf("0"));
            statement.setString(7, String.valueOf("0"));
            statement.setString(8, String.valueOf("0"));
            statement.execute();
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить addUserToTable() в БД;");
            System.out.println("Datebase error " + e);
            return false;
        }
        return true;
    }

    private static final String GET_RESTRICTION_TIME_FROM_TABLE = "SELECT restriction_time_message FROM user_params WHERE chat_id=? AND user_id=?";

    public String getRestrictionTimeMessage(long chat_id, long user_id, String name){
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_RESTRICTION_TIME_FROM_TABLE))
        {
            statement.setString(1, String.valueOf(chat_id));
            statement.setString(2, String.valueOf(user_id));
            try (ResultSet rset = statement.executeQuery()) {
                if (rset.next()){
                    return rset.getString("restriction_time_message");
                }
            }
            return null;
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить getRestrictionTimeMessage() и получить restriction_time_message с БД;");
            System.out.println(e);
            return null;
        }
    }

    private static final String GET_STICKER_COUNT_FROM_TABLE = "SELECT sticker_count FROM user_params WHERE chat_id=? AND user_id=?";

    public int getStickerCount(long chat_id, long user_id, String name){
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_STICKER_COUNT_FROM_TABLE))
        {
            statement.setString(1, String.valueOf(chat_id));
            statement.setString(2, String.valueOf(user_id));
            try (ResultSet rset = statement.executeQuery()) {
                if (rset.next()){
                    return Integer.parseInt(rset.getString("sticker_count"));
                }
            }
            return 0;
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить getGifCount() и получить sticker_count с БД;");
            System.out.println(e);
            return -1;
        }
    }

    private static final String GET_GIF_COUNT_FROM_TABLE = "SELECT gif_count FROM user_params WHERE chat_id=? AND user_id=?";

    public int getGifCount(long chat_id, long user_id, String name){
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_GIF_COUNT_FROM_TABLE))
        {
            statement.setString(1, String.valueOf(chat_id));
            statement.setString(2, String.valueOf(user_id));
            try (ResultSet rset = statement.executeQuery()) {
                if (rset.next()){
                    return Integer.parseInt(rset.getString("gif_count"));
                }
            }
            return 0;
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить getGifCount() и получить sticker_count с БД;");
            System.out.println(e);
            return -1;
        }
    }

    private static final String UPDATE_RESTRICTION_TIME_FROM_TABLE = "UPDATE user_params SET restriction_time_message=? WHERE chat_id=? AND user_id=?";
    private static final String UPDATE_STICKER_COUNT_FROM_TABLE = "UPDATE user_params SET sticker_count=? WHERE chat_id=? AND user_id=?";
    private static final String UPDATE_GIF_COUNT_FROM_TABLE = "UPDATE user_params SET gif_count=? WHERE chat_id=? AND user_id=?";
    private static final String UPDATE_REST_STICKER_COUNT_FROM_TABLE = "UPDATE user_params SET restriction_sticker_count=? WHERE chat_id=? AND user_id=?";

    public boolean UpdateRestrictionTime (long chat_id, long user_id, String name, String RestrictionTime){
        try (Connection con = dbMain.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_RESTRICTION_TIME_FROM_TABLE))
        {
            ps.setString(1, RestrictionTime);
            ps.setLong(2, chat_id);
            ps.setLong(3, user_id);
            ps.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить UpdateRestrictionTime() и обновить restriction_time_message в БД;");
            System.out.println(e);
            return false;
        }
    }

    public boolean UpdateStickerCount (long chat_id, long user_id, String name, String count){
        try (Connection con = dbMain.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_STICKER_COUNT_FROM_TABLE))
        {
            ps.setString(1, count);
            ps.setLong(2, chat_id);
            ps.setLong(3, user_id);
            ps.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить UpdateStickerCount() и обновить sticker_count в БД;");
            System.out.println(e);
            return false;
        }
    }

    public boolean UpdateGifCount (long chat_id, long user_id, String name, String count){
        try (Connection con = dbMain.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_GIF_COUNT_FROM_TABLE))
        {
            ps.setString(1, count);
            ps.setLong(2, chat_id);
            ps.setLong(3, user_id);
            ps.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить UpdateGifCount() и обновить gif_count в БД;");
            System.out.println(e);
            return false;
        }
    }

    public boolean UpdateRestrictionStickerCount (long chat_id, long user_id, String name, String count){
        try (Connection con = dbMain.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_REST_STICKER_COUNT_FROM_TABLE))
        {
            ps.setString(1, count);
            ps.setLong(2, chat_id);
            ps.setLong(3, user_id);
            ps.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить UpdateRestrictionStickerCount() и обновить restriction_sticker_count в БД;");
            System.out.println(e);
            return false;
        }
    }

}
