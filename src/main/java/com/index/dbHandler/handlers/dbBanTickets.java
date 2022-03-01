package com.index.dbHandler.handlers;

import com.index.IndexMain;
import com.index.dbHandler.dbMain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class dbBanTickets {

    IndexMain im = new IndexMain();

    private static final String CREATE_NEW_BAN_TICKET = "INSERT INTO ban_tickets (user_id,text,chat_id,status,restriction_type,until_date,user_name) VALUES (?,?,?,?,?,?,?)";

    /**
     * Вносим пользователя в список ограниченных возможностей;
     *
     * @param chat_id чат, для которого пользователь запрашивает разбан;
     * @param text комментарий пользователя;
     * @param user_id ид пользователя, который вызвал метод;
     * @param status имя пользователя, который выполнил команду;
     * @param RestrictionTime до когда заблокирован;
     * @param user_name имя пользователя, который вызвал команду;
     * @param RestrictionType тип блокировки <br> * mute - запрет писать;
     * @return <li>{@code true} пользователь заблокирован;<li>{@code false} пользователь не заблокирован;
     */
    public Boolean CreateNewBanTicket(long user_id, String text, long chat_id, String status, String RestrictionType, String RestrictionTime, String user_name)
    {
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(CREATE_NEW_BAN_TICKET))
        {
            statement.setString(1, String.valueOf(chat_id));
            statement.setString(2, String.valueOf(text));
            statement.setString(3, String.valueOf(user_id));
            statement.setString(4, String.valueOf(status));
            statement.setString(5, String.valueOf(RestrictionType));
            statement.setString(6, String.valueOf(RestrictionTime));
            statement.setString(7, String.valueOf(user_name));
            statement.execute();
            return true;
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, user_name, "Ошибка при попытке выполнить CreateNewBanTicket() в БД для пользователя " + user_id +";");
            im.SendAnswer(im.YummyReChat, user_name, "CreateNewBanTicket умер :( "+user_id+" "+" "+user_name+"\n"+e);
            System.out.println("Datebase error " + e);
            return false;
        }
    }

    private static final String GET_BAN_TICKET_TEXT = "SELECT status AND text FROM ban_tickets WHERE chat_id=? AND user_id=?";
     /**
     * Получаем комментарий с бан тикета от пользователя
     * @param user_id - ИД пользователя, для которого нужно получить комментарий;
     * @param name - имя пользователя, что вызывает команду;
     * @param chat_id - ИД чата, для которого нужно получить комментарий;
     **/
    public String GetTextFromBanTicket(long user_id, String name, long chat_id) {
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_BAN_TICKET_TEXT)) {
            statement.setString(1, String.valueOf(chat_id));
            statement.setString(2, String.valueOf(user_id));
            try (ResultSet rset = statement.executeQuery())
            {
                while (rset.next())
                {
                    final String status = rset.getString("status");
                    if ( status.equals("Открыт") ) {
                        final String text = rset.getString("text");
                        return text;
                    }
                }
            }
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить GetTextFromBanTicket() для пользователя " + user_id +";");
            System.out.println(e);
        }
        return "Комментарий не найден;";
    }

    private static final String GET_BAN_TICKET_TIME = "SELECT text FROM ban_tickets WHERE chat_id=? AND user_id=?";
    /**
     * Получаем время блокировки с бан тикета от пользователя
     * @param user_id - ИД пользователя, для которого нужно получить комментарий;
     * @param name - имя пользователя, что вызывает команду;
     * @param chat_id - ИД чата, для которого нужно получить комментарий;
     **/
    public String GetStatusFromBanTicket(long user_id, String name, long chat_id) {
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_BAN_TICKET_TIME)) {
            statement.setString(1, String.valueOf(chat_id));
            statement.setString(2, String.valueOf(user_id));
            String last_time = "0";
            try (ResultSet rset = statement.executeQuery())
            {
                while (rset.next())
                {
                    final String until_date = rset.getString("until_date");
                    if ( (System.currentTimeMillis()/1000 < Long.parseLong(until_date)) && until_date!=null )
                    {
                        return until_date;
                    }
                    else if ( until_date == null ){
                        return "Не найдено";
                    }
                }
                return "Не найдено";
            }
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке выполнить GetStatusFromBanTicket() для пользователя " + user_id +";");
            System.out.println(e);
        }
        return "Комментарий не найден;";
    }
}
