package com.index.dbHandler.handlers;

import com.index.IndexMain;
import com.index.dbHandler.dbMain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class dbRestrictionHandler {
    IndexMain im = new IndexMain();

    private static final String CHECK_USER_ID_IN_RESTRICTION_TABLE = "SELECT user_id FROM restrictions WHERE chat_id=?";

    /**
     * Проверка, есть ли пользователь в списке блокировок;
     *
     * @param chat_id чат, для которого запрашуют пользователя;
     * @param user_id искомый пользователь;
     * @param name имя пользователя, который выполнил команду;
     * @return <li>{@code true} пользователь присутствует в списке ранее блокируемых;<li>{@code false} пользователь отсутсвует в списке ранее блокируемых;
     */
    public boolean CheckUserInRestrictionTable(long chat_id, long user_id, String name)
    {
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(CHECK_USER_ID_IN_RESTRICTION_TABLE))
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
            im.SendAnswer(chat_id, name, "При попытке выполнить CheckUserInRestrictionTable() для пользователя " + user_id +" произошла ошибка;");
            return false;
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить CheckUserInRestrictionTable() для пользователя " + user_id +";");
            System.out.println(e);
            return false;
        }
    }

    private static final String GET_RESRICTION_TIME_FROM_TABLE = "SELECT restriction_time FROM restrictions WHERE chat_id=? AND user_id=?";

    /**
     * Получаем время блокировки;
     *
     * @param chat_id чат, для которого запрашуют пользователя;
     * @param user_id искомый пользователь;
     * @param name имя пользователя, который выполнил команду;
     * @return <li>{@code} возвращает максимальное время блокировки;
     */
    public long GetRestrictionTimeFromTable(long chat_id, long user_id, String name)
    {
        long result_time = 0;
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_RESRICTION_TIME_FROM_TABLE))
        {
            statement.setString(1, String.valueOf(chat_id));
            statement.setString(2, String.valueOf(user_id));
            try (ResultSet rset = statement.executeQuery())
            {
                while (rset.next())
                {
                    final long restriction_time = rset.getLong("restriction_time");
                    if ( restriction_time > System.currentTimeMillis()/1000 ) {
                        return restriction_time;
                    }
                }
            }
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить GetRestrictionTimeFromTable() для пользователя " + user_id +";");
            System.out.println(e);
        }
        return result_time;
    }

    private static final String UPDATE_RESRICTION_TIME_FROM_TABLE = "UPDATE restrictions SET restriction_time=? WHERE chat_id=? AND user_id=? AND restriction_time>=?";

    /**
     * Обновляем время блокировки;
     *
     * @param chat_id чат, для которого запрашуют пользователя;
     * @param user_id искомый пользователь;
     * @param name имя пользователя, который выполнил команду;
     * @param RestrictionTime время запрета;
     * @return <li>{@code true} время обновлено;<li>{@code false} время не обновлено;
     */
    public Boolean UpdateRestrictionTimeFromTable(long chat_id, long user_id, String name, long RestrictionTime)
    {
        try (Connection con = dbMain.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_RESRICTION_TIME_FROM_TABLE))
        {
            ps.setLong(1, chat_id);
            ps.setLong(2, user_id);
            ps.setLong(3, System.currentTimeMillis()/1000);
            ps.setLong(4, RestrictionTime);
            ps.executeUpdate();
            return true;
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить UpdateRestrictionTimeFromTable() для пользователя " + user_id +";");
            System.out.println(e);
            return false;
        }
    }

    private static final String ADD_RESTRICTED_USER_IN_TABLE = "INSERT INTO restrictions (chat_id,user_id,restriction_type,restriction_time,comment) VALUES (?,?,?,?,?)";

    /**
     * Вносим пользователя в список ограниченных возможностей;
     *
     * @param chat_id чат, для которого запрашуют пользователя;
     * @param user_id искомый пользователь;
     * @param name имя пользователя, который выполнил команду;
     * @param RestrictionTime время запрета;
     * @return <li>{@code true} пользователь заблокирован;<li>{@code false} пользователь не заблокирован;
     */
    public Boolean AddRestrictionUserToTable(long chat_id, long user_id, String name, long RestrictionTime){
        return AddRestrictionUserToTable(chat_id, user_id, name, RestrictionTime, "mute", "");
    }
    /**
     * Вносим пользователя в список ограниченных возможностей;
     *
     * @param chat_id чат, для которого запрашуют пользователя;
     * @param user_id искомый пользователь;
     * @param name имя пользователя, который выполнил команду;
     * @param RestrictionTime время запрета;
     * @param RestrictionType тип блокировки <li>mute - запрет писать;
     * @param Comment комментарий модератора;
     * @return <li>{@code true} пользователь заблокирован;<li>{@code false} пользователь не заблокирован;
     */
    public Boolean AddRestrictionUserToTable(long chat_id, long user_id, String name, long RestrictionTime, String RestrictionType, String Comment)
    {
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(ADD_RESTRICTED_USER_IN_TABLE))
        {
            statement.setString(1, String.valueOf(chat_id));
            statement.setString(2, String.valueOf(user_id));
            statement.setString(3, String.valueOf(RestrictionType));
            statement.setString(4, String.valueOf(RestrictionTime));
            statement.setString(5, String.valueOf(Comment));
            statement.execute();
            return true;
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить AddRestrictionUserToTable() в БД для пользователя " + user_id +";");
            im.SendAnswer(im.YummyReChat, name, ""+e);
            System.out.println("Datebase error " + e);
            return false;
        }

    }

    private static final String GET_RESRICTION_COMMENT_FROM_TABLE = "SELECT restriction_time AND comment FROM restrictions WHERE chat_id=? AND user_id=?";

    /**
     * Получаем время блокировки;
     *
     * @param chat_id чат, для которого запрашуют пользователя;
     * @param user_id искомый пользователь;
     * @param name имя пользователя, который выполнил команду;
     * @return <li>{@code} возвращает максимальное время блокировки;
     */
    public String GetRestrictionCommentFromTable(long chat_id, long user_id, String name)
    {
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_RESRICTION_COMMENT_FROM_TABLE))
        {
            statement.setString(1, String.valueOf(chat_id));
            statement.setString(2, String.valueOf(user_id));
            try (ResultSet rset = statement.executeQuery())
            {
                while (rset.next())
                {
                    final long restriction_time = rset.getLong("restriction_time");
                    if ( restriction_time > System.currentTimeMillis()/1000 ) {
                        final String comment = rset.getString("comment");
                        return comment;
                    }
                }
            }
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить GetRestrictionCommentFromTable() для пользователя " + user_id +";");
            System.out.println(e);
        }
        return "Комментарий не найден;";
    }
}
