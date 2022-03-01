package com.index.dbHandler.handlers;

import com.index.IndexMain;
import com.index.dbHandler.dbMain;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class dbStickerHandler {

    IndexMain im = new IndexMain();

    private static final String GET_IGNORED_STICKER = "SELECT sticker_url FROM sticker_ignore WHERE chat_id=?";

    /**
     * Проверка, имеется ли стикер в список игнорируемых
     *
     * @param sticker_url последняя часть ссылки на стикер-пак;
     * @param chat_id чат, для которого добавляется исключение;
     * @param name имя пользователя, который выполнил команду;
     * @return <li>{@code true} стикер присутствует в списке игнорируемых стикеров;<li>{@code false} отсутствует в списке игнорируемых стикеров;
     */
    public boolean checkStickerInList(String sticker_url, long chat_id, String name)
    {
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_IGNORED_STICKER))
        {
            statement.setString(1, String.valueOf(chat_id));
            try (ResultSet rset = statement.executeQuery())
            {
                while (rset.next())
                {
                    final String stickerurl = rset.getString("sticker_url");
                    if (stickerurl.equals(sticker_url)){
                        return true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке получить стикер игнорируемых стикеров;");
            System.out.println(e);
        }
        return false;
    }

    private static final String ADD_IGNORING_STICKERS = "INSERT INTO sticker_ignore (chat_id,sticker_url) VALUES (?,?)";

    /**
     * Проверка, добавлен ли стикер в список игнорируемых
     *
     * @param sticker_url последняя часть ссылки на стикер-пак;
     * @param chat_id чат, для которого добавляется исключение;
     * @param name имя пользователя, который выполнил команду;
     * @return {@code true} стикер добавлен в список игнорируемых стикеров; {@code false} стикер не добавлен в список игнорируемых стикеров;
     */
    public boolean getAddStickerStatus(String sticker_url, long chat_id, String name) {
        try (Connection con = dbMain.getConnection();
             PreparedStatement ps1 = con.prepareStatement(ADD_IGNORING_STICKERS))
        {
            ps1.setString(1, String.valueOf(chat_id));
            ps1.setString(2, sticker_url);
            ps1.execute();
            return true;
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке добавить пак " + sticker_url + " стикер игнорируемых стикеров;");
            System.out.println(e);
            return false;
        }
    }

    private static final String DELETE_IGNORING_STICKERS = "DELETE FROM sticker_ignore WHERE chat_id=? AND sticker_url=?";

    /**
     * Удаляем стикер из списка игнорируемых
     *
     * @param sticker_url последняя часть ссылки на стикер-пак;
     * @param chat_id чат, для которого добавляется исключение;
     * @param name имя пользователя, который выполнил команду;
     * @return {@code true} удален со списка игнорируемых; {@code false} не удален со списка игнорируемых;
     */
    public boolean getDeleteStickerStatus(String sticker_url, long chat_id, String name) {
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_IGNORING_STICKERS))
        {
            statement.setString(1, String.valueOf(chat_id));
            statement.setString(2, sticker_url);
            statement.execute();
            return true;
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке удалить пак " + sticker_url + " стикер из списка игнорируемых стикеров;");
            System.out.println(e);
            return false;
        }
    }

    /**
     * Проверка, добавлен ли стикер в список игнорируемых
     *
     * @param sticker_url ссылка на стикер-пак;
     * @return возвращает последнюю часть ссылки на стикер-пак;
     */
    public String getTokenFromStickerUrl(String sticker_url){
        return (sticker_url.substring(25));
    }

    public String getStickerURL(String sticker_url, long chat_id, String name)
    {
        String new_message = "Список разрешённых стикеров:\n";
        try (Connection con = dbMain.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_IGNORED_STICKER))
        {
            statement.setString(1, String.valueOf(chat_id));
            try (ResultSet rset = statement.executeQuery())
            {
                int i = 1;
                while (rset.next())
                {
                    final String stickerurl = rset.getString("sticker_url");
                    if (!sticker_url.equals("null") && sticker_url.equals(stickerurl) )
                    {
                        new_message += i + ". " + stickerurl + " - <a href=\"https://t.me/addstickers/" + stickerurl + "\">[*клик*]</a> \n";
                        i++;
                    }
                    else if ( sticker_url.equals("null") && new_message.equals("Список разрешённых стикеров:\n") ){
                        new_message += i + ". " + stickerurl + " - <a href=\"https://t.me/addstickers/" + stickerurl + "\">[*клик*]</a>\n";
                        i++;
                    }
                    else if ( !new_message.equals("Список разрешённых стикеров:\n") ){
                        new_message += i + ". " + stickerurl + " - <a href=\"https://t.me/addstickers/" + stickerurl + "\">[*клик*]</a>\n";
                        i++;
                        if (new_message.length() >= 3500 || i >= 50 ){
                            break;
                        }
                    }
                }
                if ( new_message.equals("Список разрешённых стикеров:\n") ){
                    new_message = "Достигнут конец списка";
                }
                return new_message;
            }
        }
        catch (Exception e)
        {
            im.SendAnswer(chat_id, name, "Ошибка при попытке получить стикер игнорируемых стикеров;");
            System.out.println(e);
            return "Error " + e;
        }
    }
}
